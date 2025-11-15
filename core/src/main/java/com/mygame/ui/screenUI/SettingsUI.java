package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygame.Assets;
import com.mygame.managers.audio.MusicManager;
import com.mygame.managers.audio.SoundManager;

/**
 * Settings UI screen for adjusting music and sound volumes and muting all audio.
 */
public class SettingsUI extends Screen {
    private Label settingsLabel1;
    private Slider musicVolumeSlider;
    private Label musicVolumeLabel;
    private Slider soundVolumeSlider;
    private Label soundVolumeLabel;
    private CheckBox muteAllCheckbox;
    private float lastMusicVolume;
    private float lastSoundVolume;
    private Image backgroundImage;

    public SettingsUI(Skin skin) {
        Stage stage = getStage();

        // Background image
        backgroundImage = new Image(Assets.menuBlurBack);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Settings title
        settingsLabel1 = new Label("SETTINGS", skin);
        settingsLabel1.setPosition(800, 800);
        settingsLabel1.setFontScale(5f);
        stage.addActor(settingsLabel1);

        // Music volume label
        musicVolumeLabel = new Label("Music", skin);
        musicVolumeLabel.setPosition(50, 650);
        musicVolumeLabel.setFontScale(4f);
        stage.addActor(musicVolumeLabel);

        // Music volume slider
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

        // Sound volume label
        soundVolumeLabel = new Label("Sounds", skin);
        soundVolumeLabel.setPosition(50, 550);
        soundVolumeLabel.setFontScale(4f);
        stage.addActor(soundVolumeLabel);

        // Sound volume slider
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

        // Mute all checkbox
        muteAllCheckbox = new CheckBox(" Mute All", skin);
        muteAllCheckbox.setPosition(50, 425);
        muteAllCheckbox.getLabel().setFontScale(4f);
        muteAllCheckbox.getImageCell().size(80, 80);
        muteAllCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (muteAllCheckbox.isChecked()) {
                    // Save current volumes and mute everything
                    lastMusicVolume = musicVolumeSlider.getValue();
                    lastSoundVolume = soundVolumeSlider.getValue();
                    musicVolumeSlider.setValue(0f);
                    soundVolumeSlider.setValue(0f);
                } else {
                    // Restore previous volumes
                    musicVolumeSlider.setValue(lastMusicVolume);
                    soundVolumeSlider.setValue(lastSoundVolume);
                }
            }
        });
        stage.addActor(muteAllCheckbox);
    }
}
