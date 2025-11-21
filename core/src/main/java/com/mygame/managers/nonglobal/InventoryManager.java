package com.mygame.managers.nonglobal;

import com.mygame.entity.item.ItemType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages the player's inventory, including item quantities and item effects.
 * Supports adding, removing, checking items, and applying special effects.
 */
public class InventoryManager {

    // --- Stores items and their quantities ---
    private final Map<ItemType, Integer> items;

    // --- Callback to notify UI or other systems about inventory changes ---
    private Runnable onInventoryChanged;

    // --- Constructor: initializes empty inventory and effect maps ---
    public InventoryManager() {
        items = new LinkedHashMap<>();       // Preserve insertion order
    }

    public void addItem(ItemType type, int amount) {
//        if (type == null) return; // Prevent adding null items
        items.put(type, items.getOrDefault(type, 0) + amount);
        notifyChange();
    }

    public boolean removeItem(ItemType type, int count) {
        if (type == null || !items.containsKey(type)) return false;
        int current = items.get(type);
        if (current <= count) items.remove(type);
        else items.put(type, current - count);
        notifyChange();
        return true;
    }

    public boolean hasItem(ItemType type) {
        if (type == null) return false;
        return items.containsKey(type);
    }

    public int getAmount(ItemType type) {
        if (type == null) return 0;
        return items.getOrDefault(type, 0);
    }

    public Map<ItemType, Integer> getItems() {
        return items;
    }

    // --- Set callback to be called when inventory changes ---
    public void setOnInventoryChanged(Runnable callback) {
        this.onInventoryChanged = callback;
    }

    // --- Call the callback if it exists ---
    private void notifyChange() {
        if (onInventoryChanged != null) onInventoryChanged.run();
    }

    // --- Check if an item is usable (has an effect) ---
    public boolean isUsable(ItemType item) {
        return item != null && item.isUsable();
    }
}
