package com.mygame.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygame.DialogueManager;
import com.mygame.NPC;
import com.mygame.Player;

import java.util.ArrayList;


public class UIManager {
    private final Stage stage;
    private final Skin skin;

    //Labels
    private final Label moneyLabel;
    private final Label infoLabel;

    private final DialogueManager dialogueManager;

    private float infoMessageTimer = 0f;

    //UI MANAGERS
    private QuestUI questUI;
    private InventoryUI inventoryUI;
    private DialogueUI dialogueUI;
    private TouchControlsUI touchControlsUI;

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
            touchControlsUI = new TouchControlsUI(skin, stage, player, inventoryUI, questUI);
        }
    }

    public void update(float delta, Player player, ArrayList<NPC> npcs) {
        moneyLabel.setText("Money: " + player.getMoney());

        // Оновлюємо таймер повідомлення
        if (infoMessageTimer > 0) {
            infoMessageTimer -= delta;
            if (infoMessageTimer <= 0) {infoLabel.setVisible(false);}
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {inventoryUI.toggle(player);}

        boolean interactPressed = Gdx.input.isKeyJustPressed(Input.Keys.E);

        if (touchControlsUI != null) {interactPressed = touchControlsUI.isActButtonJustPressed();}

        dialogueManager.update(delta, npcs, player, interactPressed);

        stage.act(delta);
    }

    public void render() {stage.draw();}
    public void resize(int width, int height) {stage.getViewport().update(width, height, true);}

    public void dispose() {
        stage.dispose();
        skin.dispose();
        dialogueUI.dispose();
        inventoryUI.dispose();
        questUI.dispose();
        if (touchControlsUI != null) {touchControlsUI.dispose();}
    }

    public void showInfoMessage(String message, float duration) {
        infoMessageTimer = duration;
        infoLabel.setText(message);
        infoLabel.pack();
        infoLabel.setPosition(stage.getViewport().getWorldWidth() / 2f, 850, Align.center);
        infoLabel.setVisible(true);
    }
    public void toggleQuestTable() {questUI.toggle();}
}
