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

public class QuestUI {

    private final Table questTable;
    private final Table listTable; // Таблиця, яку ми будемо чистити
    private final Skin skin;
    private final QuestManager questManager;
    private boolean visible = false;
    private final Texture bgTexture;
    private boolean showCompleted = false;

    // Кнопки виносимо в поля, щоб міняти їм колір в update()
    private final TextButton activeBtn;
    private final TextButton completedBtn;

    public QuestUI(Skin skin, Stage stage, float width, float height, QuestManager questManager) {
        this.skin = skin;
        this.questManager = questManager;

        questTable = new Table();
        questTable.setSize(width, height);
        questTable.setPosition(stage.getViewport().getWorldWidth() / 2f - width / 2,
            stage.getViewport().getWorldHeight() / 2f - height / 2);
        questTable.align(Align.top).pad(20);

        Pixmap bg = new Pixmap((int) width, (int) height, Pixmap.Format.RGBA8888);
        bg.setColor(new Color(0.05f, 0.2f, 0.1f, 0.85f));
        bg.fill();
        bgTexture = new Texture(bg);
        questTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        bg.dispose();

        // 1. Заголовок
        Label titleLabel = new Label(Assets.ui.get("ui.quest.title"), skin);
        titleLabel.setFontScale(2.5f);
        titleLabel.setColor(Color.GOLD);
        questTable.add(titleLabel).padBottom(20).colspan(2).center().row();

        // 2. Таби (Кнопки)
        activeBtn = new TextButton(Assets.ui.get("ui.quest.active"), skin);
        completedBtn = new TextButton(Assets.ui.get("ui.quest.completed"), skin);

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

        Table tabsTable = new Table();
        tabsTable.add(activeBtn).pad(10).width(200).height(50);
        tabsTable.add(completedBtn).pad(10).width(200).height(50);
        questTable.add(tabsTable).padBottom(20).colspan(2).row();

        // 3. Список квестів зі ScrollPane
        listTable = new Table();
        listTable.align(Align.topLeft);

        ScrollPane scrollPane = new ScrollPane(listTable, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        // Додаємо скрол ОДИН раз у конструкторі
        questTable.add(scrollPane).expand().fill().colspan(2);

        questTable.setVisible(false);
        stage.addActor(questTable);

        EventBus.subscribe(Events.QuestStartedEvent.class, e -> { if (visible) update(); });
        EventBus.subscribe(Events.QuestProgressEvent.class, e -> { if (visible) update(); });
        EventBus.subscribe(Events.QuestCompletedEvent.class, e -> { if (visible) update(); });
    }

    public void update() {
        // Оновлюємо колір кнопок
        activeBtn.setColor(showCompleted ? Color.LIGHT_GRAY : Color.YELLOW);
        completedBtn.setColor(showCompleted ? Color.YELLOW : Color.LIGHT_GRAY);

        listTable.clear(); // Чистимо тільки внутрішню таблицю списку

        List<QuestManager.Quest> quests = showCompleted ?
            questManager.getCompletedQuests() : questManager.getActiveQuests();

        if (quests.isEmpty()) {
            showEmpty();
        } else {
            for (QuestManager.Quest quest : quests) {
                addQuestEntry(quest);
            }
        }
    }

    private void addQuestEntry(QuestManager.Quest quest) {
        // Назва квесту
        Label nameLabel = new Label(Assets.quests.get("quest." + quest.key() + ".name"), skin);
        nameLabel.setFontScale(1.8f);
        nameLabel.setColor(quest.isCompleted() ? Color.GREEN : Color.CYAN);
        listTable.add(nameLabel).left().padLeft(10).row();

        // Опис квесту
        String description = getQuestDescription(quest);
        Label descLabel = new Label(" - " + description, skin);
        descLabel.setFontScale(1.1f);
        descLabel.setWrap(true); // Дозволяємо перенос тексту

        // Встановлюємо ширину, щоб wrap працював
        listTable.add(descLabel).left().width(questTable.getWidth() - 80).padLeft(25).padBottom(20).row();
    }

    private String getQuestDescription(QuestManager.Quest quest) {
        if (quest.progressable()) {
            return Assets.quests.format("quest." + quest.key() + ".description", quest.progress(), quest.maxProgress());
        } else {
            return Assets.quests.get("quest." + quest.key() + ".description");
        }
    }

    private void showEmpty() {
        String emptyMsg = showCompleted ? Assets.ui.get("ui.quest.completed.empty") : Assets.ui.get("ui.quest.empty");
        Label noQuest = new Label(emptyMsg, skin);
        noQuest.setFontScale(1.2f);
        listTable.add(noQuest).expandX().center().padTop(50);
    }

    public void toggle() {
        visible = !visible;
        questTable.setVisible(visible);
        if (visible) update();
    }

    public boolean isVisible() { return visible; }
    public void dispose() { bgTexture.dispose(); }
}
