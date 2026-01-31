package com.mygame.entity.player;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.mygame.entity.Entity;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemDefinition;
import com.mygame.entity.item.ItemManager;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.save.SettingsManager;
import com.mygame.world.World;


// Player entity controlled by user
public class Player extends Entity {

    protected int speed;
    public Touchpad touchpad;

    private final InventoryManager inventory = new InventoryManager();
    private ItemManager itemManager;
    private boolean isMovementLocked = false;

    public enum State {
        NORMAL("player.state.normal"),
        STONED("player.state.stoned");

        private final String localizationKey;

        State(String localizationKey) {
            this.localizationKey = localizationKey;
        }

        public String getLocalizationKey() {
            return localizationKey;
        }
    }

    private State currentState;


    public Player(int speed, int width, int height, float x, float y, Texture texture, World world) {
        super(width, height, x, y, texture, world);
        this.speed = speed;
        this.currentState = SettingsManager.load().playerState;
    }

    // Lock/unlock movement (used for dialogues, cutscenes etc.)
    public void setMovementLocked(boolean locked) {
        this.isMovementLocked = locked;
    }
    public void setItemManager(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    // --- UPDATE METHOD ---)
    @Override
    public void update(float delta) {

        if (isMovementLocked) return;

        speed = (currentState == State.STONED) ? 150 : 500;

        float moveSpeed = speed * delta;
        float dx = 0, dy = 0;

        // === INPUT ===
        if (Gdx.app.getType() != Application.ApplicationType.Android) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) moveSpeed *= 5;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) dx -= moveSpeed;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) dx += moveSpeed;
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) dy += moveSpeed;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) dy -= moveSpeed;
        } else if (touchpad != null) {
            dx = touchpad.getKnobPercentX() * speed * delta;
            dy = touchpad.getKnobPercentY() * speed * delta;
        }

        // --- COLLISION AWARE MOVEMENT ---
        moveWithPush(dx, dy);

        // --- WORLD BOUNDS ---
        if (world != null) {
            setX(MathUtils.clamp(getX(), 0, world.mapWidth - getWidth()));
            setY(MathUtils.clamp(getY(), 0, world.mapHeight - getHeight()));
        }
    }
    private void moveWithPush(float dx, float dy) {

        Rectangle rect = getBounds();

        // ===== X =====
        float oldX = getX();
        rect.x = oldX + dx;

        Item itemX = getCollidingSolidItem(rect);

        if (world.isCollidingWithMap(rect)) {
            setX(oldX); // ⬅ ЖОРСТКИЙ ROLLBACK
        }
        else if (itemX != null) {
            if (dx > 0) {
                setX(itemX.getBounds().x - getWidth());
            } else if (dx < 0) {
                setX(itemX.getBounds().x + itemX.getBounds().width);
            }
        }
        else {
            setX(oldX + dx);
        }

        rect.x = getX();


        // ===== Y =====
        float oldY = getY();
        rect.y = oldY + dy;

        Item itemY = getCollidingSolidItem(rect);

        if (world.isCollidingWithMap(rect)) {
            setY(oldY); // ⬅ ROLLBACK
        }
        else if (itemY != null) {
            if (dy > 0) {
                setY(itemY.getBounds().y - getHeight());
            } else if (dy < 0) {
                setY(itemY.getBounds().y + itemY.getBounds().height);
            }
        }
        else {
            setY(oldY + dy);
        }

        rect.y = getY();
    }


    private Item getCollidingSolidItem(Rectangle rect) {
        for (Item item : itemManager.getAllItems()) {
            if (!item.isSolid()) continue;
            if (item.getWorld() != world) continue;
            if (rect.overlaps(item.getBounds())) {
                return item;
            }
        }
        return null;
    }

    public InventoryManager getInventory() {
        return inventory;
    }

    public void useItem(ItemDefinition item) {
        if (inventory.isUsable(item) && inventory.hasItem(item)) {
            inventory.removeItem(item, 1);
            EventBus.fire(new Events.ItemUsedEvent(item));
        }
    }

    // State setters
    public void setState(Player.State state) {
        this.currentState = state;
        EventBus.fire(new Events.PlayerStateChangedEvent(state));
        EventBus.fire(new Events.SaveRequestEvent());
    }

    public State getState() { return currentState; }
}
