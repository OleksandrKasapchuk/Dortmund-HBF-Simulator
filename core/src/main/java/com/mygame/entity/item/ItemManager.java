package com.mygame.entity.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.world.World;
import com.mygame.world.WorldManager;

import java.util.Iterator;

/**
 * Manages all items in the game world, including loading them from the map.
 * Handles item updates, drawing, and player pickups.
 */
public class ItemManager {

    // --- Fields for specific, important items (Restored) ---
    private Item bush;
    private Item pfandAutomat;
    private Item table;

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

            ItemType itemType = ItemRegistry.get(itemKey);
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

            int interactionDistance = props.get("interactionDistance", 100, Integer.class);

            String textureKey = props.get("textureKey", itemKey, String.class);
            Texture texture = Assets.getTexture(textureKey);
            if (texture == null) {
                System.err.println("Texture for item '" + itemKey + "' with textureKey '" + textureKey +  "' not found!");
                continue;
            }

            Item item = new Item(itemType, (int) width, (int) height, x, y, interactionDistance, texture, world, isPickupable, isSolid);
            world.getItems().add(item);

            // --- Assign to specific fields if they match ---
            if ("bush".equalsIgnoreCase(itemKey)) {
                this.bush = item;
            }
            if ("pfandAutomat".equalsIgnoreCase(itemKey)) {
                this.pfandAutomat = item;
            }
            if ("table".equalsIgnoreCase(itemKey)) {
                this.table = item;
            }

            System.out.println("SUCCESS: ItemManager loaded item '" + itemKey + "' into world '" + world.getName() + "'");
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

    public Item getBush() {
        return bush;
    }

    public Item getPfandAutomat() {
        return pfandAutomat;
    }
    public Item getTable(){ return table;}
}
