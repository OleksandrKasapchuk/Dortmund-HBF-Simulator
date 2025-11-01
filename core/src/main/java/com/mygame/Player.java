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


    public void setMovementLocked(boolean locked) {this.isMovementLocked = locked;}

    public Player(int speed, int width, int height, float x, float y, Texture texture, World world){
        super(width, height, x, y, texture, world);
        this.speed = speed;
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

            // === Спочатку перевірка по X ===
            boolean collideX =
                world.isSolid(newX, this.getY() - this.height - 20) ||
                    world.isSolid(newX + this.width, this.getY() - this.height - 20) ||
                    world.isSolid(newX, this.getY() - 20) ||
                    world.isSolid(newX + this.width, this.getY() - 20);

            if (!collideX) {this.setX(newX);}

            // === Потім перевірка по Y ===
            boolean collideY =
                world.isSolid(this.getX(), newY - this.height - 20) ||
                    world.isSolid(this.getX() + this.width, newY - this.height - 20) ||
                    world.isSolid(this.getX(), newY - 20) ||
                    world.isSolid(this.getX() + this.width, newY - 20);

            if (!collideY) {this.setY(newY);}
        }
    }
    public int getMoney(){return inventory.getAmount("money");}
    public InventoryManager getInventory(){return inventory;}

}
