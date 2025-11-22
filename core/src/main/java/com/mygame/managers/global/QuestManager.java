package com.mygame.managers.global;

import java.util.ArrayList;
import java.util.Objects;

/**
 * QuestManager handles all quests in the game.
 * It allows adding, removing, checking, and retrieving quests.
 * All quests are stored statically so they persist globally.
 */
public class QuestManager {
    private static ArrayList<Quest> quests = new ArrayList<>();

    /** Adds a new quest to the quest list */
    public static void addQuest(Quest quest) {
        quests.add(quest);
    }

    /** Removes a quest by its key */
    public static void removeQuest(String key) {
        quests.removeIf(q -> q.getKey().equals(key));
    }

    /** Returns the list of all quests */
    public static ArrayList<Quest> getQuests() {
        return quests;
    }

    /** Checks if a quest with the given key exists */
    public static boolean hasQuest(String key) {
        return quests.stream().anyMatch(q -> q.getKey().equals(key));
    }

    /** Clears all quests from the quest list */
    public static void reset() {
        quests.clear();
    }

    /**
     * Inner class representing a single quest.
     * Contains a title and description for display purposes.
     */
    public static class Quest {
        private final String key;
        private final String titleKey;
        private final String descriptionKey;

        public Quest(String key, String titleKey, String descriptionKey) {
            this.key = key;
            this.titleKey = titleKey;
            this.descriptionKey = descriptionKey;
        }

        public String getKey() {
            return key;
        }

        public String getTitleKey() {
            return titleKey;
        }

        public String getDescriptionKey() {
            return descriptionKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Quest quest = (Quest) o;
            return Objects.equals(key, quest.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
}
