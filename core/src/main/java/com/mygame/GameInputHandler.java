package com.mygame;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygame.dialogue.Dialogue;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.Player;
import com.mygame.managers.nonglobal.GameStateManager;
import com.mygame.managers.nonglobal.NpcManager;

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

    public void handleStonedPlayer(Player player, NpcManager npcManager) {
        npcManager.getPolice().setDialogue(
            new Dialogue(
                new DialogueNode(gsm::playerDied,
                    Assets.bundle.get("dialogue.police.stoned.1"),
                    Assets.bundle.get("dialogue.police.stoned.2"))
            )
        );
    }
}
