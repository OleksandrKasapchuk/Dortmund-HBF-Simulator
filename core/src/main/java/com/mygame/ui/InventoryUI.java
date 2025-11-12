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

public class InventoryUI {
    private final Skin skin;
    private final Table inventoryTable;
    private final Texture inventoryBgTexture;
    private boolean visible = false;

    public InventoryUI(Stage stage, Skin skin) {
        this.skin = skin;

        inventoryTable = new Table();
        inventoryTable.setSize(1600, 800);
        inventoryTable.setPosition(stage.getViewport().getWorldWidth()/2f - 800, stage.getViewport().getWorldHeight()/2f - 400);
        inventoryTable.align(Align.topLeft).pad(20);

        Pixmap pixmap = new Pixmap(1600, 800, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.1f, 0.1f, 0.7f, 0.4f));
        pixmap.fill();
        inventoryBgTexture = new Texture(pixmap);
        inventoryTable.setBackground(new TextureRegionDrawable(new TextureRegion(inventoryBgTexture)));
        pixmap.dispose();

        inventoryTable.setVisible(false);
        stage.addActor(inventoryTable);
    }

    public void toggle(Player player) {
        visible = !visible;
        inventoryTable.setVisible(visible);
        if (visible) {update(player);}
    }

    public void update(Player player) {
        inventoryTable.clear();
        Label titleLabel = new Label("INVENTORY", skin);
        titleLabel.setFontScale(3f);
        titleLabel.setColor(Color.GOLD);
        inventoryTable.add(titleLabel).padBottom(20).colspan(3).row();

        Label statusLabel = new Label("Status: " + player.getState().toString(), skin);
        statusLabel.setFontScale(3f);
        inventoryTable.add(statusLabel).left().padBottom(40).colspan(3).row();

        for (Map.Entry<String, Integer> entry : player.getInventory().getItems().entrySet()) {
            String itemName = entry.getKey();
            int amount = entry.getValue();

            Label itemLabel = new Label(itemName + ": ", skin);
            itemLabel.setFontScale(3f);
            Label countLabel = new Label(String.valueOf(amount), skin);
            countLabel.setFontScale(3f);
            inventoryTable.add(itemLabel).left().pad(5);
            inventoryTable.add(countLabel).left().pad(5);

            if (player.getInventory().isUsable(itemName)) {
                TextButton useButton = new TextButton("USE", skin);
                useButton.getLabel().setFontScale(2.5f);
                useButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                    @Override
                    public boolean touchDown(com.badlogic.gdx.scenes.scene2d.InputEvent event,
                                             float x, float y, int pointer, int button) {
                        player.useItem(itemName);
                        update(player);
                        return true;
                    }
                });
                inventoryTable.add(useButton).pad(5);
            } else {
                inventoryTable.add().pad(5);
            }
            inventoryTable.row();
        }
    }
    public boolean isVisible() {return visible;}
    public void dispose() {inventoryBgTexture.dispose();}
}
