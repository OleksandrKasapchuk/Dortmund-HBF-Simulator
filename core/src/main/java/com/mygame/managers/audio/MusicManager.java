package com.mygame.managers.audio;

import com.badlogic.gdx.audio.Music;

public class MusicManager {
    private static Music currentMusic;
    private static float currentVolume = 0f;
    private static float targetVolume = 0.5f;
    private static float fadeSpeed = 1.2f;
    private static boolean isPaused = false;
    private static volatile boolean musicCompleted = false; // Прапорець для безпечної комунікації між потоками

    public static void playMusic(Music newMusic) {
        if (currentMusic == newMusic && !isPaused) return;
        if (currentMusic != null && currentMusic != newMusic) {
            currentMusic.stop();
            currentMusic.setOnCompletionListener(null);
        }

        currentMusic = newMusic;
        currentVolume = 0f; // Починаємо з нульової гучності для плавного з'явлення
        isPaused = false;
        musicCompleted = false; // Скидаємо прапорець

        // Встановлюємо listener, який просто оновить прапорець після завершення
        currentMusic.setOnCompletionListener(music -> musicCompleted = true);

        currentMusic.setVolume(currentVolume);
        currentMusic.play();
    }

    public static void update(float delta) {
        // Ця частина обробляє зациклення з основного потоку
        if (musicCompleted) {
            musicCompleted = false;
            if (currentMusic != null && !isPaused) {
                currentMusic.play();
            }
        }

        // Ця частина обробляє плавну зміну гучності
        if (currentMusic != null && !isPaused) {
            if (currentVolume < targetVolume) {
                currentVolume += fadeSpeed * delta;
                if (currentVolume > targetVolume) currentVolume = targetVolume;
                currentMusic.setVolume(currentVolume);
            } else if (currentVolume > targetVolume) {
                currentVolume -= fadeSpeed * delta;
                if (currentVolume < targetVolume) currentVolume = targetVolume;
                currentMusic.setVolume(currentVolume);
            }
        }
    }

    // Цей метод тепер лише встановлює цільову гучність, а update() плавно її змінює
    public static void setVolume(float volume) {
        targetVolume = volume;
    }

    public static float getVolume() {
        return targetVolume;
    }

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
        musicCompleted = false;
        currentVolume = 0f; // Скидаємо гучність для наступного треку
    }
}
