package com.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
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
    public static Texture textureBoss;
    public static Texture textureKamil;
    public static Texture textureSpoon;
    public static Texture brick;
    public static Texture bush;
    public static Texture pfand;
    public static Texture pfandAutomat;
    public static Texture deathBack;
    public static Texture menuBack;
    public static Texture menuBlurBack;

    // === Звуки ===
    public static Sound moneySound;
    public static Sound kosyakSound;
    public static Sound lighterSound;
    public static Sound bushSound;
    public static Sound gunShot;
    public static Sound pfandAutomatSound;

    // Music
    public static Music startMusic;
    public static Music backMusic1;
    public static Music backMusic2;
    public static Music backMusic4;
    public static Music kaifMusic;

    // === Завантаження ===
    public static void load() {
        // Текстури
        textureRyzhyi = new Texture(Gdx.files.internal("images/ryzhyi.png"));
        textureDenys = new Texture(Gdx.files.internal("images/denys.png"));
        textureIgo = new Texture(Gdx.files.internal("images/igo.png"));
        textureIgo2 = new Texture(Gdx.files.internal("images/igo2.png"));
        textureBaryga = new Texture(Gdx.files.internal("images/baryga.png"));
        textureChikita = new Texture(Gdx.files.internal("images/chikita.png"));
        texturePolice = new Texture(Gdx.files.internal("images/police.png"));
        textureKioskMan = new Texture(Gdx.files.internal("images/kioskman.png"));
        textureJunky = new Texture(Gdx.files.internal("images/junky.png"));
        textureZoe = new Texture(Gdx.files.internal("images/zoe.png"));
        textureBoss = new Texture(Gdx.files.internal("images/boss.png"));
        textureKamil = new Texture(Gdx.files.internal("images/kamil.png"));


        deathBack = new Texture(Gdx.files.internal("images/deathScreen.jpg"));
        menuBack = new Texture("images/menu.jpg");
        menuBlurBack = new Texture("images/menublur.jpg");

        textureSpoon = new Texture(Gdx.files.internal("images/spoon.png"));
        brick = new Texture(Gdx.files.internal("images/brick.png"));
        bush = new Texture(Gdx.files.internal("images/bush.png"));
        pfand= new Texture(Gdx.files.internal("images/pfand.png"));
        pfandAutomat = new Texture(Gdx.files.internal("images/pfand_automat.png"));

        // Звуки
        moneySound = Gdx.audio.newSound(Gdx.files.internal("sound/money.ogg"));
        kosyakSound = Gdx.audio.newSound(Gdx.files.internal("sound/kosyak.wav"));
        lighterSound = Gdx.audio.newSound(Gdx.files.internal("sound/lighter.ogg"));
        bushSound = Gdx.audio.newSound(Gdx.files.internal("sound/bush.ogg"));
        gunShot = Gdx.audio.newSound(Gdx.files.internal("sound/gunshots.ogg"));
        pfandAutomatSound = Gdx.audio.newSound(Gdx.files.internal("sound/pfand_automat.ogg"));

        // Фонова музика
        startMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/menu.ogg"));
        backMusic1 = Gdx.audio.newMusic(Gdx.files.internal("sound/epic_back1.ogg"));
        backMusic2 = Gdx.audio.newMusic(Gdx.files.internal("sound/epic_back2.ogg"));
        backMusic4 = Gdx.audio.newMusic(Gdx.files.internal("sound/norm.ogg"));
        kaifMusic = Gdx.audio.newMusic(Gdx.files.internal("sound/kaif.ogg"));
    }

    public static void dispose() {
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
        brick.dispose();
        bush.dispose();
        pfand.dispose();
        deathBack.dispose();
        menuBack.dispose();
        menuBlurBack.dispose();

        // Звуки
        moneySound.dispose();
        kosyakSound.dispose();
        lighterSound.dispose();
        bushSound.dispose();
        gunShot.dispose();
        pfandAutomatSound.dispose();

        // Музика
        startMusic.dispose();
        backMusic1.dispose();
        backMusic2.dispose();
        backMusic4.dispose();
        kaifMusic.dispose();
    }
}
