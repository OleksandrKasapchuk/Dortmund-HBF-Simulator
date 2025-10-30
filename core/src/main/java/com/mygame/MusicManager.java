package com.mygame;

import com.badlogic.gdx.audio.Music;

public class MusicManager {
    private static Music currentMusic;
    private static float currentVolume = 0f;
    private static float targetVolume = 0.5f;
    private static float fadeSpeed = 1.2f;
    private static boolean isPaused = false;

    public static void playMusic(Music newMusic, float volume) {
        // Якщо вже грає цей самий трек — нічого не робимо
        if (currentMusic == newMusic && !isPaused) return;

        // Якщо зараз грає інший трек — зупиняємо його
        if (currentMusic != null && currentMusic != newMusic) {
            currentMusic.stop();
        }

        currentMusic = newMusic;
        targetVolume = volume;
        currentVolume = 0f;
        isPaused = false;

        currentMusic.setLooping(true);
        currentMusic.setVolume(0f);
        currentMusic.play();
    }

    public static void update(float delta) {
        // Плавне збільшення гучності (fade-in)
        if (currentMusic != null && !isPaused) {
            if (currentVolume < targetVolume) {
                currentVolume += fadeSpeed * delta;
                if (currentVolume > targetVolume) currentVolume = targetVolume;
                currentMusic.setVolume(currentVolume);
            }
        }
    }

    public static void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
            isPaused = true;
        }
    }

    public static void resumeMusic() {
        if (currentMusic != null && isPaused) {
            currentMusic.play(); // продовжує з місця паузи
            isPaused = false;
        }
    }

    public static void stopAll() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
        isPaused = false;
    }
}
