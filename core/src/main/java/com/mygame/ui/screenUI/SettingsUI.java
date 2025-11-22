package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygame.Assets;
import com.mygame.Main;
import com.mygame.game.GameSettings;
import com.mygame.game.SettingsManager;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.managers.global.audio.SoundManager;

import java.util.Locale;

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
        settingsLabel1 = new Label(Assets.bundle.get("settings.title"), skin);
        settingsLabel1.setPosition(800, 800);
        settingsLabel1.setFontScale(2.5f);
        stage.addActor(settingsLabel1);

        // Music volume label
        musicVolumeLabel = new Label(Assets.bundle.get("settings.music"), skin);
        musicVolumeLabel.setPosition(50, 650);
        musicVolumeLabel.setFontScale(2f);
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
        soundVolumeLabel = new Label(Assets.bundle.get("settings.sounds"), skin);
        soundVolumeLabel.setPosition(50, 550);
        soundVolumeLabel.setFontScale(2f);
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
        muteAllCheckbox = new CheckBox(Assets.bundle.get("settings.muteAll"), skin);
        muteAllCheckbox.setPosition(50, 425);
        muteAllCheckbox.getLabel().setFontScale(2f);
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

        // --- Language Selection ---
        Table langTable = new Table();
        langTable.setPosition(1300, 550);

        TextButton englishButton = new TextButton("English", skin);
        englishButton.getLabel().setFontScale(2f);
        englishButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameSettings settings = SettingsManager.load();
                settings.language = "en";
                SettingsManager.save(settings);
                Assets.loadBundle(new Locale("en"));
                Main.restartGame();
            }
        });

        TextButton ukrainianButton = new TextButton("Українська", skin);
        ukrainianButton.getLabel().setFontScale(2f);
        ukrainianButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameSettings settings = SettingsManager.load();
                settings.language = "ua";
                SettingsManager.save(settings);
                Assets.loadBundle(new Locale("ua"));
                Main.restartGame();
            }
        });

        TextButton germanButton = new TextButton("Deutsch", skin);
        germanButton.getLabel().setFontScale(2f);
        germanButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameSettings settings = SettingsManager.load();
                settings.language = "de";
                SettingsManager.save(settings);
                Assets.loadBundle(new Locale("de"));
                Main.restartGame();
            }
        });

        langTable.add(englishButton).width(350).height(90).padBottom(20).row();
        langTable.add(ukrainianButton).width(350).height(90).padBottom(20).row();
        langTable.add(germanButton).width(350).height(90).row();
        stage.addActor(langTable);
    }
}
