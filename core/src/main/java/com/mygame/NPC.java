package com.mygame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NPC extends Entity {
    private float timer = 0f;
    private boolean isPaused = false;
    private int direction = 1;
    public boolean interacted = false;

    public NPC(int width, int height, float x, float y, Texture texture, World world){
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.world = world;
    }

    @Override
    public void update(float delta) {
        float pauseTime = 3f;
        float moveTime = 1f;
        int speed = 100;
        timer += delta;

        if (isPaused) {
            if (timer >= pauseTime) {
                timer = 0f;
                isPaused = false;
                direction *= -1;
            }
        } else {
            x += this.direction * speed * delta;
            if (timer > moveTime){
                isPaused = true;
                timer = 0;
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(this.texture, x, y, width, height);
    }

    public boolean isPlayerNear(Player player) {
        float distance = (float) Math.sqrt(Math.pow(player.x - this.x, 2) + Math.pow(player.y - this.y, 2));
        return distance < 150;
    }
}
