package com.mygame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class NPC extends Entity {
    private float timer = 0f;
    private boolean isPaused = false;
    private int directionX;
    private int directionY;
    public boolean interacted = false;
    private final String[] texts;
    private int count = 0;


    public NPC(int width, int height, float x, float y, Texture texture, World world, int directionX, int directionY, String[] texts){
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.texture = texture;
        this.world = world;
        this.texts = texts;
        this.directionX = directionX;
        this.directionY = directionY;
    }

    @Override
    public void update(float delta) {
        float pauseTime = 3f;
        float moveTime = 2f;
        int speed = 100;
        timer += delta;

        if (isPaused) {
            if (timer >= pauseTime) {
                timer = 0f;
                isPaused = false;
                directionX *= -1;
                directionY *= -1;
            }
        } else {

            float newX = x + directionX * speed * delta;
            float newY = y + directionY * speed * delta;

            // Перевірка колізії з світом
            boolean collide =
                world.isSolid(newX, newY) ||               // лівий верхній
                    world.isSolid(newX + width, newY) ||       // правий верхній
                    world.isSolid(newX, newY - height) ||      // лівий нижній
                    world.isSolid(newX + width, newY - height); // правий нижній

            boolean outOfBounds = newX < 0 || newX + width > Main.getWorldWidth()
                || newY < 0 || newY + height > Main.getWorldHeight();

            if (!collide && !outOfBounds) {
                x = newX;
                y = newY;
            } else {
                isPaused = true;
                timer = 0f;
            }

            if (timer > moveTime){
                isPaused = true;
                timer = 0f;
            }
        }
    }

    @Override
    public void draw(SpriteBatch batch) {batch.draw(this.texture, x, y, width, height);}

    public boolean isPlayerNear(Player player) {
        float distance = (float) Math.sqrt(Math.pow(player.x - this.x, 2) + Math.pow(player.y - this.y, 2));
        return distance < 150;
    }

    public String getCurrentPhrase() {
        if (texts == null || texts.length == 0) {
            return "";
        }
        return texts[count];
    }

    public void advanceDialogue() {
        if (count < texts.length - 1)
            count++;
    }

    public boolean isDialogueFinished() {
        return count >= texts.length - 1;
    }

    public void resetDialogue() {
        this.count = 0;
    }
}
