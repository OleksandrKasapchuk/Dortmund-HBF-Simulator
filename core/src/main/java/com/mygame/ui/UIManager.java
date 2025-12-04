package com.mygame.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.Assets;
import com.mygame.dialogue.DialogueManager;
import com.mygame.entity.player.Player;
import com.mygame.ui.inGameUI.DialogueUI;
import com.mygame.ui.inGameUI.InventoryUI;
import com.mygame.ui.inGameUI.QuestUI;
import com.mygame.ui.inGameUI.TouchControlsUI;
import com.mygame.ui.screenUI.*;
import com.mygame.world.WorldManager;

/**
 * UIManager is responsible for managing all UI components of the game.
 * It handles switching between different screens (menu, game, pause, settings, death),
 * updating in-game HUD elements (money, inventory, quests),
 * and managing touch controls for Android devices.

 * This class also forwards input events to the current active stage and updates
 * UI elements based on player actions or key presses.
 */
public class UIManager {

    private final Skin skin;

    private QuestUI questUI;
    private InventoryUI inventoryUI;
    private DialogueUI dialogueUI;
    private TouchControlsUI touchControlsUI;
    private WorldMapUI worldMapUI;

    private GameUI gameUI;
    private MenuUI menuUI;
    private PauseUI pauseUI;
    private SettingsUI settingsUI;
    private DeathUI deathUI;

    private Stage currentStage;
    private final DialogueManager dialogueManager;

    /**
     * Initializes all UI screens and attaches them to their respective stages.
     * Sets up touch controls if running on Android.
     *
     * @param player The player object to link HUD elements and touchpad
     * @param skin The fully configured skin to use for all UI components
     */
    public UIManager(Player player, Skin skin) {
        System.out.println("UIManager: Initializing...");
        this.skin = skin;

        // Initialize screens with the provided skin
        gameUI = new GameUI(skin);
        menuUI = new MenuUI(skin);
        pauseUI = new PauseUI(skin);
        settingsUI = new SettingsUI(skin);
        deathUI = new DeathUI(skin);
        worldMapUI = new WorldMapUI(skin);

        // Set initial stage to menu
        currentStage = menuUI.getStage();
        Gdx.input.setInputProcessor(currentStage);

        // Initialize in-game UI elements
        questUI = new QuestUI(skin, gameUI.getStage(), 1200, 900);
        inventoryUI = new InventoryUI(gameUI.getStage(), skin);
        dialogueUI = new DialogueUI(skin, gameUI.getStage(), 1950, 250, 25f, 10f);
        dialogueManager = new DialogueManager(dialogueUI, player);

        // Initialize touch controls only on Android
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            System.out.println("UIManager: Android detected, creating TouchControlsUI...");
            touchControlsUI = new TouchControlsUI(
                skin,
                gameUI.getStage(),
                pauseUI.getStage(),
                settingsUI.getStage(),
                player
            );
            System.out.println("UIManager: TouchControlsUI created.");
        }
        System.out.println("UIManager: Initialization complete.");
    }

    /**
     * Switches the current input stage.
     * This ensures that input events are processed by the correct screen.
     *
     * @param stageName Name of the stage: MENU, GAME, PAUSE, SETTINGS, DEATH, MAP
     */
    public void setCurrentStage(String stageName) {
        switch (stageName) {
            case "MENU": currentStage = menuUI.getStage(); break;
            case "GAME": currentStage = gameUI.getStage(); break;
            case "PAUSE": currentStage = pauseUI.getStage(); break;
            case "SETTINGS": currentStage = settingsUI.getStage(); break;
            case "DEATH": currentStage = deathUI.getStage(); break;
            case "MAP": currentStage = worldMapUI.getStage(); break;
        }
        Gdx.input.setInputProcessor(currentStage);
    }

    /**
     * Updates UI elements and handles input events.
     * This should be called every frame during the game.
     *
     * @param delta Time elapsed since last frame
     * @param player Reference to the player
     */
    public void update(float delta, Player player) {
        if (worldMapUI.isVisible()) {
            worldMapUI.update();
        } else if (currentStage == gameUI.getStage()) {
            gameUI.update(delta);
            gameUI.updateMoney(player.getMoney());
            gameUI.updateWorld(WorldManager.getCurrentWorld().getName());
            // Update dialogue manager
            dialogueManager.update(delta, isInteractPressed());

            if (questUI.isVisible()) questUI.update();
            if (inventoryUI.isVisible()) inventoryUI.update(player);

            // Toggle inventory with Tab or touch button
            if (Gdx.input.isKeyJustPressed(Input.Keys.TAB) || (touchControlsUI != null && touchControlsUI.isInvButtonJustPressed())) {
                toggleInventoryTable();
            }

            // Toggle quests with Q or touch button
            if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || (touchControlsUI != null && touchControlsUI.isQuestButtonJustPressed())) {
                toggleQuestTable();
            }
        }

        // Update the current stage actors
        currentStage.act(delta);
    }

    /** Draws the current stage */
    public void render() {
        if (worldMapUI.isVisible()) {
            worldMapUI.render();
        } else {
            currentStage.draw();
        }
    }

    /** Updates viewport size for current stage */
    public void resize(int width, int height) { currentStage.getViewport().update(width, height, true); }

    /** Dispose all UI resources to free memory */
    public void dispose() {
        menuUI.dispose();
        gameUI.dispose();
        pauseUI.dispose();
        deathUI.dispose();
        settingsUI.dispose();
        worldMapUI.dispose();
        skin.dispose();
        dialogueUI.dispose();
        inventoryUI.dispose();
        questUI.dispose();
        if (touchControlsUI != null) touchControlsUI.dispose();
    }

    /** Checks if the interact key or touch button is pressed */
    public boolean isInteractPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.E) ||
            (touchControlsUI != null && touchControlsUI.isActButtonJustPressed());
    }

    /** Toggles the quest UI; closes inventory if open */
    public void toggleQuestTable() {
        if (inventoryUI.isVisible()) inventoryUI.toggle();
        questUI.toggle();
    }

    /** Toggles the inventory UI; closes quest UI if open */
    public void toggleInventoryTable() {
        if (questUI.isVisible()) questUI.toggle();
        inventoryUI.toggle();
    }
    public void toggleWorldMap() {
        worldMapUI.toggle();
    }

    public boolean isMapVisible() {
        return worldMapUI.isVisible();
    }

    /** Resets the "just pressed" flags of touch buttons */
    public void resetButtons() {if (touchControlsUI != null) touchControlsUI.resetButtons();}

    // Getter methods for accessing UI components
    public DialogueManager getDialogueManager() { return dialogueManager; }
    public GameUI getGameUI() { return gameUI; }

    public void showEarned(int amount, String thing){
        gameUI.showInfoMessage(Assets.bundle.format("message.generic.got", amount, thing),1f);
    }
    public void showNotEnough(String thing) {
        gameUI.showInfoMessage(Assets.bundle.format("message.generic.not_enough", thing), 1f);
    }
}
