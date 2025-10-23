package com.mygame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.ArrayList;
import java.util.Map;

public class UIManager {
    private final Stage stage;
    private final Skin skin;
    private final Label moneyLabel;
    private final Label infoLabel;

    private final Table inventoryTable;
    private final DialogueManager dialogueManager;

    private boolean inventoryVisible = false;
    private float infoMessageTimer = 0f;
    private boolean actButtonJustPressed = false;

    // Disposable resources
    private final Texture dialogueBgTexture;
    private Texture knobTexture;
    private Texture bgTexture;
    private final Texture inventoryBgTexture;

    private final Table questTable;
    private boolean questVisible = false;

    public UIManager(Player player) {
        stage = new Stage(new FitViewport(2000, 1000));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // === Діалогова таблиця ===
        Pixmap dialogueBg = new Pixmap(1950, 180, Pixmap.Format.RGBA8888);
        dialogueBg.setColor(new Color(0.1f, 0.1f, 0.5f, 0.6f));
        dialogueBg.fill();
        this.dialogueBgTexture = new Texture(dialogueBg);
        dialogueBg.dispose();
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(dialogueBgTexture));
        Table dialogueTable = new Table();
        dialogueTable.setSize(1950, 180);
        dialogueTable.setPosition(25, 30);
        dialogueTable.setBackground(background);
        Label nameLabel = new Label("", skin);
        nameLabel.setFontScale(3f);
        nameLabel.setColor(Color.GOLD);
        nameLabel.setAlignment(Align.left);
        Label dialogueLabel = new Label("", skin);
        dialogueLabel.setFontScale(3f);
        dialogueLabel.setWrap(true);
        dialogueLabel.setAlignment(Align.left);
        dialogueTable.add(nameLabel).left().padLeft(10).padBottom(20).row();
        dialogueTable.add(dialogueLabel).width(1800).padLeft(60).left();
        dialogueTable.setVisible(false);
        stage.addActor(dialogueTable);

        // === Менеджер діалогів ===
        this.dialogueManager = new DialogueManager(dialogueTable, nameLabel, dialogueLabel);

        // === Таблиця інвентаря ===
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


        // --- Гроші ---
        moneyLabel = new Label("Money: " + player.getMoney(), skin);
        moneyLabel.setPosition(1700, 925);
        moneyLabel.setFontScale(3f);
        stage.addActor(moneyLabel);

        // --- Інфо ---
        infoLabel = new Label("", skin);
        infoLabel.setColor(Color.GOLD);
        infoLabel.setAlignment(Align.center);
        infoLabel.setFontScale(4f);
        infoLabel.setPosition(stage.getViewport().getWorldWidth() / 2f, 850, Align.center);
        stage.addActor(infoLabel);

        // === Таблиця квестів ===
        questTable = new Table();
        questTable.setSize(1200, 800);
        questTable.setPosition(stage.getViewport().getWorldWidth()/2f - 600, stage.getViewport().getWorldHeight()/2f - 400);
        questTable.align(Align.topLeft).pad(20);

        Pixmap questBg = new Pixmap(1200, 800, Pixmap.Format.RGBA8888);
        questBg.setColor(new Color(0.1f, 0.5f, 0.2f, 0.5f)); // зелений відтінок
        questBg.fill();
        Texture questBgTexture = new Texture(questBg);
        questTable.setBackground(new TextureRegionDrawable(new TextureRegion(questBgTexture)));
        questBg.dispose();

        questTable.setVisible(false);
        stage.addActor(questTable);


