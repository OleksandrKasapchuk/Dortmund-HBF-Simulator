package com.mygame.game.save;

import com.badlogic.gdx.Gdx;
import com.mygame.Main;
import com.mygame.entity.item.Item;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.npc.Police;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.game.GameInitializer;
import com.mygame.quest.QuestManager;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class AutoSaveManager {

    private final GameContext ctx;

    private float saveCooldown = 0f;
    private final float MIN_SAVE_INTERVAL = 1.5f; // Мінімальна пауза між записами на диск

    private float burstTimer = 0f;
    private final float BURST_DELAY = 0.5f; // Затримка "очікування" нових подій

    private boolean pendingSave = false;

    public AutoSaveManager(GameContext ctx) {
        this.ctx = ctx;

        // Підписуємося на події
        EventBus.subscribe(Events.InventoryChangedEvent.class, e -> requestSave());
        EventBus.subscribe(Events.QuestStartedEvent.class, e -> requestSave());
        EventBus.subscribe(Events.QuestCompletedEvent.class, e -> requestSave());
        EventBus.subscribe(Events.WorldChangedEvent.class, e -> requestSave());
        EventBus.subscribe(Events.DialogueFinishedEvent.class, e -> requestSave());
        EventBus.subscribe(Events.PlayerStateChangedEvent.class, e -> requestSave());
        EventBus.subscribe(Events.ItemSearchedEvent.class, e -> requestSave());

        Gdx.app.log("AutoSaveManager", "Smart event-based saving initialized.");
    }

    public void update(float delta) {
        if (saveCooldown > 0) saveCooldown -= delta;
        if (burstTimer > 0) burstTimer -= delta;

        // Зберігаємо лише тоді, коли:
        // 1. Є запит (pendingSave)
        // 2. Минуло достатньо часу з останнього запису (saveCooldown <= 0)
        // 3. Події "затихли" (burstTimer <= 0)
        if (pendingSave && saveCooldown <= 0 && burstTimer <= 0) {
            saveGame();
        }
    }

    private void requestSave() {
        pendingSave = true;
        // Кожна нова подія скидає таймер очікування.
        // Це згрупує 20 підібраних предметів в одне збереження через 0.5 сек після останнього.
        burstTimer = BURST_DELAY;
    }

    public void saveGame() {
        Gdx.app.log("AutoSaveManager", "Starting save process...");

        pendingSave = false;
        saveCooldown = MIN_SAVE_INTERVAL;

        GameInitializer gameInitializer = Main.getGameInitializer();
        if (gameInitializer == null || ctx.player == null || gameInitializer.getManagerRegistry() == null) {
            Gdx.app.error("AutoSaveManager", "Save failed: Game state not fully ready.");
            return;
        }

        try {
            GameSettings settings = SettingsManager.load();

            settings.playerState = ctx.player.getState();
            settings.playerX = ctx.player.getX();
            settings.playerY = ctx.player.getY();
            if (ctx.worldManager.getCurrentWorld() != null) {
                settings.currentWorldName = ctx.worldManager.getCurrentWorld().getName();
            }

            settings.inventory = ctx.player.getInventory().getItems().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getKey(), Map.Entry::getValue));

            settings.activeQuests = ctx.questManager.getQuests().stream()
                .collect(Collectors.toMap(
                    QuestManager.Quest::key,
                    quest -> new GameSettings.QuestSaveData(quest.progress(), quest.getStatus())
                ));

            if (ctx.questProgressTriggers != null) {
                settings.talkedNpcs = new HashSet<>(ctx.questProgressTriggers.getTalkedNpcs());
                settings.visited = new HashSet<>(ctx.questProgressTriggers.getVisited());
            }

            settings.searchedItems = ctx.worldManager.getWorlds().values().stream()
                .flatMap(world -> world.getItems().stream())
                .filter(Item::isSearched)
                .map(Item::getUniqueId)
                .collect(Collectors.toSet());

            settings.npcStates = ctx.worldManager.getWorlds().values().stream()
                .flatMap(world -> world.getNpcs().stream())
                .collect(Collectors.toMap(
                    NPC::getId,
                    npc -> new GameSettings.NpcSaveData(npc.getCurrentDialogueNodeId(), npc.getCurrentTextureKey()),
                    (existing, replacement) -> existing
                ));

            Police summonedPolice = ctx.npcManager.getSummonedPolice();
            if (summonedPolice != null && summonedPolice.getState() == Police.PoliceState.CHASING) {
                settings.policeChaseActive = true;
                settings.policeX = summonedPolice.getX();
                settings.policeY = summonedPolice.getY();
                settings.policeWorldName = summonedPolice.getWorld().getName();
            } else {
                settings.policeChaseActive = false;
            }

            SettingsManager.save(settings);
            Gdx.app.log("AutoSaveManager", "Game saved successfully. World: " + settings.currentWorldName);
        } catch (Exception e) {
            Gdx.app.error("AutoSaveManager", "Critical error during save: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
