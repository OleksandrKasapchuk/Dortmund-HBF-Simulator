package com.mygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygame.action.ActionRegistry;
import com.mygame.dialogue.DialogueRegistry;
import com.mygame.entity.item.ItemInteractionSystem;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.player.Player;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.item.PfandManager;
import com.mygame.entity.player.PlayerEffectManager;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.game.GameInputHandler;
import com.mygame.game.GameStateManager;
import com.mygame.quest.QuestManager;
import com.mygame.quest.QuestProgressTriggers;
import com.mygame.quest.QuestRegistry;
import com.mygame.scenario.ScenarioController;
import com.mygame.ui.UIManager;
import com.mygame.world.World;
import com.mygame.world.transition.TransitionManager;
import com.mygame.world.WorldManager;

public class ManagerRegistry {

    private UIManager uiManager;
    private NpcManager npcManager;
    private PfandManager pfandManager;
    private ItemManager itemManager;
    private TransitionManager transitionManager;
    private CameraManager cameraManager;
    private GameStateManager gameStateManager;
    private PlayerEffectManager playerEffectManager;
    private QuestProgressTriggers questProgressTriggers;
    private ItemRegistry itemRegistry;
    private QuestRegistry questRegistry;
    private QuestManager questManager;
    private DialogueRegistry dialogueRegistry;
    private ActionRegistry actionRegistry;
    private ScenarioController scController;
    private ItemInteractionSystem itemInteractionSystem;
    private Player player;
    private GameContext ctx;
    private GameInputHandler gameInputHandler;


    public ManagerRegistry(SpriteBatch batch, Player player, Skin skin) {
        EventBus.clear();
        this.player = player;

        // 1. Реєстри
        itemRegistry = new ItemRegistry();
        questRegistry = new QuestRegistry();
        dialogueRegistry = new DialogueRegistry();
        actionRegistry = new ActionRegistry();

        // 2. Менеджери
        questManager = new QuestManager(questRegistry);
        questProgressTriggers = new QuestProgressTriggers(questManager, itemRegistry);

        cameraManager = new CameraManager(player);
        pfandManager = new PfandManager(itemRegistry);
        uiManager = new UIManager(batch, player, skin, questManager);
        playerEffectManager = new PlayerEffectManager();
        itemManager = new ItemManager(itemRegistry);
        npcManager = new NpcManager(player, dialogueRegistry);
        transitionManager = new TransitionManager();
        gameStateManager = new GameStateManager(uiManager);

        // 3. Контекст
        ctx = createContext();

        // 4. Ініціалізація
        actionRegistry.init(ctx);
        dialogueRegistry.init(actionRegistry);

        // ПІДПИСКА НА ВИКОНАННЯ ДІЙ ЧЕРЕЗ ІВЕНТИ
        EventBus.subscribe(Events.ActionRequestEvent.class, event -> actionRegistry.executeAction(event.actionId()));

        // 5. Завантаження мап
        for (World world : WorldManager.getWorlds().values()) {
            npcManager.loadNpcsFromMap(world);
            itemManager.loadItemsFromMap(world);
            transitionManager.loadTransitionsFromMap(world);
        }

        scController = new ScenarioController(ctx);
        itemInteractionSystem = new ItemInteractionSystem();
        gameInputHandler = new GameInputHandler(ctx.gsm, ctx.ui);
    }

    public void update(float delta) {
        npcManager.update(delta);
        cameraManager.update(delta, WorldManager.getCurrentWorld());
        itemManager.update(delta, player);
        pfandManager.update(delta);
        scController.update();
        uiManager.update(delta);
    }

    public void resize() {
        cameraManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (uiManager != null) uiManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void dispose() {
        if (uiManager != null) uiManager.dispose();
    }

    public CameraManager getCameraManager() { return cameraManager; }
    public GameStateManager getGameStateManager() { return gameStateManager; }
    public GameInputHandler getGameInputHandler(){ return gameInputHandler; }
    public GameContext createContext(){
        return new GameContext(player, uiManager, npcManager, gameStateManager,
                               itemManager, itemRegistry, questRegistry, questManager,
                               questProgressTriggers, dialogueRegistry, actionRegistry);
    }
    public GameContext getContext(){ return ctx; }
}
