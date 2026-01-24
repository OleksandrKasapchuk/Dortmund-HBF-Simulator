package com.mygame.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.assets.Assets;
import com.mygame.entity.item.Item;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.DayManager;
import com.mygame.game.GameStateManager;
import com.mygame.quest.QuestManager;
import com.mygame.ui.inGameUI.DialogueUI;
import com.mygame.ui.inGameUI.InventoryUI;
import com.mygame.ui.inGameUI.QuestUI;
import com.mygame.ui.inGameUI.TouchControlsUI;
import com.mygame.ui.screenUI.*;
import com.mygame.world.WorldManager;

import java.util.EnumMap;
import java.util.Map;

public class UIManager {

    private final SpriteBatch batch;
    private final Player player;
    private final QuestManager questManager;
    private final WorldManager worldManager;

    private final Map<GameStateManager.GameState, Screen> screens = new EnumMap<>(GameStateManager.GameState.class);
    private Screen currentScreen;

    private QuestUI questUI;
    private InventoryUI inventoryUI;
    private DialogueUI dialogueUI;
    private TouchControlsUI touchControlsUI;

    private final GlyphLayout layout = new GlyphLayout();

    public UIManager(SpriteBatch batch, Player player, Skin skin, QuestManager questManager, WorldManager worldManager, DayManager dayManager) {
        this.batch = batch;
        this.player = player;
        this.questManager = questManager;
        this.worldManager = worldManager;

        createScreens(skin, dayManager);

        currentScreen = screens.get(GameStateManager.GameState.MENU);
        Gdx.input.setInputProcessor(currentScreen.getStage());

        questUI = new QuestUI(skin, getGameScreen().getStage(), 1200, 900, questManager);
        inventoryUI = new InventoryUI(getGameScreen().getStage(), skin);
        dialogueUI = new DialogueUI(skin, getGameScreen().getStage(), 1950, 250, 25f, 10f);

        subscribeToEvents();

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

    private void subscribeToEvents() {
        EventBus.subscribe(Events.MessageEvent.class, event -> getGameScreen().showInfoMessage(event.message(), 1.5f));
        EventBus.subscribe(Events.QuestStartedEvent.class, event -> {
            if(questManager.getQuest(event.questId()).getNotify()) getGameScreen().showInfoMessage(Assets.messages.get("message.quest.new"), 1.5f);
        });
        EventBus.subscribe(Events.QuestCompletedEvent.class, event -> {
            if(questManager.getQuest(event.questId()).getNotify()) getGameScreen().showInfoMessage(Assets.messages.format("message.generic.quest.completed", Assets.quests.get("quest." + event.questId() + ".name")), 1.5f);
        });
        EventBus.subscribe(Events.AddItemMessageEvent.class, event -> showEarned(event.item().getNameKey(), event.amount()));
        EventBus.subscribe(Events.NotEnoughMessageEvent.class, event -> showNotEnough(event.item().getNameKey()));
        EventBus.subscribe(Events.InteractEvent.class, e -> handleInteraction());
        EventBus.subscribe(Events.GameStateChangedEvent.class, e -> setCurrentStage(e.newState()));
    }

    public void setCurrentStage(GameStateManager.GameState state) {
        Screen newScreen = screens.get(state);
        if (newScreen != null) {
            currentScreen = newScreen;
            Gdx.input.setInputProcessor(currentScreen.getStage());
        }
    }

    public void update(float delta) {
        if (currentScreen instanceof GameScreen) {
            ((GameScreen) currentScreen).update(delta);
            inventoryUI.update(player);
        } else if (currentScreen instanceof MapScreen) {
            ((MapScreen) currentScreen).update();
        }
        currentScreen.getStage().act(delta);
    }

    public void renderWorldElements() {
        for (NPC npc : worldManager.getCurrentWorld().getNpcs()) {
            if (npc.isPlayerNear(player)) {
                Assets.myFont.draw(batch, Assets.ui.get("interact"), npc.getX() - 100, npc.getY() + npc.getHeight() + 40);
            }
        }
        for (Item item : worldManager.getCurrentWorld().getAllItems()) {
            if ((!item.isInteractable() && !item.isSearchable()) || (item.getQuestId() != null && !questManager.hasQuest(item.getQuestId())) || !item.isPlayerNear(player, item.getDistance()) || item.isSearched()) continue;
            drawText(item.isSearchable() ? Assets.ui.get("interact.search") : Assets.ui.get("interact"), item.getCenterX(), item.getCenterY());
        }
    }

    private void handleInteraction() {
        for (Item item : worldManager.getCurrentWorld().getAllItems()) {
            if ((!item.isInteractable() && !item.isSearchable()) || (item.getQuestId() != null && !questManager.hasQuest(item.getQuestId())) || !item.isPlayerNear(player, item.getDistance()) || item.isSearched()) continue;
            EventBus.fire(new Events.ItemInteractionEvent(item, player));
            return;
        }
    }

    public void render() {
        currentScreen.getStage().draw();
    }

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

    public void showEarned(String thing, int amount) {
        getGameScreen().showInfoMessage(Assets.messages.format("message.generic.got", amount, Assets.items.get(thing)), 1.5f);
    }

    public void showNotEnough(String thing) {
        getGameScreen().showInfoMessage(Assets.messages.format("message.generic.not_enough", Assets.items.get(thing)), 1.5f);
    }

    public void drawText(String text, float x, float y) {
        layout.setText(Assets.myFont, text);
        Assets.myFont.draw(batch, text, x - layout.width / 2f, y + 60);
    }
}
