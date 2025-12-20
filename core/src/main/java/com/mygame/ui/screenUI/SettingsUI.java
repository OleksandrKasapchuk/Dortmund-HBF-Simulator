package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygame.assets.Assets;
import com.mygame.Main;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.assets.audio.MusicManager;
import com.mygame.assets.audio.SoundManager;

import java.util.Locale;

/**
 * Settings UI screen for adjusting music and sound volumes and muting all audio.
 */
public class SettingsUI extends Screen {
    private Slider musicVolumeSlider;
    private Slider soundVolumeSlider;
    private CheckBox muteAllCheckbox;
    private float lastMusicVolume;
    private float lastSoundVolume;
    private Image backgroundImage;

    public SettingsUI(Skin skin) {
        Stage stage = getStage();

        // Background image
        backgroundImage = new Image(Assets.getTexture("menuBlurBack"));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Settings title
        createLabel(skin, Assets.bundle.get("settings.title"), 2.5f,800, 800);

        // Music volume label
        createLabel(skin, Assets.bundle.get("settings.music"), 2f,50, 650);

        // Sound volume label
        createLabel(skin, Assets.bundle.get("settings.sounds"), 2f,50, 550);

        // Music volume slider
        musicVolumeSlider = createSlider(skin,400, 50, 250, 625, MusicManager.getVolume(), () -> MusicManager.setVolume(musicVolumeSlider.getValue()));

        // Sound volume slider
        soundVolumeSlider = createSlider(skin,400, 50, 250, 525, SoundManager.getVolume(), () -> SoundManager.setVolume(soundVolumeSlider.getValue()));

        // Mute all checkbox
        muteAllCheckbox = new CheckBox(Assets.bundle.get("settings.muteAll"), skin);
        muteAllCheckbox.setPosition(50, 425);
        muteAllCheckbox.getLabel().setFontScale(2f);
        muteAllCheckbox.getImageCell().size(80, 80);
        muteAllCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean isMuted = muteAllCheckbox.isChecked();
                if (isMuted) {
                    lastMusicVolume = musicVolumeSlider.getValue();
                    lastSoundVolume = soundVolumeSlider.getValue();
                    musicVolumeSlider.setValue(0f);
                    soundVolumeSlider.setValue(0f);
                } else {
                    musicVolumeSlider.setValue(lastMusicVolume);
                    soundVolumeSlider.setValue(lastSoundVolume);
                }
            }
        });
        stage.addActor(muteAllCheckbox);

        // --- Language Selection ---
        Table langTable = new Table();
        langTable.setPosition(1300, 550);

        TextButton englishButton = createButton(skin, "English", 2f, () -> setLanguage("en"));
        TextButton ukrainianButton = createButton(skin, "Українська", 2f, () -> setLanguage("ua"));
        TextButton germanButton = createButton(skin, "Deutsch", 2f, () -> setLanguage("de"));

        langTable.add(englishButton).width(350).height(90).padBottom(20).row();
        langTable.add(ukrainianButton).width(350).height(90).padBottom(20).row();
        langTable.add(germanButton).width(350).height(90).row();
        stage.addActor(langTable);
    }


    private void setLanguage(String languageCode) {
        GameSettings settings = SettingsManager.load();
        settings.language = languageCode;
        SettingsManager.save(settings);
        Assets.loadBundle(new Locale(languageCode));
        Main.restartGame();
    }
}
