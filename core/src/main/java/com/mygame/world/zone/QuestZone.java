package com.mygame.world.zone;

import com.badlogic.gdx.math.Rectangle;
import com.mygame.assets.Assets;
import com.mygame.assets.audio.SoundManager;
import com.mygame.entity.item.ItemDefinition;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.managers.TimerManager;


public class QuestZone extends Zone {
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
        if (!enabled) return;

        switch (id) {
            case "jan.firework.4.1", "jan.firework.4.2": {
                ItemDefinition firework = itemRegistry.get("firework");
                if (player.getInventory().hasItem(firework)) {
                    player.getInventory().removeItem(firework, 1);
                    EventBus.fire(new Events.CreateItemEvent("firework", area.x + area.width / 2, area.y));
                    enabled = false;
                }
                break;
            }
            case "jan.firework.5": {
                SoundManager.playSound(Assets.getSound("firework_explosion"));
                TimerManager.setAction(() -> {
//                    EventBus.fire(new Events.CameraShakeEvent(2, 20));
                    EventBus.fire(new Events.DarkOverlayEvent(0.5f));
                }, 3f);
            }
        }
    }
}
