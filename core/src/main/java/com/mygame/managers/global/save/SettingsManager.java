package com.mygame.managers.global.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * Manages loading and saving game settings to a JSON file.
 */
public class SettingsManager {

    // The path is relative to the application's local storage directory.
    private static final String SETTINGS_FILE = "data/saving/settings.json";
    private static final Json json = new Json();

    /**
     * Saves the given settings object to the JSON file.
     */
    public static void save(GameSettings settings) {
        // Gdx.files.local() gets a handle to a file in a writable local directory.
        FileHandle file = Gdx.files.local(SETTINGS_FILE);
        file.writeString(json.toJson(settings), false);
    }

    /**
     * Loads settings from the JSON file.
     * If the file doesn't exist, returns a new default GameSettings object.
     */
    public static GameSettings load() {
        // Gdx.files.local() is also used for loading.
        FileHandle file = Gdx.files.local(SETTINGS_FILE);
        if (file.exists()) {
            return json.fromJson(GameSettings.class, file);
        } else {
            return new GameSettings(); // Return default if no file
        }
    }
}
