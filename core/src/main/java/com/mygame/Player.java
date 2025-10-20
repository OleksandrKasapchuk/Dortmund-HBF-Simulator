package com.mygame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

import java.util.HashMap;

public class Player extends Entity {
    protected int speed;
    public Touchpad touchpad;
    private float money = 0;
    private Inventory inventory = new Inventory();

    public Player(int speed, int width, int height, float x, float y, Texture texture, World world){
        super(width, height, x, y, texture, world);
        this.speed = speed;
    }

    @Override
    public void update(float delta){
        float newX = x;
        float newY = y;

        if (Gdx.app.getType() != Application.ApplicationType.Android) {
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) newX -= speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) newX += speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) newY += speed * delta;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) newY -= speed * delta;
        } else if (touchpad != null) {
            float dx = touchpad.getKnobPercentX(); // -1 до 1
            float dy = touchpad.getKnobPercentY(); // -1 до 1
            newX += dx * speed * delta;
            newY += dy * speed * delta;
        }

        boolean collide =
            world.isSolid(newX, newY - this.height) || // лівий нижній
            world.isSolid(newX + this.width, newY - this.height) || // правий нижній
            world.isSolid(newX, newY) || // лівий верхній
            world.isSolid(newX + this.width, newY);   // правий верхній

        if (!collide) {
            x = newX;
            y = newY;
        }
    }
    public void draw(SpriteBatch batch){
        batch.draw(this.texture, x, y, width, height);
    }
    public int getMoney(){return inventory.getAmount("money");}
    public Inventory getInventory(){return inventory;}
}
