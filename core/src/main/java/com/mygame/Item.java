package com.mygame;

import com.badlogic.gdx.graphics.Texture;

public class Item extends Entity {
    private String name;
    private boolean canBePickedUp;
    public Item(String name, int width, int height, float x, float y, Texture texture, World world, boolean canBePickedUp){
        super(width, height, x, y, texture, world);
        this.name = name;
        this.canBePickedUp = canBePickedUp;
    }

    @Override
    public void update(float delta) {}
    public boolean isPlayerNear(Player player) {return Math.sqrt(Math.pow(player.x - this.x, 2) + Math.pow(player.y - this.y, 2)) < 100;}
    public String getName(){return name;}
    public boolean canBePickedUp(){return canBePickedUp;}
}
