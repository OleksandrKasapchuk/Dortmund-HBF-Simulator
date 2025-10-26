package com.mygame.ui;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class DeathUI {
    private Stage stage;
    private Label deathLabel;
    private TextButton restartButton;

    public DeathUI(Skin skin) {
        stage = new Stage(new FitViewport(2000, 1000));

        deathLabel = new Label("You Failed!", skin);
        deathLabel.setFontScale(5f);
        deathLabel.setPosition(1000, 700, com.badlogic.gdx.utils.Align.center);
        stage.addActor(deathLabel);

        restartButton = new TextButton("Restart", skin);
        restartButton.setTransform(true);
        restartButton.setPosition(1000, 500, com.badlogic.gdx.utils.Align.center);
        restartButton.getLabel().setFontScale(3f);
        restartButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Ти можеш викликати Main.restartGame();
                // або GameManager.resetGame()
            }
        });
        stage.addActor(restartButton);
    }

    public Stage getStage() { return stage; }
    public void dispose() { stage.dispose(); }
}
