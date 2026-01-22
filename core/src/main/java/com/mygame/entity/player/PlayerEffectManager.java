package com.mygame.entity.player;

import com.mygame.events.EventBus;
import com.mygame.events.Events;

/**
 * PlayerEffectManager керує ефектами гравця.
 */
public class PlayerEffectManager {

    public PlayerEffectManager() {
        EventBus.subscribe(Events.ItemUsedEvent.class, event -> {
            if (event.item().getEffectId() != null) {
                EventBus.fire(new Events.ActionRequestEvent(event.item().getEffectId()));
            }
                System.out.println("Effect ID: " + event.item().getEffectId());
        });

        EventBus.subscribe(Events.PlayerStateChangedEvent.class, event -> {
            switch (event.newState()) {
                case STONED -> EventBus.fire(new Events.ActionRequestEvent("act.player.state.stoned.enter"));
                case NORMAL -> EventBus.fire(new Events.ActionRequestEvent("act.player.state.normal.enter"));
            }
        });
    }
}
