package com.mygame.entity.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.world.World;
import com.mygame.world.WorldManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Manages all items in the game world, including loading them from the map.
 * Handles item updates, drawing, and player pickups.
 */
public class ItemManager {

    private final ItemRegistry itemRegistry;
    private final Map<String, Item> namedItems = new HashMap<>();
    private WorldManager worldManager;

    public ItemManager(ItemRegistry itemRegistry, WorldManager worldManager) {
        this.itemRegistry = itemRegistry;
        this.worldManager = worldManager;

        // Підписка на подію обшуку предмета
        EventBus.subscribe(Events.ItemSearchedEvent.class, event -> {
            if (event.itemKey() != null && !event.itemKey().isEmpty()) {
                ItemDefinition reward = itemRegistry.get(event.itemKey());
                if (reward != null) {
                    event.player().getInventory().addItem(reward, event.amount());
                    EventBus.fire(new Events.MessageEvent(event.amount() + " " + Assets.ui.format("ui.found", Assets.items.get(reward.getNameKey()))));
                }
            } else {
                EventBus.fire(new Events.MessageEvent(Assets.ui.get("ui.not_found")));
            }
        });
        EventBus.subscribe(Events.ItemInteractionEvent.class,event -> event.item().interact(event.player()));
    }

    /**
     * Loads items from a specific world's Tiled map layer named "items".
     * @param world The world from which to load items.
     */
    public void loadItemsFromMap(World world) {
        MapLayer itemLayer = world.getMap().getLayers().get("items");
        if (itemLayer == null) {
            return; // No items layer in this world, which is fine.
        }
        GameSettings settings = SettingsManager.load();
        for (MapObject object : itemLayer.getObjects()) {
            createAndAddItemFromMapObject(object, world, settings);
        }
    }

    private void createAndAddItemFromMapObject(MapObject object, World world , GameSettings settings) {
        MapProperties props = object.getProperties();
        String itemKey = props.get("itemKey", String.class);

        if (itemKey == null || itemKey.isEmpty()) {
            System.err.println("Skipping item object: 'itemKey' property is missing.");
            return;
        }

        ItemDefinition itemType = itemRegistry.get(itemKey);
        if (itemType == null) {
            System.err.println("Skipping item: ItemType with key '" + itemKey + "' not found in ItemRegistry.");
            return;
        }

        Item item = buildItemFromProperties(props, itemType, world, itemKey);
        if (item == null) return; // Error creating item, message already printed.

        restoreItemState(item, settings);

        // Add item to the correct list based on its properties
        if (props.get("isBackground", false, Boolean.class)) {
            world.addBackgroundItem(item);
        } else {
            world.addForegroundItem(item);
        }

        registerNamedItem(object, item, itemKey);
    }

    private Item buildItemFromProperties(MapProperties props, ItemDefinition itemType, World world, String itemKey) {
        float x = props.get("x", 0f, Float.class);
        float y = props.get("y", 0f, Float.class);
        float width = props.get("width", 64f, Float.class);
        float height = props.get("height", 64f, Float.class);

        boolean isSolid = props.get("isSolid", false, Boolean.class);
        boolean isPickupable = props.get("isPickupable", false, Boolean.class);

        int interactionDistance = props.get("interactionDistance", 200, Integer.class);

        boolean searchable = props.get("searchable", false, Boolean.class);
        String questId = props.get("questId", null, String.class);

        String rewardItemKey = props.get("rewardItemKey", null, String.class);
        int rewardAmount = props.get("rewardAmount", 0, Integer.class);

        String interactionActionId = props.get("onInteractAction", null, String.class);

        String textureKey = props.get("textureKey", itemKey, String.class);
        Texture texture = Assets.getTexture(textureKey);
        if (texture == null) {
            System.err.println("Texture for item '" + itemKey + "' with textureKey '" + textureKey + "' not found!");
            return null;
        }

        return new Item(itemType, (int) width, (int) height, x, y, interactionDistance, texture, world, isPickupable, isSolid, searchable, questId, rewardItemKey, rewardAmount, interactionActionId);
    }

    private void restoreItemState(Item item, GameSettings settings) {
        if (settings.searchedItems != null && settings.searchedItems.contains(item.getUniqueId())) {
            item.setSearched(true);
        }
    }

    private void registerNamedItem(MapObject object, Item item, String itemKey) {
        String objectName = object.getName();
        if (objectName != null && !objectName.isEmpty()) {
            namedItems.put(objectName, item);
        }
        namedItems.put(itemKey, item);
    }


    // --- Update items: handle pickups by the player and cooldowns ---
    public void update(float delta, Player player) {
        World currentWorld = worldManager.getCurrentWorld();
        if (currentWorld == null) return;

        // We need to check both lists for pickups
        checkPickupsInList(currentWorld.getAllItems(), delta, player, currentWorld);
    }

    private void checkPickupsInList(List<Item> items, float delta, Player player, World currentWorld) {
        for (Iterator<Item> it = items.iterator(); it.hasNext(); ) {
            Item item = it.next();
            item.updateCooldown(delta);
            checkPickUp(item, player, currentWorld, it);
        }
    }

    private void checkPickUp(Item item, Player player, World currentWorld, Iterator<Item> it){
        // If item can be picked up and player is near, add to inventory and remove from world
        if (item.canBePickedUp() && item.isPlayerNear(player, item.getDistance())) {
            player.getInventory().addItem(item.getType(), 1);

            if (item.getType().getKey().equals("pfand")) {
                // This logic might need adjustment depending on whether pfand can be a background item
                currentWorld.getPfands().remove(item);
            }
            it.remove(); // Remove item from the world
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
}
