package com.mygame.dialogue.action;

import com.mygame.dialogue.DialogueRegistry;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.player.InventoryManager;
import com.mygame.entity.player.Player;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.ui.UIManager;
import com.mygame.ui.screenUI.GameUI;


public class ActionContext {
    public final Player player;
    public final UIManager ui;
    public final NpcManager npcManager;
    public final DialogueRegistry registry;

    public ActionContext(Player p, UIManager ui, NpcManager n, DialogueRegistry r) {
        this.player = p;
        this.ui = ui;
        this.npcManager = n;
        this.registry = r;
    }

    // Always get the current inventory from the player
    public InventoryManager getInventory() {
        return player.getInventory();
    }

    // Always get the latest UI component
    public GameUI getGameUI() {
        return ui.getGameUI();
    }

    // Always load the most recent settings
    public GameSettings getSettings() {
        return SettingsManager.load();
    }
}
