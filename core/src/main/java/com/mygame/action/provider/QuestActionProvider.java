package com.mygame.action.provider;

import com.mygame.action.ActionRegistry;
import com.mygame.game.GameContext;

public class QuestActionProvider implements ActionProvider {
    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("quest.start", (c, data) -> () -> c.questManager.startQuest(data.getString("id")));
        registry.registerCreator("quest.complete", (c, data) -> () -> c.questManager.completeQuest(data.getString("id")));
    }
}
