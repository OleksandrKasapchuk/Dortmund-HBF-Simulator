package com.mygame.ui.inGameUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygame.assets.Assets;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.quest.QuestManager;

import java.util.List;
/**
 * QuestUI displays the player's active and completed quests in a table overlay.
 * It has tabs to switch between active and completed quests.
 */
public class QuestUI {

    private final Table questTable;
    private final Skin skin;
    private boolean visible = false;
    private final Texture bgTexture;
    private boolean showCompleted = false;

    public QuestUI(Skin skin, Stage stage, float width, float height) {
        this.skin = skin;

        questTable = new Table();
        questTable.setSize(width, height);
        questTable.setPosition(stage.getViewport().getWorldWidth() / 2f - width / 2,
            stage.getViewport().getWorldHeight() / 2f - height / 2);
        questTable.align(Align.top).pad(20);

        // Semi-transparent background
        Pixmap bg = new Pixmap((int) width, (int) height, Pixmap.Format.RGBA8888);
        bg.setColor(new Color(0.05f, 0.2f, 0.1f, 0.85f));
        bg.fill();
        bgTexture = new Texture(bg);
        questTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        bg.dispose();

        questTable.setVisible(false);
        stage.addActor(questTable);

        // Subscribe to quest events to auto-refresh if UI is visible
        EventBus.subscribe(Events.QuestStartedEvent.class, e -> { if (visible) update(); });
        EventBus.subscribe(Events.QuestProgressEvent.class, e -> { if (visible) update(); });
        EventBus.subscribe(Events.QuestCompletedEvent.class, e -> { if (visible) update(); });
    }

    public void update() {
        questTable.clear();

        // --- Title ---
        Label title = new Label(Assets.ui.get("ui.quest.title"), skin);
        title.setFontScale(2.5f);
        title.setColor(Color.GOLD);
        questTable.add(title).padBottom(20).colspan(2).center().row();

        // --- Tabs ---
        Table tabsTable = new Table();
        TextButton activeBtn = new TextButton(Assets.ui.get("ui.quest.active"), skin);
        TextButton completedBtn = new TextButton(Assets.ui.get("ui.quest.completed"), skin);

        // Styling
        if (!showCompleted) {
            activeBtn.setColor(Color.YELLOW);
            completedBtn.setColor(Color.LIGHT_GRAY);
        } else {
            activeBtn.setColor(Color.LIGHT_GRAY);
            completedBtn.setColor(Color.YELLOW);
        }

        activeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCompleted = false;
                update();
            }
        });

        completedBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCompleted = true;
                update();
            }
        });

        tabsTable.add(activeBtn).pad(10).width(200).height(50);
        tabsTable.add(completedBtn).pad(10).width(200).height(50);
        questTable.add(tabsTable).padBottom(20).colspan(2).row();

        // --- Content ---
        List<QuestManager.Quest> filteredQuests = QuestManager.getQuests().stream()
            .filter(q -> q.isCompleted() == showCompleted)
            .toList();

        if (filteredQuests.isEmpty()) {
            String emptyMsg = showCompleted ? Assets.ui.get("ui.quest.completed.empty") : Assets.ui.get("ui.quest.empty");
            Label noQuest = new Label(emptyMsg, skin);
            noQuest.setFontScale(1.2f);
            questTable.add(noQuest).expand().center().colspan(2);
        } else {
            Table listTable = new Table();
            listTable.align(Align.topLeft);

            for (QuestManager.Quest quest : filteredQuests) {
                Label nameLabel = new Label(Assets.quests.get("quest." + quest.key() + ".name"), skin);
                nameLabel.setFontScale(1.8f);
                nameLabel.setColor(quest.isCompleted() ? Color.GREEN : Color.CYAN);
                listTable.add(nameLabel).left().padLeft(10).row();

                String description;
                if (quest.progressable()) {
                    description = Assets.quests.format("quest." + quest.key() + ".description", quest.progress(), quest.maxProgress());
                } else {
                    description = Assets.quests.get("quest." + quest.key() + ".description");
                }
                Label descLabel = new Label(" - " + description, skin);
                descLabel.setFontScale(1.1f);
                descLabel.setWrap(true);
                listTable.add(descLabel).left().width(questTable.getWidth() - 60).padLeft(25).padBottom(20).row();
            }

            ScrollPane scroll = new ScrollPane(listTable, skin);
            scroll.setScrollingDisabled(true, false);
            questTable.add(scroll).expand().fill().colspan(2);
        }
    }

    public void toggle() {
        visible = !visible;
        questTable.setVisible(visible);
        if (visible) update();
    }

    public boolean isVisible() { return visible; }

    public void dispose() { bgTexture.dispose(); }
}
