package com.mygame.managers.nonglobal;

import com.mygame.Assets;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.ui.UIManager;

/**
 * Manages the current state of the game and handles transitions between states.
 * Supports menu, playing, pause, settings, and death states.
 */
public class GameStateManager {

    // --- Enum of possible game states ---
    public enum GameState { MENU, PLAYING, PAUSED, SETTINGS, DEATH }

    private GameState state = GameState.MENU; // Current state of the game
    private final UIManager uiManager;        // UI manager to switch stages/screens

    // --- Constructor ---
    public GameStateManager(UIManager uiManager) {
        this.uiManager = uiManager;
    }

    // --- Get the current game state ---
    public GameState getState() {
        return state;
    }

    // --- Start the game ---
    public void startGame() {
        state = GameState.PLAYING;                // Switch state to PLAYING
        MusicManager.playMusic(Assets.backMusic1); // Play game background music
        uiManager.setCurrentStage("GAME");         // Set UI to game stage
    }

    // --- Handle player death ---
    public void playerDied() {
        state = GameState.DEATH;                  // Switch state to DEATH
        MusicManager.playMusic(Assets.backMusic4); // Play death music
        uiManager.setCurrentStage("DEATH");        // Set UI to death stage
    }

    // --- Toggle pause state ---
    public void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;          // Pause the game
            MusicManager.pauseMusic();          // Pause music
            uiManager.setCurrentStage("PAUSE"); // Show pause UI
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;          // Resume the game
            MusicManager.resumeMusic();         // Resume music
            uiManager.setCurrentStage("GAME");  // Show game UI
        }
    }

    // --- Toggle settings menu ---
    public void toggleSettings() {
        if (state == GameState.PLAYING) {
            state = GameState.SETTINGS;           // Open settings menu
            uiManager.setCurrentStage("SETTINGS");
        } else if (state == GameState.SETTINGS) {
            state = GameState.PLAYING;           // Close settings menu and return to game
            uiManager.setCurrentStage("GAME");
        }
    }
}
