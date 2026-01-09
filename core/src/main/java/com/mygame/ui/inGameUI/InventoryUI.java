package com.mygame.ui.inGameUI;

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
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.entity.item.ItemDefinition;

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
     */
    public void toggle() {
        visible = !visible;
        inventoryTable.setVisible(visible);
    }

    /**
     * Updates the inventory UI with current items and player status.
     * @param player Player whose inventory is displayed
     */
    public void update(Player player) {
        if (!visible) return;
        inventoryTable.clear();

        // Title
        Label titleLabel = new Label(Assets.ui.get("inventory.title"), skin);
        titleLabel.setFontScale(2.5f);
        titleLabel.setColor(Color.GOLD);
        inventoryTable.add(titleLabel).colspan(4).expandX().center().row();

        Label statusLabel = new Label(Assets.ui.get("inventory.status") + Assets.ui.get(player.getState().getLocalizationKey()), skin);
        statusLabel.setFontScale(1.5f);
        inventoryTable.add(statusLabel).left().padBottom(40).colspan(4).row();

        // Iterate through all items
        for (Map.Entry<ItemDefinition, Integer> entry : player.getInventory().getItems().entrySet()) {
            ItemDefinition itemType = entry.getKey();
            int amount = entry.getValue();

            String itemName = Assets.items.get(itemType.getNameKey());
            Label itemLabel = new Label(itemName + ": ", skin);
            itemLabel.setFontScale(1.5f);
            Label countLabel = new Label(String.valueOf(amount), skin);
            countLabel.setFontScale(1.5f);

            inventoryTable.add(itemLabel).left();
            inventoryTable.add(countLabel).left().padRight(20);


            // Add USE button if item is usable
            if (player.getInventory().isUsable(itemType)) {
                TextButton useButton = new TextButton(Assets.ui.get("ui.use"), skin);
                useButton.getLabel().setFontScale(1.5f);
                useButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                    @Override
                    public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event,
                                             float x, float y, int pointer, int button) {
                        player.useItem(entry.getKey());
                        return true;
                    }
                });
                inventoryTable.add(useButton).left();
            } else {
                inventoryTable.add(); // IMPORTANT: Add empty cell to keep alignment
            }

            // --- Description ---
            String descriptionKey = itemType.getDescriptionKey();
            String description = Assets.items.get(descriptionKey);
            Label descriptionLabel = new Label(description, skin);
            descriptionLabel.setFontScale(1.2f); // Smaller font for description
            inventoryTable.add(descriptionLabel).expandX().right().padRight(20);

            inventoryTable.row().padBottom(20);
        }
    }

    /** Returns whether the inventory is currently visible */
    public boolean isVisible() { return visible; }

    /** Dispose of resources when no longer needed */
    public void dispose() { inventoryBgTexture.dispose(); }
}
