package com.mygame.managers;

import java.util.ArrayList;

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

    /** Removes a quest by its title */
    public static void removeQuest(String title) {
        quests.removeIf(q -> q.getTitle().equals(title));
    }

    /** Returns the list of all quests */
    public static ArrayList<Quest> getQuests() {
        return quests;
    }

    /** Checks if a quest with the given title exists */
    public static boolean hasQuest(String title) {
        return quests.stream().anyMatch(q -> q.getTitle().equals(title));
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
        private String title;
        private String description;

        public Quest(String title, String description) {
            this.title = title;
            this.description = description;
        }

        /** Returns the quest's title */
        public String getTitle() {
            return title;
        }

        /** Returns the quest's description */
        public String getDescription() {
            return description;
        }
    }
}
