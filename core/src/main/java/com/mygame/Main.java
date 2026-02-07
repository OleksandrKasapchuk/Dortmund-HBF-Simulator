package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygame.assets.Assets;
import com.mygame.entity.Renderable;
import com.mygame.game.GameContext;
import com.mygame.game.GameInitializer;
import com.mygame.assets.audio.MusicManager;
import com.mygame.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

        ctx.player.update(delta);
        gameInitializer.getManagerRegistry().update(delta);

        OrthographicCamera camera = gameInitializer.getManagerRegistry().getCameraManager().getCamera();
        ctx.worldManager.renderBottomLayers(camera);

        SpriteBatch batch = gameInitializer.getBatch();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        ctx.entityRenderer.renderEntities(batch, ctx.itemManager.getBackgroundItems());

        List<Renderable> renderables = new ArrayList<>();
        renderables.add(ctx.player);
        renderables.addAll(ctx.npcManager.getNpcs().stream().filter(npc -> npc.getWorld()==currentWorld).toList());
        renderables.addAll(ctx.itemManager.getForegroundItems().stream().filter(item -> item.getWorld() == currentWorld).toList());
        renderables.sort(Comparator.comparing(Renderable::getY).reversed());

        if (currentWorld != null) {
            renderWithSortedEntities(batch, currentWorld, renderables);
        }

        ctx.worldManager.drawEntities(batch, Assets.myFont);
        ctx.ui.renderWorldElements();

        batch.end();

        if (currentWorld != null) {
            currentWorld.drawZones(shapeRenderer, camera);
        }

        ctx.ui.render();
        ctx.overlay.render();
    }

    private void renderWithSortedEntities(SpriteBatch batch, World world, List<Renderable> sortedRenderables) {
        List<TiledMapTileLayer> topLayers = world.getTopLayers();

        for (TiledMapTileLayer layer : topLayers) {
            for (int row = layer.getHeight() - 1; row >= 0; row--) {
                for (Renderable entity : sortedRenderables) {
                    if (Math.floor(entity.getY() / world.tileHeight) == row) {
                        entity.draw(batch);
                    }
                }
                for (int col = 0; col < layer.getWidth(); col++) {
                    TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                    if (cell != null) {
                        batch.draw(cell.getTile().getTextureRegion(), col * world.tileWidth, row * world.tileHeight);
                    }
                }
            }
        }
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
