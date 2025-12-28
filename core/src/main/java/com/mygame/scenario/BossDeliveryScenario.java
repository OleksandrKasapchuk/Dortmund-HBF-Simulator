package com.mygame.scenario;

import com.mygame.action.ActionRegistry;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.quest.QuestManager;

public class BossDeliveryScenario implements Scenario {

    private boolean bossFailureTriggered = false;

    @Override
    public void init() {
        // Логіка провалу: якщо гравець викинув або втратив траву під час квесту
        EventBus.subscribe(Events.InventoryChangedEvent.class, event -> {
            if (!bossFailureTriggered && QuestManager.hasQuest("delivery") &&
                event.item().getKey().equals("grass") && event.newAmount() < 1000) {

                bossFailureTriggered = true;
                ActionRegistry.executeAction("handle_boss_fail");
            }
        });
    }
}
