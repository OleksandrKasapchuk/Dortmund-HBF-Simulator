package com.mygame.ui;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygame.Player;

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
        inventoryTable.setPosition(stage.getViewport().getWorldWidth()/2f - 800,
            stage.getViewport().getWorldHeight()/2f - 400);
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
        if (visible) {
            update(player);
        }
    }

    public void update(Player player) {
        inventoryTable.clear();
        Label titleLabel = new Label("INVENTORY", skin);
        titleLabel.setFontScale(3f);
        titleLabel.setColor(Color.GOLD);
        inventoryTable.add(titleLabel).padBottom(20).colspan(2).row();

        for (Map.Entry<String, Integer> entry : player.getInventory().getItems().entrySet()) {
            Label itemLabel = new Label(entry.getKey() + ": ", skin);
            itemLabel.setFontScale(4f);
            Label countLabel = new Label(String.valueOf(entry.getValue()), skin);
            countLabel.setFontScale(4f);
            inventoryTable.add(itemLabel).left();
            inventoryTable.add(countLabel).left().row();
        }
    }
    public boolean isVisible() {return visible;}
    public void dispose() {
        inventoryBgTexture.dispose();
    }
}
