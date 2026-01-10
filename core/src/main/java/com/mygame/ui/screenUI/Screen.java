package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Адаптивний базовий клас для всіх екранів.
 */
public abstract class Screen {
    protected final Stage stage = new Stage(new FitViewport(2000, 1000));
    protected final Table root = new Table();

    public Screen() {
        root.setFillParent(true);
        stage.addActor(root);
    }

    public Stage getStage() { return stage; }
    public void dispose() { stage.dispose(); }

    // Універсальні методи створення елементів для Table
    public TextButton createButton(Skin skin, String text, float scale, Runnable action) {
        TextButton button = new TextButton(text, skin);
        button.getLabel().setFontScale(scale);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) { action.run(); }
        });
        return button;
    }

    public Label createLabel(Skin skin, String text, float scale) {
        Label label = new Label(text, skin);
        label.setFontScale(scale);
        return label;
    }

    public Slider createSlider(Skin skin, float value, Runnable action) {
        Slider slider = new Slider(0f, 1f, 0.01f, false, skin);
        slider.setValue(value);
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) { action.run(); }
        });
        return slider;
    }
}
