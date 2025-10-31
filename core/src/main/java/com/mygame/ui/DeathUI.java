package com.mygame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygame.Main;

public class DeathUI {
    private Stage stage;

    public DeathUI(Skin skin) {
        stage = new Stage(new FitViewport(2000, 1000));

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        Label deathLabel = new Label("WASTED", skin);
        deathLabel.setColor(Color.RED);
        deathLabel.setFontScale(6f);

        TextButton restartButton = new TextButton("Restart", skin);
        restartButton.getLabel().setFontScale(3f);
        restartButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Main.restartGame();
                return true;
            }
        });

        table.add(deathLabel).padBottom(50).row();
        table.add(restartButton).width(300).height(100);
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
    }
}
