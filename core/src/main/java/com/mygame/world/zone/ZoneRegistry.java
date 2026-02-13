package com.mygame.world.zone;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.player.Player;
import com.mygame.game.save.data.ServerSaveData;
import com.mygame.game.save.SettingsManager;
import com.mygame.world.World;

import java.util.ArrayList;

public class ZoneRegistry {

    private final ItemRegistry itemRegistry;
    private final Player player;
    private ArrayList<Zone> zones = new ArrayList<>();

    public ZoneRegistry(ItemRegistry itemRegistry, Player player) {
        this.itemRegistry = itemRegistry;
        this.player = player;
    }

    public void loadZonesFromMap(World world) {
        MapLayer zoneLayer = world.getMap().getLayers().get("zones");
        if (zoneLayer == null) return;
        ServerSaveData settings = SettingsManager.loadServer();
        for (MapObject object : zoneLayer.getObjects()) {

            Rectangle rect;

          if (object.getProperties().containsKey("width") && object.getProperties().containsKey("height")) {
                // Примітивна конвертація tile/polygon в rectangle
                float x = object.getProperties().get("x", Float.class);
                float y = object.getProperties().get("y", Float.class);
                float width = object.getProperties().get("width", Float.class);
                float height = object.getProperties().get("height", Float.class);
                rect = new Rectangle(x, y, width, height);
            } else {
                System.out.println("Skipping unknown object type in world: " + world.getName());
                continue;
            }

            MapProperties props = object.getProperties();

            String zoneType = props.get("zoneType", String.class);
            if (zoneType == null) {
                throw new RuntimeException("Zone without zoneType in world: " + world.getName());
            }
            Zone zone;
            switch (zoneType) {

                case "transition" -> {
                    String targetWorldId = props.get("targetWorldId", String.class);
                    float targetX = Float.parseFloat(props.get("targetX").toString());
                    float targetY = Float.parseFloat(props.get("targetY").toString());
                    zone = new TransitionZone(props.get("id", Integer.class).toString() ,targetWorldId, targetX, targetY, rect);
                    zones.add(zone);
                    world.getZones().add(zone);
                }

                case "place" -> {
                    String key = props.get("key", String.class);
                    zone = new PlaceZone(key, rect, player, itemRegistry, settings);
                    zones.add(zone);
                    world.getZones().add(zone);
                }

                default -> throw new RuntimeException("Unknown zoneType: " + zoneType);

            }
        }
    }
    public Zone getZone(String id){
        for (Zone zone : zones) {
            if (zone.getId().equals(id)) return zone;
        }
        return null;
    }

    public Zone findNearestZone(float x, float y) {
        Zone nearest = null;
        float bestDist = Float.MAX_VALUE;

        for (Zone zone : zones) {
            if (zone instanceof PlaceZone && ((PlaceZone) zone).isOccupied()) {
                System.out.println("Skipping zone: " + zone.getId());
                continue; // Ignore occupied place zones
            }

            Rectangle r = zone.getArea();
            float cx = r.x + r.width / 2f;
            float cy = r.y + r.height / 2f;

            float dx = cx - x;
            float dy = cy - y;
            float dist = dx * dx + dy * dy; // без sqrt — швидше

            if (dist < bestDist) {
                bestDist = dist;
                nearest = zone;
            }
        }
        return nearest;
    }
    public ArrayList<Zone> getZones() {
        return zones;
    }
}
