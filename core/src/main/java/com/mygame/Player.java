package com.mygame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

public class Player extends Entity {
    protected int speed;
    public Touchpad touchpad;

    public Player(int speed, int width, int height, float x, float y, Texture texture, World world){
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.world = world;
    }

    @Override
    public void update(float delta){
        float newX = x;
        float newY = y;

        if (Gdx.app.getType() != Application.ApplicationType.Android) {
            if ((Gdx.input.isKeyPressed(Input.Keys.LEFT))) newX -= speed * delta;
            if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT))) newX += speed * delta;
            if ((Gdx.input.isKeyPressed(Input.Keys.UP))) newY += speed * delta;
            if ((Gdx.input.isKeyPressed(Input.Keys.DOWN))) newY -= speed * delta;
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

}
