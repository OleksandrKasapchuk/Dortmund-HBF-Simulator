package com.mygame.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.action.ActionRegistry;
import com.mygame.dialogue.DialogueManager;
import com.mygame.dialogue.DialogueRegistry;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.item.PfandManager;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.player.Player;
import com.mygame.entity.player.PlayerEffectManager;
import com.mygame.game.save.SaveManager;
import com.mygame.managers.CameraManager;
import com.mygame.quest.QuestManager;
import com.mygame.quest.QuestProgressTriggers;
import com.mygame.quest.QuestRegistry;
import com.mygame.scenario.ScenarioController;
import com.mygame.ui.UIManager;
import com.mygame.ui.inGameUI.DarkOverlay;
import com.mygame.world.WorldManager;
import com.mygame.world.zone.ZoneRegistry;

// GameContext now creates and holds most of the managers.
public class GameContext {

    // Core Objects
    public final Player player;
    public final WorldManager worldManager;
    public final DayManager dayManager;
    public final DarkOverlay darkOverlay;

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
    public final GameStateManager gsm;
    public final UIManager ui;
    public final CameraManager cameraManager;
    public final SaveManager saveManager;
    public final DialogueManager dialogueManager;

    // Logic
    public final QuestProgressTriggers questProgressTriggers;
    public final ScenarioController scController;

    public GameContext(SpriteBatch batch, Player player, Skin skin) {
        this.player = player;

        // 1. Core Systems
        this.darkOverlay = new DarkOverlay();
        this.worldManager = new WorldManager(player, darkOverlay);
        this.dayManager = new DayManager();

        // 2. Registries
        this.itemRegistry = new ItemRegistry();
        this.questRegistry = new QuestRegistry();
        this.dialogueRegistry = new DialogueRegistry();
        this.actionRegistry = new ActionRegistry();

        // 3. Managers that depend on registries and core systems
        this.playerEffectManager = new PlayerEffectManager();
        this.questManager = new QuestManager(questRegistry);
        this.npcManager = new NpcManager(player, dialogueRegistry, worldManager);
        this.itemManager = new ItemManager(itemRegistry, worldManager);
        this.pfandManager = new PfandManager(itemRegistry, worldManager);
        this.zoneRegistry = new ZoneRegistry(itemRegistry, player);
        this.cameraManager = new CameraManager(player);
        this.ui = new UIManager(batch, player, skin, questManager, worldManager, dayManager);
        this.dialogueManager = new DialogueManager(ui.getDialogueUI(), player, worldManager, dialogueRegistry);
        this.gsm = new GameStateManager(ui);

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
        worldManager.update(delta);
        darkOverlay.update(delta);
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
            saveManager.saveGame();
        }
        if (ui != null) ui.dispose();
        if (worldManager != null) worldManager.disposeWorlds();
    }
}
