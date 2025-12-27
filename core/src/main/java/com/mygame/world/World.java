package com.mygame.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.mygame.assets.Assets;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.item.Item;
import com.mygame.world.transition.Transition;

import java.util.ArrayList;

/**
 * Represents a game world, loaded from a TMX map file.
 * This class is a data container for the map, its layers, and the entities within it.
 */
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
    private final ArrayList<Item> items = new ArrayList<>();
    private final ArrayList<Item> pfands = new ArrayList<>();

    public World(String name, String pathToMapFile) {
        this.name = name;
        this.map = new TmxMapLoader().load(pathToMapFile);
        this.mapRenderer = new OrthogonalTiledMapRenderer(this.map);
        this.collisionLayer = (TiledMapTileLayer) this.map.getLayers().get("collision");

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

    public void renderMap(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void draw(SpriteBatch batch) {
        for (NPC npc : npcs) npc.draw(batch);
        for (Item item : items) item.draw(batch);

        for (Transition transition : transitions) {
            float textX = transition.area.x + transition.area.width / 2 - 50;
            float textY = transition.area.y + transition.area.height / 2;
            Assets.myFont.draw(batch, Assets.ui.get("ui.world.name." + transition.targetWorldId), textX, textY);
        }
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
    public ArrayList<Item> getItems() { return items; }
    public ArrayList<Item> getPfands() { return pfands; }
}
