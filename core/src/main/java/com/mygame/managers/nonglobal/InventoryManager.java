package com.mygame.managers.nonglobal;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages the player's inventory, including item quantities and item effects.
 * Supports adding, removing, checking items, and applying special effects.
 */
public class InventoryManager {

    // --- Stores items and their quantities ---
    private Map<String, Integer> items;

    // --- Stores item effects (Runnable) ---
    private Map<String, Runnable> itemEffects;

    // --- Callback to notify UI or other systems about inventory changes ---
    private Runnable onInventoryChanged;

    // --- Constructor: initializes empty inventory and effect maps ---
    public InventoryManager() {
        items = new LinkedHashMap<>();       // Preserve insertion order
        itemEffects = new LinkedHashMap<>();
    }

    // --- Set callback to be called when inventory changes ---
    public void setOnInventoryChanged(Runnable callback) {
        this.onInventoryChanged = callback;
    }

    // --- Call the callback if it exists ---
    private void notifyChange() {
        if (onInventoryChanged != null) onInventoryChanged.run();
    }

    // --- Add an item to the inventory ---
    public void addItem(String itemName, int amount) {
        items.put(itemName, items.getOrDefault(itemName, 0) + amount); // Add or increase quantity
        notifyChange(); // Notify that inventory changed
    }

    // --- Remove a certain quantity of an item ---
    public boolean removeItem(String itemName, int count) {
        if (!items.containsKey(itemName)) return false; // Item does not exist
        int current = items.get(itemName);
        if (current <= count) items.remove(itemName);  // Remove item completely if count >= current
        else items.put(itemName, current - count);     // Otherwise, decrease quantity
        notifyChange();
        return true;
    }

    // --- Check if inventory contains an item ---
    public boolean hasItem(String itemName) {
        return items.containsKey(itemName);
    }

    // --- Get the quantity of a specific item ---
    public int getAmount(String itemName) {
        return items.getOrDefault(itemName, 0);
    }

    // --- Get all items in the inventory ---
    public Map<String, Integer> getItems() {
        return items;
    }

    // --- Register an effect for an item ---
    public void registerEffect(String itemName, Runnable effect) {
        itemEffects.put(itemName, effect);
    }

    // --- Apply the effect of an item if it has one ---
    public void applyEffect(String itemName) {
        if (itemEffects.containsKey(itemName)) {
            itemEffects.get(itemName).run(); // Execute associated effect
            notifyChange();                  // Notify about potential inventory change
        }
    }

    // --- Check if an item is usable (has an effect) ---
    public boolean isUsable(String itemName) {
        return itemEffects.containsKey(itemName);
    }
}
