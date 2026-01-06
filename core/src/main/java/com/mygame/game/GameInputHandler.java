package com.mygame.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygame.ui.UIManager;
import com.mygame.ui.inGameUI.TouchControlsUI;

public class GameInputHandler {
    private final GameStateManager gsm;
    private final UIManager uiManager;

    public GameInputHandler(GameStateManager gsm, UIManager uiManager) {
        this.gsm = gsm;
        this.uiManager = uiManager;
    }

    /**Handles key input for global game actions (pause, settings, start game).*/
    public void update() {
        TouchControlsUI touchControlsUI = uiManager.getTouchControlsUI();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P) || (gsm.getState() == GameStateManager.GameState.PAUSED && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)))
            gsm.togglePause();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && gsm.getState() == GameStateManager.GameState.MENU)
            gsm.startGame();

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB))
            uiManager.toggleInventoryTable();

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q))
            uiManager.toggleQuestTable();

        if (Gdx.input.isKeyJustPressed(Input.Keys.M))
            gsm.toggleMap();
        uiManager.resetButtons();
    }
}
