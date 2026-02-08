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
import com.mygame.world.World;


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

    public static void restartGame() {
        gameInitializer.initGame();
    }

    @Override
    public void render() {
        long startTime = System.nanoTime(); // старт таймера
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameInitializer.getManagerRegistry().getGameInputHandler().update();
        GameContext ctx = gameInitializer.getContext();

        switch (ctx.gsm.getState()) {
            case PLAYING:
                renderGame(delta);
                break;
            default:
                ctx.ui.render();
                break;
        }
        long endTime = System.nanoTime(); // кінець таймера
        float ms = (endTime - startTime) / 1_000_000f; // конвертація в мс
        Gdx.app.log("RenderDebug", "Frame render time: " + ms + " ms");
    }

    private void renderGame(float delta) {
        GameContext ctx = gameInitializer.getContext();
        World currentWorld = ctx.worldManager.getCurrentWorld();

        ctx.player.update(delta);
        gameInitializer.getManagerRegistry().update(delta);

        OrthographicCamera camera = gameInitializer.getManagerRegistry().getCameraManager().getCamera();
        ctx.worldManager.renderBottomLayers(camera);

        SpriteBatch batch = gameInitializer.getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        ctx.entityRenderer.renderUnsorted(batch, ctx.itemManager.getBackgroundItems());

         ctx.entityRenderer.collectRenderableEntities(currentWorld, ctx.player, ctx.npcManager.getNpcs(), ctx.itemManager.getForegroundItems());

        ctx.entityRenderer.renderWithSortedEntities(batch, currentWorld);

        ctx.ui.renderWorldElements();

        batch.end();

        currentWorld.drawZones(shapeRenderer, camera);

        ctx.ui.render();
        ctx.overlay.render();
    }


    @Override
    public void resize(int width, int height) {
        gameInitializer.getManagerRegistry().resize(width, height);
    }

    @Override
    public void dispose() {
        Assets.dispose();
        if (gameInitializer != null) gameInitializer.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        MusicManager.stopAll();
    }

    public static GameInitializer getGameInitializer() {
        return gameInitializer;
    }
}
