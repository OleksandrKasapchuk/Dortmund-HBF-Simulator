package com.mygame.entity.npc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.util.pathfinding.Node;
import com.mygame.util.pathfinding.Pathfinder;
import com.mygame.world.World;
import com.mygame.world.zone.TransitionZone;
import com.mygame.world.zone.Zone;

import java.util.List;

/**
 * Police NPC with A* pathfinding for chasing the player.
 * Optimized to handle tight spaces and avoid getting stuck.
 */
public class Police extends NPC {

    public enum PoliceState { IDLE, CHASING, ESCAPED, CAUGHT, TRANSITIONING }
    private PoliceState state = PoliceState.IDLE;

    private float targetX;
    private float targetY;
    private final ItemManager itemManager;
    private Player chasingPlayer;

    private boolean movingToTransition = false;
    private TransitionZone activeTransition = null;

    private float lostTimer = 0f;
    private static final float MAX_LOST_TIME = 10f;

    // Pathfinding
    private List<Node> currentPath;
    private float pathRecalculateTimer = 0f;
    private static final float PATH_RECALC_INTERVAL = 0.5f;

    private final Rectangle tmpRect = new Rectangle();

    public Police(String id, String name, int width, int height, float x, float y,
                  String textureKey, String faceTextureKey, World world, int speed, DialogueNode dialogue, ItemManager itemManager) {
        super(id, name, "police", width, height, x, y, textureKey, faceTextureKey, world,
            0, 0, 0f, 0f,
            speed, 150, dialogue, itemManager);
        this.itemManager = itemManager;
        this.targetX = x;
        this.targetY = y;
    }

    @Override
    public Rectangle getBounds() {
        // Very small collision box for feet to navigate tight spaces
        float collisionWidth = getWidth() * 0.25f;
        float collisionHeight = getHeight() * 0.08f;
        bounds.set(getX() + (getWidth() - collisionWidth) / 2f, getY(), collisionWidth, collisionHeight);
        return bounds;
    }

    public TransitionZone update(Player player) {
        this.chasingPlayer = player;

        if (state == PoliceState.CHASING) {
            if (player.getWorld() == this.getWorld()) {
                targetX = player.getX();
                targetY = player.getY();
                movingToTransition = false;
                activeTransition = null;
            } else {
                if (!movingToTransition) {
                    activeTransition = findTransitionToPlayer(player.getWorld());
                    if (activeTransition != null) {
                        targetX = activeTransition.getArea().x + activeTransition.getArea().width / 2f;
                        targetY = activeTransition.getArea().y + activeTransition.getArea().height / 2f;
                        movingToTransition = true;
                    }
                }

                if (movingToTransition && activeTransition != null) {
                    if (activeTransition.getArea().contains(getX() + getWidth() / 2f, getY())) {
                        movingToTransition = false;
                        TransitionZone transitionToReturn = activeTransition;
                        activeTransition = null;
                        setState(PoliceState.TRANSITIONING);
                        return transitionToReturn;
                    }
                }
            }

            chase();

            if (isPlayerCaught(player)) setState(PoliceState.CAUGHT);
            if (isPlayerEscaped(player)) setState(PoliceState.ESCAPED);
        }
        return null;
    }

    private void chase() {
        if (state != PoliceState.CHASING) return;

        float delta = Gdx.graphics.getDeltaTime();
        World world = getWorld();
        if (world == null || world.getPathfinder() == null) return;

        pathRecalculateTimer -= delta;
        if (pathRecalculateTimer <= 0) {
            pathRecalculateTimer = PATH_RECALC_INTERVAL;
            updatePath();
        }

        // Emergency nudge if stuck inside something
        if (isColliding(getBounds())) {
            nudgeOutOfCollision();
        }

        if (currentPath == null || currentPath.isEmpty()) {
            moveDirectly(delta);
            return;
        }

        // Target the center of the next node
        Node nextNode = currentPath.get(0);
        float nextCenterX = nextNode.x * world.tileWidth + world.tileWidth / 2f;
        float nextCenterY = nextNode.y * world.tileHeight + world.tileHeight / 2f;

        // Calculate position based on our collision box center
        Rectangle b = getBounds();
        float myCenterX = b.x + b.width / 2f;
        float myCenterY = b.y + b.height / 2f;

        float dirX = nextCenterX - myCenterX;
        float dirY = nextCenterY - myCenterY;
        float dist = (float) Math.sqrt(dirX * dirX + dirY * dirY);

        if (dist < 10) { // Increased arrival tolerance
            currentPath.remove(0);
            if (currentPath.isEmpty()) return;
        }

        if (dist > 0) {
            float speed = getSpeed() * delta;
            moveWithCollision((dirX / dist) * speed, (dirY / dist) * speed);
        }
    }

