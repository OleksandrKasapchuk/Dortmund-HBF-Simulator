package com.mygame.ui.screenUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygame.Assets;
import com.mygame.Main;

/**
 * DeathUI represents the screen shown when the player dies.
 * It displays a "WASTED" message and a button to restart the game.
 */
public class DeathUI extends Screen {
    private final Image backgroundImage;

    /**
     * Constructor sets up the death screen UI.
     *
     * @param skin The Skin used for labels and buttons
     */
    public DeathUI(Skin skin) {
        Stage stage = getStage();

        // Background image filling the screen
        backgroundImage = new Image(Assets.deathBack);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Table to layout UI elements
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Main death label
        Label deathLabel = new Label(Assets.bundle.get("death.title"), skin);
        deathLabel.setColor(Color.RED);
        deathLabel.setFontScale(3f);

        TextButton restartButton = createButton(skin, Assets.bundle.get("death.restart"), 2f, Main::restartGame);

        table.add(deathLabel).padBottom(50).row();
        table.add(restartButton).width(300).height(100);
    }
}
