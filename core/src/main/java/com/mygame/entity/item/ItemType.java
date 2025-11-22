package com.mygame.entity.item;

import java.util.Objects;

/**
 * Represents a type of item in the game, with a name, description, and an optional effect.
 * This class is designed to be immutable.
 */
public class ItemType {

    private final String nameKey;       // Key for localization, e.g., "item.money.name"
    private final String descriptionKey; // Key for localization, e.g., "item.money.description"
    private final Runnable effect;        // Optional effect when the item is used

    /**
     * Constructor for a new item type.
     *
     * @param nameKey        Localization key for the item's name
     * @param descriptionKey Localization key for the item's description
     * @param effect         Action to execute when the item is used (can be null)
     */
    public ItemType(String nameKey, String descriptionKey, Runnable effect) {
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.effect = effect;
    }

    // --- Getters ---

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

    // --- Equality and Hashing ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemType itemType = (ItemType) o;
        return Objects.equals(nameKey, itemType.nameKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameKey);
    }
}
