package com.mygame.entity;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.entity.player.Player;
import com.mygame.world.World;

/**
 * Base abstract class for all objects rendered in the world:
 * players, NPCs, items, etc.
 */
public abstract class Entity implements Renderable {

    // --- Base entity properties ---
    private int width;
    private int height;

    protected Texture texture;   // sprite of the entity
    protected static Texture shadowTexture;   // shadow texture, now static and shared
    protected World world;       // reference to the world (for collisions etc.)

    private float x;             // position X
    private float y;             // position Y

    protected Rectangle bounds;
    protected boolean hasShadow = true;

    public Entity(int width, int height, float x, float y, Texture texture, World world) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.world = world;
        this.bounds = new Rectangle(x, y, width, height);

        if (shadowTexture == null) {
            shadowTexture = createShadowTexture();
        }
    }

    private static Texture createShadowTexture() {
        Pixmap pixmap = new Pixmap(64, 32, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.4f); // Semi-transparent black
        pixmap.fillCircle(32, 16, 15);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }


    /**
     * Every entity must implement its own update logic.
     */
    public abstract void update(float delta);

    /**
     * Renders the entity sprite.
     */
    public void draw(SpriteBatch batch) {
        // Draw shadow first, then the entity
        if (hasShadow && shadowTexture != null) {
            // Make the shadow wider than the entity for a better visual effect.
            float shadowWidth = width * 2f;
            float shadowX = x - (shadowWidth - width) / 2f;
            // By setting the shadow's height as a ratio of its width, we ensure it's a nice ellipse.
            batch.draw(shadowTexture, shadowX, y - 8, shadowWidth, shadowWidth / 3f);
        }
        batch.draw(this.texture, x, y, width, height);
    }

    // --- Getters ---
    public float getX() { return x; }
    public float getY() { return y; }

    public float getCenterX() { return x + width / 2f; }
    public float getCenterY() { return y + height / 2f; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
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
    public Texture getTexture() { return texture; }

    public Rectangle getBounds() {
        bounds.set(x, y, width, height);
        return bounds;
    }
}
