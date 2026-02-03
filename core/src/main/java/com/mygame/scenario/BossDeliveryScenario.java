package com.mygame.scenario;

import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.quest.QuestManager;
import java.util.function.Consumer;

public class BossDeliveryScenario implements Scenario {
    private Consumer<Events.InventoryChangedEvent> inventoryListener;
    private GameContext ctx;

    public BossDeliveryScenario(GameContext ctx){
        this.ctx = ctx;
    }
    @Override
    public void init() {
        inventoryListener = event -> {
            if (ctx.questManager.hasQuest("delivery") &&
                event.item().getKey().equals("weed") && event.newAmount() < 1000) {

                ctx.questManager.getQuest("delivery").setStatus(QuestManager.Status.NOT_STARTED);
                EventBus.fire(new Events.ActionRequestEvent("act.quest.delivery.fail"));
            }
        };

        EventBus.subscribe(Events.InventoryChangedEvent.class, inventoryListener);
    }
}
