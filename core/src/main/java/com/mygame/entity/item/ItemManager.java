package com.mygame.entity.item;

import com.mygame.Assets;
import com.mygame.world.WorldManager;
import com.mygame.world.World;
import com.mygame.entity.Player;

import java.util.Iterator;

/**
 * Manages all items in the game world, including special items like bushes and Pfand Automat.
 * Handles item updates, drawing, and player pickups.
 */
public class ItemManager {

    private Item bush;                                  // Special bush item
    private Item pfandAutomat;                          // Special Pfand Automat item

    // --- Constructor: initialize items and special items in the world ---
    public ItemManager() {
        World world = WorldManager.getWorld("main");
        World homeWorld = WorldManager.getWorld("home");

        // Create bush
        bush = new Item(ItemRegistry.get("item.bush.name"),200, 100, 800, 1800, 125, Assets.bush, world, false, false);
        world.getItems().add(bush);

        // Create spoon
        Item spoon = new Item(ItemRegistry.get("item.spoon.name"), 60, 60, 500, 1800, 100, Assets.textureSpoon, world, true, false);
        homeWorld.getItems().add(spoon);

        // Create Pfand Automat
        pfandAutomat = new Item(ItemRegistry.get("item.pfandAutomat.name"), 150, 150, 2425, 825, 200, Assets.pfandAutomat, world, false, true);
        world.getItems().add(pfandAutomat);
    }


    // --- Update items: handle pickups by the player ---
    public void update(Player player) {
        for (Iterator<Item> it = WorldManager.getCurrentWorld().getItems().iterator(); it.hasNext();) {
            Item item = it.next();

            // If item can be picked up and player is near, add to inventory and remove from world
            if (item.canBePickedUp() && item.isPlayerNear(player)) {
                player.getInventory().addItem(item.getType(), 1);
                if (item.getType().equals(ItemRegistry.get("item.pfand.name"))) {
                    WorldManager.getCurrentWorld().getPfands().remove(item);
                }
                it.remove();
            }
        }
    }

    // --- Getters for special items ---
    public Item getBush() {
        return bush;
    }

    public Item getPfandAutomat() {
        return pfandAutomat;
    }
}
