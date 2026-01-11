package com.mygame.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
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
    public final int tileWidth;
    public final int tileHeight;
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

        ArrayList<Integer> bottomIndices = new ArrayList<>();
        int backgroundIndex = this.map.getLayers().getIndex("background");
        if (backgroundIndex != -1) {
            bottomIndices.add(backgroundIndex);
        }
        this.bottomLayersIndices = bottomIndices.stream().mapToInt(i -> i).toArray();

        ArrayList<Integer> topIndices = new ArrayList<>();
        int collisionIndex = this.map.getLayers().getIndex("collision");
        if (collisionIndex != -1) {
            topIndices.add(collisionIndex);
        }
        this.topLayersIndices = topIndices.stream().mapToInt(i -> i).toArray();

        this.tileWidth = this.map.getProperties().get("tilewidth", Integer.class);
        this.tileHeight = this.map.getProperties().get("tileheight", Integer.class);
        this.mapWidth = this.map.getProperties().get("width", Integer.class) * tileWidth;
        this.mapHeight = this.map.getProperties().get("height", Integer.class) * tileHeight;
    }

    public boolean isCollidingWithMap(Rectangle rect) {
        if (collisionLayer == null) {
            return false;
        }

        int startX = Math.max(0, (int) (rect.x / tileWidth));
        int startY = Math.max(0, (int) (rect.y / tileHeight));
        int endX = Math.min(collisionLayer.getWidth() - 1, (int) ((rect.x + rect.width) / tileWidth));
        int endY = Math.min(collisionLayer.getHeight() - 1, (int) ((rect.y + rect.height) / tileHeight));

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = collisionLayer.getCell(x, y);
                if (cell == null || cell.getTile() == null) {
                    continue;
                }

                MapObjects objects = cell.getTile().getObjects();
                if (objects.getCount() == 0) continue;

                for (MapObject object : objects) {
                    float tileWorldX = x * tileWidth;
                    float tileWorldY = y * tileHeight;

                    if (object instanceof RectangleMapObject) {
                        Rectangle mapRect = ((RectangleMapObject) object).getRectangle();
                        // LibGDX Tiled importer flips the Y-axis for objects within tiles.
                        // We must account for this by subtracting the object's Y from the tile's height.
                        float objectWorldY = tileWorldY + (tileHeight - (mapRect.y + mapRect.height));
                        Rectangle worldRect = new Rectangle(mapRect.x + tileWorldX, objectWorldY, mapRect.width, mapRect.height);

                        if (Intersector.overlaps(worldRect, rect)) {
                            return true;
                        }
                    } else if (object instanceof PolygonMapObject) {
                        Polygon mapPoly = ((PolygonMapObject) object).getPolygon();
                        Polygon worldPoly = new Polygon(mapPoly.getVertices());
                        worldPoly.setPosition(tileWorldX, tileWorldY);

                        Polygon playerPoly = new Polygon(new float[]{
                                rect.x, rect.y,
                                rect.x, rect.y + rect.height,
                                rect.x + rect.width, rect.y + rect.height,
                                rect.x + rect.width, rect.y
                        });

                        if (Intersector.overlapConvexPolygons(worldPoly, playerPoly)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


    public void renderBottomLayers(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        float bottomBuffer = 40f;
        mapRenderer.getViewBounds().y -= bottomBuffer;
        mapRenderer.getViewBounds().height += bottomBuffer;
        if (bottomLayersIndices.length > 0) {
            mapRenderer.render(bottomLayersIndices);
        }
    }

    public void renderTopLayers(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        float bottomBuffer = 40f;
        mapRenderer.getViewBounds().y -= bottomBuffer;
        mapRenderer.getViewBounds().height += bottomBuffer;
        if (topLayersIndices.length > 0) {
            mapRenderer.render(topLayersIndices);
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
    public List<Item> getBackgroundItems() { return backgroundItems; }
    public List<Item> getForegroundItems() { return foregroundItems; }
    public List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<>(backgroundItems);
        allItems.addAll(foregroundItems);
        return allItems;
    }
    public ArrayList<Item> getPfands() { return pfands; }
}
