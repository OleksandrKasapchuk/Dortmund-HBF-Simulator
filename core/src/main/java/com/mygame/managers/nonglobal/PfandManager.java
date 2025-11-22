package com.mygame.managers.nonglobal;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygame.Assets;
import com.mygame.Main;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.managers.global.WorldManager;
import com.mygame.world.World;
import java.util.Random;

/**
 * PfandManager is responsible for spawning, updating, and rendering collectible "pfand" items in the world.
 * - Spawns items randomly outside of the camera view.
 * - Prevents spawning inside blocks or too close to other items.
 * - Allows the player to pick up items when nearby.
 */
public class PfandManager {
    private final Random random = new Random();
    private float spawnTimer = 0f; // Timer to track spawn intervals

    private static final float SPAWN_INTERVAL = 7f; // Time between spawn attempts in seconds
    private static final int MAX_PFANDS = 15;       // Maximum number of pfand items at a time

    /**
     * Updates pfand items each frame.
     * - Handles spawning new items.
     * - Checks for player pickups.
     */
    public void update(float delta) {
        spawnTimer += delta;

        // Spawn a new pfand if interval passed and under max limit
        if (spawnTimer >= SPAWN_INTERVAL && WorldManager.getCurrentWorld().getPfands().size() < MAX_PFANDS) {
            spawnRandomPfand(WorldManager.getCurrentWorld());
            spawnTimer = 0f;
        }
    }

    /**
     * Spawns a new pfand item at a random valid location.
     * Ensures it is outside camera view, not colliding with blocks, and not too close to other pfands.
     */
    private void spawnRandomPfand(World world) {
        OrthographicCamera cam = Main.getGameInitializer().getManagerRegistry().getCameraManager().getCamera();
        int attempts = 0;
        int itemWidth = 60;
        int itemHeight = 60;

        while (attempts < 80) {
            attempts++;

            float x = random.nextFloat() * (4000 - itemWidth); // World width assumed 4000
            float y = random.nextFloat() * (2000 - itemHeight); // World height assumed 2000

            // Skip if inside camera view
            if (isInCameraView(x, y, cam)) continue;

            // Skip if colliding with world blocks
            if (isCollidingWithAnyBlock(world, x, y, itemWidth, itemHeight)) continue;

            // Skip if too close to other pfands
            if (isTooCloseToOtherPfands(x, y)) continue;

            // Add new pfand to the world
            Item pfand = new Item(ItemRegistry.get("pfand"), itemWidth, itemHeight, x, y, 75, Assets.pfand, WorldManager.getCurrentWorld(), true, false);
            WorldManager.getCurrentWorld().getItems().add(pfand);
            WorldManager.getCurrentWorld().getPfands().add(pfand);
            break;
        }
    }

    /** Checks if a rectangle collides with any block in the world */
    private boolean isCollidingWithAnyBlock(World world, float x, float y, float width, float height) {
        int startX = (int) (x / world.tileSize);
        int endX = (int) ((x + width) / world.tileSize);
        int startY = (int) (y / world.tileSize);
        int endY = (int) ((y + height) / world.tileSize);

        // Invert Y axis because world blocks array may start from top
        startY = world.getBlocks().length - 1 - startY;
        endY = world.getBlocks().length - 1 - endY;

        if (startY > endY) { // Swap if needed
            int tmp = startY;
            startY = endY;
            endY = tmp;
        }

        for (int ty = startY; ty <= endY; ty++) {
            for (int tx = startX; tx <= endX; tx++) {
                if (ty < 0 || ty >= world.getBlocks().length || tx < 0 || tx >= world.getBlocks()[0].length)
                    return true; // Out of world bounds
                if (world.getBlocks()[ty][tx] != null) return true; // Block exists
            }
        }
        return false;
    }

    /** Checks if a position is too close to existing pfand items */
    private boolean isTooCloseToOtherPfands(float x, float y) {
        for (Item p : WorldManager.getCurrentWorld().getPfands()) {
            float dx = p.getX() - x;
            float dy = p.getY() - y;
            if (Math.sqrt(dx * dx + dy * dy) < 150f) // Minimum distance between pfands
                return true;
        }
        return false;
    }

    /** Checks if a position is inside the camera viewport */
    private boolean isInCameraView(float x, float y, OrthographicCamera cam) {
        float camLeft = cam.position.x - cam.viewportWidth / 2f;
        float camRight = cam.position.x + cam.viewportWidth / 2f;
        float camBottom = cam.position.y - cam.viewportHeight / 2f;
        float camTop = cam.position.y + cam.viewportHeight / 2f;

        return x >= camLeft && x <= camRight && y >= camBottom && y <= camTop;
    }
}
