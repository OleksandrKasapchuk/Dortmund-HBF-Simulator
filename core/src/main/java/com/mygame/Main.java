package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygame.game.GameInitializer;
import com.mygame.managers.global.save.AutoSaveManager;
import com.mygame.world.WorldManager;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.entity.player.Player;
import com.mygame.ui.UIManager;


public class Main extends ApplicationAdapter {

    private static GameInitializer gameInitializer;
    private ShapeRenderer shapeRenderer;
    private DarkOverlay darkOverlay;

    @Override
    public void create() {
        Assets.load();                            // Load textures, sounds, music
        gameInitializer = new GameInitializer();
        gameInitializer.initGame();               // Initialize all game objects
        shapeRenderer = new ShapeRenderer();
        darkOverlay = new DarkOverlay();
    }

    public static void restartGame() {gameInitializer.initGame();}

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        AutoSaveManager.update(delta);

        Player player = gameInitializer.getPlayer();

        gameInitializer.getGameInputHandler().handleInput();

        UIManager uiManager = gameInitializer.getManagerRegistry().getUiManager();

        gameInitializer.getManagerRegistry().getGameStateManager().handleStonedPlayer(player, gameInitializer.getManagerRegistry().getNpcManager());


        switch (gameInitializer.getManagerRegistry().getGameStateManager().getState()) {
            case PLAYING:
                renderGame(delta);
                break;
            case DEATH:
                uiManager.render();
                break;
            case SETTINGS:
            case MENU:
            case PAUSED:
            case MAP:
                uiManager.update(delta, player);
                uiManager.render();
                break;
        }
    }

    private void renderGame(float delta) {
        Player player = gameInitializer.getPlayer();
        player.update(delta);

        WorldManager.update(delta, player, gameInitializer.getManagerRegistry().getUiManager().isInteractPressed(), darkOverlay);
        darkOverlay.update(delta);
        OrthographicCamera camera = gameInitializer.getManagerRegistry().getCameraManager().getCamera();

        // 1. Render the TMX map layer
        WorldManager.renderMap(camera);

        // 2. Draw sprites (entities) on top
        SpriteBatch batch = gameInitializer.getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        gameInitializer.getManagerRegistry().update(delta);
        WorldManager.drawEntities(batch, Assets.myFont, player); // Corrected method
        player.draw(batch);
        gameInitializer.getManagerRegistry().render();
        batch.end();

        // 3. Draw debug shapes
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (WorldManager.getCurrentWorld() != null) {
            WorldManager.getCurrentWorld().drawTransitions(shapeRenderer);
        }
        shapeRenderer.end();

        // 4. Draw UI
        gameInitializer.getManagerRegistry().getUiManager().render();
        darkOverlay.render();
    }

    @Override
    public void resize(int width, int height) {gameInitializer.getManagerRegistry().resize();}

    @Override
    public void dispose() {
        AutoSaveManager.saveGame();
        Assets.dispose();
        if (gameInitializer != null) gameInitializer.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        WorldManager.disposeWorlds();
        MusicManager.stopAll();
    }

    public static GameInitializer getGameInitializer() {return gameInitializer;}
}
