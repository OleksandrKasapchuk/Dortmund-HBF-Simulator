package com.mygame.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.entity.player.Player;
import com.mygame.world.World;

/**
 * Base abstract class for all objects rendered in the world:
 * players, NPCs, items, etc.
 */
public abstract class Entity {

    // --- Base entity properties ---
    private int width;
    private int height;

    protected Texture texture;   // sprite of the entity
    protected World world;       // reference to the world (for collisions etc.)

    private float x;             // position X
    private float y;             // position Y

    public Entity(int width, int height, float x, float y, Texture texture, World world) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.world = world;
    }

    /**
     * Every entity must implement its own update logic.
     */
    public abstract void update(float delta);

    /**
     * Renders the entity sprite.
     */
    public void draw(SpriteBatch batch) {
        batch.draw(this.texture, x, y, width, height);
    }

    // --- Getters ---
    public float getX() { return x; }
    public float getY() { return y; }

    public float getCenterX() { return x + width / 2f; }
    public float getCenterY() { return y + height / 2f; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // --- Setters ---
    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }

    public World getWorld(){return world;}

    public void setWorld(World world) {this.world = world;}

    // --- Player distance check ---
    public boolean isPlayerNear(Player player, int distance) {
        return distanceTo(player, distance);
    }
    public boolean isPlayerNear(Player player) {
        return distanceTo(player, 150);
    }

    public boolean distanceTo(Player player, int distance) {
        float dx = player.getCenterX() - this.getCenterX();
        float dy = player.getCenterY() - this.getCenterY();
        return Math.sqrt(dx * dx + dy * dy) < distance;
    }
    public void setTexture(Texture texture) { this.texture = texture; }
}
