package com.mygame.quest;

import com.mygame.events.EventBus;
import com.mygame.events.Events;

import java.util.ArrayList;

/**
 * QuestManager handles all quests in the game.
 * It allows adding, removing, checking, and retrieving quests.
 * All quests are stored statically so they persist globally.
 */
public class QuestManager {
    private static ArrayList<Quest> quests = new ArrayList<>();

    /**Adds a new quest to the quest list*/
    public static void addQuest(Quest quest) {
        if (hasQuest(quest.key())) return;
        quests.add(quest);
        EventBus.fire(new Events.QuestStartedEvent(quest.key()));
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

    public static Quest getQuest(String key) {
        return quests.stream().filter(q -> q.key().equals(key)).findFirst().orElse(null);
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

    public static class Quest {
        private final String key;
        private final boolean progressable;
        private int progress;
        private final int maxProgress;

        public Quest(String key, boolean progressable, int progress, int maxProgress) {
            this.key = key;
            this.progressable = progressable;
            this.progress = progress;
            this.maxProgress = maxProgress;
        }

        public String key() {
            return key;
        }

        public boolean progressable() {
            return progressable;
        }

        public int progress() {
            return progress;
        }

        public int maxProgress() {
            return maxProgress;
        }

        public void makeProgress() {
            if (!progressable) return;
            this.progress++;
            EventBus.fire(new Events.QuestProgressEvent(key, progress, maxProgress));

            if (this.progress >= maxProgress) {
                EventBus.fire(new Events.QuestCompletedEvent(key));
            }
        }
    }
}
