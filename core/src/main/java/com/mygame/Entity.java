package com.mygame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Entity {
    protected int width;
    protected int height;
    protected Texture texture;
    protected World world;
    private float x;
    private float y;
    public Entity(int width, int height, float x, float y, Texture texture, World world) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.world = world;
    }
    public abstract void update(float delta);
    public void draw(SpriteBatch batch){batch.draw(this.texture, x, y, width, height);}
    public float getX(){return x;}
    public float getY(){return y;}
    public void setX(float x){this.x = x;}
    public void setY(float y){this.y = y;}
}
