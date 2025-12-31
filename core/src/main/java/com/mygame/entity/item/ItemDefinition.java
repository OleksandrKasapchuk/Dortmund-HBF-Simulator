package com.mygame.entity.item;


/**
 * Represents a type of item in the game, with a name, description, and an optional effect.
 * This class is designed to be immutable.
 */
public class ItemDefinition {

    private final String key;             // Unique identifier, e.g., "money"
    private final String nameKey;       // Key for localization, e.g., "item.money.name"
    private final String descriptionKey; // Key for localization, e.g., "item.money.description"
    private final String effectId;        // Optional effect when the item is used

    public ItemDefinition(String key, String nameKey, String descriptionKey, String effect) {
        this.key = key;
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.effectId = effect;
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
        return effectId != null;
    }
    public String getEffectId(){return effectId;}
}
