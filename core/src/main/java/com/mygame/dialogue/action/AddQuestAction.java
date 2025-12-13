package com.mygame.dialogue.action;


import com.mygame.managers.global.QuestManager;

public class AddQuestAction implements DialogueAction {
    private final ActionContext ctx;
    private final String questId;
    private final String questNameKey;
    private final String questDescriptionKey;

    public AddQuestAction(ActionContext ctx, String questId, String questNameKey, String questDescriptionKey) {
        this.ctx = ctx;
        this.questId = questId;
        this.questNameKey = questNameKey;
        this.questDescriptionKey = questDescriptionKey;
    }

    @Override
    public void execute() {
        if (!QuestManager.hasQuest(questId)) {
            QuestManager.addQuest(new QuestManager.Quest(questId, questNameKey, questDescriptionKey));
        }
    }
}
