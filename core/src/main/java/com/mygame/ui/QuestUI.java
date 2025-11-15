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
import com.mygame.managers.QuestManager;

/**
 * QuestUI displays the player's active quests in a table overlay.
 * It can be toggled visible/hidden and dynamically updated.
 */
public class QuestUI {

    private final Table questTable;
    private final Skin skin;
    private boolean visible = false;
    private final Texture bgTexture;

    /**
     * Initializes the QuestUI overlay.
     *
     * @param skin  Skin used for labels
     * @param stage Stage to attach the quest table
     * @param width Width of the quest UI
     * @param height Height of the quest UI
     */
    public QuestUI(Skin skin, Stage stage, float width, float height) {
        this.skin = skin;

        questTable = new Table();
        questTable.setSize(width, height);
        questTable.setPosition(stage.getViewport().getWorldWidth() / 2f - width / 2,
            stage.getViewport().getWorldHeight() / 2f - height / 2);
        questTable.align(Align.topLeft).pad(20);

        // Semi-transparent background
        Pixmap bg = new Pixmap((int) width, (int) height, Pixmap.Format.RGBA8888);
        bg.setColor(new Color(0.1f, 0.5f, 0.2f, 0.5f));
        bg.fill();
        bgTexture = new Texture(bg);
        questTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        bg.dispose();

        questTable.setVisible(false);
        stage.addActor(questTable);
    }

    /**
     * Updates the quest UI with current quests.
     */
    public void update() {
        questTable.clear();

        // Title
        Label title = new Label("QUESTS", skin);
        title.setFontScale(3f);
        title.setColor(Color.GOLD);
        questTable.add(title).colspan(2).padBottom(30).center().row();

        // If no quests, show message
        if (QuestManager.getQuests().isEmpty()) {
            Label noQuest = new Label("No quests yet.", skin);
            noQuest.setFontScale(2.5f);
            questTable.add(noQuest).center();
            return;
        }

        // Display all quests
        for (QuestManager.Quest quest : QuestManager.getQuests()) {
            Label qLabel = new Label("• " + quest.getDescription(), skin);
            qLabel.setFontScale(2.5f);
            questTable.add(qLabel).left().pad(10).row();
        }
    }

    /**
     * Toggles the visibility of the quest UI.
     */
    public void toggle() {
        visible = !visible;
        questTable.setVisible(visible);
        if (visible) update();
    }

    /** Returns whether the quest UI is currently visible */
    public boolean isVisible() { return visible; }

    /** Dispose of resources when no longer needed */
    public void dispose() { bgTexture.dispose(); }
}
