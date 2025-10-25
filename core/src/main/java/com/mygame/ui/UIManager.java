package com.mygame.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygame.DialogueManager;
import com.mygame.Main;
import com.mygame.NPC;
import com.mygame.Player;

import java.util.ArrayList;


public class UIManager {
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
    private Stage gameStage;
    private Stage menuStage;
    private Stage pauseStage;
    private Stage currentStage;

    public void setCurrentStage(int num) {
        if (num == 1) {
            currentStage = menuStage;
        } else if (num == 2) {
            currentStage = gameStage;
        } else if (num == 3) {
            currentStage = pauseStage;
        }

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            Gdx.input.setInputProcessor(currentStage);
        }
    }

    public UIManager(Player player) {
        gameStage = new Stage(new FitViewport(2000, 1000));
        menuStage = new Stage(new FitViewport(2000, 1000));
        pauseStage = new Stage(new FitViewport(2000, 1000));
        currentStage = menuStage;

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));


        questUI = new QuestUI(skin, gameStage, 1200, 800);
        inventoryUI = new InventoryUI(gameStage, skin);
        dialogueUI = new DialogueUI(skin, gameStage, 1950, 180, 25f, 30f);

        this.dialogueManager = new DialogueManager(dialogueUI);

        // --- Гроші ---
        moneyLabel = new Label("Money: " + player.getMoney(), skin);
        moneyLabel.setPosition(1700, 925);
        moneyLabel.setFontScale(3f);
        gameStage.addActor(moneyLabel);

        // --- Інфо ---
        infoLabel = new Label("", skin);
        infoLabel.setColor(Color.GOLD);
        infoLabel.setAlignment(Align.center);
        infoLabel.setFontScale(4f);
        infoLabel.setPosition(gameStage.getViewport().getWorldWidth() / 2f, 850, Align.center);
        gameStage.addActor(infoLabel);

        // === Сенсорне керування (для Android) ===
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            Gdx.input.setInputProcessor(currentStage);
            touchControlsUI = new TouchControlsUI(skin, menuStage, gameStage, pauseStage, player, inventoryUI, questUI);
        }
    }

    public void update(float delta, Player player, ArrayList<NPC> npcs) {
        if (currentStage == gameStage) {
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

            boolean interactPressed = Gdx.input.isKeyJustPressed(Input.Keys.E);

            if (touchControlsUI != null) {
                interactPressed = touchControlsUI.isActButtonJustPressed();
            }

            dialogueManager.update(delta, npcs, player, interactPressed);
        }
        currentStage.act(delta);
    }

    public void render() {
        currentStage.draw();
    }

    public void resize(int width, int height) {
        gameStage.getViewport().update(width, height, true);
        menuStage.getViewport().update(width, height, true);
        pauseStage.getViewport().update(width, height, true);
    }

    public void dispose() {
        menuStage.dispose();
        gameStage.dispose();
        pauseStage.dispose();
        skin.dispose();
        dialogueUI.dispose();
        inventoryUI.dispose();
        questUI.dispose();
        if (touchControlsUI != null) {
            touchControlsUI.dispose();
        }
    }

    public void showInfoMessage(String message, float duration) {
        infoMessageTimer = duration;
        infoLabel.setText(message);
        infoLabel.pack();
        infoLabel.setPosition(gameStage.getViewport().getWorldWidth() / 2f, 850, Align.center);
        infoLabel.setVisible(true);
    }

    public void toggleQuestTable() {
        questUI.toggle();
    }
}
