package com.mygame.managers;

import com.mygame.Assets;
import com.mygame.Main;
import com.mygame.managers.audio.MusicManager;
import com.mygame.ui.UIManager;

public class GameStateManager {
    public enum GameState { MENU, PLAYING, PAUSED, SETTINGS, DEATH }

    private GameState state = GameState.MENU;
    private final UIManager uiManager;

    public GameStateManager(UIManager uiManager) {this.uiManager = uiManager;}

    public GameState getState() {return state;}

    public void startGame() {
        state = GameState.PLAYING;
        MusicManager.playMusic(Assets.backMusic1);
        uiManager.setCurrentStage("GAME");
    }

    public void playerDied() {
        state = GameState.DEATH;
        MusicManager.playMusic(Assets.backMusic4);
        uiManager.setCurrentStage("DEATH");
    }

    public void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
            MusicManager.pauseMusic();
            uiManager.setCurrentStage("PAUSE");
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
            MusicManager.resumeMusic();
            uiManager.setCurrentStage("GAME");
        }
    }

    public void toggleSettings() {
        if (state == GameState.PLAYING) {
            state = GameState.SETTINGS;
            uiManager.setCurrentStage("SETTINGS");
        } else if (state == GameState.SETTINGS) {
            state = GameState.PLAYING;
            uiManager.setCurrentStage("GAME");
        }
    }
}
