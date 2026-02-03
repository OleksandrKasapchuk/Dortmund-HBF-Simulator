package com.mygame.ui.inGameUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.assets.Assets;
import com.mygame.quest.QuestManager;


class QuestRow {
    Label name;
    Label desc;

    QuestRow(Skin skin, float width) {
        name = new Label("", skin);
        name.setFontScale(1.8f);

        desc = new Label("", skin);
        desc.setFontScale(1.1f);
        desc.setWrap(true);
        desc.setWidth(width);
    }

    void set(QuestManager.Quest quest, boolean completed) {
        name.setText(Assets.quests.get("quest." + quest.key() + ".name"));
        name.setColor(completed ? Color.GREEN : Color.CYAN);

        String text = quest.progressable()
            ? Assets.quests.format(
            "quest." + quest.key() + ".description",
            quest.progress(),
            quest.maxProgress())
            : Assets.quests.get("quest." + quest.key() + ".description");

        desc.setText(" - " + text);

        name.setVisible(true);
        desc.setVisible(true);
    }

    void hide() {
        name.setVisible(false);
        desc.setVisible(false);
    }
}
