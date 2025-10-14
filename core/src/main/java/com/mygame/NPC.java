package com.mygame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NPC extends Entity {

    @Override
    public void update(float delta, int worldWidth, int worldHeight) {

    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(this.texture, x, y, width, height);
    }
}
