package com.mygame;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygame.managers.nonglobal.GameStateManager;

public class GameInputHandler {
    private final GameStateManager gsm;

    public GameInputHandler(GameStateManager gsm) { this.gsm = gsm; }

    /**Handles key input for global game actions (pause, settings, start game).*/
    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) gsm.togglePause();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gsm.toggleSettings();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && gsm.getState() == GameStateManager.GameState.MENU)
            gsm.startGame();
    }
}
