package com.mygame.managers.audio;

import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    private static float volume = 1.0f;

    public static void playSound(Sound sound) {
        if (sound != null) {
            sound.play(volume);
        }
    }
    public static void setVolume(float newVolume) {volume = newVolume;}
    public static float getVolume() {return volume;}
}
