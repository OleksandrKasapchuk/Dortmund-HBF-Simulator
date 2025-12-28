package com.mygame.entity.item;

import com.mygame.events.EventBus;
import com.mygame.events.Events;

public class ItemInteractionSystem {

    public static void init(){
        EventBus.subscribe(Events.ItemInteractionEvent.class,event -> event.item().interact(event.player()));
    }
}
