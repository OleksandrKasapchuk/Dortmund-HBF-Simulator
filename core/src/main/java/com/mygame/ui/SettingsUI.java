package com.mygame.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygame.MusicManager;
import com.mygame.SoundManager;

public class SettingsUI {
    private Stage stage;
    private Label settingsLabel1;
    private Slider musicVolumeSlider;
    private Label musicVolumeLabel;
    private Slider soundVolumeSlider;
    private Label soundVolumeLabel;

    public SettingsUI(Skin skin){
        stage = new Stage(new FitViewport(2000, 1000));
        settingsLabel1 = new Label("SETTINGS", skin);
        settingsLabel1.setPosition(800, 800);
        settingsLabel1.setFontScale(5f);
        stage.addActor(settingsLabel1);

        musicVolumeLabel = new Label("Music", skin);
        musicVolumeLabel.setPosition(50, 650);
        musicVolumeLabel.setFontScale(4f);
        stage.addActor(musicVolumeLabel);

        musicVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        musicVolumeSlider.setValue(MusicManager.getVolume());
        musicVolumeSlider.setPosition(250, 625);
        musicVolumeSlider.setSize(400, 50);
        musicVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MusicManager.setVolume(musicVolumeSlider.getValue());
            }
        });
        stage.addActor(musicVolumeSlider);

        soundVolumeLabel = new Label("Sounds", skin);
        soundVolumeLabel.setPosition(50, 550);
        soundVolumeLabel.setFontScale(4f);
        stage.addActor(soundVolumeLabel);

        soundVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
        soundVolumeSlider.setValue(SoundManager.getVolume());
        soundVolumeSlider.setPosition(250, 525);
        soundVolumeSlider.setSize(400, 50);
        soundVolumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.setVolume(soundVolumeSlider.getValue());
            }
        });
        stage.addActor(soundVolumeSlider);
    }

    public Stage getStage() { return stage; }
    public void dispose() {stage.dispose();}
}
