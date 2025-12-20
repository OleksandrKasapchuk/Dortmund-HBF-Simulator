package com.mygame.dialogue.action;


import com.mygame.managers.QuestManager;

public class AddQuestAction implements DialogueAction {
    private final String questId;
    private final boolean progressable;
    private final int progress;
    private final int maxProgress;

    public AddQuestAction(String questId, boolean progressable, int progress, int maxProgress) {
        this.questId = questId;
        this.progressable = progressable;
        this.progress = progress;
        this.maxProgress = maxProgress;
    }

    @Override
    public void execute() {
        if (!QuestManager.hasQuest(questId)) {
            QuestManager.addQuest(new QuestManager.Quest(questId,progressable, progress, maxProgress));
        }
    }
}
