package com.mygame.world.zone;

import com.badlogic.gdx.math.Rectangle;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.save.GameSettings;


public class QuestZone extends Zone {
    private final Player player;
    private final ItemRegistry itemRegistry;
    private boolean used;

    public QuestZone(String id, Rectangle area, Player player, ItemRegistry itemRegistry, GameSettings settings) {
        super(id, area);
        this.player = player;
        this.itemRegistry = itemRegistry;

        this.enabled = settings.enabledQuestZones != null && settings.enabledQuestZones.contains(id);
    }

    @Override
    public void onInteract() {
        if (!enabled || used) return;

        switch (id) {
            case "jan.firework.4.1": {
                EventBus.fire(new Events.ActionRequestEvent("act.quest.jan.firework.4.place.firework.1"));
                enabled = false;
                break;
            }
            case "jan.firework.4.2": {
                EventBus.fire(new Events.ActionRequestEvent("act.quest.jan.firework.4.place.firework.2"));
                enabled = false;
                break;
            }
            case "jan.firework.5": {
                used = true;
                EventBus.fire(new Events.ActionRequestEvent("act.quest.jan.firework.5.completed"));
            }
        }
    }
}
