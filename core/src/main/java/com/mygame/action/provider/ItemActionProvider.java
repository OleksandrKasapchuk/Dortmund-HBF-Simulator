package com.mygame.action.provider;

import com.badlogic.gdx.math.Rectangle;
import com.mygame.action.ActionRegistry;
import com.mygame.entity.item.Item;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;
import com.mygame.world.World;
import com.mygame.world.zone.Zone;

public class ItemActionProvider implements ActionProvider {
    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("item.create", (c, data) -> () -> {
            String itemId = data.getString("itemId");

            Zone zone = c.zoneRegistry.findNearestZone(c.player.getCenterX(), c.player.getCenterY());
            if (zone == null) {
                System.err.println("No zone found near player");
                return;
            }

            Rectangle area = zone.getArea();
            float x = area.x + area.width / 2;
            float y = area.y + area.height / 2;
            int width = data.getInt("width", 64);
            int height = data.getInt("height", 64);
            EventBus.fire(new Events.CreateItemEvent(itemId, x, y));
        });

        registry.registerCreator("world.createPlant", (c, data) -> () -> {

            float x = c.player.getX(); // Placeholder for interacted object's X
            float y = c.player.getY(); // Placeholder for interacted object's Y
            World world = c.worldManager.getCurrentWorld();

           EventBus.fire(new Events.CreatePlantEvent(x, y));
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
