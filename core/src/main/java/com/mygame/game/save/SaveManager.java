package com.mygame.game.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
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
import com.mygame.game.auth.AuthManager;
import com.mygame.game.save.data.ClientSaveData;
import com.mygame.game.save.data.ServerSaveData;
import com.mygame.quest.QuestManager;
import com.mygame.world.zone.Zone;

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
        EventBus.subscribe(Events.ClientSaveEvent.class, e -> saveLocal());
    }

    public void update(float delta) {
        if (saveCooldown > 0) saveCooldown -= delta;
        if (burstTimer > 0) burstTimer -= delta;

        if (pendingSave && saveCooldown <= 0 && burstTimer <= 0) {
            saveServer();
        }
    }

    private void requestSave() {
        pendingSave = true;
        burstTimer = BURST_DELAY;
    }

    public void saveGameToServer(ServerSaveData settings) {
        if (!AuthManager.hasToken()) {
            Gdx.app.log("SaveManager", "No token, skipping server save.");
            return;
        }

        try {
            Net.HttpRequest postRequest = new Net.HttpRequest(Net.HttpMethods.POST);
            postRequest.setUrl("https://hbf-simulator-backend.onrender.com/api/save/?format=json");
            postRequest.setHeader("Content-Type", "application/json");
            postRequest.setHeader("Authorization", "Token " + AuthManager.getToken());

            // Перетворюємо GameSettings у JSON напряму
            Json json = SettingsManager.json;
            json.setOutputType(JsonWriter.OutputType.json);
            json.setUsePrototypes(false);

            String jsonData = json.toJson(settings);
            Gdx.app.log("SaveManager", "JSON Data: " + jsonData);
            postRequest.setContent(jsonData);

            Gdx.net.sendHttpRequest(postRequest, new Net.HttpResponseListener() {
                @Override
                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                    Gdx.app.log("SaveManager", httpResponse.getResultAsString());
                }

                @Override
                public void failed(Throwable t) {
                    t.printStackTrace();
                }

                @Override
                public void cancelled() {}
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveLocal(){
        ClientSaveData settings = SettingsManager.loadClient();
        settings.musicVolume = MusicManager.getVolume();
        settings.soundVolume = SoundManager.getVolume();
        SettingsManager.saveClient(settings);
    }

    public void saveServer(){
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
            ServerSaveData settings = SettingsManager.loadServer();
            settings.username = AuthManager.getUsername(); // Ensure username is always up-to-date
            System.out.println(settings.username);
            savePlayerData(settings);

            if (ctx.worldManager.getCurrentWorld() != null) settings.currentWorldName = ctx.worldManager.getCurrentWorld().getName();

            saveTime(settings);
            saveInventory(settings);
            saveActiveQuests(settings);
            saveQuestTriggers(settings);
            saveSearchedItems(settings);
            saveNpcStates(settings);
            saveSummonedPolice(settings);
            saveZones(settings);
            saveCreatedItems(settings);
            saveGameToServer(settings);
            SettingsManager.saveServer(settings);
            Gdx.app.log("AutoSaveManager", "Game saved successfully. World: " + settings.currentWorldName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveTime(ServerSaveData settings){
        settings.currentDay = ctx.dayManager.getDay();
        settings.currentTime = ctx.dayManager.getCurrentTime();
    }

    private void saveCreatedItems(ServerSaveData settings) {
        settings.createdItems.clear();
        for (Item item : ctx.itemManager.getAllItems()) {
            if (!item.isDynamic()) continue;
            settings.createdItems.add(new ServerSaveData.ItemSaveData(
                item.getType().getKey(),
                item.getX(), item.getY(),
                item.getWorld().getName(),
                item.getSearchData(), item.getInteractionData(),
                item.getWidth(), item.getHeight()
            ));
        }
    }

    private void saveInventory(ServerSaveData settings){
        settings.inventory.clear();
        ctx.player.getInventory().getItems().forEach((key, value) -> settings.inventory.put(key.getKey(), value));
    }

    private void savePlayerData(ServerSaveData settings){
        settings.playerState = ctx.player.getState();
        settings.playerX = ctx.player.getX();
        settings.playerY = ctx.player.getY();
        settings.playerHunger = ctx.player.getStatusController().getHunger();
        settings.playerThirst = ctx.player.getStatusController().getThirst();
        settings.playerVibe = ctx.player.getStatusController().getVibe();
    }

    private void saveActiveQuests(ServerSaveData settings){
        for (QuestManager.Quest quest : ctx.questManager.getQuests()) {
            ServerSaveData.QuestSaveData data = settings.activeQuests.computeIfAbsent(quest.key(), k -> new ServerSaveData.QuestSaveData());
            data.progress = quest.progress();
            data.status = quest.getStatus();
        }
    }

    private void saveSearchedItems(ServerSaveData settings){
        settings.searchedItems.clear();
        for (Item item : ctx.itemManager.getAllItems()) {
            if (item.getSearchData() != null && item.getSearchData().isSearched()) {
                settings.searchedItems.add(item.getId());
            }
        }
    }

    private void saveQuestTriggers(ServerSaveData settings){
        if (ctx.questProgressTriggers != null) {
            settings.talkedNpcs.addAll(ctx.questProgressTriggers.getTalkedNpcs());
            settings.visited.addAll(ctx.questProgressTriggers.getVisited());
        }
    }

    private void saveSummonedPolice(ServerSaveData settings){
        Police summonedPolice = ctx.npcManager.getSummonedPolice();
        if (summonedPolice != null && summonedPolice.getState() == Police.PoliceState.CHASING) {
            settings.policeX = summonedPolice.getX();
            settings.policeY = summonedPolice.getY();
            settings.policeWorldName = summonedPolice.getWorld().getName();
        }
    }

    private void saveNpcStates(ServerSaveData settings) {
        for (NPC npc : ctx.npcManager.getNpcs()) {
            ServerSaveData.NpcSaveData data = settings.npcStates.computeIfAbsent(npc.getId(), k -> new ServerSaveData.NpcSaveData());
            data.currentNode = npc.getCurrentDialogueNodeId();
            data.currentTexture = npc.getCurrentTextureKey();
            data.x = npc.getX();
            data.y = npc.getY();
            data.currentWorld = npc.getWorld() != null ? npc.getWorld().getName() : null;
        }
    }
    private void saveZones(ServerSaveData settings) {
        settings.enabledZones.clear();
        for (Zone zone : ctx.zoneRegistry.getZones()) {
            if (zone.isEnabled()) settings.enabledZones.add(zone.getId());
        }
    }
}
