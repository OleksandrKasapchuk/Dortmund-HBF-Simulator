package com.mygame.game;

import com.mygame.assets.Assets;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.assets.audio.MusicManager;
import com.mygame.ui.UIManager;

/**
 * Manages the current state of the game and handles transitions between states.
 * Supports menu, playing, pause, settings, and death states.
 */
public class GameStateManager {

    // --- Enum of possible game states ---
    public enum GameState { MENU, PLAYING, PAUSED, SETTINGS, DEATH, MAP }

    private GameState state = GameState.MENU; // Current state of the game
    private final UIManager uiManager;
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
        MusicManager.playMusic(Assets.getMusic("back1")); // Play game background music
        uiManager.setCurrentStage("GAME");         // Set UI to game stage
    }

    // --- Handle player death ---
    public void playerDied() {
        state = GameState.DEATH;                  // Switch state to DEATH
        MusicManager.playMusic(Assets.getMusic("back2")); // Play death music
        uiManager.setCurrentStage("DEATH");        // Set UI to death stage

        // Reset player progress and save it
        GameSettings settings = SettingsManager.load();
        settings.playerX = 200; // Default X
        settings.playerY = 200;  // Default Y
        settings.inventory.clear();
        settings.activeQuests.clear();
        settings.completedDialogueEvents.clear();
        SettingsManager.save(settings);
    }

    // --- Toggle pause state ---
    public void togglePause() {
        if (state == GameState.PAUSED) {
            state = GameState.PLAYING;          // Resume the game
            MusicManager.resumeMusic();         // Resume music
            uiManager.setCurrentStage("GAME");  // Show game UI
        } else {
            state = GameState.PAUSED;          // Pause the game
            MusicManager.pauseMusic();          // Pause music
            uiManager.setCurrentStage("PAUSE"); // Show pause UI
        }
    }
    public void toggleMap() {
        if (state == GameState.MAP) {
            state = GameState.PLAYING;
            uiManager.setCurrentStage("GAME");
        } else {
            state = GameState.MAP;
            uiManager.setCurrentStage("MAP");
        }
    }

    // --- Toggle settings menu ---
    public void toggleSettings() {
        if (state == GameState.SETTINGS) {
            state = GameState.PAUSED;           // Close settings menu and return to pause
            uiManager.setCurrentStage("PAUSE"); // Виправлено з "PAUSED" на "PAUSE"
        } else {
            state = GameState.SETTINGS;           // Open settings menu
            uiManager.setCurrentStage("SETTINGS");
        }
    }
}
