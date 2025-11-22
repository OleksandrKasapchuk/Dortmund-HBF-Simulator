package com.mygame.entity;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.item.ItemType;
import com.mygame.managers.global.WorldManager;
import com.mygame.managers.nonglobal.InventoryManager;
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
    private State currentState = State.NORMAL;


    public Player(int speed, int width, int height, float x, float y, Texture texture, World world) {
        super(width, height, x, y, texture, world);
        this.speed = speed;
    }

    // Lock/unlock movement (used for dialogues, cutscenes etc.)
    public void setMovementLocked(boolean locked) {
        this.isMovementLocked = locked;
    }

    @Override
    public void update(float delta) {

        // State-based movement speed
        if (currentState == State.STONED) {
            speed = 150;
        } else {
            speed = 500;
        }

        // If movement locked → stop here
        if (!isMovementLocked) {

            float newX = getX();
            float newY = getY();

            // === PC CONTROLS ===
            if (Gdx.app.getType() != Application.ApplicationType.Android) {

                if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
                    newX -= speed * delta;

                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
                    newX += speed * delta;

                if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
                    newY += speed * delta;

                if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
                    newY -= speed * delta;

                // === ANDROID TOUCHPAD ===
            } else if (touchpad != null) {
                float dx = touchpad.getKnobPercentX();
                float dy = touchpad.getKnobPercentY();

                newX += dx * speed * delta;
                newY += dy * speed * delta;
            }

            // Collision handling
            if (!isColliding(newX, getY())) {
                setX(newX);
            }
            if (!isColliding(getX(), newY)) {
                setY(newY);
            }
        }
    }

    // Check collisions with world blocks & items
    private boolean isColliding(float checkX, float checkY) {

        // Check tile-based collision (world map)
        if (world.isSolid(checkX, checkY - getHeight() - 20) ||
            world.isSolid(checkX + getWidth(), checkY - getHeight() - 20) ||
            world.isSolid(checkX, checkY - 20) ||
            world.isSolid(checkX + getWidth(), checkY - 20)) {

            return true;
        }

        // Check collision with solid items
        for (Item item : WorldManager.getCurrentWorld().getItems()) {
            if (item.isSolid() && intersects(checkX, checkY, item)) {
                return true;
            }
        }
        return false;
    }

    // Simple AABB collision check
    private boolean intersects(float px, float py, Item item) {
        return px < item.getX() + item.getWidth() &&
            px + getWidth() > item.getX() &&
            py < item.getY() + item.getHeight() &&
            py + getHeight() > item.getY();
    }

    // Money getter
    public int getMoney() {
        return inventory.getAmount(ItemRegistry.get("item.money.name"));
    }

    public InventoryManager getInventory() {
        return inventory;
    }

    // Use items (food, drugs, boosters etc.)
    public void useItem(ItemType item) {
        if (inventory.isUsable(item) && inventory.hasItem(item)) {
            item.apply();
            inventory.removeItem(item, 1);
            System.out.println("Used " + item.getName());
        }
    }

    // State setters
    public void setStone() { currentState = State.STONED; }
    public void setNormal() { currentState = State.NORMAL; }

    public State getState() { return currentState; }

    public void setWorld(World world) {
        this.world = world;
    }
}
