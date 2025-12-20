package com.mygame.game.save;

import com.mygame.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple data class to hold game settings.
 * Needs a no-arg constructor for JSON serialization.
 */
public class GameSettings {
    public String language;
    public float musicVolume;
    public float soundVolume;
    public boolean muteAll;
    public float playerX;
    public float playerY;
    public String currentWorldName;
    public Map<String, Integer> inventory;
    public Map<String, QuestSaveData> activeQuests;
    public List<String> completedDialogueEvents;
    public Player.State playerState;

    public static class QuestSaveData {
        public boolean progressable;
        public int progress;
        public int maxProgress;

        public QuestSaveData(boolean progressable, int progress, int maxProgress) {
            this.progressable = progressable;
            this.progress = progress;
            this.maxProgress = maxProgress;
        }

        public QuestSaveData() {}
    }

    public GameSettings() {
        // Default settings
        this.language = "en";
        this.musicVolume = 1.0f;
        this.soundVolume = 1.0f;
        this.muteAll = false;
        this.playerX = 200;
        this.playerY = 200;
        this.currentWorldName = "main";
        this.inventory = new HashMap<>();
        this.activeQuests = new HashMap<>();
        this.completedDialogueEvents = new ArrayList<>();
        this.playerState = Player.State.NORMAL;
    }
}
