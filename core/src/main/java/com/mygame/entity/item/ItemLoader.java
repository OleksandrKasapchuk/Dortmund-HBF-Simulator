package com.mygame.entity.item;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.mygame.assets.Assets;
import com.mygame.entity.item.itemData.InteractionData;
import com.mygame.entity.item.itemData.SearchData;
import com.mygame.game.save.data.ServerSaveData;
import com.mygame.game.save.SettingsManager;
import com.mygame.world.World;

public class ItemLoader {

    private final ItemRegistry itemRegistry;

    public ItemLoader(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    /**
     * Loads items from a specific world's Tiled map layer named "items".
     * @param world The world from which to load items.
     * @param itemManager The manager to add the loaded items to.
     */
    public void loadItemsFromMap(World world, ItemManager itemManager) {
        MapLayer itemLayer = world.getMap().getLayers().get("items");
        if (itemLayer == null) {
            return; // No items layer in this world, which is fine.
        }
        ServerSaveData settings = SettingsManager.loadServer();
        for (MapObject object : itemLayer.getObjects()) {
            createAndAddItemFromMapObject(object, world, settings, itemManager);
        }
    }

    private void createAndAddItemFromMapObject(MapObject object, World world, ServerSaveData settings, ItemManager itemManager) {
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
            itemManager.addBackgroundItem(item);
        } else {
            itemManager.addForegroundItem(item);
        }
    }

    private Item buildItemFromProperties(MapProperties props, ItemDefinition itemType, World world, String itemKey, String name) {
        float x = props.get("x", 0f, Float.class);
        float y = props.get("y", 0f, Float.class);

        boolean isSolid = props.get("isSolid", false, Boolean.class);

        int interactionDistance = props.get("interactionDistance", 200, Integer.class);

        boolean searchable = props.get("searchable", false, Boolean.class);
        String questId = props.get("questId", null, String.class);

        String rewardItemKey = null;
        int rewardAmount = 0;
        if (searchable) {
            rewardItemKey = props.get("rewardItemKey", null, String.class);
            rewardAmount = props.get("rewardAmount", 0, Integer.class);
        }
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
        Item item = new Item(id, itemType, (int) width,(int) height, x, y, texture, world, itemType.canBePickedUp(), isSolid, questId, false);
        if (searchable) {
            item.setSearchData(new SearchData(rewardItemKey, rewardAmount));
        }
        if (interactionActionId != null && !interactionActionId.isEmpty()) {
            item.setInteractionData(new InteractionData(interactionActionId));
        }

        return item;
    }

    private void restoreItemState(Item item, ServerSaveData settings) {
        if (item.getSearchData() != null && settings.searchedItems != null && settings.searchedItems.contains(item.getId())) {
            item.getSearchData().markSearched();
        }
    }
}
