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
import com.mygame.QuestManager;

public class QuestUI {
    private final Table questTable;
    private final Skin skin;
    private boolean visible = false;
    private final Texture bgTexture;

    public QuestUI(Skin skin, Stage stage, float width, float height) {
        this.skin = skin;

        questTable = new Table();
        questTable.setSize(width, height);
        questTable.setPosition(stage.getViewport().getWorldWidth()/2f - width/2, stage.getViewport().getWorldHeight()/2f - height/2);
        questTable.align(Align.topLeft).pad(20);

        Pixmap bg = new Pixmap((int)width, (int)height, Pixmap.Format.RGBA8888);
        bg.setColor(new Color(0.1f, 0.5f, 0.2f, 0.5f));
        bg.fill();
        bgTexture = new Texture(bg); // Зберігаємо текстуру
        questTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        bg.dispose();

        questTable.setVisible(false);
        stage.addActor(questTable);
    }

    public void update() {
        questTable.clear();
        Label title = new Label("QUESTS", skin);
        title.setFontScale(3f);
        title.setColor(Color.GOLD);
        questTable.add(title).colspan(2).padBottom(30).center().row();

        if (QuestManager.getQuests().isEmpty()) {
            Label noQuest = new Label("No quests yet.", skin);
            noQuest.setFontScale(2.5f);
            questTable.add(noQuest).center();
            return;
        }

        for (QuestManager.Quest quest : QuestManager.getQuests()) {
            Label qLabel = new Label("• " + quest.getDescription(), skin);
            qLabel.setFontScale(2.5f);
            questTable.add(qLabel).left().pad(10).row();
        }
    }

    public void toggle() {
        visible = !visible;
        questTable.setVisible(visible);
        if (visible) update();
    }
    public boolean isVisible() {return visible;}

    // Метод для звільнення пам'яті
    public void dispose() {
        bgTexture.dispose();
    }
}
