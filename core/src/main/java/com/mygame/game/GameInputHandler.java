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
    public void handleInput() {
        TouchControlsUI touchControlsUI = uiManager.getTouchControlsUI();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P) || (touchControlsUI != null && touchControlsUI.isPauseButtonJustPressed()))
            gsm.togglePause();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || (touchControlsUI != null && touchControlsUI.isSettingsButtonJustPressed()))
            gsm.toggleSettings();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && gsm.getState() == GameStateManager.GameState.MENU)
            gsm.startGame();

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB) || (touchControlsUI != null && touchControlsUI.isInvButtonJustPressed()))
            uiManager.toggleInventoryTable();

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || (touchControlsUI != null && touchControlsUI.isQuestButtonJustPressed()))
            uiManager.toggleQuestTable();

        if (Gdx.input.isKeyJustPressed(Input.Keys.M) || (touchControlsUI != null && touchControlsUI.isMapButtonJustPressed()))
            gsm.toggleMap();
    }
}
