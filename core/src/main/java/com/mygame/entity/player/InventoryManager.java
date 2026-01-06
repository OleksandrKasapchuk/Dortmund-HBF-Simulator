package com.mygame.entity.player;

import com.mygame.assets.Assets;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.item.ItemDefinition;
import com.mygame.assets.audio.SoundManager;
import com.mygame.events.EventBus;
import com.mygame.events.Events;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages the player's inventory, including item quantities and item effects.
 * Supports adding, removing, checking items, and applying special effects.
 */
public class InventoryManager {

    private ItemRegistry itemRegistry;

    // --- Stores items and their quantities ---
    private final Map<ItemDefinition, Integer> items;

    // --- Constructor: initializes empty inventory ---
    public InventoryManager() {
        items = new LinkedHashMap<>();       // Preserve insertion order
    }

    /**
     * Set dependencies after initialization
     */
    public void init(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public void addItem(ItemDefinition type, int amount) {
        if (type == null) return;
        items.put(type, items.getOrDefault(type, 0) + amount);
        EventBus.fire(new Events.InventoryChangedEvent(type, getAmount(type)));
    }

    public void removeItem(ItemDefinition type, int count) {
        if (type == null || !items.containsKey(type)) return;
        int current = items.get(type);
        if (current <= count) items.remove(type);
        else items.put(type, current - count);
        EventBus.fire(new Events.InventoryChangedEvent(type, getAmount(type)));
    }

    public boolean hasItem(ItemDefinition type) {
        if (type == null) return false;
        return items.containsKey(type);
    }

    public int getAmount(ItemDefinition type) {
        if (type == null) return 0;
        return items.getOrDefault(type, 0);
    }

    public Map<ItemDefinition, Integer> getItems() {return items;}

    public int getMoney() {
        if (itemRegistry == null) return 0;
        return getAmount(itemRegistry.get("money"));
    }

    public void addMoney(int amount) {
        if (itemRegistry == null) return;
        SoundManager.playSound(Assets.getSound("money"));
        addItem(itemRegistry.get("money"), amount);
    }

    public void addItemAndNotify(ItemDefinition type, int amount) {
        if (itemRegistry != null && type == itemRegistry.get("money")) {
            addMoney(amount);
        } else {
            addItem(type, amount);
        }
        EventBus.fire(new Events.AddItemMessageEvent(type, amount));
    }

    public boolean trade(ItemDefinition give, ItemDefinition receive, int giveAmount, int receiveAmount) {
        if (getAmount(give) >= giveAmount) {
            removeItem(give, giveAmount);
            addItemAndNotify(receive, receiveAmount);
            return true;
        }
        EventBus.fire(new Events.NotEnoughMessageEvent(give));
        return false;
    }

    public boolean isUsable(ItemDefinition item) {return item != null && item.isUsable();}
}
