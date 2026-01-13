package com.mygame.game;

import com.mygame.assets.Assets;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
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
        setState(GameState.PLAYING);                // Switch state to PLAYING
        MusicManager.playMusic(Assets.getMusic("back1")); // Play game background music
              // Set UI to game stage
    }

    // --- Handle player death ---
    public void playerDied() {
        setState(GameState.DEATH);                  // Switch state to DEATH
        MusicManager.playMusic(Assets.getMusic("back2")); // Play death music

        // Reset player progress and save it
        GameSettings settings = SettingsManager.load();
        settings.playerX = 200; // Default X
        settings.playerY = 200;  // Default Y
        settings.inventory.clear();
        settings.activeQuests.clear();
        settings.completedDialogueEvents.clear();
        SettingsManager.save(settings);
    }

    public void exitToMenu() {
        if (state == GameState.PAUSED) {
            setState(GameState.MENU);
            MusicManager.playMusic(Assets.getMusic("start"));
        }
    }
    // --- Toggle pause state ---
    public void togglePause() {
        if (state == GameState.PAUSED) {
            setState(GameState.PLAYING);          // Resume the game
            MusicManager.resumeMusic();         // Resume music
        } else {
            setState(GameState.PAUSED);          // Pause the game
            MusicManager.pauseMusic();          // Pause music
        }
    }
    public void toggleMap() {
        if (state == GameState.MAP) {
            setState(GameState.PLAYING);
        } else {
            setState(GameState.MAP);
        }
    }

    // --- Toggle settings menu ---
    public void toggleSettings() {
        if (state == GameState.SETTINGS) {
            setState(GameState.PAUSED);           // Close settings menu and return to pause
        } else {
            setState(GameState.SETTINGS);           // Open settings menu
        }
    }

    public void setState(GameState newState) {
        if (newState == state) return;
        state = newState;
        EventBus.fire(new Events.GameStateChangedEvent(newState));
    }

}
