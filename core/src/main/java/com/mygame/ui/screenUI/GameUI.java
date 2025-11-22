package com.mygame.ui.screenUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.mygame.Assets;
import com.mygame.entity.Player;

/**
 * GameUI handles the on-screen HUD elements during gameplay.
 * It displays player money and temporary info messages.
 */
public class GameUI extends Screen {
    private Label moneyLabel;       // Shows player's current money
    private Label infoLabel;        // Temporary info messages
    private float infoMessageTimer = 0f; // Timer to hide infoLabel automatically

    /**
     * Constructor sets up the UI elements.
     *
     * @param skin   The Skin used for labels
     * @param player The player whose money will be displayed
     */
    public GameUI(Skin skin, Player player){
        Stage stage = getStage();

        // Money display
        moneyLabel = new Label(Assets.bundle.format("ui.money", player.getMoney()), skin);
        moneyLabel.setPosition(1700, 925);
        moneyLabel.setFontScale(1.5f);
        stage.addActor(moneyLabel);

        // Info message display (temporary messages)
        infoLabel = new Label("", skin);
        infoLabel.setColor(Color.GOLD);
        infoLabel.setAlignment(Align.center);
        infoLabel.setFontScale(2f);
        infoLabel.setPosition(stage.getViewport().getWorldWidth() / 2f, 850, Align.center);
        stage.addActor(infoLabel);
        infoLabel.setVisible(false);
    }

    /**
     * Update the money display
     *
     * @param money Current player money
     */
    public void updateMoney(int money) {
        moneyLabel.setText(Assets.bundle.format("ui.money", money));
    }

    /**
     * Show a temporary info message
     *
     * @param message  The message text
     * @param duration How long the message should be visible (seconds)
     */
    public void showInfoMessage(String message, float duration) {
        infoLabel.setText(message);
        infoLabel.setVisible(true);
        infoMessageTimer = duration;
    }

    /**
     * Update method to be called every frame.
     * Handles hiding the info message after its timer expires.
     *
     * @param delta Time since last frame
     */
    public void update(float delta) {
        if (infoMessageTimer > 0) {
            infoMessageTimer -= delta;
            if (infoMessageTimer <= 0) infoLabel.setVisible(false);
        }
    }
}
