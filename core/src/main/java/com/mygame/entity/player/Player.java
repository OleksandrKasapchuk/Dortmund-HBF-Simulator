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
import com.mygame.entity.item.ItemType;
import com.mygame.game.save.SettingsManager;
import com.mygame.world.World;
import com.mygame.world.WorldManager;


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
        // Move on X axis
        if (!isCollidingWithMap(getX() + dx, getY()) && !isCollidingWithSolidItems(getX() + dx, getY())) {
            setX(getX() + dx);
        }

        // Move on Y axis
        if (!isCollidingWithMap(getX(), getY() + dy) && !isCollidingWithSolidItems(getX(), getY() + dy)) {
            setY(getY() + dy);
        }

        // --- World Bounds Clamping ---
        if (world != null) {
            float clampedX = MathUtils.clamp(getX(), 0, world.mapWidth - getWidth());
            float clampedY = MathUtils.clamp(getY(), 0, world.mapHeight - getHeight());
            setX(clampedX);
            setY(clampedY);
        }
    }

    /**
     * Checks if the player at a new position would collide with a solid tile on the map.
     * It checks the four corners of the player's bounding box.
     */
    private boolean isCollidingWithMap(float newX, float newY) {
        // Check bottom-left corner
        if (world.isSolid(newX, newY)) return true;
        // Check bottom-right corner
        if (world.isSolid(newX + getWidth(), newY)) return true;
        // Check top-left corner
        if (world.isSolid(newX, newY + getHeight())) return true;
        // Check top-right corner
        return world.isSolid(newX + getWidth(), newY + getHeight());
    }

    /**
     * Checks if the player at a new position would collide with any solid items.
     */
    private boolean isCollidingWithSolidItems(float newX, float newY) {
        Rectangle playerRect = new Rectangle(newX, newY, getWidth(), getHeight());
        for (Item item : WorldManager.getCurrentWorld().getItems()) {
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

    public void useItem(ItemType item) {
        if (inventory.isUsable(item) && inventory.hasItem(item)) {
            item.apply();
            inventory.removeItem(item, 1);
            System.out.println("Used " + item.getNameKey());
        }
    }

    // State setters
    public void setStone() { currentState = State.STONED; }
    public void setNormal() { currentState = State.NORMAL; }

    public State getState() { return currentState; }
}
