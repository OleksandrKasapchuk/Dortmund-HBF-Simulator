package com.mygame.managers.global;


import com.mygame.Main;
import com.mygame.entity.Player;
import com.mygame.game.GameInitializer;
import com.mygame.game.GameSettings;
import com.mygame.game.SettingsManager;
import com.mygame.world.WorldManager;

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
        if (gameInitializer == null || gameInitializer.getPlayer() == null) return;

        GameSettings settings = SettingsManager.load();
        Player player = gameInitializer.getPlayer();

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
            .map(QuestManager.Quest::getKey)
            .collect(Collectors.toList());

        SettingsManager.save(settings);
        System.out.println("Game state saved.");
    }
}
