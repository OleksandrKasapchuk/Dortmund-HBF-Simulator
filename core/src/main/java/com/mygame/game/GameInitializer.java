package com.mygame.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
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
    private boolean isLoading = false; // Прапор, щоб уникнути повторного завантаження

    public void loadGameFromServer() {
        Gdx.app.log("GameInitializer", "loadGameFromServer() called.");
        if (isLoading) {
            Gdx.app.log("GameInitializer", "Already loading, exiting.");
            return;
        }

        if (!AuthManager.hasToken()) {
            Gdx.app.log("GameInitializer", "No token found, returning to AUTH screen.");
            gsm.setState(GameStateManager.GameState.AUTH);
            uiManager.setCurrentStage(GameStateManager.GameState.AUTH);
            System.out.println("no token return to auth screen");
            return;
        }

        Gdx.app.log("GameInitializer", "Starting server load process...");
        isLoading = true; // Починаємо процес завантаження
        gsm.setState(GameStateManager.GameState.LOADING_SERVER);
        uiManager.setCurrentStage(GameStateManager.GameState.LOADING_SERVER);
        requestServerLoad();
    }

    private void requestServerLoad(){
        Gdx.app.log("GameInitializer", "requestServerLoad() called with token: " + AuthManager.getToken());
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl("https://hbf-simulator-backend.onrender.com/api/load/?format=json&username=" + AuthManager.getUsername());
        request.setHeader("Authorization", "Token " + AuthManager.getToken());

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.log("GameInitializer", "handleHttpResponse status: " + httpResponse.getStatus().getStatusCode());
                if (httpResponse.getStatus().getStatusCode() != 200) {
                    if (httpResponse.getStatus().getStatusCode() == 401) {
                        Gdx.app.log("GameInitializer", "Auth failed (401), logging out.");
                        AuthManager.logout();
                        isLoading = false; // Скидаємо прапор при помилці
                        return;
                    }
                    Gdx.app.log("GameInitializer", "Request failed, retrying...");
                    retryLater();
                    return;
                }

                String json = httpResponse.getResultAsString();
                try {
                    Gdx.app.log("GameInitializer", "Response successful, parsing data...");
                    Json gdxJson = new Json();
                    gdxJson.setUsePrototypes(false);
                    gdxJson.setIgnoreUnknownFields(true);
                    Gdx.app.log("GameInitializer", "JSON response:\n" + json);
                    ServerSaveData settings = gdxJson.fromJson(ServerSaveData.class, json);

                    Gdx.app.postRunnable(() -> {
                        Gdx.app.log("GameInitializer", "Data parsed, calling initAuthorithed().");
                        SettingsManager.saveServer(settings);
                        initAuthorithed();
                    });

                } catch (Exception e) {
                    Gdx.app.error("GameInitializer", "JSON parsing failed, retrying...", e);
                    retryLater();
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("GameInitializer", "Request failed (Throwable), retrying...", t);
                isLoading = false; // Скидаємо прапор при помилці
                retryLater();
            }

            @Override
            public void cancelled() {
                Gdx.app.log("GameInitializer", "Request cancelled, retrying...");
                isLoading = false; // Скидаємо прапор при помилці
                retryLater();
            }
        });
    }

    private void retryLater() {
        Gdx.app.postRunnable(() -> {
            uiManager.setServerStatus("Server waking up...");
            Gdx.app.postRunnable(this::requestServerLoad);
        });
    }

    public void initGame() {
        Gdx.app.log("GameInitializer", "initGame() called. Clearing event bus and resetting state.");
        isLoading = false; // Скидаємо прапор при кожному перезапуску
        EventBus.clear();

        if (managerRegistry != null) {
            managerRegistry.dispose(false);
        } else if (uiManager != null) {
            uiManager.dispose();
        }
        if (batch != null) batch.dispose();

        MusicManager.init();

        batch = new SpriteBatch();
        skin = SkinLoader.loadSkin();
        uiManager = new UIManager(batch, skin);
        gsm = new GameStateManager(uiManager);

        AuthManager.init(this);
        Gdx.app.log("GameInitializer", "Subscribing to TokenEvent.");
        EventBus.subscribe(Events.TokenEvent.class, (event) -> {
            Gdx.app.log("GameInitializer", "TokenEvent received, calling loadGameFromServer().");
            loadGameFromServer();
        });

        Gdx.app.log("GameInitializer", "Checking for existing token...");
        if (AuthManager.hasToken()) {
            Gdx.app.log("GameInitializer", "Token found, calling loadGameFromServer() directly.");
            loadGameFromServer();
        } else {
            Gdx.app.log("GameInitializer", "No token, setting state to AUTH.");
            gsm.setState(GameStateManager.GameState.AUTH);
            uiManager.setCurrentStage(GameStateManager.GameState.AUTH);
        }
    }

    public void initAuthorithed(){
        Gdx.app.log("GameInitializer", "initAuthorithed() called.");
        ServerSaveData settings = SettingsManager.loadServer();
        player = new Player(500, 80, 160, settings.playerX, settings.playerY, null);
        player.getStatusController().setHunger(settings.playerHunger);
        player.getStatusController().setThirst(settings.playerThirst);
        player.getStatusController().setVibe(settings.playerVibe);
        player.setState(settings.playerState);

        managerRegistry = new ManagerRegistry(batch, player, skin, uiManager, gsm);
        GameContext ctx = managerRegistry.getContext();

        player.getInventory().init(ctx.itemRegistry);
        DataLoader.load(ctx, settings);

        World startWorld = ctx.worldManager.getWorld(settings.currentWorldName != null ? settings.currentWorldName : "main");
        player.setWorld(startWorld);
        ctx.worldManager.setCurrentWorld(startWorld);

        isLoading = false; // Завершили завантаження та ініціалізацію
        Gdx.app.log("GameInitializer", "Authorization and initialization complete. Setting state to MENU.");
        gsm.setState(GameStateManager.GameState.MENU);
        uiManager.setCurrentStage(GameStateManager.GameState.MENU);
    }

    public ManagerRegistry getManagerRegistry() { return managerRegistry; }
    public SpriteBatch getBatch() { return batch; }
    public GameContext getContext() { return managerRegistry.getContext(); }

    public void dispose() {
        if (managerRegistry != null)
            managerRegistry.dispose();
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
