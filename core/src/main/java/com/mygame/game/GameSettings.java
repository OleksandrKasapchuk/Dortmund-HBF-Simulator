package com.mygame.game;

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
    public String currentWorldName; // Changed from World to String
    public Map<String, Integer> inventory;
    public List<String> activeQuests;

    public GameSettings() {
        // Default settings
        this.language = "ua";
        this.musicVolume = 1.0f;
        this.soundVolume = 1.0f;
        this.muteAll = false;
        this.playerX = 200; // Default starting position
        this.playerY = 200;  // Default starting position
        this.currentWorldName = "main"; // Default world
        this.inventory = new HashMap<>();
        this.activeQuests = new ArrayList<>();
    }
}
