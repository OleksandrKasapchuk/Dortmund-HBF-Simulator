package com.mygame.world.zone;

import com.badlogic.gdx.math.Rectangle;
import com.mygame.events.EventBus;
import com.mygame.events.Events;


public class QuestZone extends Zone {

    private boolean used = false;

    public QuestZone(String id, Rectangle area) {
        super(id, area);
        this.enabled = false;
    }

    @Override
    public void onInteract() {
        if (!enabled || used) return;

        used = true;

        EventBus.fire(new Events.ActionRequestEvent(
            "act.quest.zone." + id
        ));
    }
}

