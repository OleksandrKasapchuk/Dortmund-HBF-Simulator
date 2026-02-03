package com.mygame.ui.inGameUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.entity.item.ItemDefinition;
import com.mygame.events.EventBus;
import com.mygame.events.Events;

import java.util.Map;

/**
 * InventoryUI displays the player's inventory in a styled table,
 * showing item names, amounts, and action buttons (e.g., USE).
 * It can be toggled visible/hidden and updated dynamically.
 */
public class InventoryUI {

    private final Skin skin;
    private final Table inventoryTable;
    private final Table itemsTable;

    private final Texture inventoryBgTexture;
    private boolean visible = false;
    private Label titleLabel;
    private Label statusLabel;
    private Label hungerLabel;
    private Label thirstLabel;
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

        titleLabel = new Label(Assets.ui.get("inventory.title"), skin);
        titleLabel.setFontScale(2.5f);
        titleLabel.setColor(Color.GOLD);
        inventoryTable.add(titleLabel).colspan(4).expandX().center().row();

        // 1. Створюємо окрему таблицю для статусів
        Table statusSubTable = new Table();
        statusSubTable.align(Align.left);

        statusLabel = new Label("", skin);
        statusLabel.setFontScale(1.5f);
        statusSubTable.add(statusLabel).padRight(60); // Великий відступ між ними

        hungerLabel = new Label("", skin);
        hungerLabel.setFontScale(1.5f);
        statusSubTable.add(hungerLabel).padRight(60);

        thirstLabel = new Label("", skin);
        thirstLabel.setFontScale(1.5f);
        statusSubTable.add(thirstLabel);

        inventoryTable.add(statusSubTable).colspan(4).left().padBottom(40).row();

        // Створюємо окрему таблицю для предметів
        itemsTable = new Table();
        itemsTable.align(Align.topLeft);

        ScrollPane scrollPane = new ScrollPane(itemsTable, skin);
        scrollPane.setScrollingDisabled(true, false);
        inventoryTable.add(scrollPane).expand().fill().colspan(4);
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
        statusLabel.setText(Assets.ui.format("inventory.status", Assets.ui.get(player.getState().getLocalizationKey())));
        hungerLabel.setText(Assets.ui.format("inventory.hunger", player.getStatusController().getHunger()));
        thirstLabel.setText(Assets.ui.format("inventory.thirst", player.getStatusController().getThirst()));

        itemsTable.clear(); // Очищуємо ТІЛЬКИ список предметів
        listItems(player);
    }

    private void listItems(Player player){
        // Iterate through all items
        for (Map.Entry<ItemDefinition, Integer> entry : player.getInventory().getItems().entrySet()) {
            ItemDefinition itemType = entry.getKey();
            int amount = entry.getValue();

            String itemName = Assets.items.get(itemType.getNameKey());
            Label itemLabel = new Label(itemName + ": ", skin);
            itemLabel.setFontScale(1.5f);
            Label countLabel = new Label(String.valueOf(amount), skin);
            countLabel.setFontScale(1.5f);

            itemsTable.add(itemLabel).left();
            itemsTable.add(countLabel).left().padRight(20);


            // Add USE button if item is usable
            if (player.getInventory().isUsable(itemType)) {
                TextButton useButton = new TextButton(Assets.ui.get("ui.use"), skin);
                useButton.getLabel().setFontScale(1.5f);
                useButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                    @Override
                    public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event,
                                             float x, float y, int pointer, int button) {
                        EventBus.fire(new Events.ItemUsedEvent(entry.getKey()));
                        return true;
                    }
                });
                itemsTable.add(useButton).left();
            } else {
                itemsTable.add(); // IMPORTANT: Add empty cell to keep alignment
            }

            // --- Description ---
            String descriptionKey = itemType.getDescriptionKey();
            String description = Assets.items.get(descriptionKey);
            Label descriptionLabel = new Label(description, skin);
            descriptionLabel.setFontScale(1.2f); // Smaller font for description
            itemsTable.add(descriptionLabel).expandX().right().padRight(20);

            itemsTable.row().padBottom(20);
        }
    }
    /** Returns whether the inventory is currently visible */
    public boolean isVisible() { return visible; }

    /** Dispose of resources when no longer needed */
    public void dispose() { inventoryBgTexture.dispose(); }
}
