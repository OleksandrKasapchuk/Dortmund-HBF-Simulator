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
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.save.SettingsManager;
import com.mygame.world.World;


// Player entity controlled by user
public class Player extends Entity {

    protected int speed;
    public Touchpad touchpad;

    private final InventoryManager inventory = new InventoryManager();
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

    @Override
    public void update(float delta) {

        // If movement is locked, do nothing
        if (isMovementLocked) {
            return;
        }

        // State-based movement speed
        if (currentState == State.STONED) {
            speed = 150;
        } else {
            speed = 500;
        }


        float moveSpeed = speed * delta;
        float dx = 0, dy = 0;

        // === PC CONTROLS ===
        if (Gdx.app.getType() != Application.ApplicationType.Android) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
                moveSpeed *= 3;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
                dx -= moveSpeed;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
                dx += moveSpeed;
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
                dy += moveSpeed;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
                dy -= moveSpeed;
        } else if (touchpad != null) { // === ANDROID TOUCHPAD ===
            dx = touchpad.getKnobPercentX() * speed * delta;
            dy = touchpad.getKnobPercentY() * speed * delta;
        }

        // --- Collision Detection and Movement ---
        Rectangle playerRect = new Rectangle(getX(), getY(), getWidth(), getHeight());

        // Move on X axis
        playerRect.x += dx;
        if (!world.isCollidingWithMap(playerRect) && !isCollidingWithSolidItems(playerRect)) {
            setX(getX() + dx);
        }
        playerRect.x = getX(); // Reset X to current position

        // Move on Y axis
        playerRect.y += dy;
        if (!world.isCollidingWithMap(playerRect) && !isCollidingWithSolidItems(playerRect)) {
            setY(getY() + dy);
        }
        playerRect.y = getY(); // Reset Y to current position

        // --- World Bounds Clamping ---
        if (world != null) {
            float clampedX = MathUtils.clamp(getX(), 0, world.mapWidth - getWidth());
            float clampedY = MathUtils.clamp(getY(), 0, world.mapHeight - getHeight());
            setX(clampedX);
            setY(clampedY);
        }
    }

    /**
     * Checks if the player at a new position would collide with any solid items.
     */
    private boolean isCollidingWithSolidItems(Rectangle playerRect) {
        if (world == null) return false;
        for (Item item : world.getAllItems()) {
            if (item.isSolid()) {
                Rectangle itemRect = new Rectangle(item.getX(), item.getY(), item.getWidth(), item.getHeight());
                if (playerRect.overlaps(itemRect)) {
                    return true;
                }
            }
        }
        return false;
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
    }

    public State getState() { return currentState; }
}
