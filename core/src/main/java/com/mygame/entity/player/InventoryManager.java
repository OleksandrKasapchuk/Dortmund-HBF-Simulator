package com.mygame.entity.player;

import com.mygame.Assets;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.item.ItemType;
import com.mygame.managers.global.audio.SoundManager;
import com.mygame.ui.UIManager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages the player's inventory, including item quantities and item effects.
 * Supports adding, removing, checking items, and applying special effects.
 */
public class InventoryManager {

    UIManager uiManager;

    // --- Stores items and their quantities ---
    private final Map<ItemType, Integer> items;

    // --- Constructor: initializes empty inventory and effect maps ---
    public InventoryManager() {
        items = new LinkedHashMap<>();       // Preserve insertion order
    }

    public void setUI(UIManager uiManager) {this.uiManager = uiManager;}

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

    public Map<ItemType, Integer> getItems() {return items;}

    public int getMoney() {return getAmount(ItemRegistry.get("money"));}

    public void addMoney(int amount) {
        SoundManager.playSound(Assets.moneySound);
        addItem(ItemRegistry.get("money"), amount);
    }

    public void addItemAndNotify(ItemType type, int amount) {
        if (type == ItemRegistry.get("money")) addMoney(amount);
        else addItem(type, amount);

        uiManager.showEarned(amount, type.getKey());
    }

    public boolean trade(ItemType give, ItemType receive, int giveAmount, int receiveAmount) {
        if (getAmount(give) >= giveAmount) {
            removeItem(give, giveAmount);
            addItemAndNotify(receive, receiveAmount);
            return true;
        }
        uiManager.showNotEnough(give.getKey());
        return false;
    }

    // --- Check if an item is usable (has an effect) ---
    public boolean isUsable(ItemType item) {return item != null && item.isUsable();}
}
