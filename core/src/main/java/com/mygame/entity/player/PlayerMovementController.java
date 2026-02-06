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
    public void update(Player player, float delta) {

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

        if (player.getWorld().isCollidingWithMap(rect) || npcX != null) {
            player.setX(oldX); // ⬅ ЖОРСТКИЙ ROLLBACK
        } else if (itemX != null) {
            float playerCenterX = oldX + player.getWidth() / 2f;
            float itemCenterX = itemX.getBounds().x + itemX.getBounds().width / 2f;
            float newPlayerX;
            if (playerCenterX < itemCenterX) {
                newPlayerX = itemX.getBounds().x - player.getWidth();
            } else {
                newPlayerX = itemX.getBounds().x + itemX.getBounds().width;
            }

            Rectangle tempRect = new Rectangle(player.getBounds());
            tempRect.x = newPlayerX;

            if (player.getWorld().isCollidingWithMap(tempRect)) {
                player.setX(oldX);
            } else {
                player.setX(newPlayerX);
            }
        } else {
            player.setX(oldX + dx);
        }

        rect.x = player.getX();


        // ===== Y =====
        float oldY = player.getY();
        rect.y = oldY + dy;

        Item itemY = player.getCollidingSolidItem(rect);
        NPC npcY = player.getCollidingNpc(rect);

        if (player.getWorld().isCollidingWithMap(rect) || npcY != null) {
            player.setY(oldY); // ⬅ ROLLBACK
        } else if (itemY != null) {
            float playerCenterY = oldY + player.getHeight() / 2f;
            float itemCenterY = itemY.getBounds().y + itemY.getBounds().height / 2f;
            float newPlayerY;
            if (playerCenterY < itemCenterY) {
                newPlayerY = itemY.getBounds().y - player.getHeight();
            } else {
                newPlayerY = itemY.getBounds().y + itemY.getBounds().height;
            }

            Rectangle tempRect = new Rectangle(player.getBounds());
            tempRect.x = player.getX(); // Use resolved X
            tempRect.y = newPlayerY;

            if (player.getWorld().isCollidingWithMap(tempRect)) {
                player.setY(oldY);
            } else {
                player.setY(newPlayerY);
            }
        } else {
            player.setY(oldY + dy);
        }
    }

    public void setSpeedMultiplier(float multiplier) {
        this.speedMultiplier = multiplier;
    }

    private void clampToWorld(Player player) {
        World world = player.getWorld();
        if (world == null) return;

        player.setX(MathUtils.clamp(player.getX(), 0, world.mapWidth - player.getWidth()));
        player.setY(MathUtils.clamp(player.getY(), 0, world.mapHeight - player.getHeight()));
    }

}
