package com.mygame.managers.global.audio;

import com.badlogic.gdx.audio.Sound;

/**
 * Simple global manager for short sound effects.
 * Unlike MusicManager (which handles looping long tracks),
 * this class is for one-shot SFX such as gunshot, money sound, joint sound.
 */
public class SoundManager {

    // Global volume for all sound effects (0.0f – 1.0f)
    private static float volume = 1.0f;

    /**
     * Plays a sound effect at the current global volume.
     */
    public static void playSound(Sound sound) {
        if (sound != null) {
            sound.play(volume);
        }
    }

    /**
     * Sets global SFX volume.
     */
    public static void setVolume(float newVolume) {
        volume = newVolume;
    }

    /**
     * Returns current SFX volume.
     */
    public static float getVolume() {
        return volume;
    }
}
