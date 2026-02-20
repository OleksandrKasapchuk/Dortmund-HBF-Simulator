package com.mygame.game.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.mygame.game.save.data.ClientSaveData;
import com.mygame.game.save.data.ServerSaveData;

public class SettingsManager {

    private static final String CLIENT_SETTINGS_FILE = "assets/data/saving/client.json";
    public static final Json json = new Json();

    private static ServerSaveData cachedServer;
    private static ClientSaveData cachedClient;

    static {
        json.setOutputType(JsonWriter.OutputType.json);
        json.setIgnoreUnknownFields(true);
    }

    // --- Save ---
    public static void saveClient(ClientSaveData settings) {
        save(CLIENT_SETTINGS_FILE, settings);
        cachedClient = settings; // оновлюємо кеш
    }

    /**
     * Зберігає дані сервера ТІЛЬКИ в оперативній пам'яті (в кеші) на час сесії.
     * Більше не записує нічого в локальний файл.
     */
    public static void saveServer(ServerSaveData settings) {
        cachedServer = settings;
    }

    // Цей метод тепер використовується тільки для клієнтських налаштувань
    public static <T> void save(String fileName, T settings) {
        FileHandle file = Gdx.files.local(fileName);
        try {
            file.writeString(json.prettyPrint(settings), false);
        } catch (Exception e) {
            Gdx.app.error("SettingsManager", "Error saving settings", e);
        }
    }

    // --- Load ---
    public static ClientSaveData loadClient() {
        if (cachedClient != null) return cachedClient;
        cachedClient = load(CLIENT_SETTINGS_FILE, ClientSaveData.class, new ClientSaveData());
        return cachedClient;
    }

    /**
     * Завантажує дані сервера ТІЛЬКИ з кешу в оперативній пам'яті.
     * Дані в кеш потрапляють після успішного завантаження з бекенду.
     */
    public static ServerSaveData loadServer() {
        if (cachedServer != null) {
            return cachedServer;
        }
        // Повертаємо пустий об'єкт, якщо в кеші нічого немає
        return new ServerSaveData();
    }

    public static <T> T load(String fileName, Class<T> type, T defaultValue) {
        FileHandle file = Gdx.files.local(fileName);
        if (!file.exists()) return defaultValue;
        try {
            return json.fromJson(type, file);
        } catch (Exception e) {
            Gdx.app.error("SettingsManager", "Failed to load " + fileName, e);
            return defaultValue;
        }
    }

    /**
     * Очищує кеш серверних даних. Має викликатися при виході з акаунта.
     */
    public static void clearServerCache() {
        cachedServer = null;
    }

    /**
     * Цей метод тепер просто скидає кеш до дефолтних значень.
     */
    public static void resetSettings(){
        saveServer(new ServerSaveData());
    }
}
