package com.mygame.entity.item;

import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.world.WorldManager;

public class ItemEventHandler {

    public ItemEventHandler(ItemRegistry itemRegistry, ItemManager itemManager, WorldManager worldManager) {

        EventBus.subscribe(Events.ItemSearchedEvent.class, event -> {
            if (event.itemKey() != null && !event.itemKey().isEmpty()) {
                ItemDefinition reward = itemRegistry.get(event.itemKey());
                if (reward != null) {
                    event.player().getInventory().addItem(reward, event.amount());
                    EventBus.fire(new Events.ItemFoundEvent(reward.getKey(), event.amount(), true));
                }
            } else {
                EventBus.fire(new Events.ItemFoundEvent(null, 0, false));
            }
        });

        EventBus.subscribe(Events.ItemInteractionEvent.class,
            event -> event.item().interact(event.player()));

        EventBus.subscribe(Events.CreateItemEvent.class,
            event -> itemManager.createItem(
                event.itemKey(),
                event.x(),
                event.y(),
                worldManager.getCurrentWorld()
            ));
    }
}
