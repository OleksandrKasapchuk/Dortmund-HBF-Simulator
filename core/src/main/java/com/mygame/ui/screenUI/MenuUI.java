package com.mygame.ui.screenUI;


import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
    private TextButton startButton;
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


        // Create "START" button for menu
        startButton = new TextButton(Assets.bundle.get("button.start.text"), skin);
        startButton.setSize(300, 125);
        startButton.setPosition(200, 100);
        startButton.getLabel().setFontScale(1.5f);
        startButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Main.getGameInitializer().getManagerRegistry().getGameStateManager().startGame();
                return true;
            }
        });
        stage.addActor(startButton);

        // New Game button
        TextButton newGameButton = new TextButton(Assets.bundle.get("button.newGame.text"), skin);
        newGameButton.setSize(300, 125);
        newGameButton.setPosition(200, 300);
        newGameButton.getLabel().setFontScale(1.5f);
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
