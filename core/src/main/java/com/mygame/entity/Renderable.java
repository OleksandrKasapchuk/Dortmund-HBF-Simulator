package com.mygame.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Renderable {
    void draw(SpriteBatch batch);
    float getY();
}
