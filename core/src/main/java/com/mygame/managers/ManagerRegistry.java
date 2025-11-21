package com.mygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.entity.Player;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.managers.nonglobal.*;
import com.mygame.ui.UIManager;

public class ManagerRegistry {

    // --- Managers ---
    private UIManager uiManager;
    private NpcManager npcManager;
    private PfandManager pfandManager;
    private ItemManager itemManager;
    private PlayerEffectManager playerEffectManager;
    private CameraManager cameraManager;
    private GameStateManager gameStateManager;
    private EventManager eventManager;

    // --- Core game objects ---
    private Player player;
    private SpriteBatch batch;

    public ManagerRegistry(SpriteBatch batch, BitmapFont font, Player player) {
        this.batch = batch;
        this.player = player;


        cameraManager = new CameraManager(4000, 2000);
        pfandManager = new PfandManager();


        uiManager = new UIManager(player);

        playerEffectManager = new PlayerEffectManager(player, uiManager);

        ItemRegistry.init(this);

        itemManager = new ItemManager();

        npcManager = new NpcManager(player, uiManager);

        gameStateManager = new GameStateManager(uiManager);

        eventManager = new EventManager(player, npcManager, uiManager, itemManager, batch, font, gameStateManager);


        if (player.getInventory() != null) {
            player.getInventory().setOnInventoryChanged(() -> {
                if (uiManager.getInventoryUI() != null && uiManager.getInventoryUI().isVisible()) {
                    uiManager.getInventoryUI().update(player);
                }
            });
        }

        uiManager.setCurrentStage("MENU");
        resize();
    }

    public void update(float delta) {
        npcManager.update(delta);
        cameraManager.update(player, batch);
        itemManager.update(player);
        uiManager.update(delta, player);
        pfandManager.update(delta, player);
        eventManager.update(delta);
        uiManager.resetButtons();
    }

    public void render() {
        eventManager.render();
    }

    public void resize() {
        cameraManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (uiManager != null) uiManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void dispose() {
        if (uiManager != null) uiManager.dispose();
    }

    // --- Getters ---
    public UIManager getUiManager() { return uiManager; }
    public GameStateManager getGameStateManager() { return gameStateManager; }
    public NpcManager getNpcManager() { return npcManager; }
    public CameraManager getCameraManager() { return cameraManager; }
    public PlayerEffectManager getPlayerEffectManager() { return playerEffectManager; }
}
