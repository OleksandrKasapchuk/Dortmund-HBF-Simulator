package com.mygame.managers.global;

import com.mygame.Assets;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.game.GameSettings;
import com.mygame.game.SettingsManager;
import com.mygame.managers.nonglobal.InventoryManager;
import com.mygame.managers.nonglobal.NpcManager;
import com.mygame.managers.global.audio.SoundManager;
import com.mygame.ui.UIManager;
import com.mygame.ui.screenUI.GameUI;

public class ActionRegistry {

    public static void registerAll(DialogueRegistry dialogueRegistry, Player player, UIManager uiManager, NpcManager npcManager) {
        InventoryManager inventory = player.getInventory();
        GameUI gameUI = uiManager.getGameUI();
        GameSettings settings = SettingsManager.load();

        dialogueRegistry.registerAction("igo_give_vape", () -> {
            if (inventory.removeItem(ItemRegistry.get("joint"), 1)) {
                inventory.addItem(ItemRegistry.get("vape"), 1);
                uiManager.showEarned(1, Assets.bundle.get("item.vape.name"));
                QuestManager.removeQuest("igo");
                settings.completedDialogueEvents.add("igo_gave_vape");
                SettingsManager.save(settings);
                npcManager.findNpcByName(Assets.bundle.get("npc.igo.name")).setDialogue(dialogueRegistry.getDialogue("igo", "thanks"));
                TimerManager.setAction(() -> npcManager.findNpcByName(Assets.bundle.get("npc.igo.name")).setTexture(Assets.getTexture("igo2")), 5f);
            } else {
                uiManager.showNotEnough(Assets.bundle.format("item.joint.name"));
            }
        });

        dialogueRegistry.registerAction("igo_add_quest", () -> {
            if (!QuestManager.hasQuest("igo")) {
                QuestManager.addQuest(new QuestManager.Quest("igo", "quest.igo.name", "quest.igo.description"));
            }
        });

        dialogueRegistry.registerAction("ryzhyi_take_money", () -> {
            player.addMoney(20);
            uiManager.showEarned(20, Assets.bundle.get("item.money.name"));
            settings.completedDialogueEvents.add("ryzhyi_gave_money");
            SettingsManager.save(settings);
            npcManager.findNpcByName(Assets.bundle.get("npc.ryzhyi.name")).setDialogue(dialogueRegistry.getDialogue("ryzhyi", "after"));
        });

        dialogueRegistry.registerAction("baryga_buy_grass", () -> {
            if (inventory.removeItem(ItemRegistry.get("money"), 10)) {
                inventory.addItem(ItemRegistry.get("grass"), 1);
                uiManager.showEarned(1, Assets.bundle.get("item.grass.name"));
            } else {
                uiManager.showNotEnough(Assets.bundle.get("item.money.name"));
            }
        });

        dialogueRegistry.registerAction("chikita_craft_joint", () -> {
            if (!inventory.hasItem(ItemRegistry.get("grass"))) {
                uiManager.showNotEnough(Assets.bundle.get("item.grass.name"));
                return;
            }
            if (!inventory.hasItem(ItemRegistry.get("pape"))) {
                uiManager.showNotEnough(Assets.bundle.get("item.pape.name"));
                return;
            }
            inventory.removeItem(ItemRegistry.get("grass"), 1);
            inventory.removeItem(ItemRegistry.get("pape"), 1);
            player.setMovementLocked(true);
            SoundManager.playSound(Assets.kosyakSound);
            TimerManager.setAction(() -> {
                inventory.addItem(ItemRegistry.get("joint"), 1);
                uiManager.showEarned(1, Assets.bundle.get("item.joint.name"));
                player.setMovementLocked(false);
            }, 1f);
        });

        dialogueRegistry.registerAction("kioskman_buy_pape", () -> {
            if (inventory.removeItem(ItemRegistry.get("money"), 5)) {
                inventory.addItem(ItemRegistry.get("pape"), 1);
                uiManager.showEarned(1, Assets.bundle.get("item.pape.name"));
            } else {
                uiManager.showNotEnough(Assets.bundle.get("item.money.name"));
            }
        });

        dialogueRegistry.registerAction("kioskman_buy_icetea", () -> {
            if (inventory.getAmount(ItemRegistry.get("money")) >= 10) {
                inventory.removeItem(ItemRegistry.get("money"), 10);
                inventory.addItem(ItemRegistry.get("ice_tea"), 1);
                uiManager.showEarned(1, Assets.bundle.get("item.ice_tea.name"));
            } else {
                uiManager.showNotEnough(Assets.bundle.get("item.money.name"));
            }
        });

        dialogueRegistry.registerAction("junky_give_spoon", () -> {
            if (!QuestManager.hasQuest("spoon")) {
                QuestManager.addQuest(new QuestManager.Quest("spoon", "quest.spoon.name", "quest.spoon.description"));
            }
            if (inventory.removeItem(ItemRegistry.get("spoon"), 1)) {
                gameUI.showInfoMessage(Assets.bundle.get("message.junky.respect"), 1.5f);
                QuestManager.removeQuest("spoon");
            } else {
                uiManager.showNotEnough(Assets.bundle.get("message.junky.noSpoon"));
            }
        });

        dialogueRegistry.registerAction("junky_add_quest", () -> {
            if (!QuestManager.hasQuest("spoon")) {
                QuestManager.addQuest(new QuestManager.Quest("spoon", "quest.spoon.name", "quest.spoon.description"));
            }
        });

        dialogueRegistry.registerAction("boss_accept_quest", () -> {
            QuestManager.addQuest(new QuestManager.Quest("delivery", "quest.delivery.name", "quest.delivery.description"));
            inventory.addItem(ItemRegistry.get("grass"), 1000);
            uiManager.showEarned(1000, Assets.bundle.get("item.grass.name"));
            NPC bossRef = npcManager.findNpcByName(Assets.bundle.get("npc.boss.name"));
            if (bossRef != null) bossRef.setDialogue(dialogueRegistry.getDialogue("boss", "after"));
            settings.completedDialogueEvents.add("boss_gave_quest");
            SettingsManager.save(settings);
        });

        dialogueRegistry.registerAction("police_check", () -> {
            if (inventory.removeItem(ItemRegistry.get("grass"), 10000) || inventory.removeItem(ItemRegistry.get("joint"), 10000) || inventory.removeItem(ItemRegistry.get("vape"), 10000)) {
                gameUI.showInfoMessage(Assets.bundle.get("message.police.stuffLost"), 1.5f);
            } else {
                gameUI.showInfoMessage(Assets.bundle.get("message.police.checkPassed"), 1.5f);
            }
        });

        dialogueRegistry.registerAction("jason_give_money", () -> {
            player.addMoney(20);
            uiManager.showEarned(20, Assets.bundle.get("item.money.name"));
            NPC jason = npcManager.findNpcByName(Assets.bundle.get("npc.jason.name"));
            jason.setDialogue(dialogueRegistry.getDialogue("jason", "after"));
            settings.completedDialogueEvents.add("jason_gave_money");
            SettingsManager.save(settings);
        });

        dialogueRegistry.registerAction("murat_accept_quest", () -> QuestManager.addQuest(new QuestManager.Quest("chili", "quest.chili.name", "quest.chili.description")));

        dialogueRegistry.registerAction("walter_accept_quest", () -> QuestManager.addQuest(new QuestManager.Quest("wallet", "quest.wallet.name", "quest.wallet.description")));

        dialogueRegistry.registerAction("jamal_give_money", () -> {
            if (inventory.removeItem(ItemRegistry.get("money"), 5)) {
                gameUI.showInfoMessage(Assets.bundle.get("message.jamal.thanks"), 2f);
            } else {
                uiManager.showNotEnough(Assets.bundle.get("item.money.name"));
            }
        });
    }
}
