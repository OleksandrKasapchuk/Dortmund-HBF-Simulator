package com.mygame.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.world.World;

public interface Renderable {
    void draw(SpriteBatch batch);
    float getY();
    World getWorld();
}
