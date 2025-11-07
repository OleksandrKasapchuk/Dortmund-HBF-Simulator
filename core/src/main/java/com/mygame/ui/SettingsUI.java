package com.mygame.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygame.MusicManager;

public class SettingsUI {
    private Stage stage;
    private Label settingsLabel1;
    private Slider volumeSlider;
    private Label volumeLabel;

    public SettingsUI(Skin skin){
        stage = new Stage(new FitViewport(2000, 1000));
        settingsLabel1 = new Label("SETTINGS", skin);
        settingsLabel1.setPosition(800, 800);
        settingsLabel1.setFontScale(4f);
        stage.addActor(settingsLabel1);

        volumeLabel = new Label("Volume", skin);
        volumeLabel.setPosition(100, 650);
        volumeLabel.setFontScale(3f);
        stage.addActor(volumeLabel);

        volumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        volumeSlider.setValue(MusicManager.getVolume());
        volumeSlider.setPosition(300, 650);
        volumeSlider.setSize(300, 50);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MusicManager.setVolume(volumeSlider.getValue());
            }
        });
        stage.addActor(volumeSlider);
    }

    public Stage getStage() { return stage; }
    public void dispose() {stage.dispose();}
}
