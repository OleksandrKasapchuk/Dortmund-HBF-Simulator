package com.mygame.game.save;

import com.mygame.entity.item.itemData.InteractionData;
import com.mygame.entity.item.itemData.SearchData;
import com.mygame.entity.player.Player;
import com.mygame.quest.QuestManager;

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
    public String id;
    public String language;
    public String player_name;
    public float musicVolume;
    public float soundVolume;
    public boolean muteAll;

    public float playerX;
    public float playerY;
    public Player.State playerState;
    public float playerHunger;
    public float playerThirst;
    public float playerVibe;

    public String currentWorldName;

    public int currentDay;
    public float currentTime;

    public Map<String, Integer> inventory;
    public Map<String, QuestSaveData> activeQuests;
    public List<String> completedDialogueEvents;
    public List<ItemSaveData> createdItems;

    public Set<String> talkedNpcs;
    public Set<String> visited;
    public Set<String> searchedItems;
    public Set<String> enabledZones; // Changed from disabledQuestZones

    // NPC State: Mapping NPC ID to their current status (dialogue and texture)
    public Map<String, NpcSaveData> npcStates;

    // Police chase save data
    public boolean policeChaseActive;
    public float policeX;
    public float policeY;
    public String policeWorldName;

    public static class ItemSaveData {
        public String itemKey;
        public float x;
        public float y;
        public String worldName;
        public SearchData searchData;
        public InteractionData interactionData;
        public int width;
        public int height;
        public ItemSaveData() {}

        public ItemSaveData(String itemKey, float x, float y, String worldName, SearchData searchData, InteractionData interactionData, int width, int height) {
            this.itemKey = itemKey;
            this.x = x;
            this.y = y;
            this.worldName = worldName;
            this.searchData = searchData;
            this.interactionData = interactionData;
            this.width = width;
            this.height = height;
        }
    }

    public static class QuestSaveData {
        public int progress;
        public QuestManager.Status status;

        public QuestSaveData(int progress, QuestManager.Status status) {
            this.progress = progress;
            this.status = status;
        }

        public QuestSaveData() {}
    }

    public static class NpcSaveData {
        public String currentNode;
        public String currentTexture;
        public String currentWorld;
        public float x;
        public float y;
        public NpcSaveData() {}
        public NpcSaveData(String currentNode, String currentTexture) {
            this.currentNode = currentNode;
            this.currentTexture = currentTexture;
        }
    }

    public GameSettings() {
        // Default settings
        this.player_name = "Player";
        this.language = "en";
        this.musicVolume = 1.0f;
        this.soundVolume = 1.0f;
        this.muteAll = false;
        this.playerX = 200;
        this.playerY = 200;
        this.playerHunger = 100;
        this.playerThirst = 100;
        this.playerVibe = 100;
        this.currentWorldName = "main";
        this.inventory = new HashMap<>();
        this.activeQuests = new HashMap<>();
        this.completedDialogueEvents = new ArrayList<>();
        this.playerState = Player.State.NORMAL;
        this.createdItems = new ArrayList<>();

        this.talkedNpcs = new HashSet<>();
        this.visited = new HashSet<>();
        this.searchedItems = new HashSet<>();
        this.npcStates = new HashMap<>();
        this.enabledZones = new HashSet<>(); // Changed from disabledQuestZones

        this.policeChaseActive = false;
    }
}
