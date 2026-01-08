package com.mygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.action.ActionRegistry;
import com.mygame.dialogue.DialogueRegistry;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.player.Player;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.item.PfandManager;
import com.mygame.entity.player.PlayerEffectManager;
import com.mygame.game.GameContext;
import com.mygame.game.GameInputHandler;
import com.mygame.game.GameStateManager;
import com.mygame.game.save.AutoSaveManager;
import com.mygame.quest.QuestManager;
import com.mygame.quest.QuestProgressTriggers;
import com.mygame.quest.QuestRegistry;
import com.mygame.scenario.ScenarioController;
import com.mygame.ui.UIManager;
import com.mygame.world.DarkOverlay;
import com.mygame.world.transition.TransitionManager;
import com.mygame.world.WorldManager;

public class ManagerRegistry {

    private final UIManager uiManager;
    private final NpcManager npcManager;
    private final PfandManager pfandManager;
    private final ItemManager itemManager;
    private final TransitionManager transitionManager;
    private final CameraManager cameraManager;
    private final GameStateManager gameStateManager;
    private final PlayerEffectManager playerEffectManager;
    private final QuestProgressTriggers questProgressTriggers;
    private final ItemRegistry itemRegistry;
    private final QuestRegistry questRegistry;
    private final QuestManager questManager;
    private final DialogueRegistry dialogueRegistry;
    private final ActionRegistry actionRegistry;
    private final ScenarioController scController;
    private final Player player;
    private final GameContext ctx;
    private final GameInputHandler gameInputHandler;
    private final AutoSaveManager autoSaveManager;
    private final WorldManager worldManager;
    private DarkOverlay darkOverlay;

    public ManagerRegistry(SpriteBatch batch, Player player, Skin skin) {
        this.player = player;

        // 1. Core Managers & Registries
        darkOverlay = new DarkOverlay();
        worldManager = new WorldManager(player, darkOverlay);

        itemRegistry = new ItemRegistry();
        questRegistry = new QuestRegistry();
        dialogueRegistry = new DialogueRegistry();
        actionRegistry = new ActionRegistry();

        // 2. Dependent Managers
        questManager = new QuestManager(questRegistry);
        questProgressTriggers = new QuestProgressTriggers(questManager, itemRegistry);
        cameraManager = new CameraManager(player);
        pfandManager = new PfandManager(itemRegistry, worldManager); // Injected worldManager
        uiManager = new UIManager(batch, player, skin, questManager, worldManager); // Injected worldManager
        playerEffectManager = new PlayerEffectManager();
        itemManager = new ItemManager(itemRegistry, worldManager); // Injected worldManager
        npcManager = new NpcManager(player, dialogueRegistry, worldManager); // Injected worldManager
        transitionManager = new TransitionManager();
        gameStateManager = new GameStateManager(uiManager);

        // 3. Game Context
        ctx = createContext();

        // 4. Initializations that require context
        actionRegistry.init(ctx);
        dialogueRegistry.init(actionRegistry);
        autoSaveManager = new AutoSaveManager(ctx);
        scController = new ScenarioController(ctx);
        gameInputHandler = new GameInputHandler(ctx.gsm, ctx.ui);
    }

    public void update(float delta) {
        worldManager.update(delta);
        darkOverlay.update(delta);
        npcManager.update(delta);
        itemManager.update(delta, player);
        pfandManager.update(delta);
        scController.update();
        autoSaveManager.update(delta);
        cameraManager.update(delta, worldManager.getCurrentWorld());
        uiManager.update(delta);
        worldManager.update(delta);
    }

    public void resize() {
        cameraManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (uiManager != null) uiManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void dispose() {
        dispose(true);
    }

    public void dispose(boolean save) {
        if (save && autoSaveManager != null) {
            Gdx.app.log("ManagerRegistry", "Disposing managers, saving game...");
            autoSaveManager.saveGame();
        }
        if (uiManager != null) uiManager.dispose();
        if (worldManager != null) worldManager.disposeWorlds();
    }

    public CameraManager getCameraManager() { return cameraManager; }
    public GameStateManager getGameStateManager() { return gameStateManager; }
    public GameInputHandler getGameInputHandler(){ return gameInputHandler; }
    public WorldManager getWorldManager() { return worldManager; }

    public GameContext createContext(){
        return new GameContext(player, uiManager, npcManager, gameStateManager,
                itemManager, itemRegistry, questRegistry, questManager,
                questProgressTriggers, dialogueRegistry,
                actionRegistry, transitionManager, worldManager, darkOverlay);
    }
    public GameContext getContext(){ return ctx; }
}
