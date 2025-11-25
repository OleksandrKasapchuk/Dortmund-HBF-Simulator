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
import com.mygame.game.GameSettings;
import com.mygame.game.SettingsManager;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.managers.global.audio.SoundManager;

import java.util.Locale;

/**
 * Static class that manages all game assets: textures, sounds, and music.
 * Provides methods for loading and disposing resources to prevent memory leaks.

 * Usage:
 * - Call Assets.load() at the start of the game to load all assets.
 * - Call Assets.dispose() when exiting the game to release resources.
 */
public class Assets {

    // === Internationalization ===
    public static I18NBundle bundle;
    private static Locale currentLocale; // Keep track of the current locale
    public static BitmapFont myFont;

    // === Textures ===

    // === NPC ===
    public static Texture textureRyzhyi;
    public static Texture textureDenys;
    public static Texture textureIgo;
    public static Texture textureIgo2;
    public static Texture textureBaryga;
    public static Texture textureChikita;
    public static Texture texturePolice;
    public static Texture textureKioskMan;
    public static Texture textureJunky;
    public static Texture textureZoe;
    public static Texture textureBoss;
    public static Texture textureKamil;
    public static Texture textureJan;
    public static Texture textureFilip;

    //  === ITEM ===
    public static Texture textureSpoon;
    public static Texture pfand;
    public static Texture pfandAutomat;

    // === BLOCK ===
    public static Texture bush;
    public static Texture rock;

    // === BACKGROUND ===
    public static Texture deathBack;
    public static Texture menuBack;
    public static Texture menuBlurBack;

    // === SOUND ===
    public static Sound moneySound;
    public static Sound kosyakSound;
    public static Sound lighterSound;
    public static Sound bushSound;
    public static Sound gunShot;
    public static Sound pfandAutomatSound;

    // === MUSIC ===
    public static Music startMusic;
    public static Music backMusic1;
    public static Music backMusic2;
    public static Music backMusic4;
    public static Music kaifMusic;

    /**
     * Loads all assets from the file system into memory.
     * Should be called once at the start of the game.
     */
    public static void load() {
        // Load settings and apply them
        GameSettings settings = SettingsManager.load();
        currentLocale = new Locale(settings.language);
        MusicManager.setVolume(settings.musicVolume);
        SoundManager.setVolume(settings.soundVolume);
        if (settings.muteAll) {
            MusicManager.setVolume(0);
            SoundManager.setVolume(0);
        }

        loadBundle(currentLocale);

        // --- Correct Font Generation with full Ukrainian Alphabet ---
        FreeTypeFontGenerator generator =
            new FreeTypeFontGenerator(Gdx.files.internal("fonts/ScienceGothic-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter params =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        // Add all default characters + the full Ukrainian alphabet
        params.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "АаБбВвГгҐґДдЕеЄєЖжЗзИиІіЇїЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЬьЮюЯя" + "ÄäÖöÜüß";
        params.size = 24; // You can adjust size
        params.borderWidth = 1; // Optional: for outline
        params.borderColor = Color.BLACK;
        params.color = Color.WHITE;

        myFont = generator.generateFont(params);
        generator.dispose();
        // --- End of Font Generation ---

        // Textures

        // === NPC ===
        textureRyzhyi = new Texture(Gdx.files.internal("images/npc/ryzhyi.png"));
        textureDenys = new Texture(Gdx.files.internal("images/npc/denys.png"));
        textureIgo = new Texture(Gdx.files.internal("images/npc/igo.png"));
        textureIgo2 = new Texture(Gdx.files.internal("images/npc/igo2.png"));
        textureBaryga = new Texture(Gdx.files.internal("images/npc/baryga.png"));
        textureChikita = new Texture(Gdx.files.internal("images/npc/chikita.png"));
        texturePolice = new Texture(Gdx.files.internal("images/npc/police.png"));
        textureKioskMan = new Texture(Gdx.files.internal("images/npc/kioskman.png"));
        textureJunky = new Texture(Gdx.files.internal("images/npc/junky.png"));
        textureZoe = new Texture(Gdx.files.internal("images/npc/zoe.png"));
        textureBoss = new Texture(Gdx.files.internal("images/npc/boss.png"));
        textureKamil = new Texture(Gdx.files.internal("images/npc/kamil.png"));

        textureJan = new Texture(Gdx.files.internal("images/npc/jan.png"));
        textureFilip = new Texture(Gdx.files.internal("images/npc/filip.png"));

        // === BACKGROUND ===
        deathBack = new Texture(Gdx.files.internal("images/background/deathScreen.jpg"));
        menuBack = new Texture("images/background/menu.jpg");
        menuBlurBack = new Texture("images/background/menublur.jpg");

        // === ITEM ===
        textureSpoon = new Texture(Gdx.files.internal("images/item/spoon.png"));
        pfand = new Texture(Gdx.files.internal("images/item/pfand.png"));
        pfandAutomat = new Texture(Gdx.files.internal("images/item/pfand_automat.png"));

        // === BLOCK ===
        bush = new Texture(Gdx.files.internal("images/block/bush.jpg"));
        rock = new Texture(Gdx.files.internal("images/block/rock.jpg"));


        // === SOUND ===
        moneySound = Gdx.audio.newSound(Gdx.files.internal("sound/money.ogg"));
        kosyakSound = Gdx.audio.newSound(Gdx.files.internal("sound/kosyak.wav"));
        lighterSound = Gdx.audio.newSound(Gdx.files.internal("sound/lighter.ogg"));
        bushSound = Gdx.audio.newSound(Gdx.files.internal("sound/bush.ogg"));
        gunShot = Gdx.audio.newSound(Gdx.files.internal("sound/gunshots.ogg"));
        pfandAutomatSound = Gdx.audio.newSound(Gdx.files.internal("sound/pfand_automat.ogg"));

        // === MUSIC ===
        startMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/menu.ogg"));
        backMusic1 = Gdx.audio.newMusic(Gdx.files.internal("sound/epic_back1.ogg"));
        backMusic2 = Gdx.audio.newMusic(Gdx.files.internal("sound/epic_back2.ogg"));
        backMusic4 = Gdx.audio.newMusic(Gdx.files.internal("sound/norm.ogg"));
        kaifMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/kaif.ogg"));
    }

    /**
     * Loads the I18NBundle for a specific locale and sets it as the current one.
     * @param locale The locale to load (e.g., new Locale("en"), new Locale("ua")).
     */
    public static void loadBundle(Locale locale) {
        currentLocale = locale;
        FileHandle baseFileHandle = Gdx.files.internal("i18n/strings");
        bundle = I18NBundle.createBundle(baseFileHandle, currentLocale, "UTF-8");
    }

    /**
     * Disposes all loaded assets from memory.
     * Should be called when the game exits to free resources.
     */
    public static void dispose() {
        myFont.dispose();
        // Textures
        textureRyzhyi.dispose();
        textureDenys.dispose();
        textureIgo.dispose();
        textureIgo2.dispose();
        textureBaryga.dispose();
        textureChikita.dispose();
        texturePolice.dispose();
        textureKioskMan.dispose();
        textureJunky.dispose();
        textureZoe.dispose();
        textureSpoon.dispose();
        textureBoss.dispose();
        textureKamil.dispose();
        bush.dispose();
        pfand.dispose();
        pfandAutomat.dispose();
        deathBack.dispose();
        menuBack.dispose();
        menuBlurBack.dispose();

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
