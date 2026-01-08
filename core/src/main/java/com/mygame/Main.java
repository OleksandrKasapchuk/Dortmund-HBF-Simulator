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
import com.mygame.assets.audio.MusicManager;


public class Main extends ApplicationAdapter {

    private static GameInitializer gameInitializer;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        Assets.load();                            // Load textures, sounds, music
        gameInitializer = new GameInitializer();
        gameInitializer.initGame();               // Initialize all game objects
        shapeRenderer = new ShapeRenderer();
    }

    public static void restartGame() {gameInitializer.initGame();}

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

        OrthographicCamera camera = gameInitializer.getManagerRegistry().getCameraManager().getCamera();

        // 1. Render the TMX map layer
        ctx.worldManager.renderMap(camera);

        // 2. Draw sprites (entities) on top
        SpriteBatch batch = gameInitializer.getBatch();
        batch.setProjectionMatrix(camera.combined);

        gameInitializer.getManagerRegistry().update(delta);

        batch.begin();
        ctx.worldManager.drawEntities(batch, Assets.myFont);
        ctx.ui.renderWorldElements();
        ctx.player.draw(batch);
        batch.end();

        // 3. Draw debug shapes
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        ctx.worldManager.getCurrentWorld().drawTransitions(shapeRenderer);

        shapeRenderer.end();

        // 4. Draw UI
        ctx.ui.render();
        ctx.darkOverlay.render();
    }

    @Override
    public void resize(int width, int height) {gameInitializer.getManagerRegistry().resize();}

    @Override
    public void dispose() {
        Assets.dispose();
        if (gameInitializer != null) gameInitializer.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        MusicManager.stopAll();
    }

    public static GameInitializer getGameInitializer() {return gameInitializer;}
}
