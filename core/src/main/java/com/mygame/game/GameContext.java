package com.mygame.game;

import com.mygame.action.ActionRegistry;
import com.mygame.dialogue.DialogueRegistry;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.player.InventoryManager;
import com.mygame.entity.player.Player;
import com.mygame.quest.QuestManager;
import com.mygame.quest.QuestProgressTriggers;
import com.mygame.quest.QuestRegistry;
import com.mygame.ui.UIManager;
import com.mygame.world.transition.TransitionManager;

public class GameContext {

    public final Player player;
    public final UIManager ui;
    public final NpcManager npcManager;
    public final GameStateManager gsm;
    public final ItemManager itemManager;
    public final ItemRegistry itemRegistry;
    public final QuestRegistry questRegistry;
    public final QuestManager questManager;
    public final QuestProgressTriggers questProgressTriggers;
    public final DialogueRegistry dialogueRegistry;
    public final ActionRegistry actionRegistry;
    public final TransitionManager transitionManager;


    public GameContext(Player p, UIManager ui, NpcManager n, GameStateManager gsm,
                       ItemManager i, ItemRegistry ir, QuestRegistry qr, QuestManager qm, QuestProgressTriggers qpt, DialogueRegistry dr, ActionRegistry actionRegistry, TransitionManager transitionManager) {
        this.player = p;
        this.ui = ui;
        this.npcManager = n;
        this.gsm = gsm;
        this.itemManager = i;
        this.itemRegistry = ir;
        this.questRegistry = qr;
        this.questManager = qm;
        this.questProgressTriggers = qpt;
        this.dialogueRegistry = dr;
        this.actionRegistry = actionRegistry;
        this.transitionManager = transitionManager;
    }

    // Always get the current inventory from the player
    public InventoryManager getInventory() {
        return player.getInventory();
    }
}
