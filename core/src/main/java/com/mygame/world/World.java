package com.mygame.world;

import com.badlogic.gdx.graphics.Color;
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
import com.mygame.entity.Entity;
import com.mygame.world.zone.QuestZone;
import com.mygame.world.zone.TransitionZone;
import com.mygame.world.zone.Zone;

import java.util.ArrayList;

public class World {
    private final String name;
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final TiledMapTileLayer collisionLayer;
    public int tileWidth;
    public int tileHeight;
    public int mapWidth;
    public int mapHeight;

    private final ArrayList<Zone> zones = new ArrayList<>();
    private final ArrayList<Entity> entities = new ArrayList<>();

    private int[] bottomLayersIndices;
    private int[] topLayersIndices;

    public World(String name, String pathToMapFile) {
        this.name = name;
        this.map = new TmxMapLoader().load(pathToMapFile);
        this.mapRenderer = new OrthogonalTiledMapRenderer(this.map);
        this.collisionLayer = (TiledMapTileLayer) this.map.getLayers().get("collision");
        initializeRenderLayers();
        initializeMapDimensions();
    }

    private void initializeRenderLayers() {
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
    }

    private void initializeMapDimensions() {
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
                    float tileWorldX = (float) x * tileWidth;
                    float tileWorldY = (float) y * tileHeight;

                    if (object instanceof RectangleMapObject && isOverlappingWithRectangle(rect, (RectangleMapObject) object, tileWorldX, tileWorldY)) {
                        return true;
                    } else if (object instanceof PolygonMapObject && isOverlappingWithPolygon(rect, (PolygonMapObject) object, tileWorldX, tileWorldY)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isOverlappingWithRectangle(Rectangle rect, RectangleMapObject mapObject, float tileWorldX, float tileWorldY) {
        Rectangle mapRect = mapObject.getRectangle();
        // LibGDX Tiled importer flips the Y-axis for objects within tiles.
        // We must account for this by subtracting the object's Y from the tile's height.
        float objectWorldY = tileWorldY + (tileHeight - (mapRect.y + mapRect.height));
        Rectangle worldRect = new Rectangle(mapRect.x + tileWorldX, objectWorldY, mapRect.width, mapRect.height);
        return Intersector.overlaps(worldRect, rect);
    }

    private boolean isOverlappingWithPolygon(Rectangle rect, PolygonMapObject mapObject, float tileWorldX, float tileWorldY) {
        Polygon mapPoly = mapObject.getPolygon();
        Polygon worldPoly = new Polygon(mapPoly.getVertices());
        worldPoly.setPosition(tileWorldX, tileWorldY);

        Polygon playerPoly = new Polygon(new float[]{
                rect.x, rect.y,
                rect.x, rect.y + rect.height,
                rect.x + rect.width, rect.y + rect.height,
                rect.x + rect.width, rect.y
        });

        return Intersector.overlapConvexPolygons(worldPoly, playerPoly);
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

    public void drawZones(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Zone zone : zones) {
            if (!zone.isEnabled()) continue;
            if (zone instanceof TransitionZone tz) {
                shapeRenderer.setColor(Color.WHITE); // білий для переходів
                shapeRenderer.rect(tz.getArea().x, tz.getArea().y, tz.getArea().width, tz.getArea().height);
            } else if (zone instanceof QuestZone qz) {
                shapeRenderer.setColor(Color.YELLOW); // жовтий для квестів
                shapeRenderer.rect(qz.getArea().x, qz.getArea().y, qz.getArea().width, qz.getArea().height);
            } else {
                // просто для загальних зон
                shapeRenderer.setColor(Color.CYAN);
                shapeRenderer.rect(zone.getArea().x, zone.getArea().y, zone.getArea().width, zone.getArea().height);
            }
        }
        shapeRenderer.end();
    }

    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
    }

    // --- Getters ---
    public TiledMap getMap() { return map; }
    public String getName() { return name; }
    public ArrayList<Zone> getZones() { return zones; }
    public ArrayList<Entity> getEntities() { return entities; }
}
