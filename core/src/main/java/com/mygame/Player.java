package com.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player extends Entity {
    protected int speed;
    public boolean moveLeftPressed = false;
    public boolean moveRightPressed = false;
    public boolean moveUpPressed = false;
    public boolean moveDownPressed = false;
    public boolean actPressed = false;

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

        // рух
        if ((Gdx.input.isKeyPressed(Input.Keys.LEFT) || moveLeftPressed)) newX -= speed * delta;
        if ((Gdx.input.isKeyPressed(Input.Keys.RIGHT) || moveRightPressed)) newX += speed * delta;
        if ((Gdx.input.isKeyPressed(Input.Keys.UP) || moveUpPressed)) newY += speed * delta;
        if ((Gdx.input.isKeyPressed(Input.Keys.DOWN) || moveDownPressed)) newY -= speed * delta;


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
