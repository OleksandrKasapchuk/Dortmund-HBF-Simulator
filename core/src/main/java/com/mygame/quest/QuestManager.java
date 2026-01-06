package com.mygame.quest;

import com.mygame.events.EventBus;
import com.mygame.events.Events;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * QuestManager handles all quests in the game.
 * It manages their status and progress.
 */
public class QuestManager {
    private final QuestRegistry questRegistry;
    private ArrayList<Quest> quests = new ArrayList<>();

    public enum Status {
        NOT_STARTED,
        ACTIVE,
        COMPLETED
    }

    /**
     * Initializes the manager by creating all quests from Registry
     * with NOT_STARTED status.
     */
    public QuestManager(QuestRegistry questRegistry) {
        this.questRegistry = questRegistry;
        for (QuestRegistry.QuestDefinition def : questRegistry.getDefinitions()) {
            quests.add(new Quest(def.key(), def.progressable(), 0, def.maxProgress()));
        }
    }

    /** Sets quest status to ACTIVE */
    public void startQuest(String key) {
        Quest quest = getQuest(key);
        if (quest != null && quest.getStatus() == Status.NOT_STARTED) {
            quest.setStatus(Status.ACTIVE);
            EventBus.fire(new Events.QuestStartedEvent(key));
        }
    }

    /** Completes a quest by its key */
    public void completeQuest(String key) {
        Quest quest = getQuest(key);
        if (quest != null) {
            quest.complete();
        }
    }

    /** Returns the list of all quests */
    public ArrayList<Quest> getQuests() {
        return quests;
    }

    /** Returns only active quests */
    public List<Quest> getActiveQuests() {
        return quests.stream()
                .filter(q -> q.getStatus() == Status.ACTIVE)
                .collect(Collectors.toList());
    }

    /** Returns only completed quests */
    public List<Quest> getCompletedQuests() {
        return quests.stream()
            .filter(Quest::isCompleted)
            .collect(Collectors.toList());
    }

    public Quest getQuest(String key) {
        return quests.stream().filter(q -> q.key().equals(key)).findFirst().orElse(null);
    }

    /** Checks if a quest is active */
    public boolean hasQuest(String key) {
        Quest q = getQuest(key);
        return q != null && q.getStatus() == Status.ACTIVE;
    }

    public class Quest {
        private final String key;
        private boolean progressable;
        private int progress;
        private int maxProgress;
        private Status status;

        public Quest(String key, boolean progressable, int progress, int maxProgress) {
            this.key = key;
            this.progressable = progressable;
            this.progress = progress;
            this.maxProgress = maxProgress;
            this.status = (progress >= maxProgress && maxProgress > 0) ? Status.COMPLETED : Status.NOT_STARTED;
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

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public String getOnComplete() {
            QuestRegistry.QuestDefinition def = questRegistry.get(key);
            return def != null ? def.onComplete() : null;
        }

        public boolean isCompleted() {
            return status == Status.COMPLETED;
        }

        public void complete() {
            if (this.isCompleted()) return;
            this.status = Status.COMPLETED;
            if (progressable) this.progress = maxProgress;
            EventBus.fire(new Events.QuestCompletedEvent(key));
        }

        public void makeProgress() {
            if (!progressable || status != Status.ACTIVE) return;
            this.progress++;
            EventBus.fire(new Events.QuestProgressEvent(key, progress, maxProgress));

            if (this.progress >= maxProgress) {
                complete();
            }
        }
    }
}
