package com.mygame.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygame.entity.item.Item;
import com.mygame.entity.npc.NPC;
import com.mygame.world.transition.Transition;

import java.util.ArrayList;
import java.util.List;

public class World {
    private final String name;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final TiledMapTileLayer collisionLayer;
    public final int tileSize;
    public final int mapWidth;
    public final int mapHeight;

    private final ArrayList<Transition> transitions = new ArrayList<>();
    private final ArrayList<NPC> npcs = new ArrayList<>();
    private final ArrayList<Item> backgroundItems = new ArrayList<>();
    private final ArrayList<Item> foregroundItems = new ArrayList<>();
    private final ArrayList<Item> pfands = new ArrayList<>();

    private final int[] bottomLayersIndices;
    private final int[] topLayersIndices;

    public World(String name, String pathToMapFile) {
        this.name = name;
        this.map = new TmxMapLoader().load(pathToMapFile);
        this.mapRenderer = new OrthogonalTiledMapRenderer(this.map);
        this.collisionLayer = (TiledMapTileLayer) this.map.getLayers().get("collision");

        // --- Layers to be rendered BEHIND the player ---
        ArrayList<Integer> bottomIndices = new ArrayList<>();
        int backgroundIndex = this.map.getLayers().getIndex("background");
        if (backgroundIndex != -1) {
            bottomIndices.add(backgroundIndex);
        }
        this.bottomLayersIndices = bottomIndices.stream().mapToInt(i -> i).toArray();

        // --- Layers to be rendered IN FRONT of the player ---
        ArrayList<Integer> topIndices = new ArrayList<>();
        int collisionIndex = this.map.getLayers().getIndex("collision");
        if (collisionIndex != -1) {
            topIndices.add(collisionIndex);
        }
        this.topLayersIndices = topIndices.stream().mapToInt(i -> i).toArray();

        this.tileSize = this.map.getProperties().get("tilewidth", Integer.class);
        this.mapWidth = this.map.getProperties().get("width", Integer.class) * tileSize;
        this.mapHeight = this.map.getProperties().get("height", Integer.class) * tileSize;
    }

    public boolean isSolid(float x, float y) {
        if (collisionLayer == null) {
            return false;
        }
        int tileX = (int) (x / tileSize);
        int tileY = (int) (y / tileSize);

        if (tileX < 0 || tileX >= collisionLayer.getWidth() || tileY < 0 || tileY >= collisionLayer.getHeight()) {
            return true; // Out of bounds is solid
        }

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);
        return cell != null;
    }

    private void renderLayers(OrthographicCamera camera, int[] layers) {
        mapRenderer.setView(camera);
        // Expand the renderer's view bounds only at the bottom to prevent culling
        float bottomBuffer = 40f;
        mapRenderer.getViewBounds().y -= bottomBuffer;
        mapRenderer.getViewBounds().height += bottomBuffer;

        if (layers.length > 0) {
            mapRenderer.render(layers);
        }
    }

    public void renderBottomLayers(OrthographicCamera camera) {
        renderLayers(camera, bottomLayersIndices);
    }

    public void renderTopLayers(OrthographicCamera camera) {
        renderLayers(camera, topLayersIndices);
    }

    public void drawTransitions(ShapeRenderer shapeRenderer) {
        for (Transition t : transitions) {
            t.drawDebug(shapeRenderer);
        }
    }

    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
    }

    // --- Getters ---
    public TiledMap getMap() { return map; }
    public String getName() { return name; }
    public ArrayList<Transition> getTransitions() { return transitions; }
    public ArrayList<NPC> getNpcs() { return npcs; }
    public List<Item> getBackgroundItems() { return backgroundItems; }
    public List<Item> getForegroundItems() { return foregroundItems; }
    public List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<>(backgroundItems);
        allItems.addAll(foregroundItems);
        return allItems;
    }
    public ArrayList<Item> getPfands() { return pfands; }
}