        // === Сенсорне керування (для Android) ===
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            Gdx.input.setInputProcessor(stage);
            setupTouchControls(player);
        }
    }

    private void setupTouchControls(Player player) {
        // Кнопка руху
        Pixmap knobPixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
        knobPixmap.setColor(Color.WHITE);
        knobPixmap.fillCircle(25, 25, 25);
        knobTexture = new Texture(knobPixmap);
        knobPixmap.dispose();
        Pixmap bgPixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(new Color(0.3f, 0.3f, 0.3f, 0.5f));
        bgPixmap.fillCircle(50, 50, 50);
        bgTexture = new Texture(bgPixmap);
        bgPixmap.dispose();
        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));
        touchpadStyle.background = new TextureRegionDrawable(new TextureRegion(bgTexture));
        Touchpad touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setBounds(150, 150, 200, 200);
        stage.addActor(touchpad);
        player.touchpad = touchpad;

        // Кнопка взаємодії
        TextButton actButton = new TextButton("ACT", skin);
        actButton.setSize(150, 150);
        actButton.setPosition(1800, 150);
        actButton.getLabel().setFontScale(4f);
        actButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                actButtonJustPressed = true;
                return true;
            }
        });
        stage.addActor(actButton);

        // Кнопка інвентаря
        TextButton inventoryButton = new TextButton("INV", skin);
        inventoryButton.setSize(150, 150);
        inventoryButton.setPosition(1800, 325); // трохи лівіше від ACT
        inventoryButton.getLabel().setFontScale(3.5f);
        inventoryButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                inventoryVisible = !inventoryVisible;
                inventoryTable.setVisible(inventoryVisible);
                if (inventoryVisible) {
                    updateInventoryTable(player);
                }
                return true;
            }
        });
        stage.addActor(inventoryButton);
    }

    public void update(float delta, Player player, ArrayList<NPC> npcs) {
        // Оновлення грошей
        moneyLabel.setText("Money: " + player.getMoney());

        // Оновлюємо таймер повідомлення
        if (infoMessageTimer > 0) {
            infoMessageTimer -= delta;
            if (infoMessageTimer <= 0) {
                infoLabel.setVisible(false);
            }
        }

        // Перевірка інпут для інвентаря
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            inventoryVisible = !inventoryVisible;
            inventoryTable.setVisible(inventoryVisible);
            if (inventoryVisible) {
                updateInventoryTable(player);
            }
        }

        // Оновлення діалогів
        boolean interactPressed = Gdx.input.isKeyJustPressed(Input.Keys.E) || actButtonJustPressed;
        dialogueManager.update(delta, npcs, player, interactPressed);

        // Скидання кнопки
        if (actButtonJustPressed) {
            actButtonJustPressed = false;
        }

        stage.act(delta);
    }

    public void render() {
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
        dialogueBgTexture.dispose();
        inventoryBgTexture.dispose();
        if (knobTexture != null) knobTexture.dispose();
        if (bgTexture != null) bgTexture.dispose();
    }

    public void showInfoMessage(String message, float duration) {
        infoMessageTimer = duration;
        infoLabel.setText(message);
        infoLabel.pack();
        infoLabel.setPosition(stage.getViewport().getWorldWidth() / 2f, 850, Align.center);
        infoLabel.setVisible(true);
    }

    private void updateInventoryTable(Player player) {
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
    public void updateQuestTable() {
        questTable.clear();

        Label titleLabel = new Label("QUESTS", skin);
        titleLabel.setFontScale(3f);
        titleLabel.setColor(Color.GOLD);
        questTable.add(titleLabel).colspan(2).padBottom(30).center().row();

        if (QuestManager.getQuests().isEmpty()) {
            Label noQuestLabel = new Label("No quests yet.", skin);
            noQuestLabel.setFontScale(2.5f);
            questTable.add(noQuestLabel).center();
            return;
        }

        for (QuestManager.Quest quest : QuestManager.getQuests()) {
            Label questLabel = new Label("• " + quest.getDescription(), skin);
            questLabel.setFontScale(2.5f);
            questTable.add(questLabel).left().pad(10).row();
        }
    }

    public void toggleQuestTable() {
        questVisible = !questVisible;
        questTable.setVisible(questVisible);

        if (questVisible) {
            updateQuestTable();
        }
    }
}
