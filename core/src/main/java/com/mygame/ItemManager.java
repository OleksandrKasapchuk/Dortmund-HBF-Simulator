package com.mygame;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.Iterator;

public class ItemManager {
    private ArrayList<Item> items = new ArrayList<>();
    private Item bush;

    public ItemManager(World world) {
        bush = new Item("bush", 200, 100, 800, 1800, Assets.bush, world, false);
        items.add(new Item("spoon", 60, 60, 500, 1800, Assets.textureSpoon, world,true));
        items.add(bush);
    }

    public void update(Player player) {
        for (Iterator<Item> it = items.iterator(); it.hasNext();) {
            Item item = it.next();
            if (item.canBePickedUp() && item.isPlayerNear(player)) {
                player.getInventory().addItem(item.getName(), 1);
                it.remove();
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (Item item : items)
            item.draw(batch);
    }
    public Item getBush(){return bush;}
}
