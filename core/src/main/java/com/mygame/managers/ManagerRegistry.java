package com.mygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.item.PfandManager;
import com.mygame.entity.player.PlayerEffectManager;
import com.mygame.game.GameStateManager;
import com.mygame.scenario.ScenarioController;
import com.mygame.ui.UIManager;
import com.mygame.world.transition.TransitionManager;
import com.mygame.world.World;
import com.mygame.world.WorldManager;

public class ManagerRegistry {

    // --- Managers ---
    private UIManager uiManager;
    private NpcManager npcManager;
    private PfandManager pfandManager;
    private ItemManager itemManager;
    private TransitionManager transitionManager;
    private PlayerEffectManager playerEffectManager;
    private CameraManager cameraManager;
    private GameStateManager gameStateManager;

    // --- Core game objects ---
    private Player player;
    private Skin skin;

    public ManagerRegistry(Player player) {
        this.player = player;

        cameraManager = new CameraManager(player);
        pfandManager = new PfandManager();

        // --- Skin Loading ---
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        BitmapFont cyrillicFont = Assets.myFont;

        for (Label.LabelStyle style : skin.getAll(Label.LabelStyle.class).values()) {
            style.font = cyrillicFont;
        }
        for (TextButton.TextButtonStyle style : skin.getAll(TextButton.TextButtonStyle.class).values()) {
            style.font = cyrillicFont;
        }
        for (TextField.TextFieldStyle style : skin.getAll(TextField.TextFieldStyle.class).values()) {
            style.font = cyrillicFont;
            if (style.messageFont != null) style.messageFont = cyrillicFont;
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

        World mainWorld = new World("main", "maps/main_station.tmx");
        World backWorld = new World("leopold", "maps/leopold.tmx");
        World subwayWorld = new World("subway", "maps/subway.tmx");
        World homeWorld = new World("home", "maps/home.tmx");
        World kampWorld = new World("kamp", "maps/kamp.tmx");
        World clubWorld = new World("club", "maps/club.tmx");

        WorldManager.addWorld(mainWorld);
        WorldManager.addWorld(backWorld);
        WorldManager.addWorld(subwayWorld);
        WorldManager.addWorld(homeWorld);
        WorldManager.addWorld(kampWorld);
        WorldManager.addWorld(clubWorld);

        uiManager = new UIManager(player, skin);
        playerEffectManager = new PlayerEffectManager(player, uiManager);

        ItemRegistry.init(this);
        itemManager = new ItemManager();
        npcManager = new NpcManager(player, uiManager);
        transitionManager = new TransitionManager();

        for (World world : WorldManager.getWorlds().values()) {
            npcManager.loadNpcsFromMap(world);
            itemManager.loadItemsFromMap(world);
            transitionManager.loadTransitionsFromMap(world);
        }

        gameStateManager = new GameStateManager(uiManager);

        // --- Initialize Observers and Scenarios ---
        QuestObserver.init();
        ScenarioController.init(gameStateManager,  npcManager, uiManager, itemManager, player);
        uiManager.setCurrentStage("MENU");
        resize();
    }

    public void update(float delta) {
        npcManager.update(delta);
        cameraManager.update(delta, WorldManager.getCurrentWorld());
        itemManager.update(player);
        uiManager.update(delta, player);
        pfandManager.update(delta);
    }

    public void render() {
        // EventManager render call is removed because rendering is now handled by GameUI (Stage-based)
    }

    public void resize() {
        cameraManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (uiManager != null) uiManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void dispose() {
        if (uiManager != null) uiManager.dispose();
        if (skin != null) skin.dispose();
    }

    public UIManager getUiManager() { return uiManager; }
    public GameStateManager getGameStateManager() { return gameStateManager; }
    public NpcManager getNpcManager() { return npcManager; }
    public CameraManager getCameraManager() { return cameraManager; }
    public PlayerEffectManager getPlayerEffectManager() { return playerEffectManager; }
}
