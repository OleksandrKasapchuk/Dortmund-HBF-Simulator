package com.mygame.managers.global;

import java.util.ArrayList;

/**
 * QuestManager handles all quests in the game.
 * It allows adding, removing, checking, and retrieving quests.
 * All quests are stored statically so they persist globally.
 */
public class QuestManager {
    private static ArrayList<Quest> quests = new ArrayList<>();

    /**
     * Adds a new quest to the quest list
     */
    public static void addQuest(Quest quest) {
        quests.add(quest);
    }

    /**
     * Removes a quest by its key
     */
    public static void removeQuest(String key) {
        quests.removeIf(q -> q.key().equals(key));
    }

    /**
     * Returns the list of all quests
     */
    public static ArrayList<Quest> getQuests() {
        return quests;
    }

    /**
     * Checks if a quest with the given key exists
     */
    public static boolean hasQuest(String key) {
        return quests.stream().anyMatch(q -> q.key().equals(key));
    }

    /**
     * Clears all quests from the quest list
     */
    public static void reset() {quests.clear();}

    public record Quest(String key, String titleKey, String descriptionKey) {}
}
