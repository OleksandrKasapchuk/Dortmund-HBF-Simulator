package com.mygame.entity.item;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygame.assets.Assets;
import com.mygame.Main;
import com.mygame.world.WorldManager;
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

    private final float SPAWN_INTERVAL = 7f; // Time between spawn attempts in seconds
    private final int MAX_PFANDS = 15;       // Maximum number of pfand items at a time
    private ItemRegistry itemRegistry;
    private WorldManager worldManager;

    /**
     * Updates pfand items each frame.
     * - Handles spawning new items.
     * - Checks for player pickups.
     */
    public PfandManager(ItemRegistry itemRegistry, WorldManager worldManager){
        this.itemRegistry = itemRegistry;
        this.worldManager = worldManager;
    }

    public void update(float delta) {
        spawnTimer += delta;

        // Spawn a new pfand if interval passed and under max limit
        if (spawnTimer >= SPAWN_INTERVAL && worldManager.getCurrentWorld().getPfands().size() < MAX_PFANDS) {
            spawnRandomPfand(worldManager.getCurrentWorld());
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

            // Use world dimensions from the TMX map
            float x = random.nextFloat() * (world.mapWidth - itemWidth);
            float y = random.nextFloat() * (world.mapHeight - itemHeight);

            // Skip if inside camera view
            if (isInCameraView(x, y, cam)) continue;

            // Skip if colliding with world blocks
            if (isCollidingWithAnyBlock(world, x, y, itemWidth, itemHeight)) continue;

            // Skip if too close to other pfands
            if (isTooCloseToOtherPfands(x, y)) continue;

            // Add new pfand to the world
            Item pfand = new Item(itemRegistry.get("pfand"), itemWidth, itemHeight, x, y, 75, Assets.getTexture("pfand"), worldManager.getCurrentWorld(), true, false, false, null, null, 0, null);
            worldManager.getCurrentWorld().getItems().add(pfand);
            worldManager.getCurrentWorld().getPfands().add(pfand);
            break;
        }
    }

    /**
     * Checks if an item's bounding box collides with any solid tile in the world.
     * It checks the four corners of the bounding box.
     */
    private boolean isCollidingWithAnyBlock(World world, float x, float y, float width, float height) {
        // Check the four corners of the item's bounding box against the world's collision layer
        if (world.isSolid(x, y)) return true;                      // Bottom-left
        if (world.isSolid(x + width, y)) return true;             // Bottom-right
        if (world.isSolid(x, y + height)) return true;             // Top-left
        return world.isSolid(x + width, y + height); // Top-right
    }

    /** Checks if a position is too close to existing pfand items */
    private boolean isTooCloseToOtherPfands(float x, float y) {
        for (Item p : worldManager.getCurrentWorld().getPfands()) {
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
