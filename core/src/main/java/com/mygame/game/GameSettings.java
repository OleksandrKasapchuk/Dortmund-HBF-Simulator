package com.mygame.game;

/**
 * A simple data class to hold game settings.
 * Needs a no-arg constructor for JSON serialization.
 */
public class GameSettings {
    public String language;

    public GameSettings() {
        // Default language
        this.language = "ua";
    }
}
