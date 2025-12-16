package com.mygame.ui.inGameUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygame.entity.player.Player;
import com.mygame.ui.UIFactory;

import java.util.HashMap;
import java.util.Map;

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
    private boolean settingsButtonJustPressed = false;
    private boolean pauseButtonJustPressed = false;
    private boolean mapButtonJustPressed = false;

    /**
     * Initializes touch controls and attaches them to corresponding stages.
     *
     * @param skin          Skin used for buttons
     * @param gameStage     Stage for in-game buttons and joystick
     * @param pauseStage    Stage for pause buttons
     * @param settingsStage Stage for settings buttons
     * @param player        Player to link the touchpad movement
     */
    public TouchControlsUI(Skin skin, Stage gameStage, Stage pauseStage, Stage settingsStage, Stage mapStage, Player player) {

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

        // Map stage names to Stage objects for the factory
        Map<String, Stage> stages = new HashMap<>();
        stages.put("gameStage", gameStage);
        stages.put("pauseStage", pauseStage);
        stages.put("settingsStage", settingsStage);
        stages.put("mapStage", mapStage);

        // Map action names to their specific listeners that set flags
        Map<String, InputListener> actionListeners = new HashMap<>();
        actionListeners.put("ACT", createFlagListener(() -> actButtonJustPressed = true));
        actionListeners.put("INVENTORY", createFlagListener(() -> invButtonJustPressed = true));
        actionListeners.put("QUESTS", createFlagListener(() -> questButtonJustPressed = true));
        actionListeners.put("TOGGLE_PAUSE", createFlagListener(() -> pauseButtonJustPressed = true));
        actionListeners.put("TOGGLE_SETTINGS", createFlagListener(() -> settingsButtonJustPressed = true));
        actionListeners.put("TOGGLE_MAP", createFlagListener(() -> mapButtonJustPressed = true));

        // Use the factory to create the buttons from the JSON file
        UIFactory.createButtonsFromJson(Gdx.files.internal("data/ui/touch_controls.json"), skin, stages, actionListeners);
    }

    private InputListener createFlagListener(Runnable flagSetter) {
        return new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                flagSetter.run();
                return true;
            }
        };
    }

    /** Returns if buttons were just pressed */
    public boolean isActButtonJustPressed() { return actButtonJustPressed; }
    public boolean isInvButtonJustPressed() { return invButtonJustPressed; }
    public boolean isQuestButtonJustPressed() { return questButtonJustPressed; }
    public boolean isSettingsButtonJustPressed() { return settingsButtonJustPressed; }
    public boolean isPauseButtonJustPressed() { return pauseButtonJustPressed; }
    public boolean isMapButtonJustPressed() { return mapButtonJustPressed; }

    /** Resets all "just pressed" flags */
    public void resetButtons() {
        actButtonJustPressed = false;
        invButtonJustPressed = false;
        questButtonJustPressed = false;
        settingsButtonJustPressed = false;
        pauseButtonJustPressed = false;
        mapButtonJustPressed = false;
    }

    /** Dispose textures when no longer needed */
    public void dispose() {
        if (knobTexture != null) knobTexture.dispose();
        if (bgTexture != null) bgTexture.dispose();
    }
}
