package com.mygame.game.save;

import com.mygame.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public Set<String> talkedNpcs;
    public Set<String> visited;
    public Set<String> searchedItems;

    // NPC State: Mapping NPC ID to their current status (dialogue and texture)
    public Map<String, NpcSaveData> npcStates;

    // Police chase save data
    public boolean policeChaseActive;
    public float policeX;
    public float policeY;
    public String policeWorldName;

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

    public static class NpcSaveData {
        public String currentNode;
        public String currentTexture;

        public NpcSaveData() {}
        public NpcSaveData(String currentNode, String currentTexture) {
            this.currentNode = currentNode;
            this.currentTexture = currentTexture;
        }
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

        this.talkedNpcs = new HashSet<>();
        this.visited = new HashSet<>();
        this.searchedItems = new HashSet<>();
        this.npcStates = new HashMap<>();

        this.policeChaseActive = false;
    }
}
