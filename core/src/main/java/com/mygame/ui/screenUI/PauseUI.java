package com.mygame.ui.screenUI;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.assets.Assets;

/**
 * PauseUI displays the pause screen.
 * It shows a "Game Paused" message and a prompt to resume the game.
 */
public class PauseUI extends Screen {
    /**
     * Constructor sets up the pause UI elements.
     *
     * @param skin Skin used for the labels
     */
    public PauseUI(Skin skin) {
        // "GAME PAUSED" label
        createLabel(skin, Assets.ui.get("pause.title"), 2.5f,750, 600);

        // Instruction label to resume game
        if (Gdx.app.getType() != Application.ApplicationType.Android)
            createLabel(skin, Assets.ui.get("pause.resume"), 1.5f,775, 500);
    }
}
