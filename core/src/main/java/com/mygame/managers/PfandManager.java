package com.mygame.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.Assets;
import com.mygame.Main;
import com.mygame.entity.Item;
import com.mygame.world.World;
import com.mygame.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class PfandManager {
    private final ArrayList<Item> pfands = new ArrayList<>();
    private final Random random = new Random();
    private float spawnTimer = 0f;

    private static final float SPAWN_INTERVAL = 7f;
    private static final int MAX_PFANDS = 15;

    public void update(float delta, Player player, World world) {
        spawnTimer += delta;

        if (spawnTimer >= SPAWN_INTERVAL && pfands.size() < MAX_PFANDS) {
            spawnRandomPfand(world);
            spawnTimer = 0f;
        }

        for (Iterator<Item> it = pfands.iterator(); it.hasNext();) {
            Item p = it.next();
            if (p.isPlayerNear(player)) {
                player.getInventory().addItem(p.getName(), 1);
                it.remove();
            }
        }
    }

    private void spawnRandomPfand(World world) {
        OrthographicCamera cam = Main.getManagerRegistry().getCameraManager().getCamera();
        int attempts = 0;
        int itemWidth = 60;
        int itemHeight = 60;

        while (attempts < 80) {
            attempts++;

            float x = random.nextFloat() * (4000 - itemWidth);
            float y = random.nextFloat() * (2000 - itemHeight);

            if (isInCameraView(x, y, cam)) continue;

            if (isCollidingWithAnyBlock(world, x, y, itemWidth, itemHeight)) continue;

            if (isTooCloseToOtherPfands(x, y)) continue;

            pfands.add(new Item("pfand", itemWidth, itemHeight, x, y,75, Assets.pfand, world, true, false));
            break;
        }
    }

    private boolean isCollidingWithAnyBlock(World world, float x, float y, float width, float height) {
        int startX = (int)(x / world.tileSize);
        int endX = (int)((x + width) / world.tileSize);
        int startY = (int)((y) / world.tileSize);
        int endY = (int)((y + height) / world.tileSize);

        startY = world.getBlocks().length - 1 - startY;
        endY = world.getBlocks().length - 1 - endY;

        if (startY > endY) {
            int tmp = startY;
            startY = endY;
            endY = tmp;
        }

        for (int ty = startY; ty <= endY; ty++) {
            for (int tx = startX; tx <= endX; tx++) {
                if (ty < 0 || ty >= world.getBlocks().length || tx < 0 || tx >= world.getBlocks()[0].length)
                    return true; // поза картою
                if (world.getBlocks()[ty][tx] != null) return true;
            }
        }
        return false;
    }

    private boolean isTooCloseToOtherPfands(float x, float y) {
        for (Item p : pfands) {
            float dx = p.getX() - x;
            float dy = p.getY() - y;
            if (Math.sqrt(dx * dx + dy * dy) < 150f)
                return true;
        }
        return false;
    }

    private boolean isInCameraView(float x, float y, OrthographicCamera cam) {
        float camLeft = cam.position.x - cam.viewportWidth / 2f;
        float camRight = cam.position.x + cam.viewportWidth / 2f;
        float camBottom = cam.position.y - cam.viewportHeight / 2f;
        float camTop = cam.position.y + cam.viewportHeight / 2f;

        return x >= camLeft && x <= camRight && y >= camBottom && y <= camTop;
    }

    public void draw(SpriteBatch batch) {
        for (Item p : pfands)
            p.draw(batch);
    }
}
