package com.mygame.action.provider;

import com.badlogic.gdx.math.Rectangle;
import com.mygame.action.ActionRegistry;
import com.mygame.entity.item.Item;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.world.zone.Zone;

public class ItemActionProvider implements ActionProvider {
    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("item.startCooldown", (c, data) -> () -> {
            Item item = c.itemManager.getItem(data.getString("id"));
            if (item != null) item.startCooldown(data.getFloat("seconds", 1f));
        });
        registry.registerCreator("item.create", (c, data) -> () -> {
            String itemId = data.getString("itemId");
            String zoneId = data.getString("zoneId");
            Zone zone = c.zoneRegistry.getZone(zoneId);
            if (zone != null) {
                Rectangle area = zone.getArea();
                float x = area.x + area.width / 2;
                float y = area.y + area.height / 2;
                int width = data.getInt("width", 64);
                int height = data.getInt("height", 64);
                EventBus.fire(new Events.CreateItemEvent(itemId, x, y));
            } else {
                System.err.println("Zone not found: " + zoneId);
            }
        });

        registry.registerCreator("item.remove", (c, data) -> () -> {
            String itemId = data.getString("itemId");
            boolean removeAll = "all_by_key".equals(data.getString("remove", ""));

            if (removeAll) {
                c.itemManager.removeItemsByKey(itemId);
            } else {
                Item item = c.itemManager.getItem(itemId);
                if (item != null) {
                    c.itemManager.removeItem(item);
                } else {
                    System.err.println("Item to remove not found: " + itemId);
                }
            }
        });
    }
}
