package com.mygame.ui.screenUI;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygame.Assets;
import com.mygame.Main;
import com.mygame.game.GameSettings;
import com.mygame.game.SettingsManager;

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
            Label menuLabel2 = new Label(Assets.bundle.get("menu.pressEnterToStart"), skin);
            menuLabel2.setPosition(710, 750);
            menuLabel2.setFontScale(2f);
            stage.addActor(menuLabel2);
        }

        // New Game button
        TextButton newGameButton = new TextButton(Assets.bundle.get("button.newGame.text"), skin);
        newGameButton.setSize(300, 100);
        newGameButton.setPosition(stage.getWidth() / 2 - 150, 300);
        newGameButton.getLabel().setFontScale(2f);
        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SettingsManager.save(new GameSettings()); // Reset settings to default
                Main.restartGame();
                Main.getGameInitializer().getManagerRegistry().getGameStateManager().startGame();
            }
        });
        stage.addActor(newGameButton);
    }
}
