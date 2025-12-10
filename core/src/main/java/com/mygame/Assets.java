package com.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.I18NBundle;
import com.mygame.managers.global.save.GameSettings;
import com.mygame.managers.global.save.SettingsManager;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.managers.global.audio.SoundManager;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Assets {

    // A map to hold all loaded textures, accessible by a string key.
    private static final Map<String, Texture> textureMap = new HashMap<>();

    // === Internationalization ===
    public static I18NBundle bundle;
    private static Locale currentLocale;
    public static BitmapFont myFont;

    // === Textures (Kept for backward compatibility) ===
    public static Texture textureRyzhyi, textureDenys, textureIgo, textureIgo2, textureBaryga, textureChikita, texturePolice, textureKioskMan, textureJunky, textureZoe, textureBoss, textureKamil, textureJan, textureFilip, textureJason, textureTalahon1, textureTalahon2, textureGrandpa, textureTurkish, textureNigga, textureRussian;
    public static Texture textureSpoon, pfand, pfandAutomat;
    public static Texture bush;
    public static Texture deathBack, menuBack, menuBlurBack;

    // === SOUND & MUSIC ===
    public static Sound moneySound, kosyakSound, lighterSound, bushSound, gunShot, pfandAutomatSound;
    public static Music startMusic, backMusic1, backMusic2, backMusic4, kaifMusic;

    private static Texture loadAndStore(String key, String internalPath) {
        Texture texture = new Texture(Gdx.files.internal(internalPath));
        textureMap.put(key, texture);
        return texture;
    }

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

        // --- Texture Loading ---
        // The loadAndStore method automatically puts them into our new map.
        textureRyzhyi = loadAndStore("ryzhyi", "images/npc/ryzhyi.png");
        textureDenys = loadAndStore("denys", "images/npc/denys.png");
        textureIgo = loadAndStore("igo", "images/npc/igo.png");
        textureIgo2 = loadAndStore("igo2", "images/npc/igo2.png");
        textureBaryga = loadAndStore("baryga", "images/npc/baryga.png");
        textureChikita = loadAndStore("chikita", "images/npc/chikita.png");
        texturePolice = loadAndStore("police", "images/npc/police.png");
        textureKioskMan = loadAndStore("kioskman", "images/npc/kioskman.png");
        textureJunky = loadAndStore("junky", "images/npc/junky.png");
        textureZoe = loadAndStore("zoe", "images/npc/zoe.png");
        textureBoss = loadAndStore("boss", "images/npc/boss.png");
        textureKamil = loadAndStore("kamil", "images/npc/kamil.png");
        textureJan = loadAndStore("jan", "images/npc/jan.png");
        textureFilip = loadAndStore("filip", "images/npc/filip.png");
        textureJason = loadAndStore("jason", "images/npc/jason.png");
        textureTalahon1 = loadAndStore("talahon1", "images/npc/talahon1.png");
        textureTalahon2 = loadAndStore("talahon2", "images/npc/talahon2.png");
        textureGrandpa = loadAndStore("walter", "images/npc/walter.png");
        textureTurkish = loadAndStore("murat", "images/npc/murat.png");
        textureNigga = loadAndStore("jamal", "images/npc/jamal.png");
        textureRussian = loadAndStore("dmitri", "images/npc/dmitri.png");

        deathBack = loadAndStore("deathBack", "images/background/deathScreen.jpg");
        menuBack = loadAndStore("menuBack", "images/background/menu.jpg");
        menuBlurBack = loadAndStore("menuBlurBack", "images/background/menublur.jpg");

        textureSpoon = loadAndStore("spoon", "images/item/spoon.png");
        pfand = loadAndStore("pfand", "images/item/pfand.png");
        pfandAutomat = loadAndStore("pfandAutomat", "images/item/pfand_automat.png");

        bush = loadAndStore("bush", "images/block/bush.jpg");

        // === SOUNDS & MUSIC ===
        moneySound = Gdx.audio.newSound(Gdx.files.internal("sound/money.ogg"));
        kosyakSound = Gdx.audio.newSound(Gdx.files.internal("sound/kosyak.ogg"));
        lighterSound = Gdx.audio.newSound(Gdx.files.internal("sound/lighter.ogg"));
        bushSound = Gdx.audio.newSound(Gdx.files.internal("sound/bush.ogg"));
        gunShot = Gdx.audio.newSound(Gdx.files.internal("sound/gunshots.ogg"));
        pfandAutomatSound = Gdx.audio.newSound(Gdx.files.internal("sound/pfand_automat.ogg"));
        startMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/menu.ogg"));
        backMusic1 = Gdx.audio.newMusic(Gdx.files.internal("sound/epic_back1.ogg"));
        backMusic4 = Gdx.audio.newMusic(Gdx.files.internal("sound/norm.ogg"));
        kaifMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/kaif2.ogg"));
    }

    public static void loadBundle(Locale locale) {
        currentLocale = locale;
        FileHandle baseFileHandle = Gdx.files.internal("i18n/strings");
        bundle = I18NBundle.createBundle(baseFileHandle, currentLocale, "UTF-8");
    }

    /**
     * Returns a loaded texture from the central texture map by its key.
     * @param key The key of the texture (e.g., "ryzhyi", "bush", "pfandAutomat").
     * @return The Texture, or null if not found.
     */
    public static Texture getTexture(String key) {
        return textureMap.get(key);
    }

    public static void dispose() {
        myFont.dispose();

        // Dispose all textures from the map
        for (Texture texture : textureMap.values()) {
            texture.dispose();
        }
        textureMap.clear();

        // Sounds
        moneySound.dispose();
        kosyakSound.dispose();
        lighterSound.dispose();
        bushSound.dispose();
        gunShot.dispose();
        pfandAutomatSound.dispose();

        // Music
        startMusic.dispose();
        backMusic1.dispose();
        backMusic2.dispose();
        backMusic4.dispose();
        kaifMusic.dispose();
    }
}
