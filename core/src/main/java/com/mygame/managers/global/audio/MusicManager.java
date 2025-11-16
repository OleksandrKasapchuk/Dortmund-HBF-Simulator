package com.mygame.managers.global.audio;

import com.badlogic.gdx.audio.Music;

/**
 * Global music controller for the game.
 * Handles switching tracks, pausing, resuming and volume control.
 */
public class MusicManager {

    private static Music currentMusic;   // currently playing track
    private static float currentVolume = 1f;
    private static boolean isPaused = false;

    /**
     * Plays new music track.
     * Automatically stops previous one (if different).
     */
    public static void playMusic(Music newMusic) {

        // If the same track is already playing — do nothing.
        if (currentMusic == newMusic && !isPaused) return;

        // Stop previous track if switching to a new one
        if (currentMusic != null && currentMusic != newMusic) {
            currentMusic.stop();
            currentMusic.setOnCompletionListener(null);
        }

        currentMusic = newMusic;
        isPaused = false;

        currentMusic.setLooping(true);
        currentMusic.setVolume(currentVolume);
        currentMusic.play();
    }

    // --- Volume control ---
    public static void setVolume(float volume) { currentVolume = volume; currentMusic.setVolume(currentVolume); }
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
            currentMusic.setOnCompletionListener(null);
            currentMusic = null;
        }
        isPaused = false;
    }
}
