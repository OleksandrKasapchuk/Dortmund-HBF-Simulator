package com.mygame.ui.screenUI;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygame.Assets;
import com.mygame.Main;
import com.mygame.managers.global.save.GameSettings;
import com.mygame.managers.global.save.SettingsManager;

/**
 * MenuUI displays the main menu screen.
 * It shows a background image and a "Press Enter to Start" prompt on non-Android platforms.
 */
public class MenuUI extends Screen {
    private Image backgroundImage; // Background image for the menu
    /**
     * Constructor sets up the menu UI elements.
     * @param skin Skin for labels and UI elements
     */
    public MenuUI(Skin skin){
        Stage stage = getStage();

        // Background image
        backgroundImage = new Image(Assets.menuBack);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        TextButton startButton = createButton(skin, Assets.bundle.get("button.start.text"), 1.5f, 300, 125, 200, 100, () ->
            Main.getGameInitializer().getManagerRegistry().getGameStateManager().startGame());
        stage.addActor(startButton);

        TextButton newGameButton = createButton(skin, Assets.bundle.get("button.newGame.text"), 1.5f, 300, 125, 200, 300, () -> {
                SettingsManager.save(new GameSettings());
                Main.restartGame();
                Main.getGameInitializer().getManagerRegistry().getGameStateManager().startGame();
            });
        stage.addActor(newGameButton);
    }
}
