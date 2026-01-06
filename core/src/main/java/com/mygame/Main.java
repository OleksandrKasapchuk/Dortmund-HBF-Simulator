package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygame.assets.Assets;
import com.mygame.game.GameContext;
import com.mygame.game.GameInitializer;
import com.mygame.game.save.AutoSaveManager;
import com.mygame.world.DarkOverlay;
import com.mygame.world.WorldManager;
import com.mygame.assets.audio.MusicManager;


public class Main extends ApplicationAdapter {

    private static GameInitializer gameInitializer;
    private ShapeRenderer shapeRenderer;
    private DarkOverlay darkOverlay;
    private AutoSaveManager autoSaveManager;

    @Override
    public void create() {
        Assets.load();                            // Load textures, sounds, music
        gameInitializer = new GameInitializer();
        gameInitializer.initGame();               // Initialize all game objects
        shapeRenderer = new ShapeRenderer();
        darkOverlay = new DarkOverlay();
        autoSaveManager = new AutoSaveManager(gameInitializer.getContext());
    }

    public static void restartGame() {gameInitializer.initGame();}

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        autoSaveManager.update(delta);

        gameInitializer.getManagerRegistry().getGameInputHandler().update();
        GameContext ctx = gameInitializer.getContext();

        switch (ctx.gsm.getState()) {
            case PLAYING:
                renderGame(delta);
                break;
            case DEATH:
            case SETTINGS:
            case MENU:
            case PAUSED:
            case MAP:
                ctx.ui.render();
                break;
        }
    }

    private void renderGame(float delta) {
        GameContext ctx = gameInitializer.getContext();
        ctx.player.update(delta);

        WorldManager.update(delta, ctx.player, ctx.ui.isInteractPressed(), darkOverlay);
        darkOverlay.update(delta);
        OrthographicCamera camera = gameInitializer.getManagerRegistry().getCameraManager().getCamera();

        // 1. Render the TMX map layer
        WorldManager.renderMap(camera);

        // 2. Draw sprites (entities) on top
        SpriteBatch batch = gameInitializer.getBatch();
        batch.setProjectionMatrix(camera.combined);

        gameInitializer.getManagerRegistry().update(delta);

        batch.begin();
        WorldManager.drawEntities(batch, Assets.myFont, ctx.player);
        ctx.ui.renderWorldElements();
        ctx.player.draw(batch);
        batch.end();

        // 3. Draw debug shapes
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        WorldManager.getCurrentWorld().drawTransitions(shapeRenderer);

        shapeRenderer.end();

        // 4. Draw UI
        ctx.ui.render();
        darkOverlay.render();
    }

    @Override
    public void resize(int width, int height) {gameInitializer.getManagerRegistry().resize();}

    @Override
    public void dispose() {
        Assets.dispose();
        if (gameInitializer != null) gameInitializer.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        WorldManager.disposeWorlds();
        MusicManager.stopAll();
    }

    public static GameInitializer getGameInitializer() {return gameInitializer;}
}
