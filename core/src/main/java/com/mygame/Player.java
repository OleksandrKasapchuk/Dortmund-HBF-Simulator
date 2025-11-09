package com.mygame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;


public class Player extends Entity {
    protected int speed;
    public Touchpad touchpad;
    private final InventoryManager inventory = new InventoryManager();
    private boolean isMovementLocked = false;
    private ItemManager itemManager;


    public void setMovementLocked(boolean locked) {this.isMovementLocked = locked;}

    public Player(int speed, int width, int height, float x, float y, Texture texture, World world,ItemManager itemManager){
        super(width, height, x, y, texture, world);
        this.speed = speed;
        this.itemManager = itemManager;
    }

    @Override
    public void update(float delta) {
        if (!isMovementLocked) {

            float newX = getX();
            float newY = getY();

            // === Керування ===
            if (Gdx.app.getType() != Application.ApplicationType.Android) {
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
                    newX -= speed * delta;
                if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
                    newX += speed * delta;
                if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
                    newY += speed * delta;
                if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
                    newY -= speed * delta;
            } else if (touchpad != null) {
                float dx = touchpad.getKnobPercentX();
                float dy = touchpad.getKnobPercentY();
                newX += dx * speed * delta;
                newY += dy * speed * delta;
            }

            // === Перевірка колізій по X ===
            if (!isColliding(newX, getY(), itemManager)) {
                setX(newX);
            }

            // === Перевірка колізій по Y ===
            if (!isColliding(getX(), newY, itemManager)) {
                setY(newY);
            }
        }
    }

    private boolean isColliding(float checkX, float checkY, ItemManager itemManager) {
        // Перевірка по блоках
        if (world.isSolid(checkX, checkY - height - 20) ||
            world.isSolid(checkX + width, checkY - height - 20) ||
            world.isSolid(checkX, checkY - 20) ||
            world.isSolid(checkX + width, checkY - 20)) {
            return true;
        }

        for (Item item : itemManager.getItems()) { // потрібно, щоб World міг повертати ItemManager
            if (item.isSolid() && intersects(checkX, checkY, item)) {
                return true;
            }
        }
        return false;
    }

    private boolean intersects(float px, float py, Item item) {
        return px < item.getX() + item.width &&
            px + width > item.getX() &&
            py < item.getY() + item.height &&
            py + height > item.getY();
    }

    public int getMoney(){return inventory.getAmount("money");}
    public InventoryManager getInventory(){return inventory;}

    public void useItem(String itemName) {
        if (inventory.isUsable(itemName) && inventory.hasItem(itemName)) {
            inventory.applyEffect(itemName);
            inventory.removeItem(itemName, 1);
            System.out.println("Used " + itemName);
        }
    }
}
