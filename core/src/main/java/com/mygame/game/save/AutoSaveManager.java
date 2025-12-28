package com.mygame.game.save;


import com.mygame.Main;
import com.mygame.entity.item.Item;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.npc.Police;
import com.mygame.entity.player.Player;
import com.mygame.game.GameInitializer;
import com.mygame.quest.QuestManager;
import com.mygame.quest.QuestObserver;
import com.mygame.world.WorldManager;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;


public class AutoSaveManager {

    private static float timer = 0f;

    private static final float SAVE_INTERVAL = 5f; // раз на 5 секунд

    public static void update(float delta) {
        timer += delta;
        if (timer >= SAVE_INTERVAL) {
            saveGame();
        }
    }

    public static void saveGame() {
        timer = 0;

        GameInitializer gameInitializer = Main.getGameInitializer();
        if (gameInitializer == null || gameInitializer.getPlayer() == null || gameInitializer.getContext() == null) return;

        GameSettings settings = SettingsManager.load();

        Player player = gameInitializer.getPlayer();
        settings.playerState = player.getState();

        // Save player data
        settings.playerX = player.getX();
        settings.playerY = player.getY();
        if (WorldManager.getCurrentWorld() != null) {
            settings.currentWorldName = WorldManager.getCurrentWorld().getName();
        }

        // Save inventory
        settings.inventory = player.getInventory().getItems().entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey().getKey(), Map.Entry::getValue));

        // Save active quests
        settings.activeQuests = QuestManager.getQuests().stream()
            .collect(Collectors.toMap(
                QuestManager.Quest::key,
                quest -> new GameSettings.QuestSaveData(quest.progressable(), quest.progress(), quest.maxProgress(), quest.isCompleted())
            ));

        settings.talkedNpcs = new HashSet<>(QuestObserver.getTalkedNpcs());
        settings.visited = new HashSet<>(QuestObserver.getVisited());

        // Save searched items
        settings.searchedItems = WorldManager.getWorlds().values().stream()
            .flatMap(world -> world.getItems().stream())
            .filter(Item::isSearched)
            .map(Item::getUniqueId)
            .collect(Collectors.toSet());

        // Save NPC dialogue states
        settings.npcStates = WorldManager.getWorlds().values().stream()
            .flatMap(world -> world.getNpcs().stream())
            .collect(Collectors.toMap(
                NPC::getId,
                npc -> new GameSettings.NpcSaveData(npc.getCurrentDialogueNodeId(), npc.getCurrentTextureKey()),
                (existing, replacement) -> existing
            ));

        // Save Police Chase State
        Police summonedPolice = gameInitializer.getContext().npcManager.getSummonedPolice();
        if (summonedPolice != null && summonedPolice.getState() == Police.PoliceState.CHASING) {
            settings.policeChaseActive = true;
            settings.policeX = summonedPolice.getX();
            settings.policeY = summonedPolice.getY();
            settings.policeWorldName = summonedPolice.getWorld().getName();
        } else {
            settings.policeChaseActive = false;
        }

        SettingsManager.save(settings);
        System.out.println("Game state saved.");
    }
}
