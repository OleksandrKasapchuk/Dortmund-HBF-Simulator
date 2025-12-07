package com.mygame.entity.player;

import com.mygame.entity.item.ItemRegistry;
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

    // --- Constructor: initializes empty inventory and effect maps ---
    public InventoryManager() {
        items = new LinkedHashMap<>();       // Preserve insertion order
    }

    public void addItem(ItemType type, int amount) {
        if (type == null) return; // Prevent adding null items
        items.put(type, items.getOrDefault(type, 0) + amount);
    }

    public void removeItem(ItemType type, int count) {
        if (type == null || !items.containsKey(type)) return;
        int current = items.get(type);
        if (current <= count) items.remove(type);
        else items.put(type, current - count);
    }

    public boolean hasItem(ItemType type) {
        if (type == null) return false;
        return items.containsKey(type);
    }

    public int getAmount(ItemType type) {
        if (type == null) return 0;
        return items.getOrDefault(type, 0);
    }

    public boolean trade(String item1, String item2, int amount1, int amount2) {
        if (getAmount(ItemRegistry.get(item1)) >= amount1) {
            removeItem(ItemRegistry.get(item1), amount1);
            addItem(ItemRegistry.get(item2), amount2);
            return true;
        }
        return false;
    }

    public Map<ItemType, Integer> getItems() {
        return items;
    }

    // --- Check if an item is usable (has an effect) ---
    public boolean isUsable(ItemType item) {
        return item != null && item.isUsable();
    }
}
