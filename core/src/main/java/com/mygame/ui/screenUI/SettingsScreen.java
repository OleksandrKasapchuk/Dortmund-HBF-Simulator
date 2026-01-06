package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.mygame.assets.Assets;
import com.mygame.Main;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.assets.audio.MusicManager;
import com.mygame.assets.audio.SoundManager;

import java.util.Locale;

/**
 * Адаптивний екран налаштувань.
 */
public class SettingsScreen extends Screen {
    private Slider musicVolumeSlider;
    private Slider soundVolumeSlider;
    private CheckBox muteAllCheckbox;
    private float lastMusicVolume;
    private float lastSoundVolume;

    public SettingsScreen(Skin skin) {
        super();

        // Фонове зображення
        Image backgroundImage = new Image(Assets.getTexture("menuBlurBack"));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        backgroundImage.toBack();

        // Заголовок
        Label title = createLabel(skin, Assets.ui.get("settings.title"), 2.5f);
        root.add(title).colspan(2).padBottom(80).center().row();

        // --- ЛІВА ПАНЕЛЬ: Звук ---
        Table audioTable = new Table();
        audioTable.align(Align.left);

        // Музика
        audioTable.add(createLabel(skin, Assets.ui.get("settings.music"), 1.8f)).left().padRight(20);
        musicVolumeSlider = createSlider(skin, MusicManager.getVolume(), () -> MusicManager.setVolume(musicVolumeSlider.getValue()));
        audioTable.add(musicVolumeSlider).width(400).padBottom(20).row();

        // Ефекти
        audioTable.add(createLabel(skin, Assets.ui.get("settings.sounds"), 1.8f)).left().padRight(20);
        soundVolumeSlider = createSlider(skin, SoundManager.getVolume(), () -> SoundManager.setVolume(soundVolumeSlider.getValue()));
        audioTable.add(soundVolumeSlider).width(400).padBottom(20).row();

        // Mute All
        muteAllCheckbox = new CheckBox(" " + Assets.ui.get("settings.muteAll"), skin);
        muteAllCheckbox.getLabel().setFontScale(1.5f);
        muteAllCheckbox.getImageCell().size(60, 60);
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
        audioTable.add(muteAllCheckbox).left().colspan(2).padTop(30);

        root.add(audioTable).expandX().center();

        // --- ПРАВА ПАНЕЛЬ: Мова ---
        Table langTable = new Table();

        TextButton englishButton = createButton(skin, "English", 1.8f, () -> setLanguage("en"));
        TextButton ukrainianButton = createButton(skin, "Українська", 1.8f, () -> setLanguage("ua"));
        TextButton germanButton = createButton(skin, "Deutsch", 1.8f, () -> setLanguage("de"));

        langTable.add(englishButton).width(400).height(90).padBottom(20).row();
        langTable.add(ukrainianButton).width(400).height(90).padBottom(20).row();
        langTable.add(germanButton).width(400).height(90).row();

        root.add(langTable).expandX().center();

        // Кнопка НАЗАД (опціонально, бо ESC і так працює)
        root.row();
        TextButton backBtn = createButton(skin, Assets.ui.get("button.back.text"), 1.5f, () -> EventBus.fire (new Events.ActionRequestEvent("system.settings")));
        root.add(backBtn).colspan(2).padTop(50).width(300).height(70);
    }

    private void setLanguage(String languageCode) {
        GameSettings settings = SettingsManager.load();
        settings.language = languageCode;
        SettingsManager.save(settings);
        Assets.loadBundle(new Locale(languageCode));
        Main.restartGame();
    }
}
