package com.mygame.managers.nonglobal;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.Assets;
import com.mygame.entity.Item;
import com.mygame.world.World;
import com.mygame.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Manages all items in the game world, including special items like bushes and Pfand Automat.
 * Handles item updates, drawing, and player pickups.
 */
public class ItemManager {

    private ArrayList<Item> items = new ArrayList<>(); // List of all items in the world
    private Item bush;                                  // Special bush item
    private Item pfandAutomat;                          // Special Pfand Automat item

    // --- Constructor: initialize items and special items in the world ---
    public ItemManager(World world, InventoryManager inventoryManager) {
        // Create bush
        bush = new Item("bush", 200, 100, 800, 1800, 125, Assets.bush, world, false, false);

        // Add a spoon and the bush to the items list
        items.add(new Item("spoon", 60, 60, 500, 1800, 100, Assets.textureSpoon, world, true, false));
        items.add(bush);

        // Create Pfand Automat
        pfandAutomat = new Item("pfandAutomat", 150, 150, 1900, 100, 200, Assets.pfandAutomat, world, false, true);
        items.add(pfandAutomat);
        registerItemProperties(inventoryManager);
    }

    /**
     * Registers properties like descriptions and effects for all items.
     * This should be called once at game start.
     * @param inventoryManager The player's inventory manager to register properties with.
     */
    public void registerItemProperties(InventoryManager inventoryManager) {
        // Register Descriptions
        inventoryManager.registerDescription("joint", "Very useful thing, makes you stoned");
        inventoryManager.registerDescription("spoon", "Junky needs it");
        inventoryManager.registerDescription("ice tea", "Nice and tasty tea, sets your status back to normal while stoned");
        inventoryManager.registerDescription("pfand", "Just a bottle, you can get money for it at pfand automat.");
        inventoryManager.registerDescription("grass", "The most needed thing in this world");
        inventoryManager.registerDescription("money", "With this you can buy everything");
        inventoryManager.registerDescription("pape", "Is needed for making a joint");
    }

    // --- Update items: handle pickups by the player ---
    public void update(Player player) {
        for (Iterator<Item> it = items.iterator(); it.hasNext();) {
            Item item = it.next();

            // If item can be picked up and player is near, add to inventory and remove from world
            if (item.canBePickedUp() && item.isPlayerNear(player)) {
                player.getInventory().addItem(item.getName(), 1);
                it.remove();
            }
        }
    }

    // --- Draw all items in the world ---
    public void draw(SpriteBatch batch) {
        for (Item item : items)
            item.draw(batch);
    }

    // --- Getters for special items ---
    public Item getBush() {
        return bush;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public Item getPfandAutomat() {
        return pfandAutomat;
    }
}
