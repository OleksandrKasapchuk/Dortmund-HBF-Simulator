package com.mygame.managers;

import java.util.LinkedHashMap;
import java.util.Map;

public class InventoryManager {
    private Map<String, Integer> items;
    private Map<String, Runnable> itemEffects;

    private Runnable onInventoryChanged;

    public InventoryManager() {
        items = new LinkedHashMap<>();
        itemEffects = new LinkedHashMap<>();
    }

    public void setOnInventoryChanged(Runnable callback) {
        this.onInventoryChanged = callback;
    }

    private void notifyChange() {
        if (onInventoryChanged != null) onInventoryChanged.run();
    }

    public void addItem(String itemName, int amount) {
        items.put(itemName, items.getOrDefault(itemName, 0) + amount);
        notifyChange();
    }

    public boolean removeItem(String itemName, int count) {
        if (!items.containsKey(itemName)) return false;
        int current = items.get(itemName);
        if (current <= count) items.remove(itemName);
        else items.put(itemName, current - count);
        notifyChange();
        return true;
    }

    public boolean hasItem(String itemName) {return items.containsKey(itemName);}
    public int getAmount(String itemName) {return items.getOrDefault(itemName, 0);}
    public Map<String, Integer> getItems() {return items;}

    public void registerEffect(String itemName, Runnable effect) {itemEffects.put(itemName, effect);}
    public void applyEffect(String itemName) {
        if (itemEffects.containsKey(itemName)) {
            itemEffects.get(itemName).run();
            notifyChange();
        }
    }

    public boolean isUsable(String itemName) {return itemEffects.containsKey(itemName);}
}
