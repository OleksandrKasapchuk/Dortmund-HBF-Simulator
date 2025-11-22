package com.mygame.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.managers.nonglobal.DialogueManager;
import com.mygame.entity.Player;
import com.mygame.ui.screenUI.DeathUI;
import com.mygame.ui.screenUI.GameUI;
import com.mygame.ui.screenUI.MenuUI;
import com.mygame.ui.screenUI.PauseUI;
import com.mygame.ui.screenUI.SettingsUI;

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
        gameUI = new GameUI(skin, player);
        menuUI = new MenuUI(skin);
        pauseUI = new PauseUI(skin);
        settingsUI = new SettingsUI(skin);
        deathUI = new DeathUI(skin);

        // Set initial stage to menu
        currentStage = menuUI.getStage();
        Gdx.input.setInputProcessor(currentStage);

        // Initialize in-game UI elements
        questUI = new QuestUI(skin, gameUI.getStage(), 1200, 800);
        inventoryUI = new InventoryUI(gameUI.getStage(), skin);
        dialogueUI = new DialogueUI(skin, gameUI.getStage(), 1950, 250, 25f, 10f);
        dialogueManager = new DialogueManager(dialogueUI, player);

        // Initialize touch controls only on Android
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            System.out.println("UIManager: Android detected, creating TouchControlsUI...");
            touchControlsUI = new TouchControlsUI(
                skin,
                menuUI.getStage(),
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
     * @param stageName Name of the stage: MENU, GAME, PAUSE, SETTINGS, DEATH
     */
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

    /**
     * Updates UI elements and handles input events.
     * This should be called every frame during the game.
     *
     * @param delta Time elapsed since last frame
     * @param player Reference to the player
     */
    public void update(float delta, Player player) {
        if (currentStage == gameUI.getStage()) {
            gameUI.update(delta);
            gameUI.updateMoney(player.getMoney());

            // Update dialogue manager
            dialogueManager.update(delta, isInteractPressed());

            // Toggle inventory with Tab or touch button
            if (Gdx.input.isKeyJustPressed(Input.Keys.TAB) || (touchControlsUI != null && touchControlsUI.isInvButtonJustPressed())) {
                toggleInventoryTable(player);
            }

            // Toggle quests with Q or touch button
            if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || (touchControlsUI != null && touchControlsUI.isQuestButtonJustPressed())) {
                toggleQuestTable(player);
            }
        }

        // Update the current stage actors
        currentStage.act(delta);
    }

    /** Draws the current stage */
    public void render() { currentStage.draw(); }

    /** Updates viewport size for current stage */
    public void resize(int width, int height) { currentStage.getViewport().update(width, height, true); }

    /** Dispose all UI resources to free memory */
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

    /** Checks if the interact key or touch button is pressed */
    public boolean isInteractPressed() {
        return Gdx.input.isKeyJustPressed(Input.Keys.E) ||
            (touchControlsUI != null && touchControlsUI.isActButtonJustPressed());
    }

    /** Toggles the quest UI; closes inventory if open */
    public void toggleQuestTable(Player player) {
        if (inventoryUI.isVisible()) inventoryUI.toggle(player);
        questUI.toggle();
    }

    /** Toggles the inventory UI; closes quest UI if open */
    public void toggleInventoryTable(Player player) {
        if (questUI.isVisible()) questUI.toggle();
        inventoryUI.toggle(player);
    }

    /** Resets the "just pressed" flags of touch buttons */
    public void resetButtons() {if (touchControlsUI != null) touchControlsUI.resetButtons();}

    // Getter methods for accessing UI components
    public DialogueManager getDialogueManager() { return dialogueManager; }
    public GameUI getGameUI() { return gameUI; }
    public InventoryUI getInventoryUI() { return inventoryUI; }
}
