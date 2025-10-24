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

public class DialogueUI {
    private final Table dialogueTable;
    private final Label nameLabel;
    private final Label dialogueLabel;
    private final Texture dialogueBgTexture;

    public DialogueUI(Skin skin, Stage stage, int width, int height, float x, float y) {
        // Створюємо фон
        Pixmap dialogueBg = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        dialogueBg.setColor(new Color(0.1f, 0.1f, 0.5f, 0.6f));
        dialogueBg.fill();
        dialogueBgTexture = new Texture(dialogueBg);
        dialogueBg.dispose();

        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(dialogueBgTexture));

        // Створюємо таблицю
        dialogueTable = new Table();
        dialogueTable.setSize(width, height);
        dialogueTable.setPosition(x, y);
        dialogueTable.setBackground(background);

        nameLabel = new Label("", skin);
        nameLabel.setFontScale(3f);
        nameLabel.setColor(Color.GOLD);
        nameLabel.setAlignment(Align.left);

        dialogueLabel = new Label("", skin);
        dialogueLabel.setFontScale(3f);
        dialogueLabel.setWrap(true);
        dialogueLabel.setAlignment(Align.left);

        dialogueTable.add(nameLabel).left().padLeft(10).padBottom(20).row();
        dialogueTable.add(dialogueLabel).width(width - 150).padLeft(60).left();

        dialogueTable.setVisible(false);
        stage.addActor(dialogueTable);
    }

    public void show(String npcName, String text) {
        nameLabel.setText(npcName);
        dialogueLabel.setText(text);
        dialogueTable.setVisible(true);
    }

    public void hide() {
        dialogueTable.setVisible(false);
    }

    public void dispose() {
        dialogueBgTexture.dispose();
    }
}
