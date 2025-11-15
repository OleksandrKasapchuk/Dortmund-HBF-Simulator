package com.mygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.entity.Player;
import com.mygame.managers.nonglobal.*;
import com.mygame.ui.UIManager;
import com.mygame.world.World;

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
    private World world;
    private Player player;
    private SpriteBatch batch;

    public ManagerRegistry(SpriteBatch batch, BitmapFont font, Player player, World world) {
        this.batch = batch;
        this.world = world;
        this.player = player;

        // 1️⃣ Базові менеджери без залежностей
        cameraManager = new CameraManager(4000, 2000);
        pfandManager = new PfandManager();

        // 2️⃣ ItemManager і прив'язка до Player
        itemManager = new ItemManager(world);
        player.setItemManager(itemManager);

        // 3️⃣ UIManager (тепер Player має ItemManager)
        uiManager = new UIManager(player);

        // 4️⃣ PlayerEffectManager
        playerEffectManager = new PlayerEffectManager(player, uiManager);
        playerEffectManager.registerEffects();

        // 5️⃣ NPC Manager (залежить від UIManager, Player, World)
        npcManager = new NpcManager(batch, player, world, uiManager, font);

        // 6️⃣ GameStateManager
        gameStateManager = new GameStateManager(uiManager);

        // 7️⃣ EventManager (залежить від Player, NPC, UI, ItemManager)
        eventManager = new EventManager(player, npcManager, uiManager, itemManager, batch, font, gameStateManager);

        // 8️⃣ Inventory callback (теперь UIManager точно існує)
        if (player.getInventory() != null) {
            player.getInventory().setOnInventoryChanged(() -> {
                if (uiManager.getInventoryUI() != null && uiManager.getInventoryUI().isVisible()) {
                    uiManager.getInventoryUI().update(player);
                }
            });
        }

        // 9️⃣ Налаштування UI stage
        uiManager.setCurrentStage("MENU");

        // 10️⃣ Apply initial resize
        resize();
    }

    public void update(float delta) {
        npcManager.update(delta);
        cameraManager.update(player, batch);
        itemManager.update(player);
        uiManager.update(delta, player, npcManager.getNpcs());
        pfandManager.update(delta, player, world);
        eventManager.update(delta);
        uiManager.resetButtons();
    }

    public void render() {
        itemManager.draw(batch);
        pfandManager.draw(batch);
        npcManager.render();
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
    public ItemManager getItemManager() { return itemManager; }
    public NpcManager getNpcManager() { return npcManager; }
    public CameraManager getCameraManager() { return cameraManager; }
}
