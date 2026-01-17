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
        stopAll();
        EventBus.subscribe(Events.GameStateChangedEvent.class, event -> {
            switch (event.newState()) {
                case PAUSED, SETTINGS -> {
                    if(!isPaused) pauseMusic();
                }
                case MENU -> playMusic(Assets.getMusic("start"));
                case DEATH -> playMusic(Assets.getMusic("back2"));
                case PLAYING, MAP -> {
                    if (temporaryMusic != null) playMusic(temporaryMusic);
                    else playBackgroundMusic();
                }
        }});
    }
    public static void playBackgroundMusic(){
        playMusic(Assets.getMusic("back1"));
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
        if (newMusic == null) return;
        // If the same track is already playing — do nothing.
        if (currentMusic == newMusic && !isPaused) return;

        // Stop previous track if switching to a new one
        if (currentMusic != null && currentMusic != newMusic) {
            currentMusic.stop();
        }

        currentMusic = newMusic;
        isPaused = false;

        currentMusic.setLooping(true);
        currentMusic.setVolume(currentVolume);
        currentMusic.play();
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
     * Resume music if it's paused.
     */
    public static void resumeMusic() {
        if (currentMusic != null && isPaused) {
            currentMusic.play();
            isPaused = false;
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
