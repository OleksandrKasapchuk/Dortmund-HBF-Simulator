package com.mygame.entity.item.plant;

import com.badlogic.gdx.graphics.Texture;
import com.mygame.assets.Assets;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.world.World;
import com.mygame.world.WorldManager;
import com.mygame.world.zone.PlaceZone;
import com.mygame.world.zone.Zone;
import com.mygame.world.zone.ZoneRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlantSystem {
    private final ZoneRegistry zoneRegistry;
    private final ItemManager itemManager;
    private final ItemRegistry itemRegistry;
    private final WorldManager worldManager;


    public PlantSystem(ItemManager itemManager, ItemRegistry itemRegistry, ZoneRegistry zoneRegistry, WorldManager worldManager) {
        this.itemManager = itemManager;
        this.zoneRegistry = zoneRegistry;
        this.itemRegistry = itemRegistry;
        this.worldManager = worldManager;
        EventBus.subscribe(Events.CreatePlantEvent.class, this::handleCreatePlantEvent);
        EventBus.subscribe(Events.HarvestPlantEvent.class, this::handleHarvestPlantEvent);
    }

    private void handleCreatePlantEvent(Events.CreatePlantEvent event) {
        createPlant(event.x(), event.y(), worldManager.getCurrentWorld(), event.player(), event.zoneId());
    }

    public void createPlant(float x, float y, World world, Player player, String zoneId) {
        Zone zone = zoneRegistry.getZone(zoneId);
        if (zone instanceof PlaceZone placeZone && placeZone.isOccupied()) return;

        Map<PlantItem.Phase, Texture> textures = new HashMap<>();
        textures.put(PlantItem.Phase.SEED, Assets.getTexture("plant_1"));
        textures.put(PlantItem.Phase.SPROUT, Assets.getTexture("plant_2"));
        textures.put(PlantItem.Phase.FLOWERING, Assets.getTexture("plant_3"));
        textures.put(PlantItem.Phase.HARVESTABLE, Assets.getTexture("plant_4"));

        float growthTime = 3f;

        PlantItem plant = new PlantItem("plant_"+ UUID.randomUUID(), itemRegistry.get("weed_plant"), x, y, world, textures, growthTime, zoneId);
        itemManager.addForegroundItem(plant);
        if (zone instanceof PlaceZone placeZone) {
            placeZone.setPlacedItem(plant);
        }
    }

    private void handleHarvestPlantEvent(Events.HarvestPlantEvent event) {
        EventBus.fire(new Events.ActionRequestEvent("act.item.harvest"));
        itemManager.removeItem(event.plant());
        Zone zone = zoneRegistry.getZone(event.plant().getZoneId());
        if (zone instanceof PlaceZone placeZone) {
            placeZone.clearPlacedItem();
        }
    }
}
