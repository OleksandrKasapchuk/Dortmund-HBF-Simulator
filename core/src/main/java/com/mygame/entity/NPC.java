package com.mygame.entity;

import com.badlogic.gdx.graphics.Texture;
import com.mygame.Dialogue;
import com.mygame.world.World;

public class NPC extends Entity {
    private float timer = 0f;
    private boolean isPaused = false;
    private int directionX;
    private int directionY;
    private Dialogue dialogue;

    private String name;
    private float pauseTime;
    private float moveTime;
    private int speed;
    private int distance;


    public NPC(String name, int width, int height, float x, float y, Texture texture, World world, int directionX, int directionY, float pauseTime, float moveTime, int speed, int distance, Dialogue dialogue){
        super(width, height, x, y, texture, world);
        this.name = name;
        this.dialogue = dialogue;
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
                float newX = this.getX() + directionX * speed * delta;
                float newY = this.getY() + directionY * speed * delta;

                boolean collideX =
                    world.isSolid(newX, this.getY() - this.getHeight() - 20) ||
                    world.isSolid(newX + this.getWidth(), this.getY() - this.getHeight() - 20) ||
                    world.isSolid(newX, this.getY()  - 20) ||
                    world.isSolid(newX + this.getWidth(), this.getY()  - 20);

                if (!collideX) {this.setX(newX);}

                boolean collideY =
                    world.isSolid(this.getX(), newY - this.getHeight() - 20) ||
                    world.isSolid(this.getX() + this.getWidth(), newY - this.getHeight() - 20) ||
                    world.isSolid(this.getX(), newY - 20) ||
                    world.isSolid(this.getX() + this.getWidth(), newY - 20);

                if (!collideY) {this.setY(newY);}

                if (timer > moveTime){
                    isPaused = true;
                    timer = 0f;
                }
            }
        }
    }

    public Dialogue getDialogue() {return dialogue;}
    public void setDialogue(Dialogue dialogue) {this.dialogue = dialogue;}

    public boolean isPlayerNear(Player player) {return Math.sqrt(Math.pow(player.getX() - this.getX(), 2) + Math.pow(player.getY() - this.getY(), 2)) < this.distance;}
    public boolean isPlayerNear(Player player, int distance) {return Math.sqrt(Math.pow(player.getX() - this.getX(), 2) + Math.pow(player.getY() - this.getY(), 2)) < distance;}
    public String getName(){return this.name;}

    public void setTexture(Texture texture) {this.texture = texture;}
    public int getSpeed(){return speed;}
}
