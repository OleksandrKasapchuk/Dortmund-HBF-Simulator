package com.mygame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.auth.AuthManager;
import com.mygame.game.save.DataLoader;
import com.mygame.managers.ManagerRegistry;
import com.mygame.game.save.data.ServerSaveData;
import com.mygame.game.save.SettingsManager;
import com.mygame.ui.UIManager;
import com.mygame.ui.load.SkinLoader;
import com.mygame.assets.audio.MusicManager;
import com.mygame.world.World;

public class GameInitializer {

    private Player player;
    private SpriteBatch batch;
    private Skin skin;
    private ManagerRegistry managerRegistry;
    private UIManager uiManager;
    public GameStateManager gsm;

    public void loadGameFromServer() {
        if (!AuthManager.hasToken()) {
            Gdx.app.log("GameInitializer", "No token, skipping server load.");
            initGame();
            return;
        }

        try {
            Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
            request.setUrl("https://hbf-simulator-backend.onrender.com/api/load/?format=json&username=" + AuthManager.getUsername());
            request.setHeader("Authorization", "Token " + AuthManager.getToken());
            System.out.println("Token: " + AuthManager.getToken());
            Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

                @Override
                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                    if (httpResponse.getStatus().getStatusCode() != 200) {
                        Gdx.app.error("GameInitializer", "Failed to load game from server. Status: " + httpResponse.getStatus().getStatusCode());
                        if (httpResponse.getStatus().getStatusCode() == 401) { // Unauthorized
                            AuthManager.logout();
                        }
                        Gdx.app.postRunnable(GameInitializer.this::initGame);
                        return;
                    }

                    String json = httpResponse.getResultAsString();
                    try {
                        Json gdxJson = new Json();
                        gdxJson.setUsePrototypes(false);
                        gdxJson.setIgnoreUnknownFields(true);
                        gdxJson.setOutputType(JsonWriter.OutputType.json);

                        ServerSaveData settings = gdxJson.fromJson(ServerSaveData.class, json);

                        Gdx.app.postRunnable(() -> {
                            SettingsManager.saveServer(settings);
                            initGame();
                        });
                    } catch (SerializationException e) {
                        Gdx.app.error("GameInitializer", "Failed to parse game data from server.", e);
                        Gdx.app.postRunnable(GameInitializer.this::initGame);
                    }
                }

                @Override
                public void failed(Throwable t) {
                    Gdx.app.error("GameInitializer", "Game load request failed.", t);
                    Gdx.app.postRunnable(GameInitializer.this::initGame);
                }
                @Override
                public void cancelled() {
                    Gdx.app.log("GameInitializer", "Game load request cancelled.");
                     Gdx.app.postRunnable(GameInitializer.this::initGame);
                }
            });
        } catch (Exception e) {
            Gdx.app.error("GameInitializer", "Error creating load request.", e);
            Gdx.app.postRunnable(this::initGame);
        }
    }

    public void initGame() {
        EventBus.clear();
        EventBus.subscribe(Events.TokenEvent.class, (event) -> initAuthorithed());
        if (managerRegistry != null) managerRegistry.dispose(false);
        if (batch != null) batch.dispose();

        MusicManager.init();

        batch = new SpriteBatch();

        skin = SkinLoader.loadSkin();
        uiManager = new UIManager(batch, skin);
        gsm = new GameStateManager(uiManager);

        if (AuthManager.hasToken()) {
            // якщо токен є — відразу ініціалізуємо авторизовану гру
            initAuthorithed();
        } else {
            // показати логін/реєстрацію
            gsm.setState(GameStateManager.GameState.AUTH);
            uiManager.setCurrentStage(GameStateManager.GameState.AUTH);
        }
    }

    public void initAuthorithed(){
        ServerSaveData settings = SettingsManager.loadServer();
        // 1. Створюємо гравця з початковими даними, але без світу
        player = new Player(500, 80, 160, settings.playerX, settings.playerY, null);
        player.getStatusController().setHunger(settings.playerHunger);
        player.getStatusController().setThirst(settings.playerThirst);
        player.getStatusController().setVibe(settings.playerVibe);
        player.setState(settings.playerState);

        // 2. Створюємо реєстр менеджерів, передаючи туди вже існуючого гравця
        managerRegistry = new ManagerRegistry(batch, player, skin, uiManager, gsm);
        GameContext ctx = managerRegistry.getContext();

        // 3. Ініціалізуємо інвентар гравця, використовуючи реєстр предметів з контексту
        player.getInventory().init(ctx.itemRegistry);

        // 4. Завантажуємо дані гри
        DataLoader.load(ctx, settings);

        // 5. Встановлюємо початковий світ для гравця та менеджера світів
        World startWorld = ctx.worldManager.getWorld(settings.currentWorldName != null ? settings.currentWorldName : "main");
        player.setWorld(startWorld);
        ctx.worldManager.setCurrentWorld(startWorld);
        gsm.setState(GameStateManager.GameState.MENU);
        uiManager.setCurrentStage(GameStateManager.GameState.MENU);
    }

    public ManagerRegistry getManagerRegistry() { return managerRegistry; }
    public SpriteBatch getBatch() { return batch; }
    public GameContext getContext() { return managerRegistry.getContext(); }

    public void dispose() {
        if (managerRegistry != null) managerRegistry.dispose();
        if (batch != null) batch.dispose();
        if (skin != null) skin.dispose();
    }
    public UIManager getUiManager(){
        return uiManager;
    }
    public GameStateManager getGameStateManager(){
        return gsm;
    }
}
