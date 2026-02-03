package com.mygame.ui.inGameUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.entity.item.ItemDefinition;
import com.mygame.events.EventBus;
import com.mygame.events.Events;

import java.util.ArrayList;
import java.util.List;

/**
 * Optimized Inventory UI: uses cached slots to avoid creating new objects each update.
 */
public class InventoryUI {

    private final Skin skin;
    private final Table inventoryTable;
    private final Texture inventoryBgTexture;
    private boolean visible = false;

    private Label titleLabel;
    private Label statusLabel;
    private Label hungerLabel;
    private Label thirstLabel;

    private static final int MAX_SLOTS = 50; // максимальна кількість айтемів в інвентарі
    private final List<InventorySlot> slots = new ArrayList<>();

    public InventoryUI(Stage stage, Skin skin) {
        this.skin = skin;

        inventoryTable = new Table();
        inventoryTable.setSize(1600, 800);
        inventoryTable.setPosition(stage.getViewport().getWorldWidth() / 2f - 800,
            stage.getViewport().getWorldHeight() / 2f - 400);
        inventoryTable.align(Align.topLeft).pad(20);

        // background
        Pixmap pixmap = new Pixmap(1600, 800, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.1f, 0.1f, 0.7f, 0.4f));
        pixmap.fill();
        inventoryBgTexture = new Texture(pixmap);
        inventoryTable.setBackground(new TextureRegionDrawable(new TextureRegion(inventoryBgTexture)));
        pixmap.dispose();

        inventoryTable.setVisible(false);
        stage.addActor(inventoryTable);

        // HUD labels
        titleLabel = new Label(Assets.ui.get("inventory.title"), skin);
        titleLabel.setFontScale(2.5f);
        titleLabel.setColor(Color.GOLD);
        inventoryTable.add(titleLabel).colspan(4).expandX().center().row();

        statusLabel = new Label("", skin);
        statusLabel.setFontScale(1.5f);
        inventoryTable.add(statusLabel).left().padBottom(40).colspan(4).row();

        hungerLabel = new Label("", skin);
        hungerLabel.setFontScale(1.5f);
        inventoryTable.add(hungerLabel).left().padBottom(40).colspan(4).row();

        thirstLabel = new Label("", skin);
        thirstLabel.setFontScale(1.5f);
        inventoryTable.add(thirstLabel).left().padBottom(40).colspan(4).row();

        // create cached slots
        for (int i = 0; i < MAX_SLOTS; i++) {
            InventorySlot slot = new InventorySlot(skin);
            slots.add(slot);

            inventoryTable.add(slot.itemLabel).left();
            inventoryTable.add(slot.countLabel).left().padRight(20);
            inventoryTable.add(slot.useButton != null ? slot.useButton : new Label("", skin)).left();
            inventoryTable.add(slot.descriptionLabel).expandX().right().padRight(20);
            inventoryTable.row().padBottom(20);

            slot.setVisible(false);
        }
    }

    public void toggle() {
        visible = !visible;
        inventoryTable.setVisible(visible);
    }

    public void update(Player player) {
        if (!visible) return;
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Used memory before: " + (runtime.totalMemory() - runtime.freeMemory())/1024 + " KB");

        long start = System.nanoTime();
        statusLabel.setText(Assets.ui.get("inventory.status") + Assets.ui.get(player.getState().getLocalizationKey()));
        hungerLabel.setText(String.valueOf(player.getStatusController().getHunger()));
        thirstLabel.setText(String.valueOf(player.getStatusController().getThirst()));

        int i = 0;
        for (ItemDefinition item : player.getInventory().getItems().keySet()) {
            if (i >= MAX_SLOTS) break;
            int amount = player.getInventory().getAmount(item);
            InventorySlot slot = slots.get(i);
            slot.set(item, amount, player);
            i++;
        }

        // hide unused slots
        for (; i < MAX_SLOTS; i++) {
            slots.get(i).setVisible(false);
        }
        long end = System.nanoTime();
        System.out.println("Inventory update time: " + (end - start)/1_000_000f + " ms");

        Runtime runtime2 = Runtime.getRuntime();
        System.out.println("Used memory after: " + (runtime2.totalMemory() - runtime2.freeMemory())/1024 + " KB");

    }

    public boolean isVisible() { return visible; }

    public void dispose() { inventoryBgTexture.dispose(); }

    // ---------- Inner Slot Class ----------
    private static class InventorySlot {
        Label itemLabel;
        Label countLabel;
        TextButton useButton;
        Label descriptionLabel;
        Skin skin;
        InventorySlot(Skin skin) {
            itemLabel = new Label("", skin);
            itemLabel.setFontScale(1.5f);
            countLabel = new Label("", skin);
            countLabel.setFontScale(1.5f);
            descriptionLabel = new Label("", skin);
            descriptionLabel.setFontScale(1.2f);
            this.skin = skin;
        }

        void set(ItemDefinition item, int amount, Player player) {
            itemLabel.setText(Assets.items.get(item.getNameKey()));
            countLabel.setText(String.valueOf(amount));
            descriptionLabel.setText(Assets.items.get(item.getDescriptionKey()));

            if (player.getInventory().isUsable(item)) {
                if (useButton == null) {
                    useButton = new TextButton(Assets.ui.get("ui.use"), skin);
                    useButton.getLabel().setFontScale(1.5f);
                    useButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                        @Override
                        public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event,
                                                 float x, float y, int pointer, int button) {
                            EventBus.fire(new Events.ItemUsedEvent(item));
                            return true;
                        }
                    });
                }
                useButton.setVisible(true);
            } else if (useButton != null) {
                useButton.setVisible(false);
            }

            setVisible(true);
        }

        void setVisible(boolean visible) {
            itemLabel.setVisible(visible);
            countLabel.setVisible(visible);
            descriptionLabel.setVisible(visible);
            if (useButton != null) useButton.setVisible(visible);
        }
    }
}
