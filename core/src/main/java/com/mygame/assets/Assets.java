package com.mygame.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.assets.audio.MusicManager;
import com.mygame.assets.audio.SoundManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Assets {

    // Maps to hold all loaded assets, accessible by a string key.
    private static final Map<String, Texture> textureMap = new HashMap<>();
    private static final Map<String, Sound> soundMap = new HashMap<>();
    private static final Map<String, Music> musicMap = new HashMap<>();

    // === Internationalization ===
    public static I18NBundle bundle;
    private static Locale currentLocale;
    public static BitmapFont myFont;

    public static void load() {
        GameSettings settings = SettingsManager.load();
        currentLocale = new Locale(settings.language);
        MusicManager.setVolume(settings.musicVolume);
        SoundManager.setVolume(settings.soundVolume);
        if (settings.muteAll) {
            MusicManager.setVolume(0);
            SoundManager.setVolume(0);
        }

        loadBundle(currentLocale);

        // Font Generation
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ScienceGothic-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "АаБбВвГгҐґДдЕеЄєЖжЗзИиІіЇїЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЬьЮюЯя" + "ÄäÖöÜüß";
        params.size = 24;
        params.borderWidth = 1;
        params.borderColor = Color.BLACK;
        params.color = Color.WHITE;
        myFont = generator.generateFont(params);
        generator.dispose();

        // --- Asset Loading from JSON ---
        JsonReader json = new JsonReader();

        // Load Textures
        JsonValue textureBase = json.parse(Gdx.files.internal("data/assets.json"));
        for (JsonValue val = textureBase.child; val != null; val = val.next) {
            textureMap.put(val.getString("key"), new Texture(Gdx.files.internal(val.getString("path"))));
        }

        // Load Audio
        JsonValue audioBase = json.parse(Gdx.files.internal("data/audio.json"));
        for (JsonValue val = audioBase.child; val != null; val = val.next) {
            String type = val.getString("type");
            String key = val.getString("key");
            String path = val.getString("path");
            if ("SOUND".equals(type)) {
                soundMap.put(key, Gdx.audio.newSound(Gdx.files.internal(path)));
            } else if ("MUSIC".equals(type)) {
                musicMap.put(key, Gdx.audio.newMusic(Gdx.files.internal(path)));
            }
        }
    }

    public static void loadBundle(Locale locale) {
        currentLocale = locale;
        FileHandle baseFileHandle = Gdx.files.internal("i18n/strings");
        bundle = I18NBundle.createBundle(baseFileHandle, currentLocale, "UTF-8");
    }

    public static Texture getTexture(String key) {
        return textureMap.get(key);
    }

    public static Sound getSound(String key) {
        return soundMap.get(key);
    }

    public static Music getMusic(String key) {
        return musicMap.get(key);
    }

    public static void dispose() {
        myFont.dispose();

        for (Texture texture : textureMap.values()) {
            texture.dispose();
        }
        textureMap.clear();

        for (Sound sound : soundMap.values()) {
            sound.dispose();
        }
        soundMap.clear();

        for (Music music : musicMap.values()) {
            music.dispose();
        }
        musicMap.clear();
    }
}
