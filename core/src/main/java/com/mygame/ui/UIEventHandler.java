package com.mygame.ui;

import com.mygame.assets.Assets;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.quest.QuestManager;

public class UIEventHandler {

    private final UIManager ui;
    private final QuestManager questManager;

    public UIEventHandler(UIManager uiManager, QuestManager questManager) {
        this.ui = uiManager;
        this.questManager = questManager;
        subscribe();
    }

    private void subscribe() {
        EventBus.subscribe(Events.MessageEvent.class, event -> ui.getGameScreen().showInfoMessage(event.message(), 1.5f));
        EventBus.subscribe(Events.QuestStartedEvent.class, event -> {
            if(questManager.getQuest(event.questId()).getNotify()) ui.getGameScreen().showInfoMessage(Assets.messages.get("message.quest.new"), 1.5f);
        });
        EventBus.subscribe(Events.QuestCompletedEvent.class, event -> {
            if(questManager.getQuest(event.questId()).getNotify()) ui.getGameScreen().showInfoMessage(Assets.messages.format("message.generic.quest.completed", Assets.quests.get("quest." + event.questId() + ".name")), 1.5f);
        });
        EventBus.subscribe(Events.AddItemMessageEvent.class, event -> ui.showEarned(event.item().getKey(), event.amount()));
        EventBus.subscribe(Events.NotEnoughMessageEvent.class, event -> ui.showNotEnough(event.item().getKey()));
        EventBus.subscribe(Events.ItemFoundEvent.class, e -> {

            if (!e.found()) {
                ui.getGameScreen().showInfoMessage(Assets.messages.get("message.not_found"), 1.5f);
                return;
            }

            ui.showFound(e.itemKey(), e.amount());
        });

        EventBus.subscribe(Events.GameStateChangedEvent.class, e -> ui.setCurrentStage(e.newState()));
    }
}
