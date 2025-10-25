package com.mygame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.mygame.Main;
import com.mygame.Player;

public class TouchControlsUI {
    private Texture knobTexture;
    private Texture bgTexture;
    private TextButton startButton;

    private boolean actButtonJustPressed = false;

    public TouchControlsUI(Skin skin, Stage menuStage,Stage gameStage,Stage pauseStage, Player player, InventoryUI inventoryUI, QuestUI questUI) {
        Pixmap knobPixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
        knobPixmap.setColor(Color.WHITE);
        knobPixmap.fillCircle(25, 25, 25);
        knobTexture = new Texture(knobPixmap);
        knobPixmap.dispose();

        Pixmap bgPixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(new Color(0.3f, 0.3f, 0.3f, 0.5f));
        bgPixmap.fillCircle(50, 50, 50);
        bgTexture = new Texture(bgPixmap);
        bgPixmap.dispose();

        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));
        touchpadStyle.background = new TextureRegionDrawable(new TextureRegion(bgTexture));

        Touchpad touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setBounds(150, 150, 200, 200);
        gameStage.addActor(touchpad);
        player.touchpad = touchpad;

        // Кнопка взаємодії
        TextButton actButton = new TextButton("ACT", skin);
        actButton.setSize(150, 150);
        actButton.setPosition(1800, 150);
        actButton.getLabel().setFontScale(4f);
        actButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                actButtonJustPressed = true;
                return true;
            }
        });
        gameStage.addActor(actButton);

        // Кнопка інвентаря
        TextButton inventoryButton = new TextButton("INV", skin);
        inventoryButton.setSize(150, 150);
        inventoryButton.setPosition(1800, 325);
        inventoryButton.getLabel().setFontScale(3.5f);
        inventoryButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                inventoryUI.toggle(player);  // Тепер безпосередньо через InventoryUI
                return true;
            }
        });
        gameStage.addActor(inventoryButton);


        TextButton questButton = new TextButton("QUESTS", skin);
        questButton.setSize(200, 100); // ширина, висота
        questButton.setPosition(20, gameStage.getViewport().getWorldHeight() - 120); // лівий верхній кут
        questButton.getLabel().setFontScale(2f);

        questButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                questUI.toggle();
                return true;
            }
        });

        gameStage.addActor(questButton);

        // Кнопка паузи
        TextButton pauseButton = new TextButton("PAUSE", skin);
        pauseButton.setSize(150, 75);
        pauseButton.setPosition(1750,800);
        pauseButton.getLabel().setFontScale(2f);
        pauseButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Main.togglePause();
                return true;
            }
        });
        gameStage.addActor(pauseButton);

        startButton = new TextButton("START", skin);
        startButton.setSize(300, 150);
        startButton.setPosition(800,400);
        startButton.getLabel().setFontScale(3f);
        startButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Main.startGame();
                return true;
            }
        });
        menuStage.addActor(startButton);


        TextButton resumeButton = new TextButton("RESUME", skin);
        resumeButton.setSize(300, 150);
        resumeButton.setPosition(pauseStage.getViewport().getWorldWidth()/2 - 150,400);
        resumeButton.getLabel().setFontScale(3f);
        resumeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Main.togglePause();
                return true;
            }
        });
        pauseStage.addActor(resumeButton);
    }


    public boolean isActButtonJustPressed() {
        if (actButtonJustPressed) {
            actButtonJustPressed = false;
            return true;
        }
        return false;
    }

    public void dispose() {
        if (knobTexture != null) knobTexture.dispose();
        if (bgTexture != null) bgTexture.dispose();
    }
}
