package com.mygame.world.zone;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.player.Player;
import com.mygame.world.World;

public class ZoneRegistry {

    private final ItemRegistry itemRegistry;
    private final Player player;

    public ZoneRegistry(ItemRegistry itemRegistry, Player player) {
        this.itemRegistry = itemRegistry;
        this.player = player;
    }

    public void loadZonesFromMap(World world) {
        MapLayer zoneLayer = world.getMap().getLayers().get("zones");
        if (zoneLayer == null) return;

        for (MapObject object : zoneLayer.getObjects()) {

            Rectangle rect;

            if (object instanceof RectangleMapObject rectObj) {
                rect = rectObj.getRectangle();
            } else if (object.getProperties().containsKey("width") && object.getProperties().containsKey("height")) {
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

            switch (zoneType) {

                case "transition" -> {
                    String targetWorldId = props.get("targetWorldId", String.class);
                    float targetX = Float.parseFloat(props.get("targetX").toString());
                    float targetY = Float.parseFloat(props.get("targetY").toString());

                    world.getZones().add(
                        new TransitionZone(props.get("id", Integer.class).toString() ,targetWorldId, targetX, targetY, rect)
                    );
                }

                case "quest" -> {
                    String questId = props.get("questId", String.class);

                    world.getZones().add(
                        new QuestZone(questId, rect, player, itemRegistry)
                    );
                }

                default -> throw new RuntimeException("Unknown zoneType: " + zoneType);
            }
        }
    }
}
