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
    }

    private void renderGame(float delta) {
        GameContext ctx = gameInitializer.getContext();
        World currentWorld = ctx.worldManager.getCurrentWorld();

        // 1. Update all logic
        ctx.player.update(delta);
        gameInitializer.getManagerRegistry().update(delta);

        // 2. Get the updated camera
        OrthographicCamera camera = gameInitializer.getManagerRegistry().getCameraManager().getCamera();

        // 3. Render the absolute bottom layer (background/floor)
        ctx.worldManager.renderBottomLayers(camera);

        SpriteBatch batch = gameInitializer.getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (currentWorld != null) {
            // 4. Draw background items (carpets, etc.)
            ctx.itemManager.renderBackgroundItems(batch);

            // 5. Draw player and NPCs
            ctx.player.draw(batch);
            ctx.npcManager.renderNpcs(batch);
        }

        batch.end();

        // 6. Render the collision layer (walls), which now covers the player
        ctx.worldManager.renderTopLayers(camera);

        // 7. Draw foreground items (e.g., items on tables) over the walls
        batch.begin();
        if (currentWorld != null) {
            ctx.itemManager.renderForegroundItems(batch);
        }

        // Draw non-gameplay world elements like transition texts
        ctx.worldManager.drawEntities(batch, Assets.myFont);
        ctx.ui.renderWorldElements();

        batch.end();


        if (currentWorld != null) {
            currentWorld.drawZones(shapeRenderer, camera);
        }


        // 9. Draw screen-space UI
        ctx.ui.render();
        ctx.darkOverlay.render();
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
