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
 * It displays player money, temporary info messages, and interaction hints.
 */
public class GameUI extends Screen {
    private Label moneyLabel;       // Shows player's current money
    private Label infoLabel;        // Temporary info messages
    private Label hintLabel;        // Context-sensitive interaction hints (e.g., "Press E to interact")
    private float infoMessageTimer = 0f; // Timer to hide infoLabel automatically
    private Label worldLabel;

    /**
     * Constructor sets up the UI elements.
     *
     * @param skin   The Skin used for labels
     */
    public GameUI(Skin skin){
        Stage stage = getStage();

        // Money display
        moneyLabel = createLabel(skin, "", 1.5f, 1700, 925);

        // Info message display (temporary messages)
        infoLabel = createLabel(skin, "", 2f, stage.getViewport().getWorldWidth() / 2f, 850);
        infoLabel.setColor(Color.GOLD);
        infoLabel.setAlignment(Align.center);
        infoLabel.setVisible(false);

        // Hint display (at the bottom or near the center)
        hintLabel = createLabel(skin, "", 1.5f, stage.getViewport().getWorldWidth() / 2f, 300);
        hintLabel.setAlignment(Align.center);
        hintLabel.setVisible(false);

        worldLabel = createLabel(skin, "", 1.5f, 10, Gdx.graphics.getHeight() - 100);
    }

    public void updateMoney(int money) {
        moneyLabel.setText(Assets.bundle.format("ui.money", money));
    }

    public void updateWorld(String worldName) {
        worldLabel.setText(Assets.bundle.format("ui.world.name", Assets.bundle.get("ui.world.name." + worldName)));
    }

    /**
     * Shows or hides an interaction hint.
     * @param message The hint text. If null or empty, the hint will be hidden.
     */
    public void setHint(String message) {
        if (message == null || message.isEmpty()) {
            hintLabel.setVisible(false);
        } else {
            hintLabel.setText(message);
            hintLabel.setVisible(true);
        }
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

        // Reset hint every frame so that systems must re-set it if player is still near
        // Or handle it more smartly in a specialized system.
    }
}
