package com.mygame.managers.nonglobal;

import com.mygame.Assets;
import com.mygame.dialogue.Dialogue;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.entity.Police;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.managers.global.WorldManager;
import com.mygame.managers.global.audio.SoundManager;
import com.mygame.managers.global.QuestManager;
import com.mygame.managers.global.TimerManager;
import com.mygame.ui.UIManager;
import com.mygame.world.World;

import java.util.ArrayList;

/**
 * Manager for creating, updating, rendering, and managing NPCs.
 * Includes special NPCs such as boss and police.
 */
public class NpcManager {
    private final ArrayList<NPC> npcs = new ArrayList<>();
    private Police police;
    private NPC boss;
    private Police police1;

    private final Player player;

    /**
     * Constructor initializes NPCs and sets up dialogues.
     */
    public NpcManager(Player player, UIManager uiManager) {
        this.player = player;
        createNpcs(uiManager);
    }

    /**
     * Initialize all NPCs with dialogues and actions.
     */
    private void createNpcs(UIManager uiManager) {
        World world = WorldManager.getWorld("main");
        World world2 = WorldManager.getWorld("back");

        // --- IGO NPC ---
        DialogueNode igoNodeStart = new DialogueNode(Assets.bundle.get("dialogue.igo.start"));
        DialogueNode igoNodeThanks = new DialogueNode(Assets.bundle.get("dialogue.igo.thanks"));
        DialogueNode igoNodeBye = new DialogueNode(() -> {
            if (!QuestManager.hasQuest("igo")) {
                QuestManager.addQuest(new QuestManager.Quest("igo", "quest.igo.name", "quest.igo.description"));
            }
        }, Assets.bundle.get("dialogue.igo.bye"));

        Runnable igoAction = () -> {
            if (!QuestManager.hasQuest("igo")) {
                QuestManager.addQuest(new QuestManager.Quest("igo", "quest.igo.name", "quest.igo.description"));
            }
            if (player.getInventory().removeItem(ItemRegistry.get("joint"), 1)) {
                player.getInventory().addItem(ItemRegistry.get("vape"), 1);
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.igo.questItemReceived"), 1.5f);
                QuestManager.removeQuest("igo");
                SoundManager.playSound(Assets.lighterSound);

                NPC igo = findNpcByName(Assets.bundle.get("npc.igo.name"));
                if (igo != null) {
                    igo.setDialogue(new Dialogue(igoNodeThanks));
                    TimerManager.setAction(() -> igo.setTexture(Assets.textureIgo2), 5f);
                }
            } else {
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.igo.notEnoughJoint"), 1.5f);
            }
        };

        igoNodeStart.addChoice(Assets.bundle.get("dialogue.igo.choice.give"), igoAction);
        igoNodeStart.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), igoNodeBye);

        NPC igo = new NPC(Assets.bundle.get("npc.igo.name"), 90, 90, 2500, 1200, Assets.textureIgo, world2, 1, 0, 3f, 0f, 0, 150,
            new Dialogue(igoNodeStart));
        npcs.add(igo);
        world.getNpcs().add(igo);


        // --- RYZHYI NPC ---
        DialogueNode ryzhyiNodeStart = new DialogueNode(Assets.bundle.get("dialogue.ryzhyi.start"));
        DialogueNode ryzhyiNodeAfter = new DialogueNode(Assets.bundle.get("dialogue.ryzhyi.after"));
        Runnable ryzhyiAction = () -> {
            player.getInventory().addItem(ItemRegistry.get("money"), 20);
            SoundManager.playSound(Assets.moneySound);
            uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.ryzhyi.moneyReceived"), 1.5f);
            NPC ryzhyi = findNpcByName(Assets.bundle.get("npc.ryzhyi.name"));
            if (ryzhyi != null) ryzhyi.setDialogue(new Dialogue(ryzhyiNodeAfter));
        };
        ryzhyiNodeStart.addChoice(Assets.bundle.get("dialogue.ryzhyi.choice.take"), ryzhyiAction);
        NPC ryzhyi = new NPC(Assets.bundle.get("npc.ryzhyi.name"), 90, 90, 2900, 500, Assets.textureRyzhyi, world, 0, 1, 1f, 2f, 200, 150,
            new Dialogue(ryzhyiNodeStart));
        npcs.add(ryzhyi);
        world.getNpcs().add(ryzhyi);

        // --- DENYS NPC ---
        NPC denys = new NPC(Assets.bundle.get("npc.denys.name"), 90, 90, 200, 1400, Assets.textureDenys, world, 0, 1, 2f, 1f, 100, 150,
            new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.denys.start"), Assets.bundle.get("dialogue.denys.declined"))));
        npcs.add(denys);
        world.getNpcs().add(denys);

        // --- BARYGA NPC ---
        DialogueNode barygaNode = new DialogueNode(Assets.bundle.get("dialogue.baryga.start.1"), Assets.bundle.get("dialogue.baryga.start.2"));
        barygaNode.addChoice(Assets.bundle.get("dialogue.baryga.choice.buy"), () -> {
            if (player.getInventory().removeItem(ItemRegistry.get("money"), 10)) {
                player.getInventory().addItem(ItemRegistry.get("grass"), 1);
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.baryga.grassBought"), 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.generic.notEnoughMoney"), 1.5f);
            }
        });
        barygaNode.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), new DialogueNode(Assets.bundle.get("dialogue.baryga.bye")));
        NPC baryga = new NPC(Assets.bundle.get("npc.baryga.name"), 90, 90, 1500, 600, Assets.textureBaryga, world, 0, 1, 3f, 0f, 0, 150,
            new Dialogue(barygaNode));
        npcs.add(baryga);
        world2.getNpcs().add(baryga);


        // --- CHIKITA NPC ---
        DialogueNode chikitaNode = new DialogueNode(Assets.bundle.get("dialogue.chikita.start"));
        chikitaNode.addChoice(Assets.bundle.get("dialogue.chikita.choice.give"), () -> {
            if (player.getInventory().hasItem(ItemRegistry.get("grass")) && player.getInventory().hasItem(ItemRegistry.get("pape"))) {
                player.getInventory().removeItem(ItemRegistry.get("grass"), 1);
                player.getInventory().removeItem(ItemRegistry.get("pape"), 1);
                player.setMovementLocked(true);
                SoundManager.playSound(Assets.kosyakSound);
                TimerManager.setAction(() -> {
                    player.getInventory().addItem(ItemRegistry.get("joint"),1);
                    uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.chikita.jointReceived"), 1.5f);
                    player.setMovementLocked(false);
                }, 1f);
            } else {
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.chikita.notEnoughItems"), 1.5f);
            }
        });
        chikitaNode.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), () -> {});
        NPC chikita = new NPC(Assets.bundle.get("npc.chikita.name"), 90, 90, 450, 600, Assets.textureChikita, world, 0, 1, 3f, 0f, 0, 150,
            new Dialogue(chikitaNode));
        npcs.add(chikita);
        world2.getNpcs().add(chikita);


        // --- POLICE NPC ---
        DialogueNode policeNode = new DialogueNode(() -> {
            if (player.getInventory().removeItem(ItemRegistry.get("grass"), 10000) ||
                player.getInventory().removeItem(ItemRegistry.get("joint"), 10000) ||
                player.getInventory().removeItem(ItemRegistry.get("vape"), 10000)) {
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.police.stuffLost"), 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.police.checkPassed"), 1.5f);
            }
        }, Assets.bundle.get("dialogue.police.check"));
        police = new Police(Assets.bundle.get("npc.police.name"), 100, 100, 1000, 600, Assets.texturePolice, world, 0, 100, new Dialogue(policeNode));
        npcs.add(police);
        world.getNpcs().add(police);


        // --- KIOSKMAN NPC ---
        DialogueNode kioskNodeStart = new DialogueNode(Assets.bundle.get("dialogue.kioskman.start"));
        kioskNodeStart.addChoice(Assets.bundle.get("dialogue.kioskman.choice.buyPape"), () -> {
            if (player.getInventory().removeItem(ItemRegistry.get("money"), 5)) {
                player.getInventory().addItem(ItemRegistry.get("pape"), 1);
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.kioskman.papeBought"), 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.generic.notEnoughMoney"), 1.5f);
            }
        });

        kioskNodeStart.addChoice(Assets.bundle.get("dialogue.kioskman.choice.buyIceTea"), () -> {
            if (player.getInventory().getAmount(ItemRegistry.get("money")) >= 10) {
                player.getInventory().removeItem(ItemRegistry.get("money"), 10);
                player.getInventory().addItem(ItemRegistry.get("ice_tea"), 1);
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.kioskman.iceTeaBought"), 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.generic.notEnoughMoney"), 1.5f);
            }
        });
        kioskNodeStart.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), new DialogueNode(Assets.bundle.get("dialogue.kioskman.bye")));
        NPC kioskman = new NPC(Assets.bundle.get("npc.kioskman.name"), 90, 90, 1575, 300, Assets.textureKioskMan, world, 1, 0, 3f, 0f, 75, 100,
            new Dialogue(kioskNodeStart));
        npcs.add(kioskman);
        world.getNpcs().add(kioskman);


        // --- JUNKY NPC ---
        DialogueNode junkyNode = new DialogueNode(Assets.bundle.get("dialogue.junky.start"));
        junkyNode.addChoice(Assets.bundle.get("dialogue.junky.choice.give"), () -> {
            if (!QuestManager.hasQuest("spoon")) {
                QuestManager.addQuest(new QuestManager.Quest("spoon", "quest.spoon.name", "quest.spoon.description"));
            }
            if (player.getInventory().removeItem(ItemRegistry.get("spoon"), 1)) {
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.junky.respect"), 1.5f);
                QuestManager.removeQuest("spoon");
            } else {
                uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.junky.noSpoon"), 1.5f);
            }
        });
        junkyNode.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), () -> {
            if (!QuestManager.hasQuest("spoon")) {
                QuestManager.addQuest(new QuestManager.Quest("spoon", "quest.spoon.name", "quest.spoon.description"));
            }
        });
        NPC junky = new NPC(Assets.bundle.get("npc.junky.name"), 100, 100, 2800, 1600, Assets.textureJunky, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(junkyNode));
        npcs.add(junky);
        world.getNpcs().add(junky);


        // --- BOSS NPC ---
        DialogueNode bossNodeStart = new DialogueNode(
            Assets.bundle.get("dialogue.boss.start.1"),
            Assets.bundle.get("dialogue.boss.start.2"),
            Assets.bundle.get("dialogue.boss.start.3"),
            Assets.bundle.get("dialogue.boss.start.4")
        );
        DialogueNode bossNodeAfter = new DialogueNode(Assets.bundle.get("dialogue.boss.after.1"), Assets.bundle.get("dialogue.boss.after.2"));
        bossNodeStart.addChoice(Assets.bundle.get("dialogue.boss.choice.accept"), () -> {
            QuestManager.addQuest(new QuestManager.Quest("delivery", "quest.delivery.name", "quest.delivery.description"));
            uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.boss.questItemReceived"), 1.5f);
            player.getInventory().addItem(ItemRegistry.get("grass"), 1000);
            NPC boss = findNpcByName(Assets.bundle.get("npc.boss.name"));
            if (boss != null) boss.setDialogue(new Dialogue(bossNodeAfter));
        });
        bossNodeStart.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), () -> {});
        boss = new NPC(Assets.bundle.get("npc.boss.name"), 100, 100, 1850, 100, Assets.textureBoss, world, 1, 0, 3f, 0f, 75, 100,
            new Dialogue(bossNodeStart));
        npcs.add(boss);
        world.getNpcs().add(boss);


        // --- KAMIL NPC ---
        NPC kamil = new NPC(Assets.bundle.get("npc.kamil.name"), 90, 90, 1200, 800, Assets.textureKamil, world, 1, 0, 3f, 0f, 75, 100,
            new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.kamil.start"))));
        npcs.add(kamil);
        world.getNpcs().add(kamil);
    }

    /** Update all NPCs */
    public void update(float delta) {
        for (NPC npc : WorldManager.getCurrentWorld().getNpcs()) {
           npc.update(delta);
        }
    }

    /** Find NPC by name */
    public NPC findNpcByName(String name) {
        for (NPC npc : npcs) {
            if (npc.getName().equals(name)) return npc;
        }
        return null;
    }

    public NPC getBoss() { return boss; }
    public Police getPolice1() { return police1; }
    public Police getPolice() { return police; }

    /** Remove NPC from the game */
    public void kill(NPC npc) {
        npcs.remove(npc);
        if (npc == police1) police1 = null;
        if (npc == police) police = null;
        if (npc == boss) boss = null;
    }

    /** Call police to the player's position */
    public void callPolice() {
        police1 = new Police(Assets.bundle.get("npc.police.name"), 100, 100, player.getX(), player.getY() - 300, Assets.texturePolice, WorldManager.getWorld("main"), 200, 100,
            new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.police.called"))));
        npcs.add(police1);
        WorldManager.getWorld("main").getNpcs().add(police1);
    }
}
