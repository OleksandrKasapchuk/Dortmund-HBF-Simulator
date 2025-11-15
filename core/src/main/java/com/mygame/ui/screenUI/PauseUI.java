package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * PauseUI displays the pause screen.
 * It shows a "Game Paused" message and a prompt to resume the game.
 */
public class PauseUI extends Screen {
    private final Label pauseLabel1; // Main "GAME PAUSED" label
    private final Label pauseLabel2; // Instruction to resume ("PRESS P TO RESUME")

    /**
     * Constructor sets up the pause UI elements.
     *
     * @param skin Skin used for the labels
     */
    public PauseUI(Skin skin){
        Stage stage = getStage();

        // "GAME PAUSED" label
        pauseLabel1 = new Label("GAME PAUSED", skin);
        pauseLabel1.setPosition(825, 600);
        pauseLabel1.setFontScale(4f);
        stage.addActor(pauseLabel1);

        // Instruction label to resume game
        pauseLabel2 = new Label("PRESS P TO RESUME", skin);
        pauseLabel2.setPosition(775, 500);
        pauseLabel2.setFontScale(4f);
        stage.addActor(pauseLabel2);
    }
}
