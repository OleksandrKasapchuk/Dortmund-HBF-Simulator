package com.mygame.game;

import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.save.SettingsManager;
import com.mygame.ui.UIManager;

/**
 * Manages the current state of the game and handles transitions between states.
 * Supports menu, playing, pause, settings, and death states.
 */
public class GameStateManager {

    // --- Enum of possible game states ---
    public enum GameState { MENU, PLAYING, PAUSED, SETTINGS, DEATH, MAP, AUTH }

    private GameState state; // Current state of the game
    private final UIManager uiManager;

    // --- Constructor ---
    public GameStateManager(UIManager uiManager) {
        this.uiManager = uiManager;
        setState(GameState.AUTH);
    }

    // --- Get the current game state ---
    public GameState getState() {
        return state;
    }

    // --- Start the game ---
    public void startGame() {
        setState(GameState.PLAYING);                // Switch state to PLAYING
    }

    // --- Handle player death ---
    public void playerDied() {
        // Reset player progress and save it
        SettingsManager.resetSettings();
        setState(GameState.DEATH);                  // Switch state to DEATH
    }

    public void exitToMenu() {
        if (state == GameState.PAUSED) {
            setState(GameState.MENU);
        }
    }
    // --- Toggle pause state ---
    public void togglePause() {
        if (state == GameState.PAUSED) {
            setState(GameState.PLAYING);          // Resume the game
        } else if (state == GameState.PLAYING) {
            setState(GameState.PAUSED);          // Pause the game
        }
    }
    public void toggleMap() {
        if (state == GameState.MAP) {
            setState(GameState.PLAYING);
        } else if (state == GameState.PLAYING) {
            setState(GameState.MAP);
        }
    }

    // --- Toggle settings menu ---
    public void toggleSettings() {
        if (state == GameState.SETTINGS) {
            setState(GameState.PAUSED);           // Close settings menu and return to pause
        } else if (state == GameState.PAUSED) {
            setState(GameState.SETTINGS);           // Open settings menu
        }
    }

    public void setState(GameState newState) {
        if (newState == state) return;
        state = newState;
        EventBus.fire(new Events.GameStateChangedEvent(newState));
    }

}
