package com.mygame.ui.inGameUI;

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
import com.mygame.Assets;
import com.mygame.Main;
import com.mygame.entity.player.Player;

/**
 * TouchControlsUI handles on-screen touch controls for mobile devices.
 * It creates a virtual joystick (Touchpad) and buttons for actions, inventory, quests, pause, start, and settings.
 */
public class TouchControlsUI {

    private Texture knobTexture;
    private Texture bgTexture;


    // Flags to track one-time button presses
    private boolean actButtonJustPressed = false;
    private boolean invButtonJustPressed = false;
    private boolean questButtonJustPressed = false;

    /**
     * Initializes touch controls and attaches them to corresponding stages.
     *
     * @param skin          Skin used for buttons
     * @param menuStage     Stage for menu buttons
     * @param gameStage     Stage for in-game buttons and joystick
     * @param pauseStage    Stage for pause buttons
     * @param settingsStage Stage for settings buttons
     * @param player        Player to link the touchpad movement
     */
    public TouchControlsUI(Skin skin, Stage gameStage, Stage pauseStage, Stage settingsStage, Player player) {

        // Create textures for the joystick knob and background
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

        // Create and add Touchpad (joystick) to the game stage
        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));
        touchpadStyle.background = new TextureRegionDrawable(new TextureRegion(bgTexture));

        Touchpad touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setBounds(150, 150, 200, 200);
        gameStage.addActor(touchpad);
        player.touchpad = touchpad;

        // Create "ACT" button
        TextButton actButton = new TextButton(Assets.bundle.get("button.act.text"), skin);
        actButton.setSize(150, 150);
        actButton.setPosition(1800, 150);
        actButton.getLabel().setFontScale(2f);
        actButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                actButtonJustPressed = true;
                return true;
            }
        });
        gameStage.addActor(actButton);

        // Create "INVENTORY" button
        TextButton inventoryButton = new TextButton(Assets.bundle.get("button.inventory.text"), skin);
        inventoryButton.setSize(150, 150);
        inventoryButton.setPosition(1800, 325);
        inventoryButton.getLabel().setFontScale(2f);
        inventoryButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                invButtonJustPressed = true;
                return true;
            }
        });
        gameStage.addActor(inventoryButton);

        // Create "QUESTS" button
        TextButton questButton = new TextButton(Assets.bundle.get("button.quests.text"), skin);
        questButton.setSize(200, 100);
        questButton.setPosition(20, gameStage.getViewport().getWorldHeight() - 300);
        questButton.getLabel().setFontScale(1f);
        questButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                questButtonJustPressed = true;
                return true;
            }
        });
        gameStage.addActor(questButton);

        // Create "PAUSE" button
        TextButton pauseButton = new TextButton(Assets.bundle.get("button.pause.text"), skin);
        pauseButton.setSize(150, 75);
        pauseButton.setPosition(1750, 800);
        pauseButton.getLabel().setFontScale(1f);
        pauseButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Main.getGameInitializer().getManagerRegistry().getGameStateManager().togglePause();
                return true;
            }
        });
        gameStage.addActor(pauseButton);


        // Create "RESUME" button for pause stage
        TextButton resumeButton = new TextButton(Assets.bundle.get("button.resume.text"), skin);
        resumeButton.setSize(300, 150);
        resumeButton.setPosition(pauseStage.getViewport().getWorldWidth()/2 - 150, 250);
        resumeButton.getLabel().setFontScale(1f);
        resumeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Main.getGameInitializer().getManagerRegistry().getGameStateManager().togglePause();
                return true;
            }
        });
        pauseStage.addActor(resumeButton);

        // Create "SETTINGS" button
        TextButton settingsButton = new TextButton(Assets.bundle.get("button.settings.text"), skin);
        settingsButton.setSize(200, 100);
        settingsButton.setPosition(20, 850);
        settingsButton.getLabel().setFontScale(0.85f);
        settingsButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Main.getGameInitializer().getManagerRegistry().getGameStateManager().toggleSettings();
                return true;
            }
        });
        gameStage.addActor(settingsButton);

        // Create "BACK" button in settings
        TextButton backButton = new TextButton(Assets.bundle.get("button.back.text"), skin);
        backButton.setSize(200, 100);
        backButton.setPosition(20, 850);
        backButton.getLabel().setFontScale(1f);
        backButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Main.getGameInitializer().getManagerRegistry().getGameStateManager().toggleSettings();
                return true;
            }
        });
        settingsStage.addActor(backButton);
    }

    /** Returns if "ACT" button was just pressed */
    public boolean isActButtonJustPressed() { return actButtonJustPressed; }

    /** Returns if "INVENTORY" button was just pressed */
    public boolean isInvButtonJustPressed() { return invButtonJustPressed; }

    /** Returns if "QUESTS" button was just pressed */
    public boolean isQuestButtonJustPressed() { return questButtonJustPressed; }

    /** Resets all "just pressed" flags */
    public void resetButtons() {
        actButtonJustPressed = false;
        invButtonJustPressed = false;
        questButtonJustPressed = false;
    }

    /** Dispose textures when no longer needed */
    public void dispose() {
        if (knobTexture != null) knobTexture.dispose();
        if (bgTexture != null) bgTexture.dispose();
    }
}
