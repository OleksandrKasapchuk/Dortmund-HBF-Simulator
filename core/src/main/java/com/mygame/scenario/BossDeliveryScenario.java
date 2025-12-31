package com.mygame.scenario;

import com.mygame.action.ActionRegistry;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.quest.QuestManager;
import java.util.function.Consumer;

public class BossDeliveryScenario implements Scenario {
    private Consumer<Events.InventoryChangedEvent> inventoryListener;

    @Override
    public void init() {
        inventoryListener = event -> {
            if (QuestManager.hasQuest("delivery") &&
                event.item().getKey().equals("grass") && event.newAmount() < 1000) {

                QuestManager.getQuest("delivery").setStatus(QuestManager.Status.NOT_STARTED);
                ActionRegistry.executeAction("quest.delivery.fail");
            }
        };

        EventBus.subscribe(Events.InventoryChangedEvent.class, inventoryListener);
    }
}
