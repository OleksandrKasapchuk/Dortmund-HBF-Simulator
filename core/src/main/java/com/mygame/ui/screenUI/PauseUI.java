package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class PauseUI extends Screen {
    private final Label pauseLabel1;
    private final Label pauseLabel2;

    public PauseUI(Skin skin){
        Stage stage = getStage();

        pauseLabel1 = new Label("GAME PAUSED", skin);
        pauseLabel1.setPosition(825, 600);
        pauseLabel1.setFontScale(4f);
        stage.addActor(pauseLabel1);

        pauseLabel2 = new Label("PRESS P TO RESUME", skin);
        pauseLabel2.setPosition(775, 500);
        pauseLabel2.setFontScale(4f);
        stage.addActor(pauseLabel2);
    }
}
