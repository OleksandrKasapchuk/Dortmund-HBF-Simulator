package com.mygame.game.save;

import com.mygame.Main;
import com.mygame.entity.item.Item;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.npc.Police;
import com.mygame.entity.player.Player;
import com.mygame.game.GameInitializer;
import com.mygame.quest.QuestManager;
import com.mygame.quest.QuestProgressTriggers;
import com.mygame.world.WorldManager;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class AutoSaveManager {

    private static float timer = 0f;
    private static final float SAVE_INTERVAL = 5f;

    public static void update(float delta) {
        timer += delta;
        if (timer >= SAVE_INTERVAL) {
            saveGame();
        }
    }

    public static void saveGame() {
        timer = 0;

        GameInitializer gameInitializer = Main.getGameInitializer();
        if (gameInitializer == null || gameInitializer.getPlayer() == null || gameInitializer.getManagerRegistry() == null) return;

        GameSettings settings = SettingsManager.load();
        Player player = gameInitializer.getPlayer();
        QuestProgressTriggers triggers = gameInitializer.getManagerRegistry().getQuestProgressTriggers();

        settings.playerState = player.getState();
        settings.playerX = player.getX();
        settings.playerY = player.getY();
        if (WorldManager.getCurrentWorld() != null) {
            settings.currentWorldName = WorldManager.getCurrentWorld().getName();
        }

        settings.inventory = player.getInventory().getItems().entrySet().stream()
            .collect(Collectors.toMap(entry -> entry.getKey().getKey(), Map.Entry::getValue));

        settings.activeQuests = QuestManager.getQuests().stream()
            .collect(Collectors.toMap(
                QuestManager.Quest::key,
                quest -> new GameSettings.QuestSaveData(quest.progress(), quest.getStatus())
            ));

        // Використовуємо нестатичні методи екземпляра
        if (triggers != null) {
            settings.talkedNpcs = new HashSet<>(triggers.getTalkedNpcs());
            settings.visited = new HashSet<>(triggers.getVisited());
        }

        settings.searchedItems = WorldManager.getWorlds().values().stream()
            .flatMap(world -> world.getItems().stream())
            .filter(Item::isSearched)
            .map(Item::getUniqueId)
            .collect(Collectors.toSet());

        settings.npcStates = WorldManager.getWorlds().values().stream()
            .flatMap(world -> world.getNpcs().stream())
            .collect(Collectors.toMap(
                NPC::getId,
                npc -> new GameSettings.NpcSaveData(npc.getCurrentDialogueNodeId(), npc.getCurrentTextureKey()),
                (existing, replacement) -> existing
            ));

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
    }
}
