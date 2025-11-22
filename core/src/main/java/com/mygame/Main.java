package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygame.game.GameInitializer;
import com.mygame.game.GameSettings;
import com.mygame.game.SettingsManager;
import com.mygame.managers.global.WorldManager;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.entity.Player;
import com.mygame.ui.UIManager;

public class Main extends ApplicationAdapter {

    private static GameInitializer gameInitializer;
    private ShapeRenderer shapeRenderer;
    private Player.State previousPlayerState;

    @Override
    public void create() {
        System.out.println("Main: Starting application...");
        Assets.load();                            // Load textures, sounds, music
        System.out.println("Main: Assets loaded.");
        gameInitializer = new GameInitializer();
        System.out.println("Main: GameInitializer created.");
        gameInitializer.initGame();               // Initialize all game objects
        System.out.println("Main: Game initialized.");
        shapeRenderer = new ShapeRenderer();
        previousPlayerState = gameInitializer.getPlayer().getState();
        System.out.println("Main: Create method finished.");
    }

    public static void restartGame() {
        if (gameInitializer != null) {
            gameInitializer.initGame();
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameInitializer.getGameInputHandler().handleInput();

        Player player = gameInitializer.getPlayer();
        if (player.getState() == Player.State.STONED && previousPlayerState == Player.State.NORMAL) {
            gameInitializer.getGameInputHandler().handleStonedPlayer(player, gameInitializer.getManagerRegistry().getNpcManager());
        }
        previousPlayerState = player.getState();

        UIManager uiManager = gameInitializer.getManagerRegistry().getUiManager();

        switch (gameInitializer.getManagerRegistry().getGameStateManager().getState()) {
            case PLAYING:
                renderGame(delta);
                break;
            case DEATH:
                uiManager.render();
                break;
            case MENU:
            case PAUSED:
            case SETTINGS:
                uiManager.update(delta, gameInitializer.getPlayer());
                uiManager.render();
                break;
        }
    }

    private void renderGame(float delta) {
        Player player = gameInitializer.getPlayer();
        player.update(delta);

        WorldManager.update(delta, player, gameInitializer.getManagerRegistry().getUiManager().isInteractPressed());

        SpriteBatch batch = gameInitializer.getBatch();

        // Draw sprites
        batch.setProjectionMatrix(gameInitializer.getManagerRegistry().getCameraManager().getCamera().combined);
        batch.begin();
        gameInitializer.getManagerRegistry().update(delta);
        WorldManager.draw(batch, gameInitializer.getFont(), player);
        player.draw(batch);
        gameInitializer.getManagerRegistry().render();
        batch.end();

        // Draw shapes
        shapeRenderer.setProjectionMatrix(gameInitializer.getManagerRegistry().getCameraManager().getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (WorldManager.getCurrentWorld() != null) {
            WorldManager.getCurrentWorld().drawTransitions(shapeRenderer);
        }
        shapeRenderer.end();

        // Draw UI
        gameInitializer.getManagerRegistry().getUiManager().render();
    }

    @Override
    public void resize(int width, int height) {
        gameInitializer.getManagerRegistry().resize();
    }

    @Override
    public void dispose() {
        GameSettings settings = SettingsManager.load();
        settings.playerX = gameInitializer.getPlayer().getX();
        settings.playerY = gameInitializer.getPlayer().getY();
        if (WorldManager.getCurrentWorld() != null) {
            settings.currentWorldName = WorldManager.getCurrentWorld().getName();
        }
        SettingsManager.save(settings);

        Assets.dispose();
        if (gameInitializer != null) gameInitializer.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        WorldManager.disposeWorlds();
        MusicManager.stopAll();
    }

    public static GameInitializer getGameInitializer() {
        return gameInitializer;
    }
}
