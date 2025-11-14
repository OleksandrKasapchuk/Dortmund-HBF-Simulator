package com.mygame.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.managers.DialogueManager;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.ui.screenUI.DeathUI;
import com.mygame.ui.screenUI.GameUI;
import com.mygame.ui.screenUI.MenuUI;
import com.mygame.ui.screenUI.PauseUI;
import com.mygame.ui.screenUI.SettingsUI;

import java.util.ArrayList;

public class UIManager {
    private final Skin skin;
    private final DialogueManager dialogueManager;

    private QuestUI questUI;
    private InventoryUI inventoryUI;
    private DialogueUI dialogueUI;
    private TouchControlsUI touchControlsUI;

    private GameUI gameUI;
    private MenuUI menuUI;
    private PauseUI pauseUI;
    private SettingsUI settingsUI;
    private DeathUI deathUI;

    private Stage currentStage;

    public UIManager(Player player) {
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        gameUI = new GameUI(skin, player);
        menuUI = new MenuUI(skin);
        pauseUI = new PauseUI(skin);
        settingsUI = new SettingsUI(skin);
        deathUI = new DeathUI(skin);

        currentStage = menuUI.getStage();
        Gdx.input.setInputProcessor(currentStage);

        questUI = new QuestUI(skin, gameUI.getStage(), 1200, 800);
        inventoryUI = new InventoryUI(gameUI.getStage(), skin);
        dialogueUI = new DialogueUI(skin, gameUI.getStage(), 1950, 180, 25f, 30f);
        dialogueManager = new DialogueManager(dialogueUI, player);

        if (Gdx.app.getType() == Application.ApplicationType.Android) {touchControlsUI = new TouchControlsUI(skin, menuUI.getStage(), gameUI.getStage(), pauseUI.getStage(), settingsUI.getStage(), player);}
    }

    public void setCurrentStage(String stageName) {
        switch (stageName) {
            case "MENU": currentStage = menuUI.getStage(); break;
            case "GAME": currentStage = gameUI.getStage(); break;
            case "PAUSE": currentStage = pauseUI.getStage(); break;
            case "SETTINGS": currentStage = settingsUI.getStage(); break;
            case "DEATH": currentStage = deathUI.getStage(); break;
        }
        Gdx.input.setInputProcessor(currentStage);
    }

    public void update(float delta, Player player, ArrayList<NPC> npcs) {
        if (currentStage == gameUI.getStage()) {
            gameUI.update(delta);
            gameUI.updateMoney(player.getMoney());

            dialogueManager.update(delta, isInteractPressed(), npcs);

            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.TAB) || (touchControlsUI != null && touchControlsUI.isInvButtonJustPressed())) {toggleInventoryTable(player);}
            if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || (touchControlsUI != null && touchControlsUI.isQuestButtonJustPressed())) {toggleQuestTable(player);}
        }
        currentStage.act(delta);
    }

    public void render() {currentStage.draw();}
    public void resize(int width, int height) {currentStage.getViewport().update(width, height, true);}

    public void dispose() {
        menuUI.dispose();
        gameUI.dispose();
        pauseUI.dispose();
        deathUI.dispose();
        settingsUI.dispose();
        skin.dispose();
        dialogueUI.dispose();
        inventoryUI.dispose();
        questUI.dispose();
        if (touchControlsUI != null) touchControlsUI.dispose();
    }
    public boolean isInteractPressed() {return Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.E) || (touchControlsUI != null && touchControlsUI.isActButtonJustPressed());}

    public void toggleQuestTable(Player player) {
        if (inventoryUI.isVisible()) inventoryUI.toggle(player);
        questUI.toggle();
    }

    public void toggleInventoryTable(Player player) {
        if (questUI.isVisible()) questUI.toggle();
        inventoryUI.toggle(player);
    }

    public void resetButtons() {if (touchControlsUI != null) touchControlsUI.resetButtons();}

    public DialogueManager getDialogueManager() {return dialogueManager;}
    public GameUI getGameUI() {return gameUI;}
    public InventoryUI getInventoryUI() {return inventoryUI;}
}
