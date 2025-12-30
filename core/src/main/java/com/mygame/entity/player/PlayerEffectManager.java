package com.mygame.entity.player;

import com.mygame.action.ActionRegistry;
import com.mygame.events.EventBus;
import com.mygame.events.Events;

/**
 * PlayerEffectManager is responsible for managing special effects that are triggered
 * when the player uses certain items.
 */
public class PlayerEffectManager {
    public static void init() {
        EventBus.subscribe(Events.ItemUsedEvent.class, (event) -> {
            if (event.item().getEffectId() != null) {
                ActionRegistry.executeAction(event.item().getEffectId());
            }
        });

        EventBus.subscribe(Events.PlayerStateChangedEvent.class, (event) -> {
            switch (event.newState()) {
                case STONED -> ActionRegistry.executeAction("player_stoned");
                case NORMAL -> ActionRegistry.executeAction("player_normal");
            }
        });
    }
}
