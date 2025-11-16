package com.mygame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygame.entity.Player;

import java.util.Map;

/**
 * InventoryUI displays the player's inventory in a styled table,
 * showing item names, amounts, and action buttons (e.g., USE).
 * It can be toggled visible/hidden and updated dynamically.
 */
public class InventoryUI {

    private final Skin skin;
    private final Table inventoryTable;
    private final Texture inventoryBgTexture;
    private boolean visible = false;

    /**
     * Creates the InventoryUI and adds it to the given stage.
     *
     * @param stage Stage to attach the inventory UI
     * @param skin  Skin for labels and buttons
     */
    public InventoryUI(Stage stage, Skin skin) {
        this.skin = skin;

        // Main table setup
        inventoryTable = new Table();
        inventoryTable.setSize(1600, 800);
        inventoryTable.setPosition(stage.getViewport().getWorldWidth() / 2f - 800,
            stage.getViewport().getWorldHeight() / 2f - 400);
        inventoryTable.align(Align.topLeft).pad(20);

        // Semi-transparent background
        Pixmap pixmap = new Pixmap(1600, 800, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.1f, 0.1f, 0.7f, 0.4f));
        pixmap.fill();
        inventoryBgTexture = new Texture(pixmap);
        inventoryTable.setBackground(new TextureRegionDrawable(new TextureRegion(inventoryBgTexture)));
        pixmap.dispose();

        inventoryTable.setVisible(false);
        stage.addActor(inventoryTable);
    }

    /**
     * Toggles the inventory visibility.
     * If shown, updates the inventory contents.
     *
     * @param player Player whose inventory is displayed
     */
    public void toggle(Player player) {
        visible = !visible;
        inventoryTable.setVisible(visible);
        if (visible) update(player);
    }

    /**
     * Updates the inventory UI with current items and player status.
     *
     * @param player Player whose inventory is displayed
     */
    public void update(Player player) {
        inventoryTable.clear();

        // Title
        Label titleLabel = new Label("INVENTORY", skin);
        titleLabel.setFontScale(3f);
        titleLabel.setColor(Color.GOLD);
        inventoryTable.add(titleLabel).padBottom(20).colspan(4).row();

        // Player status
        Label statusLabel = new Label("Status: " + player.getState().toString(), skin);
        statusLabel.setFontScale(3f);
        inventoryTable.add(statusLabel).left().padBottom(40).colspan(4).row();

        // Iterate through all items
        for (Map.Entry<String, Integer> entry : player.getInventory().getItems().entrySet()) {
            String itemName = entry.getKey();
            int amount = entry.getValue();

            Label itemLabel = new Label(itemName + ": ", skin);
            itemLabel.setFontScale(3f);
            Label countLabel = new Label(String.valueOf(amount), skin);
            countLabel.setFontScale(3f);

            inventoryTable.add(itemLabel).left();
            inventoryTable.add(countLabel).left().padRight(20);


            // Add USE button if item is usable
            if (player.getInventory().isUsable(itemName)) {
                TextButton useButton = new TextButton("USE", skin);
                useButton.getLabel().setFontScale(2.5f);
                useButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                    @Override
                    public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event,
                                             float x, float y, int pointer, int button) {
                        player.useItem(itemName);
                        update(player);  // Refresh after using
                        return true;
                    }
                });
                inventoryTable.add(useButton).left();
            } else {
                inventoryTable.add(); // IMPORTANT: Add empty cell to keep alignment
            }

            // --- Description ---
            String description = player.getInventory().getDescription(itemName);
            Label descriptionLabel = new Label(description, skin);
            descriptionLabel.setFontScale(2f); // Smaller font for description
            inventoryTable.add(descriptionLabel).expandX().right().padRight(20);

            inventoryTable.row().padBottom(20);
        }
    }

    /** Returns whether the inventory is currently visible */
    public boolean isVisible() { return visible; }

    /** Dispose of resources when no longer needed */
    public void dispose() { inventoryBgTexture.dispose(); }
}
