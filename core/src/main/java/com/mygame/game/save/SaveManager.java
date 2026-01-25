package com.mygame.game.save;

import com.badlogic.gdx.Gdx;
import com.mygame.Main;
import com.mygame.assets.audio.MusicManager;
import com.mygame.assets.audio.SoundManager;
import com.mygame.entity.item.Item;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.npc.Police;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.game.GameInitializer;
import com.mygame.game.GameStateManager;
import com.mygame.quest.QuestManager;
import com.mygame.world.zone.Zone;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class SaveManager {

    private final GameContext ctx;

    private float saveCooldown = 0f;
    private final float MIN_SAVE_INTERVAL = 1.5f; // Мінімальна пауза між записами на диск

    private float burstTimer = 0f;
    private final float BURST_DELAY = 0.5f; // Затримка "очікування" нових подій

    private boolean pendingSave = false;

    public SaveManager(GameContext ctx) {
        this.ctx = ctx;
        EventBus.subscribe(Events.SaveRequestEvent.class, e -> requestSave());
        Gdx.app.log("AutoSaveManager", "Smart event-based saving initialized.");
    }

    public void update(float delta) {
        if (saveCooldown > 0) saveCooldown -= delta;
        if (burstTimer > 0) burstTimer -= delta;

        if (pendingSave && saveCooldown <= 0 && burstTimer <= 0) {
            saveGame();
        }
    }

    private void requestSave() {
        pendingSave = true;
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
        if (ctx.gsm.getState() == GameStateManager.GameState.DEATH) return;

        try {
            GameSettings settings = SettingsManager.load();

            settings.musicVolume = MusicManager.getVolume();
            settings.soundVolume = SoundManager.getVolume();

            savePlayerData(settings);

            if (ctx.worldManager.getCurrentWorld() != null) settings.currentWorldName = ctx.worldManager.getCurrentWorld().getName();

            settings.currentDay = ctx.dayManager.getDay();
            settings.currentTime = ctx.dayManager.getCurrentTime();

            saveInventory(settings);

            saveActiveQuests(settings);

            saveQuestTriggers(settings);

            saveSearchedItems(settings);

            saveNpcStates(settings);

            saveSummonedPolice(settings);
            saveQuestZones(settings);
            saveCreatedItems(settings);

            SettingsManager.save(settings);
            Gdx.app.log("AutoSaveManager", "Game saved successfully. World: " + settings.currentWorldName);
        } catch (Exception e) {
            Gdx.app.error("AutoSaveManager", "Critical error during save: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveCreatedItems(GameSettings settings) {
        settings.createdItems = ctx.itemManager.getAllItems().stream()
                .filter(Item::isDynamic)
                .map(item -> new GameSettings.ItemSaveData(
                        item.getType().getKey(),
                        item.getX(),
                        item.getY(),
                        item.getWorld().getName(),
                        item.isSearched()))
                .collect(Collectors.toList());
    }

    private void saveInventory(GameSettings settings){
        settings.inventory = ctx.player.getInventory().getItems().entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey().getKey(), Map.Entry::getValue));
    }

    private void savePlayerData(GameSettings settings){
        settings.playerState = ctx.player.getState();
        settings.playerX = ctx.player.getX();
        settings.playerY = ctx.player.getY();
    }

    private void saveActiveQuests(GameSettings settings){
        settings.activeQuests = ctx.questManager.getQuests().stream()
            .collect(Collectors.toMap(
                QuestManager.Quest::key,
                quest -> new GameSettings.QuestSaveData(quest.progress(), quest.getStatus())
            ));
    }

    private void saveSearchedItems(GameSettings settings){
        settings.searchedItems = ctx.worldManager.getWorlds().values().stream()
            .flatMap(world -> ctx.itemManager.getAllItems().stream())
            .filter(Item::isSearched)
            .map(Item::getUniqueId)
            .collect(Collectors.toSet());
    }

    private void saveQuestTriggers(GameSettings settings){
        if (ctx.questProgressTriggers != null) {
            settings.talkedNpcs = new HashSet<>(ctx.questProgressTriggers.getTalkedNpcs());
            settings.visited = new HashSet<>(ctx.questProgressTriggers.getVisited());
        }
    }

    private void saveSummonedPolice(GameSettings settings){
        Police summonedPolice = ctx.npcManager.getSummonedPolice();
        if (summonedPolice != null && summonedPolice.getState() == Police.PoliceState.CHASING) {
            settings.policeChaseActive = true;
            settings.policeX = summonedPolice.getX();
            settings.policeY = summonedPolice.getY();
            settings.policeWorldName = summonedPolice.getWorld().getName();
        } else {
            settings.policeChaseActive = false;
        }
    }

    private void saveNpcStates(GameSettings settings) {
        settings.npcStates = ctx.npcManager.getNpcs().stream().collect(Collectors.toMap(
                NPC::getId,
                npc -> {
                    GameSettings.NpcSaveData data = new GameSettings.NpcSaveData(npc.getCurrentDialogueNodeId(), npc.getCurrentTextureKey());
                    data.x = npc.getX();
                    data.y = npc.getY();
                    if (npc.getWorld() != null) {
                        data.currentWorld = npc.getWorld().getName();
                    }
                    return data;
                },
                (existing, replacement) -> existing
        ));
    }
    private void saveQuestZones(GameSettings settings) {
        settings.enabledQuestZones = ctx.zoneRegistry.getZones().stream()
                .filter(zone -> zone.isEnabled())
                .map(Zone::getId)
                .collect(Collectors.toSet());
    }

}
