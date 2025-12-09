package com.mygame.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygame.ui.UIManager;

public class GameInputHandler {
    private final GameStateManager gsm;
    private final UIManager uiManager;

    public GameInputHandler(GameStateManager gsm, UIManager uiManager) {
        this.gsm = gsm;
        this.uiManager = uiManager;
    }

    public void update(){
        handleInput();
    }


    /**Handles key input for global game actions (pause, settings, start game).*/
    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) gsm.togglePause();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gsm.toggleSettings();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && gsm.getState() == GameStateManager.GameState.MENU)
            gsm.startGame();

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            uiManager.toggleWorldMap();
            if (uiManager.isMapVisible()) {
                gsm.setGameState(GameStateManager.GameState.MAP);
            } else {
                gsm.setGameState(GameStateManager.GameState.PLAYING);
            }
        }
    }
}
