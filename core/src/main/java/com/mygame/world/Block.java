package com.mygame.world;

import com.badlogic.gdx.graphics.Texture;

/**
 * Represents a single block in the game world.
 * Each block can be solid (impassable) or non-solid (passable)
 * and has a texture for rendering.
 */
public class Block {

    /** Whether the block is solid and blocks player/NPC movement */
    boolean issolid;

    /** The texture used to render this block */
    Texture texture;

    /**
     * Constructs a Block with the given solidity and texture.
     *
     * @param issolid true if block is solid, false otherwise
     * @param texture texture used for rendering the block
     */
    public Block(boolean issolid, Texture texture) {
        this.issolid = issolid;
        this.texture = texture;
    }
}
