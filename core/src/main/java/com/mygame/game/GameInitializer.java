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
import com.mygame.game.save.DataLoader;
import com.mygame.managers.ManagerRegistry;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.ui.load.SkinLoader;
import com.mygame.assets.audio.MusicManager;
import com.mygame.world.World;

public class GameInitializer {

    private Player player;
    private SpriteBatch batch;
    private Skin skin;
    private ManagerRegistry managerRegistry;

    public void loadGameFromServer(Runnable onLoaded) {
        try {
            Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
            request.setUrl("http://localhost:8000/api/load/?format=json");

            Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

                @Override
                public void handleHttpResponse(Net.HttpResponse httpResponse) {
                    if (httpResponse.getStatus().getStatusCode() != 200) {
                        Gdx.app.error("GameInitializer", "Failed to load game from server. Status: " + httpResponse.getStatus().getStatusCode());
                        Gdx.app.postRunnable(() -> {
                            initGame(); // Fallback to local/default settings
                        });
                        return;
                    }

                    String json = httpResponse.getResultAsString();
                    try {
                        Json gdxJson = new Json();
                        gdxJson.setUsePrototypes(false);
                        gdxJson.setIgnoreUnknownFields(true);
                        gdxJson.setOutputType(JsonWriter.OutputType.json);

                        GameSettings settings = gdxJson.fromJson(GameSettings.class, json);

                        Gdx.app.postRunnable(() -> {
                            SettingsManager.save(settings);
                            initGame(); // створюємо SpriteBatch і все інше вже безпечно
                        });
                    } catch (SerializationException e) {
                        Gdx.app.error("GameInitializer", "Failed to parse game data from server.", e);
                        Gdx.app.postRunnable(() -> {
                            initGame(); // Fallback to local/default settings
                        });
                    }
                }

                @Override
                public void failed(Throwable t) {
                    Gdx.app.error("GameInitializer", "Game load request failed.", t);
                    Gdx.app.postRunnable(() -> {
                        initGame(); // Fallback to local/default settings
                    });
                }
                @Override
                public void cancelled() {
                    Gdx.app.log("GameInitializer", "Game load request cancelled.");
                     Gdx.app.postRunnable(() -> {
                        initGame(); // Fallback
                    });
                }
            });
        } catch (Exception e) {
            Gdx.app.error("GameInitializer", "Error creating load request.", e);
            Gdx.app.postRunnable(this::initGame);
        }
    }

    public void initGame() {
        EventBus.clear();

        if (managerRegistry != null) managerRegistry.dispose(false);
        if (batch != null) batch.dispose();

        MusicManager.init();

        batch = new SpriteBatch();

        skin = SkinLoader.loadSkin();

        GameSettings settings = SettingsManager.load();

        // 1. Створюємо гравця з початковими даними, але без світу
        player = new Player(500, 80, 160, settings.playerX, settings.playerY, null);
        player.getStatusController().setHunger(settings.playerHunger);
        player.getStatusController().setThirst(settings.playerThirst);
        player.getStatusController().setVibe(settings.playerVibe);
        player.setState(settings.playerState);

        // 2. Створюємо реєстр менеджерів, передаючи туди вже існуючого гравця
        managerRegistry = new ManagerRegistry(batch, player, skin);
        GameContext ctx = managerRegistry.getContext();

        // 3. Ініціалізуємо інвентар гравця, використовуючи реєстр предметів з контексту
        player.getInventory().init(ctx.itemRegistry);

        // 4. Завантажуємо дані гри
        DataLoader.load(ctx, settings);

        // 5. Встановлюємо початковий світ для гравця та менеджера світів
        World startWorld = ctx.worldManager.getWorld(settings.currentWorldName != null ? settings.currentWorldName : "main");
        player.setWorld(startWorld);
        ctx.worldManager.setCurrentWorld(startWorld);
    }

    public ManagerRegistry getManagerRegistry() { return managerRegistry; }
    public SpriteBatch getBatch() { return batch; }
    public GameContext getContext() { return managerRegistry.getContext(); }

    public void dispose() {
        if (managerRegistry != null) managerRegistry.dispose();
        if (batch != null) batch.dispose();
        if (skin != null) skin.dispose();
    }
}
