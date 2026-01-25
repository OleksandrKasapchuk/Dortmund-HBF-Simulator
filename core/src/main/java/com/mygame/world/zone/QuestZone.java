package com.mygame.world.zone;

import com.badlogic.gdx.math.Rectangle;
import com.mygame.entity.item.ItemDefinition;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;


public class QuestZone extends Zone {

    private boolean used = false;
    private final Player player;
    private final ItemRegistry itemRegistry;

    public QuestZone(String id, Rectangle area, Player player, ItemRegistry itemRegistry) {
        super(id, area);
        this.player = player;
        this.itemRegistry = itemRegistry;
        this.enabled = false;
    }

    @Override
    public void onInteract() {
        if (!enabled || used) return;

        ItemDefinition firework = itemRegistry.get("firework");
        if (player.getInventory().hasItem(firework)) {
            player.getInventory().removeItem(firework, 1);
            EventBus.fire(new Events.CreateItemEvent("firework", area.x + area.width / 2, area.y));
            enabled = false;
        }
    }
}
