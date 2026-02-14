package com.mygame.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.assets.Assets;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.player.Player;
import com.mygame.game.DayManager;
import com.mygame.game.GameStateManager;
import com.mygame.quest.QuestManager;
import com.mygame.ui.inGameUI.DialogueUI;
import com.mygame.ui.inGameUI.InWorldUIRenderer;
import com.mygame.ui.inGameUI.InventoryUI;
import com.mygame.ui.inGameUI.QuestUI;
import com.mygame.ui.inGameUI.TouchControlsUI;
import com.mygame.ui.screenUI.*;
import com.mygame.util.I18nUtils;
import com.mygame.world.WorldManager;
import com.mygame.world.zone.ZoneManager;

import java.util.EnumMap;
import java.util.Map;

public class UIManager {
    private SpriteBatch batch;
    private Player player;
    private QuestManager questManager;
    private WorldManager worldManager;
    private InWorldUIRenderer inWorldUIRenderer;
    private Skin skin;

    private final Map<GameStateManager.GameState, Screen> screens = new EnumMap<>(GameStateManager.GameState.class);
    private Screen currentScreen;

    private QuestUI questUI;
    private InventoryUI inventoryUI;
    private DialogueUI dialogueUI;
    private TouchControlsUI touchControlsUI;
    private UIEventHandler uiEventHandler;

    public UIManager(SpriteBatch batch, Skin skin) {
        this.batch = batch;
        this.skin = skin;

        screens.put(GameStateManager.GameState.LOADING, new LoadingScreen(skin)); // новий порожній екран
        currentScreen = screens.get(GameStateManager.GameState.LOADING);
        Gdx.input.setInputProcessor(currentScreen.getStage());
        screens.put(GameStateManager.GameState.AUTH, new AuthScreen(skin, this));
    }

    public void init(Player player, QuestManager questManager, WorldManager worldManager, DayManager dayManager, NpcManager npcManager, ItemManager itemManager, ZoneManager zoneManager){
        this.player = player;
        this.questManager = questManager;
        this.worldManager = worldManager;
        this.inWorldUIRenderer = new InWorldUIRenderer(batch, player, questManager, worldManager, npcManager, itemManager, zoneManager);
        createScreens(skin, dayManager);

        questUI = new QuestUI(skin, getGameScreen().getStage(), 1200, 900, questManager);
        inventoryUI = new InventoryUI(getGameScreen().getStage(), skin);
        dialogueUI = new DialogueUI(skin, getGameScreen().getStage(), 1500, 350, 250f, 10f);

        this.uiEventHandler = new UIEventHandler(this, questManager);

        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            touchControlsUI = new TouchControlsUI(skin, screens.get(GameStateManager.GameState.PLAYING).getStage(), screens.get(GameStateManager.GameState.PAUSED).getStage(), screens.get(GameStateManager.GameState.SETTINGS).getStage(), screens.get(GameStateManager.GameState.MAP).getStage(), player);
        }
    }

    private void createScreens(Skin skin, DayManager dayManager) {
        screens.put(GameStateManager.GameState.PLAYING, new GameScreen(skin, worldManager, dayManager, player));
        screens.put(GameStateManager.GameState.MENU, new MenuScreen(skin));
        screens.put(GameStateManager.GameState.PAUSED, new PauseScreen(skin));
        screens.put(GameStateManager.GameState.SETTINGS, new SettingsScreen(skin));
        screens.put(GameStateManager.GameState.DEATH, new DeathScreen(skin));
        screens.put(GameStateManager.GameState.MAP, new MapScreen(skin, worldManager));
    }

    public void setCurrentStage(GameStateManager.GameState state) {
        Screen newScreen = screens.get(state);
        if (newScreen != null) {
            currentScreen = newScreen;
            Gdx.input.setInputProcessor(currentScreen.getStage());
        }
    }

    public void update(float delta) {
        if (currentScreen instanceof GameScreen gameScreen) {
            gameScreen.update(delta);
            inventoryUI.update(player);
        } else if (currentScreen instanceof MapScreen mapScreen) {
            mapScreen.update();
        }
        currentScreen.getStage().act(delta);
    }



    public void render() {currentScreen.getStage().draw();}

    public void resize(int width, int height) {
        for (Screen screen : screens.values()) {
            screen.getStage().getViewport().update(width, height, true);
        }
    }

    public void dispose() {
        for (Screen screen : screens.values()) {
            screen.dispose();
        }
        dialogueUI.dispose();
        inventoryUI.dispose();
        questUI.dispose();
        if (touchControlsUI != null) touchControlsUI.dispose();
    }

    public void toggleQuestTable() {
        if (inventoryUI.isVisible()) inventoryUI.toggle();
        questUI.toggle();
    }

    public void toggleInventoryTable() {
        if (questUI.isVisible()) questUI.toggle();
        inventoryUI.toggle();
    }

    public GameScreen getGameScreen() { return (GameScreen) screens.get(GameStateManager.GameState.PLAYING); }
    public DialogueUI getDialogueUI(){ return dialogueUI; }

    private String resolveItemName(String itemKey, int amount) {
        if (amount == 1) {
            return I18nUtils.getAccusative(Assets.items, itemKey);
        }
        return I18nUtils.getPluralized(Assets.items, itemKey, amount);
    }

    public void showEarned(String itemKey, int amount) {
        String itemName = resolveItemName(itemKey, amount);

        getGameScreen().showInfoMessage(Assets.messages.format("message.generic.got", amount, itemName), 1.5f);
    }

    public void showFound(String itemKey, int amount) {
        String itemName = resolveItemName(itemKey, amount);

        getGameScreen().showInfoMessage(
            Assets.messages.format("message.found",  itemName, amount),
            1.5f
        );
    }

    public void showNotEnough(String itemKey) {
        getGameScreen().showInfoMessage(Assets.messages.format("message.generic.not_enough", I18nUtils.getPluralized(Assets.items, itemKey, 0)), 1.5f);
    }

    public void renderWorldElements(){inWorldUIRenderer.renderWorldElements();}
}
