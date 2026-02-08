package com.mygame.entity.item;

import com.badlogic.gdx.graphics.Texture;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.world.World;
import com.mygame.world.WorldManager;
import com.mygame.world.zone.ZoneRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages all items in the game world, including loading them from the map.
 * Handles item updates, drawing, and player pickups.
 */
public class ItemManager {

    private final ItemRegistry itemRegistry;
    private final ItemLoader itemLoader;
    private WorldManager worldManager;
    private final ArrayList<Item> backgroundItems = new ArrayList<>();
    private final ArrayList<Item> foregroundItems = new ArrayList<>();
    private final ArrayList<Item> allItems = new ArrayList<>();

    public ItemManager(ItemRegistry itemRegistry, WorldManager worldManager, ZoneRegistry zoneRegistry) {
        this.itemRegistry = itemRegistry;
        this.worldManager = worldManager;
        this.itemLoader = new ItemLoader(itemRegistry);
    }

    public Item createItem(String itemKey, float x, float y, World world) {
        ItemDefinition itemType = itemRegistry.get(itemKey);
        if (itemType == null) {
            System.err.println("Item type not found: " + itemKey);
            return null;
        }

        Texture texture = Assets.getTexture(itemKey);
        if (texture == null) {
            System.err.println("Texture not found for item: " + itemKey);
            return null;
        }

        String id = itemKey + "_" + UUID.randomUUID(); // Unique ID for dynamic items
        Item item = new Item(id, itemType, itemType.getWidth(), itemType.getHeight(), x, y, texture, world, itemType.canBePickedUp(),  false, null,  true);
        addBackgroundItem(item);
        System.out.println("Created item " + itemKey + " with ID " + id + " at " + x + "," + y + " in world " + world.getName());
        return item;
    }

    /**
     * Loads items from a specific world's Tiled map layer named "items".
     * @param world The world from which to load items.
     */
    public void loadItemsFromMap(World world) {
        itemLoader.loadItemsFromMap(world, this);
    }


    // --- Update items: handle pickups by the player and cooldowns ---
    public void update(float delta, Player player) {
        if (worldManager.getCurrentWorld() == null) return;

        // We need to check both lists for pickups
        checkPickupsInList(delta, player);
    }

    private void checkPickupsInList(float delta, Player player) {
        for (int i = allItems.size() - 1; i >= 0; i--) {
            Item item = allItems.get(i);
            if (item.getWorld() != worldManager.getCurrentWorld()) continue;
            item.update(delta);

            if (item.canBePickedUp() && item.isPlayerNear(player, item.getDistance())) {
                player.getInventory().addItem(item.getType(), 1);
                removeItem(item); // безпечне видалення, бо йдемо з кінця
            }
        }

    }
    /**
     * Retrieves an item by its name (set in Tiled) or its itemKey.
     * @param identifier The name or key of the item.
     * @return The Item object, or null if not found.
     */
    public Item getItem(String identifier) {
        for (Item item : allItems) {
            if (item.getId().equals(identifier)) {
                return item;
            }
        }
        return null;
    }

    public List<Item> getBackgroundItems() { return backgroundItems; }
    public List<Item> getForegroundItems() { return foregroundItems; }

    public List<Item> getAllItems() {return allItems;}

    public void addBackgroundItem(Item item) {
        backgroundItems.add(item);
        allItems.add(item);
    }

    public void addForegroundItem(Item item) {
        foregroundItems.add(item);
        allItems.add(item);
    }

    public void removeItem(Item item) {
        backgroundItems.remove(item);
        foregroundItems.remove(item);
        allItems.remove(item);
    }
    public void removeItemsByKey(String itemKey) {
        allItems.removeIf(item -> item.getType().getKey().equals(itemKey));
        backgroundItems.removeIf(item -> item.getType().getKey().equals(itemKey));
        foregroundItems.removeIf(item -> item.getType().getKey().equals(itemKey));
    }
}
