package com.mygame.game.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SerializationException;

/**
 * Manages loading and saving game settings to a JSON file.
 */
public class SettingsManager {

    // The path is relative to the application's local storage directory.
    private static final String SETTINGS_FILE = "assets/data/saving/settings.json";
    private static final Json json = new Json();

    /**
     * Saves the given settings object to the JSON file.
     */
    public static void save(GameSettings settings) {
        FileHandle file = Gdx.files.local(SETTINGS_FILE);
        try {
            file.writeString(json.toJson(settings), false);
        } catch (Exception e) {
            Gdx.app.error("SettingsManager", "Error saving settings", e);
        }
    }

    /**
     * Loads settings from the JSON file.
     * If the file doesn't exist or is corrupt, returns a new default GameSettings object.
     */
    public static GameSettings load() {
        FileHandle file = Gdx.files.local(SETTINGS_FILE);
        if (file.exists()) {
            try {
                return json.fromJson(GameSettings.class, file);
            } catch (SerializationException e) {
                Gdx.app.error("SettingsManager", "Error loading settings, file is corrupt or outdated. Deleting and creating new settings.", e);
                file.delete(); // Delete the corrupt file
                return new GameSettings(); // Return default settings
            }
        } else {
            return new GameSettings(); // Return default if no file
        }
    }
}
