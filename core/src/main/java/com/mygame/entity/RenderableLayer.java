package com.mygame.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygame.world.World;

public class RenderableLayer implements Renderable {
    private final TiledMapTileLayer layer;
    private final OrthogonalTiledMapRenderer renderer;
    private final World world;

    public RenderableLayer(TiledMapTileLayer layer, OrthogonalTiledMapRenderer renderer, World world) {
        this.layer = layer;
        this.renderer = renderer;
        this.world = world;
    }

    @Override
    public void draw(SpriteBatch batch) {
        renderer.getBatch().begin();
        renderer.renderTileLayer(layer);
        renderer.getBatch().end();
    }

    @Override
    public float getY() {
        return 0; // Or some other logic to determine Y
    }

    @Override
    public World getWorld() {
        return world;
    }
}
