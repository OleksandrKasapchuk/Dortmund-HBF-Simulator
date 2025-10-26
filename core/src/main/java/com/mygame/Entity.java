package com.mygame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class Entity {
    protected int width;
    protected int height;
    protected float x;
    protected float y;
    protected Texture texture;
    protected World world;
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
}
