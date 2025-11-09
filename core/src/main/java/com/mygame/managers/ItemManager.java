package com.mygame.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.Assets;
import com.mygame.entity.Item;
import com.mygame.world.World;
import com.mygame.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;

public class ItemManager {
    private ArrayList<Item> items = new ArrayList<>();
    private Item bush;
    private Item pfandAutomat;

    public ItemManager(World world) {
        bush = new Item("bush", 200, 100, 800, 1800,125, Assets.bush, world, false, false);
        items.add(new Item("spoon", 60, 60, 500, 1800, 100,Assets.textureSpoon, world,true, false));
        items.add(bush);
        pfandAutomat = new Item("pfandAutomat", 150,150,1900,100,200,Assets.pfandAutomat,world,false, true);
        items.add(pfandAutomat);
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
    public ArrayList<Item> getItems(){return items;}
    public Item getPfandAutomat(){return pfandAutomat;}
}
