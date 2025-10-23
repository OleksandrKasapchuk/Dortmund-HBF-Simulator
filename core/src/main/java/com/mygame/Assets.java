package com.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class Assets {

    // === Текстури ===
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
    public static Texture textureSpoon;
    public static Texture brick;
    public static Texture bush;

    // === Звуки ===
    public static Sound moneySound;
    public static Sound kosyakSound;
    public static Sound lighterSound;

    // === Завантаження ===
    public static void load() {
        // Текстури
        textureRyzhyi = new Texture(Gdx.files.internal("ryzhyi.png"));
        textureDenys = new Texture(Gdx.files.internal("denys.png"));
        textureIgo = new Texture(Gdx.files.internal("igo.png"));
        textureIgo2 = new Texture(Gdx.files.internal("igo2.png"));
        textureBaryga = new Texture(Gdx.files.internal("baryga.png"));
        textureChikita = new Texture(Gdx.files.internal("chikita.png"));
        texturePolice = new Texture(Gdx.files.internal("police.png"));
        textureKioskMan = new Texture(Gdx.files.internal("kioskman.png"));
        textureJunky = new Texture(Gdx.files.internal("junky.png"));
        textureZoe = new Texture(Gdx.files.internal("zoe.png"));
        textureSpoon = new Texture(Gdx.files.internal("spoon.png"));
        brick = new Texture(Gdx.files.internal("brick.png"));
        bush = new Texture(Gdx.files.internal("bush.png"));

        // Звуки
        moneySound = Gdx.audio.newSound(Gdx.files.internal("money.ogg"));
        kosyakSound = Gdx.audio.newSound(Gdx.files.internal("kosyak.wav"));
        lighterSound = Gdx.audio.newSound(Gdx.files.internal("lighter.ogg"));
    }

    // === Звільнення пам’яті ===
    public static void dispose() {
        // Текстури
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
        brick.dispose();
        bush.dispose();

        // Звуки
        moneySound.dispose();
        kosyakSound.dispose();
        lighterSound.dispose();
    }
}
