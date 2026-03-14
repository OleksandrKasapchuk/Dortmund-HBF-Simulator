package com.mygame.assets.audio;

import com.badlogic.gdx.audio.Music;
import com.mygame.assets.Assets;
import com.mygame.events.EventBus;
import com.mygame.events.Events;

/**
 * Global music controller for the game.
 * Handles switching tracks, pausing, resuming and volume control.
 */
public class MusicManager {

    private static Music currentMusic;   // currently playing track
    private static float currentVolume = 1f;
    private static boolean isPaused = false;
    private static Music temporaryMusic;

    /**
     * Plays new music track.
     * Automatically stops previous one (if different).
     */

    public static void init(){
        // Не викликаємо stopAll(), щоб не втратити поточну музику меню при ресеті гри
        EventBus.subscribe(Events.GameStateChangedEvent.class, event -> {
            switch (event.newState()) {
                case PAUSED, SETTINGS -> {
                    if(!isPaused) pauseMusic();
                }
                case MENU -> playMusic(Assets.getMusic("start"));
                case DEATH -> {
                    temporaryMusic = null;
                    playMusic(Assets.getMusic("back2"));
                }
                case PLAYING, MAP -> {
                    if (temporaryMusic != null) playMusic(temporaryMusic);
                    else playBackgroundMusic();
                }
        }});
    }

    public static void playBackgroundMusic(){
        // Перевіряємо, чи зараз грає музика меню (start)
        Music startMusic = Assets.getMusic("start");
        boolean sync = (currentMusic != null && currentMusic == startMusic);
        playMusic(Assets.getMusic("back1"), sync);
        currentMusic.setVolume(0.1f);
    }

    public static void playTemporaryMusic(Music music){
        temporaryMusic = music;
        playMusic(music);
    }

    public static void resetTemporaryMusic(){
        temporaryMusic = null;
        playBackgroundMusic();
    }

    public static void playMusic(Music newMusic) {
        playMusic(newMusic, false);
    }

    /**
     * Plays new music track with optional position synchronization.
     */
    public static void playMusic(Music newMusic, boolean syncPosition) {
        if (newMusic == null) return;
        // If the same track is already playing — do nothing.
        if (currentMusic == newMusic && !isPaused && currentMusic.isPlaying()) return;

        float position = 0;
        // Stop previous track and capture position if needed
        if (currentMusic != null && currentMusic != newMusic) {
            if (syncPosition) {
                position = currentMusic.getPosition();
            }
            currentMusic.stop();
        }

        currentMusic = newMusic;
        isPaused = false;

        currentMusic.setLooping(true);
        currentMusic.setVolume(currentVolume);

        // Спочатку запускаємо відтворення
        currentMusic.play();

        // Після play() встановлюємо позицію
        if (syncPosition && position > 0) {
            currentMusic.setPosition(position);
        }
    }

    // --- Volume control ---
    public static void setVolume(float volume) {
        currentVolume = volume;
        if (currentMusic != null) {
            currentMusic.setVolume(currentVolume);
        }
    }
    public static float getVolume() { return currentVolume; }

    /**
     * Pause music if it's currently playing.
     */
    public static void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
            isPaused = true;
        }
    }

    /**
     * Stops ANY playing music and clears reference.
     */
    public static void stopAll() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
        isPaused = false;
    }
}
