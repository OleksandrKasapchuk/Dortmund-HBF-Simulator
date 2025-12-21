package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Abstract base class for all UI screens.
 * Provides a Stage with a FitViewport for UI rendering and management.
 */
public abstract class Screen {
    // Stage used to hold all UI actors for this screen
    private Stage stage = new Stage(new FitViewport(2000, 1000));

    /**
     * Returns the Stage associated with this screen.
     * @return the Stage
     */
    public Stage getStage() {
        return stage;
    }


    public void dispose() {
        stage.dispose();
    }

    public TextButton createButton(Skin skin,  String label, float scale, Runnable action) {
        TextButton button = new TextButton(label, skin);
        button.getLabel().setFontScale(scale);

        button.addListener(new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            action.run();
        }});
    return button;
    }

    public TextButton createButton(Skin skin,  String label, float scale, int width, int height, int x, int y, Runnable action) {
        TextButton button = new TextButton(label, skin);
        button.getLabel().setFontScale(scale);

        button.setSize(width, height);
        button.setPosition(x, y);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }});
        return button;
    }

    public Label createLabel(Skin skin, String text, float scale, float x, float y) {
        Label label = new Label(text, skin);
        label.setFontScale(scale);
        label.setPosition(x, y);

        stage.addActor(label);
        return label;
    }

    public Slider createSlider(Skin skin, int width, int height, int x, int y, float value, Runnable action){
        Slider slider = new Slider(0f, 1f, 0.1f, false, skin);
        slider.setValue(value);
        slider.setPosition(x, y);
        slider.setSize(width, height);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                action.run();
            }
        });
        stage.addActor(slider);
        return slider;
    }

}
