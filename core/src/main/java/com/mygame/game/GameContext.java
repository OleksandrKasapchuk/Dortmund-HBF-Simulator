package com.mygame.game;


import com.mygame.entity.item.ItemManager;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.player.InventoryManager;
import com.mygame.entity.player.Player;
import com.mygame.ui.UIManager;
public class GameContext {

    public final Player player;
    public final UIManager ui;
    public final NpcManager npcManager;
    public final GameStateManager gsm;
    public final ItemManager itemManager;


    public GameContext(Player p, UIManager ui, NpcManager n, GameStateManager gsm, ItemManager i) {
        this.player = p;
        this.ui = ui;
        this.npcManager = n;
        this.gsm = gsm;
        this.itemManager = i;
    }

    // Always get the current inventory from the player
    public InventoryManager getInventory() {
        return player.getInventory();
    }
}
