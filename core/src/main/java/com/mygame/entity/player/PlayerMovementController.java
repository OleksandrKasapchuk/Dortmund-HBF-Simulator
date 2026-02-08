package com.mygame.entity.player;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.entity.item.Item;
import com.mygame.entity.npc.NPC;
import com.mygame.world.World;

public class PlayerMovementController {

    private float speedMultiplier = 1;
    private int baseSpeed = 500;
    private boolean isMoving;

    public void update(Player player, float delta) {
        isMoving = false;
        int speed = player.getState() == Player.State.STONED ? 150 : baseSpeed;

        float moveSpeed = speed * delta;
        float dx = 0, dy = 0;

        // === INPUT ===
        if (Gdx.app.getType() != Application.ApplicationType.Android) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) moveSpeed *= 5;
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A))
                dx -= moveSpeed;
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
                dx += moveSpeed;
            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W))
                dy += moveSpeed;
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S))
                dy -= moveSpeed;
        } else if (player.touchpad != null) {
            dx = player.touchpad.getKnobPercentX() * speed * delta;
            dy = player.touchpad.getKnobPercentY() * speed * delta;
        }
        dx *= speedMultiplier;
        dy *= speedMultiplier;
        if (dy < 0) isMoving = true;
        // === MOVEMENT ===
        moveWithCollision(player, dx, dy);
        clampToWorld(player);
    }

    private void moveWithCollision(Player player, float dx, float dy) {

        Rectangle rect = player.getBounds();

        // ===== X =====
        float oldX = player.getX();
        rect.x = oldX + dx;

        Item itemX = player.getCollidingSolidItem(rect);
        NPC npcX = player.getCollidingNpc(rect);

        if (player.getWorld().isCollidingWithMap(rect) || npcX != null || itemX != null) {
            player.setX(oldX);
        } else {
            player.setX(oldX + dx);
        }

        rect.x = player.getX();


        // ===== Y =====
        float oldY = player.getY();
        rect.y = oldY + dy;

        Item itemY = player.getCollidingSolidItem(rect);
        NPC npcY = player.getCollidingNpc(rect);

        if (player.getWorld().isCollidingWithMap(rect) || npcY != null || itemY != null) {
            player.setY(oldY);
        } else {
            player.setY(oldY + dy);
        }
    }

    public void setSpeedMultiplier(float multiplier) {this.speedMultiplier = multiplier;}
    public boolean getMoving() {return isMoving;}

    private void clampToWorld(Player player) {
        World world = player.getWorld();
        if (world == null) return;

        player.setX(MathUtils.clamp(player.getX(), 0, world.mapWidth - player.getWidth()));
        player.setY(MathUtils.clamp(player.getY(), 0, world.mapHeight - player.getHeight()));
    }
}
