package com.mygame.entity.npc;

import com.badlogic.gdx.Gdx;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.world.World;
import com.mygame.world.zone.TransitionZone;
import com.mygame.world.zone.Zone;

import java.util.Objects;

/**
 * Police NPC that can chase the player and change states depending on distance.
 */
public class Police extends NPC {

    public enum PoliceState { IDLE, CHASING, ESCAPED, CAUGHT, TRANSITIONING }
    private PoliceState state = PoliceState.IDLE;

    // Target coordinates for the chase
    private float targetX;
    private float targetY;

    private boolean movingToTransition = false;
    private TransitionZone activeTransition = null; // The transition the police is currently moving towards

    private float lostTimer = 0f;
    private static final float MAX_LOST_TIME = 5f; // якщо поліція не бачить 5 секунд — втеча

    public Police(String id, String name, int width, int height, float x, float y,
                  String textureKey, String faceTextureKey, World world, int speed, DialogueNode dialogue, ItemManager itemManager) {
        super(id, name, width, height, x, y, textureKey, faceTextureKey, world,
            0, 0, 0f, 0f, // Static patrol path, not used for police
            speed, dialogue, itemManager);
    }

    public TransitionZone findTransitionToPlayer(World playerWorld) {
        for (Zone zone : getWorld().getZones()) {
            if (!(zone instanceof TransitionZone t)) continue;
            if (t.targetWorldId.equals(playerWorld.getName())) {
                Gdx.app.log("Police", "Found a transition to " + playerWorld.getName() + " at " + t.getArea());
                return t;
            }
        }
        Gdx.app.log("Police", "Could not find a transition to " + playerWorld.getName());
        return null;
    }

    /**
     * Updates police behaviour depending on its current state.
     * @param player The player being chased.
     * @return The Transition object if the police enters a transition zone, otherwise null.
     */
    public TransitionZone update(Player player) {

        if (Objects.requireNonNull(state) == PoliceState.CHASING) {
            if (player.getWorld() == this.getWorld()) {
                // Гравець у тому ж світі – звичайне переслідування
                targetX = player.getX();
                targetY = player.getY();
                movingToTransition = false;
                activeTransition = null;
            } else {
                // Гравець в іншому світі
                if (!movingToTransition) {
                    activeTransition = findTransitionToPlayer(player.getWorld());
                    if (activeTransition != null) {
                        targetX = activeTransition.getArea().x + activeTransition.getArea().width / 2;
                        targetY = activeTransition.getArea().y + activeTransition.getArea().height / 2;
                        movingToTransition = true;
                    }
                }

                if (movingToTransition && activeTransition != null) {
                    // Якщо поліція досягла зони переходу
                    if (activeTransition.getArea().contains(getX(), getY())) {
                        movingToTransition = false; // Reset for next chase
                        TransitionZone transitionToReturn = activeTransition;
                        activeTransition = null;
                        setState(PoliceState.TRANSITIONING);
                        return transitionToReturn;
                    }
                }
            }

            chase();

            // Check for state changes
            if (isPlayerCaught(player)) setState(PoliceState.CAUGHT);
            if (isPlayerEscaped(player)) setState(PoliceState.ESCAPED);
        }
        return null; // No transition triggered
    }

    private boolean isPlayerCaught(Player player) {
        // Caught only if in the same world and near the player
        return player.getWorld() == this.getWorld() && isPlayerNear(player);
    }

   private boolean isPlayerEscaped(Player player) {
        if (player.getWorld() != this.getWorld()) {
            return !movingToTransition && activeTransition == null;
        }
        if (!isPlayerNear(player, 800)) {
            lostTimer += Gdx.graphics.getDeltaTime();
            return lostTimer >= MAX_LOST_TIME;
        } else {
            lostTimer = 0f;
            return false;
        }
    }

    private void chase() {
        if (state != PoliceState.CHASING) return;

        float delta = Gdx.graphics.getDeltaTime();

        // Move horizontally toward targetX
        if (this.getX() > targetX) {
            this.setX(this.getX() - getSpeed() * delta);
        } else if (this.getX() < targetX) {
            this.setX(this.getX() + getSpeed() * delta);
        }

        // Move vertically toward targetY
        if (this.getY() > targetY) {
            this.setY(this.getY() - getSpeed() * delta);
        } else if (this.getY() < targetY) {
            this.setY(this.getY() + getSpeed() * delta);
        }
    }

    public void startChase(Player player) {
        this.setState(PoliceState.CHASING);
        // Set initial target coordinates
        this.targetX = player.getX();
        this.targetY = player.getY();
    }

    public PoliceState getState() {
        return state;
    }

    public void setState(PoliceState state) {
        if (this.state == state) return;
        this.state = state;
        Gdx.app.log("Police", "State changed to: " + state);
        EventBus.fire(new Events.PoliceStateChangedEvent(state));
    }
}
