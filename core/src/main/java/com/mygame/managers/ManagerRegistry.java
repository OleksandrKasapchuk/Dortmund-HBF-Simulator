package com.mygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygame.Assets;
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
    private Skin skin;

    public ManagerRegistry(SpriteBatch batch, BitmapFont font, Player player) {
        this.batch = batch;
        this.player = player;

        cameraManager = new CameraManager(4000, 2000);
        pfandManager = new PfandManager();

        // --- Skin Loading ---
        // Load the skin and then manually replace the font in all styles
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // The font is now generated in Assets.java, so we just get it
        BitmapFont cyrillicFont = Assets.myFont;

        // Manually iterate through all styles and force them to use the new font.
        for (Label.LabelStyle style : skin.getAll(Label.LabelStyle.class).values()) {
            style.font = cyrillicFont;
        }
        for (TextButton.TextButtonStyle style : skin.getAll(TextButton.TextButtonStyle.class).values()) {
            style.font = cyrillicFont;
        }
        for (TextField.TextFieldStyle style : skin.getAll(TextField.TextFieldStyle.class).values()) {
            style.font = cyrillicFont;
            if (style.messageFont != null) {
                style.messageFont = cyrillicFont;
            }
        }
        for (SelectBox.SelectBoxStyle style : skin.getAll(SelectBox.SelectBoxStyle.class).values()) {
            style.font = cyrillicFont;
        }
        for (List.ListStyle style : skin.getAll(List.ListStyle.class).values()) {
            style.font = cyrillicFont;
        }
        for (Window.WindowStyle style : skin.getAll(Window.WindowStyle.class).values()) {
            style.titleFont = cyrillicFont;
        }
        // --- End of Skin and Font Loading ---

        // Pass the configured skin to the UIManager
        uiManager = new UIManager(player, skin);

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
        pfandManager.update(delta);
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
        if (skin != null) skin.dispose();
    }

    // --- Getters ---
    public UIManager getUiManager() { return uiManager; }
    public GameStateManager getGameStateManager() { return gameStateManager; }
    public NpcManager getNpcManager() { return npcManager; }
    public CameraManager getCameraManager() { return cameraManager; }
    public PlayerEffectManager getPlayerEffectManager() { return playerEffectManager; }
}
