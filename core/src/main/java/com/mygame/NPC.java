package com.mygame;

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

                // Перевірка колізії з світом
                boolean collide =
                    world.isSolid(newX, newY) ||                       // лівий верхній
                    world.isSolid(newX + width, newY) ||            // правий верхній
                    world.isSolid(newX, newY - height) ||           // лівий нижній
                    world.isSolid(newX + width, newY - height);  // правий нижній

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
    public void setTexts(String[] texts) {
        this.texts = texts;
    }
    public void nextDialogueCount(){this.dialogueCount++;}
    public int getDialogueCount(){return this.dialogueCount;}
    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
