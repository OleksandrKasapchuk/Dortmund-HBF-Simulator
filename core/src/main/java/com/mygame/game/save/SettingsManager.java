package com.mygame.game.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;

/**
 * Manages loading and saving game settings to a JSON file.
 */
public class SettingsManager {

    private static final String SETTINGS_FILE = "assets/data/saving/settings.json";
    private static final Json json = new Json();

    static {
        // Налаштовуємо стандартний JSON формат без тегів класів
        json.setOutputType(JsonWriter.OutputType.json);
        // Завжди зберігати значення за замовчуванням (щоб списки не зникали)
        json.setIgnoreUnknownFields(true);
    }

    public static void save(GameSettings settings) {
        FileHandle file = Gdx.files.local(SETTINGS_FILE);
        try {
            // Використовуємо prettyPrint для читабельності
            file.writeString(json.prettyPrint(settings), false);
        } catch (Exception e) {
            Gdx.app.error("SettingsManager", "Error saving settings", e);
        }
    }

    public static void resetSettings() {
        GameSettings newSettings = new GameSettings();
        newSettings.language = SettingsManager.load().language;
        newSettings.musicVolume = SettingsManager.load().musicVolume;
        newSettings.soundVolume = SettingsManager.load().soundVolume;

        SettingsManager.save(newSettings);
    }


    public static GameSettings load() {
        FileHandle file = Gdx.files.local(SETTINGS_FILE);
        if (file.exists()) {
            try {
                GameSettings settings = json.fromJson(GameSettings.class, file);
                // Гарантуємо, що списки не будуть null після завантаження
                if (settings.completedDialogueEvents == null) settings.completedDialogueEvents = new java.util.ArrayList<>();
                if (settings.talkedNpcs == null) settings.talkedNpcs = new java.util.HashSet<>();
                if (settings.visited == null) settings.visited = new java.util.HashSet<>();
                if (settings.enabledQuestZones == null) settings.enabledQuestZones = new java.util.HashSet<>();
                return settings;
            } catch (SerializationException e) {
                Gdx.app.error("SettingsManager", "Error loading settings, creating new.", e);
                return new GameSettings();
            }
        } else {
            return new GameSettings();
        }
    }
}
