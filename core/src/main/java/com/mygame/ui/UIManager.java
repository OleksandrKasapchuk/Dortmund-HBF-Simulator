package com.mygame.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.assets.Assets;
import com.mygame.dialogue.DialogueManager;
import com.mygame.entity.item.Item;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.quest.QuestManager;
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
    private final SpriteBatch batch;
    private Player player;
    private QuestManager questManager;
    private WorldManager worldManager;

    private QuestUI questUI;
    private InventoryUI inventoryUI;
    private DialogueUI dialogueUI;
    private TouchControlsUI touchControlsUI;
    private MapScreen mapScreen;

    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private PauseScreen pauseScreen;
    private SettingsScreen settingsScreen;
    private DeathScreen deathScreen;

    private Stage currentStage;
    private final DialogueManager dialogueManager;
    private final GlyphLayout layout = new GlyphLayout();

    /**
     * Initializes all UI screens and attaches them to their respective stages.
     * Sets up touch controls if running on Android.
     *
     * @param player The player object to link HUD elements and touchpad
     * @param skin The fully configured skin to use for all UI components
     * @param batch The SpriteBatch used for world rendering
     */
    public UIManager(SpriteBatch batch,Player player, Skin skin, QuestManager questManager, WorldManager worldManager) {
        System.out.println("UIManager: Initializing...");
        this.skin = skin;
        this.batch = batch;
        this.questManager = questManager;
        this.worldManager = worldManager;

        this.player = player;
        // Initialize screens with the provided skin
        gameScreen = new GameScreen(skin, worldManager);
        menuScreen = new MenuScreen(skin);
        pauseScreen = new PauseScreen(skin);
        settingsScreen = new SettingsScreen(skin);
        deathScreen = new DeathScreen(skin);
        mapScreen = new MapScreen(skin, worldManager);

        // Set initial stage to menu
        currentStage = menuScreen.getStage();
        Gdx.input.setInputProcessor(currentStage);

        // Initialize in-game UI elements
        questUI = new QuestUI(skin, gameScreen.getStage(), 1200, 900, questManager);
        inventoryUI = new InventoryUI(gameScreen.getStage(), skin);
        dialogueUI = new DialogueUI(skin, gameScreen.getStage(), 1950, 250, 25f, 10f);
        dialogueManager = new DialogueManager(dialogueUI, player, worldManager);

        EventBus.subscribe(Events.MessageEvent.class, event -> gameScreen.showInfoMessage(event.message(), 2f));
        EventBus.subscribe(Events.QuestStartedEvent.class, event -> gameScreen.showInfoMessage(Assets.ui.get("ui.quest.new"), 2f));
        EventBus.subscribe(Events.AddItemMessageEvent.class, event -> showEarned(event.item().getNameKey(), event.amount()));
        EventBus.subscribe(Events.NotEnoughMessageEvent.class, event -> showNotEnough(event.item().getNameKey()));
        EventBus.subscribe(Events.InteractEvent.class, e -> handleInteraction());

        // Initialize touch controls only on Android
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            System.out.println("UIManager: Android detected, creating TouchControlsUI...");
            touchControlsUI = new TouchControlsUI(
                skin,
                gameScreen.getStage(), pauseScreen.getStage(),
                settingsScreen.getStage(), mapScreen.getStage(),
                player
            );
            System.out.println("UIManager: TouchControlsUI created.");
        }
        System.out.println("UIManager: Initialization complete.");
    }

    /**
     * Switches the current input stage.
     * This ensures that input events are processed by the correct screen.
     * @param stageName Name of the stage: MENU, GAME, PAUSE, SETTINGS, DEATH, MAP
     */
    public void setCurrentStage(String stageName) {
        switch (stageName) {
            case "MENU": currentStage = menuScreen.getStage(); break;
            case "GAME": currentStage = gameScreen.getStage(); break;
            case "PAUSE": currentStage = pauseScreen.getStage(); break;
            case "SETTINGS": currentStage = settingsScreen.getStage(); break;
            case "DEATH": currentStage = deathScreen.getStage(); break;
            case "MAP": currentStage = mapScreen.getStage(); break;
        }
        Gdx.input.setInputProcessor(currentStage);
    }

    /**
     * Updates UI elements and handles input events.
     * This should be called every frame during the game.
     * @param delta Time elapsed since last frame
     */
    public void update(float delta) {
        if (currentStage == mapScreen.getStage()) {
            mapScreen.update();
        } else if (currentStage == gameScreen.getStage()) {
            gameScreen.update(delta, player);
            dialogueManager.update(delta);
            if (inventoryUI.isVisible()) inventoryUI.update(player);
        }
        // Update the current stage actors
        currentStage.act(delta);
    }

    /** Draws UI elements that are positioned in the game world (e.g., interaction labels) */
    public void renderWorldElements() {
        for (NPC npc : worldManager.getCurrentWorld().getNpcs()) {
            if (npc.isPlayerNear(player)) {
                Assets.myFont.draw(batch, Assets.ui.get("interact"), npc.getX() - 100, npc.getY() + npc.getHeight() + 40);
            }
        }

        for (Item item : worldManager.getCurrentWorld().getItems()) {
            // Check if it's a quest item and if the quest is active
            if (item.getQuestId() != null && !questManager.hasQuest(item.getQuestId())) continue;

            if (item.isPlayerNear(player, item.getDistance()) && !item.isSearched()) {
                if (item.isSearchable()){
                    drawText(Assets.ui.get("interact.search"), item.getCenterX(), item.getCenterY());
                } else {
                    drawText(Assets.ui.get("interact"), item.getCenterX(), item.getCenterY());
                }
            }
        }
    }

    private void handleInteraction() {
        // Find the nearest item the player can interact with
        for (Item item : worldManager.getCurrentWorld().getItems()) {
            if (item.getQuestId() != null && !questManager.hasQuest(item.getQuestId())) continue;

            if (item.isPlayerNear(player, item.getDistance()) && !item.isSearched()) {
                EventBus.fire(new Events.ItemInteractionEvent(item, player));
                return; // Prioritize items and interact with only one
            }
        }
    }

    /** Draws the current stage (HUD, Menus) in screen space */
    public void render() {
        currentStage.draw();
    }

    /** Updates viewport size for current stage */
    public void resize(int width, int height) { currentStage.getViewport().update(width, height, true); }

    /** Dispose all UI resources to free memory */
    public void dispose() {
        menuScreen.dispose();
        gameScreen.dispose();
        pauseScreen.dispose();
        deathScreen.dispose();
        settingsScreen.dispose();
        mapScreen.dispose();
        skin.dispose();
        dialogueUI.dispose();
        inventoryUI.dispose();
        questUI.dispose();
        if (touchControlsUI != null) touchControlsUI.dispose();
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

    // Getter methods for accessing UI components
    public DialogueManager getDialogueManager() { return dialogueManager; }
    public GameScreen getGameScreen() { return gameScreen; }
    public TouchControlsUI getTouchControlsUI() { return touchControlsUI; }


    public void showEarned(String thing, int amount){
        gameScreen.showInfoMessage(Assets.messages.format("message.generic.got", amount, Assets.items.get(thing)),2f);
    }

    public void showNotEnough(String thing) {
        gameScreen.showInfoMessage(Assets.messages.format("message.generic.not_enough", Assets.items.get(thing)), 2f);
    }

    /**
     * Draws centered text at world coordinates.
     * @param text The text to draw
     * @param x World X coordinate
     * @param y World Y coordinate
     */
    public void drawText(String text, float x, float y) {
        layout.setText(Assets.myFont, text);
        Assets.myFont.draw(batch, text, x - layout.width / 2f, y + 60);
    }
}
