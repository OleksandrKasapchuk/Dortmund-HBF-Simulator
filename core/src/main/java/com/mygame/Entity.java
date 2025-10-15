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

    public abstract void update(float delta);
    public abstract void draw(SpriteBatch batch);
}
