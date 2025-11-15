package com.mygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.entity.Player;
import com.mygame.ui.UIManager;
import com.mygame.world.World;

/**
 * Central registry to manage and update all game managers.
 * Handles creation, updates, rendering, resizing, and disposal.
 */
public class ManagerRegistry {

    // --- Managers ---
    private UIManager uiManager;
    private NpcManager npcManager;
    private PfandManager pfandManager;
    private ItemManager itemManager;
    private PlayerEffectManager playerEffectManager;
    private CameraManager cameraManager;
    private static GameStateManager gameStateManager;
    private EventManager eventManager;

    // --- Core game objects ---
    private World world;
    private Player player;
    private SpriteBatch batch;

    /**
     * Constructor initializes all managers and sets up their relationships.
     */
    public ManagerRegistry(SpriteBatch batch, BitmapFont font, Player player, World world) {
        this.batch = batch;
        this.world = world;
        this.player = player;

        // Initialize Item Manager and UI Manager
        itemManager = new ItemManager(world);
        uiManager = new UIManager(player);

        // Initialize Player Effects Manager
        playerEffectManager = new PlayerEffectManager(player, uiManager);
        playerEffectManager.registerEffects();

        // Initialize Camera Manager with world size
        cameraManager = new CameraManager(4000, 2000);

        // Initialize NPC Manager and Pfand Manager
        npcManager = new NpcManager(batch, player, world, uiManager, font);
        pfandManager = new PfandManager();

        // Initialize Game State Manager and Event Manager
        gameStateManager = new GameStateManager(uiManager);
        eventManager = new EventManager(player, npcManager, uiManager, itemManager, batch, font);

        // Set initial UI stage
        uiManager.setCurrentStage("MENU");

        // Set inventory callback to update UI when inventory changes
        player.getInventory().setOnInventoryChanged(() -> {
            if (uiManager.getInventoryUI().isVisible()) {
                uiManager.getInventoryUI().update(player);
            }
        });

        // Apply initial resize to set camera and UI correctly
        resize();
    }

    /**
     * Update all managers each frame.
     */
    public void update(float delta) {
        npcManager.update(delta);
        cameraManager.update(player, batch);
        itemManager.update(player);
        uiManager.update(delta, player, npcManager.getNpcs());
        pfandManager.update(delta, player, world);
        eventManager.update(delta);
        uiManager.resetButtons(); // Reset button states after input handling
    }

    /**
     * Render all game objects and UI hints.
     * Order is important: world items → Pfand → NPC → event hints.
     */
    public void render() {
        // Draw world objects and items
        itemManager.draw(batch);
        pfandManager.draw(batch);

        // Draw NPCs and event hints on top
        npcManager.render();
        eventManager.render();
    }

    // --- Getters for managers ---
    public UIManager getUiManager() { return uiManager; }
    public GameStateManager getGameStateManager() { return gameStateManager; }
    public ItemManager getItemManager() { return itemManager; }
    public NpcManager getNpcManager() { return npcManager; }
    public CameraManager getCameraManager() { return cameraManager; }

    /**
     * Resize camera and UI according to screen dimensions.
     */
    public void resize() {
        cameraManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (uiManager != null) uiManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Dispose UI resources when shutting down the game.
     */
    public void dispose() {
        uiManager.dispose();
    }
}
