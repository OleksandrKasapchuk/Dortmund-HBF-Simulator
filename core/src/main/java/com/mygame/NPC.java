package com.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class NPC extends Entity {
    private float timer = 0f;
    private boolean isPaused = false;
    private int directionX;
    private int directionY;
    private  String[] texts;
    private int count = 0;
    private String name;
    private float pauseTime;
    private float moveTime;
    private int speed;
    private Runnable action = null;
    private int dialogueCount = 1;
    private int distance;


    public NPC(String name, int width, int height, float x, float y, Texture texture, World world, int directionX, int directionY, float pauseTime, float moveTime, int speed,int distance, String[] texts){
        super(width, height, x, y, texture, world);
        this.name = name;
        this.texts = texts;
        this.directionX = directionX;
        this.directionY = directionY;
        this.pauseTime = pauseTime;
        this.moveTime = moveTime;
        this.speed = speed;
        this.distance = distance;
    }
    public boolean followPlayer(Player player, float offsetX, float offsetY) {
        float delta = Gdx.graphics.getDeltaTime();

        float targetX = player.x + offsetX;
        float targetY = player.y + offsetY;

        if (x > targetX) x -= speed * delta;
        else if (x < targetX) x += speed * delta;

        if (y > targetY) y -= speed * delta;
        else if (y < targetY) y += speed * delta;

        return Math.sqrt(Math.pow(player.x - this.x, 2) + Math.pow(player.y - this.y, 2)) < 1400;
    }
//    public boolean collidesWith(float px, float py, float pWidth, float pHeight) {
//        return px < x + width &&
//            px + pWidth > x &&
//            py < y + height &&
//            py + pHeight > y;
//    }
    @Override
    public void update(float delta) {
        if (moveTime != 0 && speed != 0) {
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

                boolean collideX =
                    world.isSolid(newX, y - this.height - 20) ||
                    world.isSolid(newX + this.width, y - this.height - 20) ||
                    world.isSolid(newX, y - 20) ||
                    world.isSolid(newX + this.width, y - 20);

                if (!collideX) {
                    x = newX;
                }
                boolean collideY =
                    world.isSolid(x, newY - this.height - 20) ||
                    world.isSolid(x + this.width, newY - this.height - 20) ||
                    world.isSolid(x, newY - 20) ||
                    world.isSolid(x + this.width, newY - 20);

                if (!collideY) {
                    y = newY;
                }
                if (timer > moveTime){
                    isPaused = true;
                    timer = 0f;
                }
            }
        }
    }

    public String getCurrentPhrase() {
        if (texts == null || texts.length == 0) {
            return "";
        }
        return texts[count];
    }

    public boolean isPlayerNear(Player player) {return Math.sqrt(Math.pow(player.x - this.x, 2) + Math.pow(player.y - this.y, 2)) < this.distance;}
    public void advanceDialogue(){count++;}
    public boolean isDialogueFinished(){return count >= texts.length;}
    public void resetDialogue(){this.count = 0;}
    public String getName(){return this.name;}

    public void setAction(Runnable action){this.action = action;}
    public void runAction(){if (action != null) action.run();}

    public void setTexts(String[] texts) {this.texts = texts;}

    public void nextDialogueCount(){this.dialogueCount++;}
    public int getDialogueCount(){return this.dialogueCount;}

    public void setTexture(Texture texture) {this.texture = texture;}
}
