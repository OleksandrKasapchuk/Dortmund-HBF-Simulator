package com.mygame.entity.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages all items in the game world, including loading them from the map.
 * Handles item updates, drawing, and player pickups.
 */
public class ItemManager {

    private final ItemRegistry itemRegistry;
    private WorldManager worldManager;
    private final ArrayList<Item> backgroundItems = new ArrayList<>();
    private final ArrayList<Item> foregroundItems = new ArrayList<>();
    private final ArrayList<Item> allItems = new ArrayList<>();

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
        EventBus.subscribe(Events.CreateItemEvent.class, this::handleCreateItemEvent);
    }

    public void createItem(String itemKey, float x, float y, World world) {
        ItemDefinition itemType = itemRegistry.get(itemKey);
        if (itemType == null) {
            System.err.println("Item type not found: " + itemKey);
            return;
        }

        Texture texture = Assets.getTexture(itemKey);
        if (texture == null) {
            System.err.println("Texture not found for item: " + itemKey);
            return;
        }

        String id = itemKey + "_" + UUID.randomUUID(); // Unique ID for dynamic items
        Item item = new Item(id, itemType, itemType.getWidth(), itemType.getHeight(), x, y, 75, texture, world, itemType.canBePickedUp(), false, false, null, null, 0, null, true);
        addBackgroundItem(item);
        System.out.println("Created item " + itemKey + " with ID " + id + " at " + x + "," + y + " in world " + world.getName());
    }


    private void handleCreateItemEvent(Events.CreateItemEvent event) {
        createItem(event.itemKey(), event.x(), event.y(), worldManager.getCurrentWorld());
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

        Item item = buildItemFromProperties(props, itemType, world, itemKey, object.getName());
        if (item == null) return; // Error creating item, message already printed.

        restoreItemState(item, settings);

        // Add item to the correct list based on its properties
        if (props.get("isBackground", false, Boolean.class)) {
            addBackgroundItem(item);
        } else {
            addForegroundItem(item);
        }
    }

    private Item buildItemFromProperties(MapProperties props, ItemDefinition itemType, World world, String itemKey, String name) {
        float x = props.get("x", 0f, Float.class);
        float y = props.get("y", 0f, Float.class);

        boolean isSolid = props.get("isSolid", false, Boolean.class);

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
        float width = props.get("width", 64f, Float.class);
        float height = props.get("height", 64f, Float.class);

        String id = (name != null && !name.isEmpty()) ? name : itemKey + "_" + world.getName() + "_" + (int)x + "_" + (int)y;

        return new Item(id, itemType, (int) width,(int) height, x, y, interactionDistance, texture, world, itemType.canBePickedUp(), isSolid, searchable, questId, rewardItemKey, rewardAmount, interactionActionId, false);
    }

    private void restoreItemState(Item item, GameSettings settings) {
        if (settings.searchedItems != null && settings.searchedItems.contains(item.getId())) {
            item.setSearched(true);
        }
    }

    public void renderBackgroundItems(SpriteBatch batch) {
        if (worldManager.getCurrentWorld() == null) return;
        for (Item item : backgroundItems) {
            if (item.getWorld() != worldManager.getCurrentWorld()) continue;
            item.draw(batch);
        }
    }

    public void renderForegroundItems(SpriteBatch batch) {
        if (worldManager.getCurrentWorld() == null) return;
        for (Item item : foregroundItems) {
            if (item.getWorld() != worldManager.getCurrentWorld()) continue;
            item.draw(batch);
        }
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
            item.updateCooldown(delta);
            if (item.canBePickedUp() && item.isPlayerNear(player, item.getDistance())) {
                player.getInventory().addItem(item.getType(), 1);
                removeItem(item); // безпечне видалення, бо йдемо з кінця
                System.out.println(item.getId() + " was picked up");
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
