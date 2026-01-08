package com.mygame.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.ui.UIManager;

public class GameInputHandler {
    private final GameStateManager gsm;
    private final UIManager uiManager;

    public GameInputHandler(GameStateManager gsm, UIManager uiManager) {
        this.gsm = gsm;
        this.uiManager = uiManager;
    }

    /**Handles key input for global game actions (pause, settings, start game).*/
    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            EventBus.fire(new Events.InteractEvent());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || (gsm.getState() == GameStateManager.GameState.PAUSED && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)))
            gsm.togglePause();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && gsm.getState() == GameStateManager.GameState.MENU)
            gsm.startGame();

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB))
            uiManager.toggleInventoryTable();

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q))
            uiManager.toggleQuestTable();

        if (Gdx.input.isKeyJustPressed(Input.Keys.M))
            gsm.toggleMap();
    }
}
