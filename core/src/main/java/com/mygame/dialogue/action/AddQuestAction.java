package com.mygame.dialogue.action;


import com.mygame.managers.global.QuestManager;

public class AddQuestAction implements DialogueAction {
    private final ActionContext ctx;
    private final String questId;
    private final String questNameKey;
    private final String questDescriptionKey;
    private final boolean progressable;
    private final int progress;
    private final int maxProgress;

    public AddQuestAction(ActionContext ctx, String questId, String questNameKey, String questDescriptionKey, boolean progressable, int progress, int maxProgress) {
        this.ctx = ctx;
        this.questId = questId;
        this.questNameKey = questNameKey;
        this.questDescriptionKey = questDescriptionKey;
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
