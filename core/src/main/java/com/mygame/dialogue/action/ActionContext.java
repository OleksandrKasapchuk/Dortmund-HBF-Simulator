package com.mygame.dialogue.action;

import com.mygame.dialogue.DialogueRegistry;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.player.InventoryManager;
import com.mygame.entity.player.Player;
import com.mygame.managers.global.save.GameSettings;
import com.mygame.managers.global.save.SettingsManager;
import com.mygame.ui.UIManager;
import com.mygame.ui.screenUI.GameUI;


public class ActionContext {
    public final Player player;
    public final InventoryManager inventory;
    public final UIManager ui;
    public final GameUI gameUI;
    public final NpcManager npcManager;
    public final DialogueRegistry registry;
    public final GameSettings settings;

    public ActionContext(Player p, UIManager ui, NpcManager n, DialogueRegistry r) {
        this.player = p;
        this.inventory = p.getInventory();
        this.ui = ui;
        this.gameUI = ui.getGameUI();
        this.npcManager = n;
        this.registry = r;
        this.settings = SettingsManager.load();
    }
}
