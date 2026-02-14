package com.mygame.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.action.ActionRegistry;
import com.mygame.dialogue.DialogueManager;
import com.mygame.dialogue.DialogueRegistry;
import com.mygame.entity.InteractionManager;
import com.mygame.entity.item.ItemEventHandler;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.EntityRenderer;
import com.mygame.entity.item.PfandManager;
import com.mygame.entity.item.plant.PlantSystem;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.player.Player;
import com.mygame.entity.player.PlayerEffectManager;
import com.mygame.game.auth.AuthManager;
import com.mygame.game.save.SaveManager;
import com.mygame.managers.CameraManager;
import com.mygame.quest.QuestManager;
import com.mygame.quest.QuestProgressTriggers;
import com.mygame.quest.QuestRegistry;
import com.mygame.scenario.ScenarioController;
import com.mygame.ui.UIManager;
import com.mygame.ui.inGameUI.Overlay;
import com.mygame.world.WorldManager;
import com.mygame.world.zone.ZoneManager;
import com.mygame.world.zone.ZoneRegistry;


public class GameContext {

    // Core Objects
    public final Player player;
    public final WorldManager worldManager;
    public final DayManager dayManager;
    public final Overlay overlay;

    // Registries
    public final ItemRegistry itemRegistry;
    public final QuestRegistry questRegistry;
    public final DialogueRegistry dialogueRegistry;
    public final ActionRegistry actionRegistry;

    // Managers
    public final PlayerEffectManager playerEffectManager;
    public final QuestManager questManager;
    public final NpcManager npcManager;
    public final ItemManager itemManager;
    public final PfandManager pfandManager;
    public final ZoneRegistry zoneRegistry;
    public final PlantSystem plantSystem;
    public final ItemEventHandler itemEventHandler;
    public final EntityRenderer entityRenderer;
    public final ZoneManager zoneManager;

    public UIManager ui;
    public GameStateManager gsm;
    public final CameraManager cameraManager;
    public final SaveManager saveManager;
    public final DialogueManager dialogueManager;
    public final InteractionManager interactionManager;

    // Logic
    public final QuestProgressTriggers questProgressTriggers;
    public final ScenarioController scController;

    public GameContext(SpriteBatch batch, Player player, Skin skin, UIManager ui, GameStateManager gsm) {
        this.ui = ui;
        this.gsm = gsm;
        this.player = player;

        // 1. Core Systems
        this.overlay = new Overlay();
        this.worldManager = new WorldManager(player, overlay);
        this.dayManager = new DayManager();

        // 2. Registries
        this.itemRegistry = new ItemRegistry();
        this.questRegistry = new QuestRegistry();
        this.dialogueRegistry = new DialogueRegistry();
        this.actionRegistry = new ActionRegistry();

        // 3. Managers that depend on registries and core systems
        this.playerEffectManager = new PlayerEffectManager();
        this.questManager = new QuestManager(questRegistry);
        this.zoneRegistry = new ZoneRegistry(itemRegistry, player);
        this.itemManager = new ItemManager(itemRegistry, worldManager, zoneRegistry);
        this.plantSystem = new PlantSystem(itemManager, itemRegistry, zoneRegistry, worldManager);
        this.itemEventHandler = new ItemEventHandler(itemRegistry, itemManager, worldManager);
        this.cameraManager = new CameraManager(player);
        this.entityRenderer = new EntityRenderer(worldManager, cameraManager.getCamera());
        this.zoneManager = new ZoneManager(worldManager, player);
        player.setItemManager(itemManager);
        this.npcManager = new NpcManager(player, dialogueRegistry, worldManager, itemManager);
        player.setNpcManager(npcManager);
        this.ui.init(player, questManager, worldManager, dayManager, npcManager, itemManager, zoneManager);
        this.pfandManager = new PfandManager(itemRegistry, itemManager, worldManager);
        this.interactionManager = new InteractionManager(batch, player, questManager, worldManager, npcManager, itemManager);
        this.dialogueManager = new DialogueManager(ui.getDialogueUI(), player, worldManager, dialogueRegistry, npcManager);

        // 4. High-level logic
        this.questProgressTriggers = new QuestProgressTriggers(questManager, itemRegistry, npcManager, worldManager);
        this.scController = new ScenarioController(this);

        // 5. Systems that need the full context
        this.actionRegistry.init(this);
        this.dialogueRegistry.init(actionRegistry);
        this.saveManager = new SaveManager(this);
    }

    public void update(float delta) {
        dayManager.update(delta);
        zoneManager.update(delta);
        overlay.update(delta);
        npcManager.update(delta);
        dialogueManager.update(delta);
        itemManager.update(delta, player);
        pfandManager.update(delta);
        scController.update();
        saveManager.update(delta);
        cameraManager.update(delta, worldManager.getCurrentWorld());
        ui.update(delta);
    }

    public void dispose(boolean save) {
        if (save && saveManager != null) {
            saveManager.saveLocal();
            if (AuthManager.hasToken()){
                saveManager.saveServer();
            }
        }
        if (ui != null) ui.dispose();
        if (worldManager != null) worldManager.disposeWorlds();
    }
}
