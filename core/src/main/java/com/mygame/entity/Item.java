package com.mygame.entity;

import com.badlogic.gdx.graphics.Texture;
import com.mygame.world.World;

public class Item extends Entity {
    private String name;
    private boolean canBePickedUp;
    private boolean solid;
    private int distance;
    private float cooldownTimer = 0f;

    public Item(String name, int width, int height, float x, float y, int distance, Texture texture, World world, boolean canBePickedUp, boolean solid){
        super(width, height, x, y, texture, world);
        this.name = name;
        this.canBePickedUp = canBePickedUp;
        this.solid = solid;
        this.distance = distance;
    }

    @Override
    public void update(float delta) {}
    public boolean isPlayerNear(Player player) {return Math.sqrt(Math.pow(player.getX() - this.getX(), 2) + Math.pow(player.getY() - this.getY(), 2)) < this.distance;}
    public String getName(){return name;}
    public boolean canBePickedUp(){return canBePickedUp;}
    public boolean isSolid(){return solid;}

    public void updateCooldown(float delta) {
        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
            if (cooldownTimer < 0) cooldownTimer = 0;
        }
    }
    public boolean canInteract() {
        return cooldownTimer <= 0;
    }
    public void startCooldown(float seconds) {
        cooldownTimer = seconds;
    }
}
