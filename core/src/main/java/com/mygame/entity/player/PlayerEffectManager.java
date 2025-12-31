package com.mygame.entity.player;

import com.mygame.action.ActionRegistry;
import com.mygame.events.EventBus;
import com.mygame.events.Events;

/**
 * PlayerEffectManager керує ефектами гравця.
 */
public class PlayerEffectManager {

    public PlayerEffectManager() {
        EventBus.subscribe(Events.ItemUsedEvent.class, event -> {
            if (event.item().getEffectId() != null) {
                ActionRegistry.executeAction(event.item().getEffectId());
            }
        });

        EventBus.subscribe(Events.PlayerStateChangedEvent.class, event -> {
            switch (event.newState()) {
                case STONED -> ActionRegistry.executeAction("player.state.stoned.enter");
                case NORMAL -> ActionRegistry.executeAction("player.state.normal.enter");
            }
        });
    }
}
