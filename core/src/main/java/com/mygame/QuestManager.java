package com.mygame;

import java.util.ArrayList;

public class QuestManager {
    private static ArrayList<Quest> quests = new ArrayList<>();

    public static void addQuest(Quest quest) {
        quests.add(quest);
    }

    public static void removeQuest(String title) {
        quests.removeIf(q -> q.getTitle().equals(title));
    }

    public static ArrayList<Quest> getQuests() {
        return quests;
    }
    public static boolean hasQuest(String title){
        return quests.stream().anyMatch(q -> q.getTitle().equals(title));
    }

    public static void reset() {
        quests.clear();
    }

    // --- Вкладений клас ---
    public static class Quest {
        private String title;
        private String description;
        private boolean completed;

        public Quest(String title, String description) {
            this.title = title;
            this.description = description;
            this.completed = false;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public boolean isCompleted() { return completed; }
        public void complete() { this.completed = true; }
    }
}
