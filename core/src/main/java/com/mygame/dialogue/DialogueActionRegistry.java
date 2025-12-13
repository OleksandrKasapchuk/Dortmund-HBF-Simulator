package com.mygame.dialogue;

import com.mygame.Assets;
import com.mygame.dialogue.action.ActionContext;
import com.mygame.dialogue.action.AddQuestAction;
import com.mygame.dialogue.action.ChikitaCraftJointAction;
import com.mygame.dialogue.action.CompleteEventAction;
import com.mygame.dialogue.action.HasItemCondition;
import com.mygame.dialogue.action.PoliceCheckAction;
import com.mygame.dialogue.action.SetDialogueAction;
import com.mygame.dialogue.action.TradeAction;
import com.mygame.entity.player.Player;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.player.InventoryManager;
import com.mygame.entity.npc.NpcManager;
import com.mygame.managers.global.QuestManager;
import com.mygame.managers.global.TimerManager;
import com.mygame.ui.UIManager;

public class DialogueActionRegistry {

    public static void registerAll(DialogueRegistry dialogueRegistry, Player player, UIManager uiManager, NpcManager npcManager) {
        InventoryManager inventory = player.getInventory();

        ActionContext ctx = new ActionContext(player, uiManager, npcManager, dialogueRegistry);

        dialogueRegistry.registerAction("baryga_buy_grass", () -> new TradeAction(ctx, "money", "grass", 10, 1).execute());
        dialogueRegistry.registerAction("kioskman_buy_pape", () -> new TradeAction(ctx, "money", "pape", 5, 1).execute());
        dialogueRegistry.registerAction("kioskman_buy_icetea", () -> new TradeAction(ctx, "money", "ice_tea", 10, 1).execute());

        dialogueRegistry.registerAction("murat_accept_quest", () -> new AddQuestAction(ctx, "chili", "quest.chili.name", "quest.chili.description").execute());
        dialogueRegistry.registerAction("walter_accept_quest", () -> new AddQuestAction(ctx, "wallet", "quest.wallet.name", "quest.wallet.description").execute());
        dialogueRegistry.registerAction("igo_add_quest", () -> new AddQuestAction(ctx, "igo", "quest.igo.name", "quest.igo.description").execute());


        dialogueRegistry.registerAction("ryzhyi_take_money", () -> {
            inventory.addItemAndNotify(ItemRegistry.get("money"), 20);
            new SetDialogueAction(ctx, "npc.ryzhyi.name", "ryzhyi", "after").execute();
            new CompleteEventAction(ctx, "ryzhyi_gave_money").execute();
        });

        dialogueRegistry.registerAction("jason_give_money", () -> {
            inventory.addItemAndNotify(ItemRegistry.get("money"), 20);
            new SetDialogueAction(ctx, "npc.ryzhyi.name", "ryzhyi", "after").execute();
            new CompleteEventAction(ctx, "jason_gave_money").execute();
        });

        dialogueRegistry.registerAction("igo_give_vape", () -> {
            if (inventory.trade(ItemRegistry.get("joint"), ItemRegistry.get("vape"), 1, 1)) {
                QuestManager.removeQuest("igo");
                npcManager.findNpcByName(Assets.bundle.get("npc.igo.name")).setDialogue(dialogueRegistry.getDialogue("igo", "thanks"));
                new CompleteEventAction(ctx, "igo_gave_vape").execute();
                TimerManager.setAction(() -> npcManager.findNpcByName(Assets.bundle.get("npc.igo.name")).setTexture(Assets.getTexture("igo2")), 5f);
            }
        });

        dialogueRegistry.registerAction("chikita_craft_joint", () -> new ChikitaCraftJointAction(ctx).execute());

        dialogueRegistry.registerAction("boss_accept_quest", () -> {
            inventory.addItemAndNotify(ItemRegistry.get("grass"), 1000);
            new AddQuestAction(ctx, "delivery", "quest.delivery.name", "quest.delivery.description").execute();
            new SetDialogueAction(ctx, "npc.boss.name", "boss", "after").execute();
            new CompleteEventAction(ctx, "boss_gave_quest").execute();
        });

        dialogueRegistry.registerAction("police_check", () -> new PoliceCheckAction(ctx));


        dialogueRegistry.registerAction("give_vape_action", () -> {
            if (new HasItemCondition(ctx, "vape", "item.vape.name").check()) {
                npcManager.findNpcByName(Assets.bundle.get("npc.talahon2.name")).setDialogue(dialogueRegistry.getDialogue("talahon2", "accept"));
            }
        });
    }
}
