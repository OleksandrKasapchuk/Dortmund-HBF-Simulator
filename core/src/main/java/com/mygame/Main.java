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
    private boolean loading = true;

    @Override
    public void create() {
        Assets.load();                            // Load textures, sounds, music
        gameInitializer = new GameInitializer();
        shapeRenderer = new ShapeRenderer();
        restartGame();              // Initialize all game objects
    }

    public static void restartGame() {
        gameInitializer.initGame();
        gameInitializer.loadGameFromServer();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (gameInitializer.getManagerRegistry() != null){
            gameInitializer.getManagerRegistry().getGameInputHandler().update();
            GameContext ctx = gameInitializer.getContext();
        }
        switch (gameInitializer.getGameStateManager().getState()) {
            case PLAYING:
                renderGame(delta);
                break;
            default:
                gameInitializer.getUiManager().render();
                break;
        }
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

        gameInitializer.getUiManager().renderWorldElements();

        batch.end();

        currentWorld.drawZones(shapeRenderer, camera);

        gameInitializer.getUiManager().render();
        ctx.overlay.render();
    }


    @Override
    public void resize(int width, int height) {
        if (gameInitializer.getManagerRegistry() != null) {
            gameInitializer.getManagerRegistry().resize(width, height);
        }
        gameInitializer.getUiManager().resize(width, height);
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
