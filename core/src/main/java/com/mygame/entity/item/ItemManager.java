package com.mygame.entity.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.world.World;
import com.mygame.world.WorldManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Manages all items in the game world, including loading them from the map.
 * Handles item updates, drawing, and player pickups.
 */
public class ItemManager {

    /**
     * Store items by their unique name (from Tiled) or their itemKey.
     */
    private final Map<String, Item> namedItems = new HashMap<>();

    /**
     * Loads items from a specific world's Tiled map layer named "items".
     * @param world The world from which to load items.
     */
    public void loadItemsFromMap(World world) {
        MapLayer itemLayer = world.getMap().getLayers().get("items");
        if (itemLayer == null) {
            return; // No items layer in this world, which is fine.
        }

        for (MapObject object : itemLayer.getObjects()) {
            MapProperties props = object.getProperties();

            String itemKey = props.get("itemKey", String.class);
            if (itemKey == null || itemKey.isEmpty()) {
                System.err.println("Skipping item object: 'itemKey' property is missing.");
                continue;
            }

            ItemDefinition itemType = ItemRegistry.get(itemKey);
            if (itemType == null) {
                System.err.println("Skipping item: ItemType with key '" + itemKey + "' not found in ItemRegistry.");
                continue;
            }

            float x = props.get("x", 0f, Float.class);
            float y = props.get("y", 0f, Float.class);
            float width = props.get("width", 64f, Float.class);
            float height = props.get("height", 64f, Float.class);

            boolean isSolid = props.get("isSolid", false, Boolean.class);
            boolean isPickupable = props.get("isPickupable", false, Boolean.class);

            int interactionDistance = props.get("interactionDistance", 200, Integer.class);

            boolean searchable = props.get("searchable", false, Boolean.class);
            String  questId = props.get("questId", null, String.class);

            String rewardItemKey = props.get("rewardItemKey", null, String.class);
            int rewardAmount = props.get("rewardAmount", 0, Integer.class);

            String textureKey = props.get("textureKey", itemKey, String.class);
            Texture texture = Assets.getTexture(textureKey);
            if (texture == null) {
                System.err.println("Texture for item '" + itemKey + "' with textureKey '" + textureKey +  "' not found!");
                continue;
            }

            Item item = new Item(itemType, (int) width, (int) height, x, y, interactionDistance, texture, world, isPickupable, isSolid, searchable, questId, rewardItemKey, rewardAmount);
            world.getItems().add(item);

            // --- Editor-driven identification ---
            // 1. If the object has a "Name" in Tiled, use it (highest priority)
            String objectName = object.getName();
            if (objectName != null && !objectName.isEmpty()) {
                namedItems.put(objectName, item);
            }

            // 2. Also map it by its itemKey for generic access (e.g. getItem("bush"))
            // Note: If multiple bushes exist, this will store the LAST one loaded.
            namedItems.put(itemKey, item);

            System.out.println("SUCCESS: ItemManager loaded item '" + itemKey + "' (Name: " + objectName + ") into world '" + world.getName() + "'");
        }
    }

    // --- Update items: handle pickups by the player ---
    public void update(Player player) {
        for (Iterator<Item> it = WorldManager.getCurrentWorld().getItems().iterator(); it.hasNext(); ) {
            Item item = it.next();

            // If item can be picked up and player is near, add to inventory and remove from world
            if (item.canBePickedUp() && item.isPlayerNear(player, item.getDistance())) {
                player.getInventory().addItem(item.getType(), 1);

                if (item.getType().getKey().equals("pfand")) { // Use getKey() for safety
                    WorldManager.getCurrentWorld().getPfands().remove(item);
                }
                it.remove(); // Remove item from the world
            }
        }
    }

    /**
     * Retrieves an item by its name (set in Tiled) or its itemKey.
     * @param identifier The name or key of the item.
     * @return The Item object, or null if not found.
     */
    public Item getItem(String identifier) {
        return namedItems.get(identifier);
    }

    // --- Compatibility methods for existing scenarios ---
    public Item getBush() { return getItem("bush"); }
    public Item getPfandAutomat() { return getItem("pfandAutomat"); }
    public Item getTable() { return getItem("table"); }
}
