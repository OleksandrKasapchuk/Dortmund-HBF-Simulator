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
    private boolean isFollowing = false;


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
    public boolean followPlayer(Player player) {
        if (isFollowing) {
            float delta = Gdx.graphics.getDeltaTime();

            float targetX = player.getX();
            float targetY = player.getY();

            if (this.getX() > targetX) this.setX(this.getX() - speed * delta);
            else if (this.getX() < targetX) this.setX(this.getX() + speed * delta);

            if (this.getY() > targetY) this.setY(this.getY() - speed * delta);
            else if (this.getY() < targetY) this.setY(this.getY() + speed * delta);

        }
        return Math.sqrt(Math.pow(player.getX() - this.getX(), 2) + Math.pow(player.getY() - this.getY(), 2)) < 1400;
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
                float newX = this.getX() + directionX * speed * delta;
                float newY = this.getY() + directionY * speed * delta;

                boolean collideX =
                    world.isSolid(newX, this.getY() - this.height - 20) ||
                    world.isSolid(newX + this.width, this.getY() - this.height - 20) ||
                    world.isSolid(newX, this.getY()  - 20) ||
                    world.isSolid(newX + this.width, this.getY()  - 20);

                if (!collideX) {this.setX(newX);}

                boolean collideY =
                    world.isSolid(this.getX(), newY - this.height - 20) ||
                    world.isSolid(this.getX() + this.width, newY - this.height - 20) ||
                    world.isSolid(this.getX(), newY - 20) ||
                    world.isSolid(this.getX() + this.width, newY - 20);

                if (!collideY) {this.setY(newY);}

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

    public boolean isPlayerNear(Player player) {return Math.sqrt(Math.pow(player.getX() - this.getX(), 2) + Math.pow(player.getY() - this.getY(), 2)) < this.distance;}
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
    public void setFollowing(boolean isFollowing) {this.isFollowing = isFollowing;}
    public boolean isFollowing() {return this.isFollowing;}
}
