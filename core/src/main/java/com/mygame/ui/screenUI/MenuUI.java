package com.mygame.ui.screenUI;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.Assets;

/**
 * MenuUI displays the main menu screen.
 * It shows a background image and a "Press Enter to Start" prompt on non-Android platforms.
 */
public class MenuUI extends Screen {
    private Image backgroundImage; // Background image for the menu

    /**
     * Constructor sets up the menu UI elements.
     *
     * @param skin Skin for labels and UI elements
     */
    public MenuUI(Skin skin){
        Stage stage = getStage();

        // Background image
        backgroundImage = new Image(Assets.menuBack);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Show "Press Enter to Start" for non-Android platforms
        if (Gdx.app.getType() != Application.ApplicationType.Android){
            Label menuLabel2 = new Label("PRESS ENTER TO START", skin);
            menuLabel2.setPosition(710, 750);
            menuLabel2.setFontScale(4f);
            stage.addActor(menuLabel2);
        }
    }
}
