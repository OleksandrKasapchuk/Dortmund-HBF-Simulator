package com.mygame.managers.audio;

import com.badlogic.gdx.audio.Music;

public class MusicManager {
    private static Music currentMusic;
    private static float currentVolume = 1f;
    private static boolean isPaused = false;

    public static void playMusic(Music newMusic) {
        if (currentMusic == newMusic && !isPaused) return;
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

    public static void setVolume(float volume) {currentVolume = volume;}
    public static float getVolume() {return currentVolume;}

    public static void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
            isPaused = true;
        }
    }

    public static void resumeMusic() {
        if (currentMusic != null && isPaused) {
            currentMusic.play();
            isPaused = false;
        }
    }

    public static void stopAll() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.setOnCompletionListener(null);
            currentMusic = null;
        }
        isPaused = false;
    }
}
