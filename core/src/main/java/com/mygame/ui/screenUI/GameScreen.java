package com.mygame.ui.screenUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.world.WorldManager;

/**
 * GameUI handles the on-screen HUD elements during gameplay.
 * It displays player money and temporary info messages.
 */
public class GameScreen extends Screen {
    private Label moneyLabel;       // Shows player's current money
    private Label infoLabel;        // Temporary info messages
    private float infoMessageTimer = 0f; // Timer to hide infoLabel automatically
    private Label worldLabel;

    /**
     * Constructor sets up the UI elements.
     *
     * @param skin   The Skin used for labels
     */
    public GameScreen(Skin skin){
        Stage stage = getStage();

        // Money display
        moneyLabel = createLabel(skin, "", 1.5f, 1700, 925);

        // Info message display (temporary messages)
        infoLabel = createLabel(skin, "", 2f, stage.getViewport().getWorldWidth() / 2f, 850);
        infoLabel.setColor(Color.GOLD);
        infoLabel.setAlignment(Align.center);
        infoLabel.setVisible(false);

        worldLabel = createLabel(skin, "", 1.5f, 10, Gdx.graphics.getHeight() - 100);
    }

    public void updateMoney(int money) {
        moneyLabel.setText(Assets.ui.format("ui.money", money));
    }

    public void updateWorld(String worldName) {
        worldLabel.setText(Assets.ui.format("ui.world.name", Assets.ui.get("ui.world.name." + worldName)));
    }

    /**
     * Show a temporary info message
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
     * @param delta Time since last frame
     */
    public void update(float delta, Player player) {
        if (infoMessageTimer > 0) {
            infoMessageTimer -= delta;
            if (infoMessageTimer <= 0) infoLabel.setVisible(false);
        }
        updateMoney(player.getInventory().getMoney());
        if (WorldManager.getCurrentWorld() != null) {
            updateWorld(WorldManager.getCurrentWorld().getName());
        }
    }
}