    private void updatePath() {
        World world = getWorld();
        Pathfinder pf = world.getPathfinder();
        if (pf == null) return;

        // Mark current static items as non-walkable in the grid
        if (itemManager != null) {
            for (Item item : itemManager.getAllItems()) {
                if (item.isSolid() && item.getWorld() == world) {
                    int ix = (int) ((item.getX() + item.getWidth() / 2f) / world.tileWidth);
                    int iy = (int) ((item.getY() + item.getHeight() / 2f) / world.tileHeight);
                    pf.setNodeWalkable(ix, iy, false);
                }
            }
        }

        Rectangle b = getBounds();
        int startX = (int) ((b.x + b.width / 2f) / world.tileWidth);
        int startY = (int) ((b.y + b.height / 2f) / world.tileHeight);
        int endX = (int) (targetX / world.tileWidth);
        int endY = (int) (targetY / world.tileHeight);

        currentPath = pf.findPath(startX, startY, endX, endY);
    }

    private void moveDirectly(float delta) {
        Rectangle b = getBounds();
        float dirX = targetX - (b.x + b.width / 2f);
        float dirY = targetY - (b.y + b.height / 2f);
        float dist = (float) Math.sqrt(dirX * dirX + dirY * dirY);

        if (dist < 15) return;

        float speed = getSpeed() * delta;
        moveWithCollision((dirX / dist) * speed, (dirY / dist) * speed);
    }

    private void moveWithCollision(float dx, float dy) {
        Rectangle rect = tmpRect.set(getBounds());

        // Try full movement
        rect.x += dx;
        rect.y += dy;
        if (!isColliding(rect)) {
            setX(getX() + dx);
            setY(getY() + dy);
            return;
        }

        // Sliding logic: try X only
        rect.set(getBounds());
        rect.x += dx;
        if (!isColliding(rect)) {
            setX(getX() + dx);
        }

        // Sliding logic: try Y only
        rect.set(getBounds());
        rect.y += dy;
        if (!isColliding(rect)) {
            setY(getY() + dy);
        }
    }

    private void nudgeOutOfCollision() {
        float step = 2f;
        Rectangle b = getBounds();
        // Try 4 directions to find a free spot
        if (!isColliding(tmpRect.set(b.x + step, b.y, b.width, b.height))) setX(getX() + step);
        else if (!isColliding(tmpRect.set(b.x - step, b.y, b.width, b.height))) setX(getX() - step);
        else if (!isColliding(tmpRect.set(b.x, b.y + step, b.width, b.height))) setY(getY() + step);
        else if (!isColliding(tmpRect.set(b.x, b.y - step, b.width, b.height))) setY(getY() - step);
    }

    private boolean isColliding(Rectangle rect) {
        World world = getWorld();
        if (world == null) return false;
        if (world.isCollidingWithMap(rect)) return true;

        if (itemManager != null) {
            for (Item item : itemManager.getAllItems()) {
                if (item.isSolid() && item.getWorld() == world) {
                    if (rect.overlaps(item.getBounds())) return true;
                }
            }
        }
        return false;
    }

    public void startChase(Player player) {
        this.chasingPlayer = player;
        this.setState(PoliceState.CHASING);
        this.targetX = player.getX();
        this.targetY = player.getY();
        this.pathRecalculateTimer = 0;
    }

    public void setState(PoliceState state) {
        if (this.state == state) return;
        this.state = state;
        EventBus.fire(new Events.PoliceStateChangedEvent(state));
        if (state == PoliceState.CHASING) {
            EventBus.fire(new Events.ActionRequestEvent("act.quest.chase.restore_ui"));
        }
    }

    private boolean isPlayerCaught(Player player) {
        return player.getWorld() == this.getWorld() && isPlayerNear(player);
    }

    private boolean isPlayerEscaped(Player player) {
        if (player.getWorld() != this.getWorld()) return !movingToTransition && activeTransition == null;
        if (!isPlayerNear(player, 800)) {
            lostTimer += Gdx.graphics.getDeltaTime();
            return lostTimer >= MAX_LOST_TIME;
        } else {
            lostTimer = 0f;
            return false;
        }
    }

    public TransitionZone findTransitionToPlayer(World playerWorld) {
        World currentWorld = getWorld();
        if (currentWorld == null) return null;
        for (Zone zone : currentWorld.getZones()) {
            if (zone instanceof TransitionZone t && t.targetWorldId.equals(playerWorld.getName())) return t;
        }
        return null;
    }

    public PoliceState getState() { return state; }

    @Override
    public void update(float delta) {
        if (state == PoliceState.CHASING && chasingPlayer != null) {
            update(chasingPlayer);
        }
    }
}
