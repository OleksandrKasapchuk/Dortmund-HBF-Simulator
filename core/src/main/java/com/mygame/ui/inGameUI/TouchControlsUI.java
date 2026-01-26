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
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.ui.load.UIFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * TouchControlsUI handles on-screen touch controls for mobile devices.
 */
public class TouchControlsUI {

    private Texture knobTexture;
    private Texture bgTexture;

    public TouchControlsUI(Skin skin, Stage gameStage, Stage pauseStage, Stage settingsStage, Stage mapStage, Player player) {

        createJoystickTextures();

        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));
        touchpadStyle.background = new TextureRegionDrawable(new TextureRegion(bgTexture));

        Touchpad touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setBounds(150, 150, 200, 200);
        gameStage.addActor(touchpad);
        player.touchpad = touchpad;

        Map<String, Stage> stages = new HashMap<>();
        stages.put("gameStage", gameStage);
        stages.put("pauseStage", pauseStage);
        stages.put("settingsStage", settingsStage);
        stages.put("mapStage", mapStage);

        Map<String, InputListener> actionListeners = new HashMap<>();

        // Всі кнопки тепер відправляють івенти
        actionListeners.put("ACT", createListener(() -> EventBus.fire(new Events.InteractEvent())));
        actionListeners.put("INVENTORY", createListener(() -> EventBus.fire(new Events.ActionRequestEvent("ui.inventory.toggle"))));
        actionListeners.put("QUESTS", createListener(() -> EventBus.fire(new Events.ActionRequestEvent("ui.quests.toggle"))));
        actionListeners.put("TOGGLE_PAUSE", createListener(() -> EventBus.fire(new Events.ActionRequestEvent("act.system.pause"))));
        actionListeners.put("TOGGLE_SETTINGS", createListener(() -> EventBus.fire(new Events.ActionRequestEvent("act.system.settings"))));
        actionListeners.put("TOGGLE_MAP", createListener(() -> EventBus.fire(new Events.ActionRequestEvent("act.system.map"))));

        UIFactory.createButtonsFromJson(Gdx.files.internal("data/ui/touch_controls.json"), skin, stages, actionListeners);
    }

    private void createJoystickTextures() {
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
    }

    private InputListener createListener(Runnable action) {
        return new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                action.run();
                return true;
            }
        };
    }

    public void dispose() {
        if (knobTexture != null) knobTexture.dispose();
        if (bgTexture != null) bgTexture.dispose();
    }
}
