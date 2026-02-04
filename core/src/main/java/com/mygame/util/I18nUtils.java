package com.mygame.util;

import com.badlogic.gdx.utils.I18NBundle;

import java.util.MissingResourceException;

public class I18nUtils {

    /**
     * Attempts to get a noun in its accusative form from the bundle.
     * Falls back to the default nominative form if not found.
     */
    public static String getAccusative(I18NBundle bundle, String itemKey) {
        try {
            return bundle.get("item." + itemKey + ".name.accusative");
        } catch (MissingResourceException e) {
            return bundle.get("item." + itemKey + ".name");
        }
    }

    /**
     * Gets the correct plural form for counts greater than 1.
     * This method handles 'few' (2-4) and 'many' (0, 5+) rules.
     * The 'one' case should be handled separately, likely with getAccusative().
     */
    public static String getPluralized(I18NBundle bundle, String itemKey, int count) {
        String pluralCategory;

        int absCount = Math.abs(count);
        int lastDigit = absCount % 10;
        int lastTwoDigits = absCount % 100;

        if (lastTwoDigits >= 11 && lastTwoDigits <= 14) {
            pluralCategory = "many";
        } else if (lastDigit >= 2 && lastDigit <= 4) {
            pluralCategory = "few";
        } else {
            // Covers 0, 5-9, 10, etc.
            pluralCategory = "many";
        }

        String fullKey = "item." + itemKey + ".name." + pluralCategory;

        try {
            return bundle.get(fullKey);
        } catch (MissingResourceException e) {
            // Fallback for languages that don't have pluralization keys
            return bundle.get("item." + itemKey + ".name");
        }
    }
}
