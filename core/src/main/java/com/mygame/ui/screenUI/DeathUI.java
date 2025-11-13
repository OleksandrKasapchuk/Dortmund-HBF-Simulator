package com.mygame.ui.screenUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygame.Assets;
import com.mygame.Main;

public class DeathUI extends Screen {
    private final Image backgroundImage;

    public DeathUI(Skin skin) {
        Stage stage = getStage();

        backgroundImage = new Image(Assets.deathBack);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

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
}
