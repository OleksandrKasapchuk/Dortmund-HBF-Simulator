package com.mygame.action;

import com.mygame.quest.QuestManager;

public class AddQuestAction implements GameAction {
    private final String questId;

    public AddQuestAction(String questId) {
        this.questId = questId;
    }

    @Override
    public void execute() {
        QuestManager.startQuest(questId);
    }
}
