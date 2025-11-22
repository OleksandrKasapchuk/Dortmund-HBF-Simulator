package com.mygame.entity.item;


/**
 * Represents a type of item in the game, with a name, description, and an optional effect.
 * This class is designed to be immutable.
 */
public class ItemType {

    private final String key;             // Unique identifier, e.g., "money"
    private final String nameKey;       // Key for localization, e.g., "item.money.name"
    private final String descriptionKey; // Key for localization, e.g., "item.money.description"
    private final Runnable effect;        // Optional effect when the item is used

    public ItemType(String key, String nameKey, String descriptionKey, Runnable effect) {
        this.key = key;
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.effect = effect;
    }

    // --- Getters ---
    public String getKey() {
        return key;
    }

    public String getNameKey() {
        return nameKey;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public boolean isUsable() {
        return effect != null;
    }

    public void apply() {
        if (effect != null) {
            effect.run();
        }
    }
}
