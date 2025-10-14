package com.mygame;

import com.badlogic.gdx.graphics.Texture;

public class Block {
    boolean issolid;
    Texture texture;

    public Block(boolean issolid, Texture texture) {
        this.issolid = issolid;
        this.texture = texture;
    }
}
