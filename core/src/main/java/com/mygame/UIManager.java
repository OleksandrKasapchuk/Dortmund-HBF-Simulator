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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.ArrayList;


public class UIManager {
    private final Stage stage;
    private final Skin skin;

    //Labels
    private final Label moneyLabel;
    private final Label infoLabel;

    private final DialogueManager dialogueManager;

    private float infoMessageTimer = 0f;
    private boolean actButtonJustPressed = false;

    // Disposable resources
    private Texture knobTexture;
    private Texture bgTexture;

    //UI MANAGERS
    private QuestUI questUI;
    private InventoryUI inventoryUI;
    private DialogueUI dialogueUI;

    public UIManager(Player player) {
        stage = new Stage(new FitViewport(2000, 1000));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        questUI = new QuestUI(skin, stage, 1200, 800);
        inventoryUI = new InventoryUI(stage, skin);
        dialogueUI = new DialogueUI(skin, stage, 1950, 180, 25f, 30f);


        this.dialogueManager = new DialogueManager(dialogueUI);


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
                inventoryUI.toggle(player);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            inventoryUI.toggle(player);
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

    public void render() {stage.draw();}

    public void resize(int width, int height) {stage.getViewport().update(width, height, true);}

    public void dispose() {
        stage.dispose();
        skin.dispose();
        dialogueUI.dispose();
        inventoryUI.dispose();
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

    public void toggleQuestTable() {
        questUI.toggle();
    }
}
