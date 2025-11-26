package com.mygame.managers.nonglobal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.mygame.Assets;
import com.mygame.dialogue.Dialogue;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.entity.Police;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.game.GameSettings;
import com.mygame.game.SettingsManager;
import com.mygame.managers.global.QuestManager;
import com.mygame.managers.global.TimerManager;
import com.mygame.managers.global.audio.SoundManager;
import com.mygame.ui.UIManager;
import com.mygame.world.World;
import com.mygame.world.WorldManager;

import java.util.ArrayList;
import java.util.List;

public class NpcManager {
    private final ArrayList<NPC> allNpcs = new ArrayList<>();
    private final Player player;
    private final UIManager uiManager;

    // Direct references for special NPCs if needed
    private Police police;
    private NPC boss;
    private Police police1; // For the summoned police

    public NpcManager(Player player, UIManager uiManager) {
        this.player = player;
        this.uiManager = uiManager;
    }

    public void loadNpcsFromMap(World world) {
        MapLayer npcLayer = world.getMap().getLayers().get("npcs");
        if (npcLayer == null) return;

        for (MapObject object : npcLayer.getObjects()) {
            MapProperties props = object.getProperties();
            String npcId = props.get("name", String.class);
            if (npcId == null) continue;

            createNpcById(npcId, props, world);
        }
    }

    private void createNpcById(String npcId, MapProperties props, World world) {
        float x = props.get("x", 0f, Float.class);
        float y = props.get("y", 0f, Float.class);
        Texture texture = Assets.getTexture(npcId.toLowerCase());

        if (texture == null) {
            System.err.println("Texture for '" + npcId + "' not found! Using fallback.");
            texture = Assets.getTexture("zoe");
        }

        NPC npc = null;
        GameSettings settings = SettingsManager.load();
        List<String> completedEvents = settings.completedDialogueEvents;

        switch (npcId.toLowerCase()) {
            case "igo":
                DialogueNode igoNodeStart = new DialogueNode(Assets.bundle.get("dialogue.igo.start"));
                DialogueNode igoNodeThanks = new DialogueNode(Assets.bundle.get("dialogue.igo.thanks"));
                igoNodeStart.addChoice(Assets.bundle.get("dialogue.igo.choice.give"), () -> {
                    if (player.getInventory().removeItem(ItemRegistry.get("joint"), 1)) {
                        player.getInventory().addItem(ItemRegistry.get("vape"), 1);
                        QuestManager.removeQuest("igo");
                        settings.completedDialogueEvents.add("igo_gave_vape");
                        SettingsManager.save(settings);
                        findNpcByName(Assets.bundle.get("npc.igo.name")).setDialogue(new Dialogue(igoNodeThanks));
                        TimerManager.setAction(() -> findNpcByName(Assets.bundle.get("npc.igo.name")).setTexture(Assets.getTexture("igo2")), 5f);
                    } else {
                        uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.igo.notEnoughJoint"), 1.5f);
                    }
                });
                igoNodeStart.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), () -> { if (!QuestManager.hasQuest("igo")) QuestManager.addQuest(new QuestManager.Quest("igo", "quest.igo.name", "quest.igo.description")); });
                npc = new NPC(Assets.bundle.get("npc.igo.name"), 90, 90, x, y, texture, world, 1, 0, 3f, 0f, 0, 150, new Dialogue(completedEvents.contains("igo_gave_vape") ? igoNodeThanks : igoNodeStart));
                if (completedEvents.contains("igo_gave_vape")) npc.setTexture(Assets.getTexture("igo2"));
                break;

            case "ryzhyi":
                DialogueNode ryzhyiNodeStart = new DialogueNode(Assets.bundle.get("dialogue.ryzhyi.start"));
                DialogueNode ryzhyiNodeAfter = new DialogueNode(Assets.bundle.get("dialogue.ryzhyi.after"));
                ryzhyiNodeStart.addChoice(Assets.bundle.get("dialogue.ryzhyi.choice.take"), () -> {
                    player.getInventory().addItem(ItemRegistry.get("money"), 20);
                    SoundManager.playSound(Assets.moneySound);
                    settings.completedDialogueEvents.add("ryzhyi_gave_money");
                    SettingsManager.save(settings);
                    findNpcByName(Assets.bundle.get("npc.ryzhyi.name")).setDialogue(new Dialogue(ryzhyiNodeAfter));
                });
                npc = new NPC(Assets.bundle.get("npc.ryzhyi.name"), 90, 90, x, y, texture, world, 0, 1, 1f, 2f, 200, 150, new Dialogue(completedEvents.contains("ryzhyi_gave_money") ? ryzhyiNodeAfter : ryzhyiNodeStart));
                break;

            case "denys":
                npc = new NPC(Assets.bundle.get("npc.denys.name"), 90, 90, x, y, texture, world, 0, 1, 2f, 1f, 100, 150, new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.denys.start"), Assets.bundle.get("dialogue.denys.declined"))));
                break;

            case "baryga":
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
                npc = new NPC(Assets.bundle.get("npc.baryga.name"), 90, 90, x, y, texture, world, 0, 1, 3f, 0f, 0, 150, new Dialogue(barygaNode));
                break;

            case "chikita":
                DialogueNode chikitaNode = new DialogueNode(Assets.bundle.get("dialogue.chikita.start"));
                chikitaNode.addChoice(Assets.bundle.get("dialogue.chikita.choice.give"), () -> {
                    if (player.getInventory().hasItem(ItemRegistry.get("grass")) && player.getInventory().hasItem(ItemRegistry.get("pape"))) {
                        player.getInventory().removeItem(ItemRegistry.get("grass"), 1);
                        player.getInventory().removeItem(ItemRegistry.get("pape"), 1);
                        player.setMovementLocked(true);
                        SoundManager.playSound(Assets.kosyakSound);
                        TimerManager.setAction(() -> {
                            player.getInventory().addItem(ItemRegistry.get("joint"), 1);
                            uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.chikita.jointReceived"), 1.5f);
                            player.setMovementLocked(false);
                        }, 1f);
                    } else {
                        uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.chikita.notEnoughItems"), 1.5f);
                    }
                });
                chikitaNode.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), () -> {});
                npc = new NPC(Assets.bundle.get("npc.chikita.name"), 90, 90, x, y, texture, world, 0, 1, 3f, 0f, 0, 150, new Dialogue(chikitaNode));
                break;

            case "kioskman":
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
                npc = new NPC(Assets.bundle.get("npc.kioskman.name"), 90, 90, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(kioskNodeStart));
                break;

            case "junky":
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
                junkyNode.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), () -> { if (!QuestManager.hasQuest("spoon")) QuestManager.addQuest(new QuestManager.Quest("spoon", "quest.spoon.name", "quest.spoon.description")); });
                npc = new NPC(Assets.bundle.get("npc.junky.name"), 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(junkyNode));
                break;

            case "boss":
                DialogueNode bossNodeStart = new DialogueNode(Assets.bundle.get("dialogue.boss.start.1"), Assets.bundle.get("dialogue.boss.start.2"), Assets.bundle.get("dialogue.boss.start.3"), Assets.bundle.get("dialogue.boss.start.4"));
                DialogueNode bossNodeAfter = new DialogueNode(Assets.bundle.get("dialogue.boss.after.1"), Assets.bundle.get("dialogue.boss.after.2"));
                bossNodeStart.addChoice(Assets.bundle.get("dialogue.boss.choice.accept"), () -> {
                    QuestManager.addQuest(new QuestManager.Quest("delivery", "quest.delivery.name", "quest.delivery.description"));
                    player.getInventory().addItem(ItemRegistry.get("grass"), 1000);
                    NPC bossRef = findNpcByName(Assets.bundle.get("npc.boss.name"));
                    if (bossRef != null) bossRef.setDialogue(new Dialogue(bossNodeAfter));
                    settings.completedDialogueEvents.add("boss_gave_quest");
                    SettingsManager.save(settings);
                });
                bossNodeStart.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), () -> {});
                this.boss = new NPC(Assets.bundle.get("npc.boss.name"), 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(completedEvents.contains("boss_gave_quest") ? bossNodeAfter : bossNodeStart));
                npc = this.boss;
                break;

            case "police":
                DialogueNode policeNode = new DialogueNode(() -> {
                    if (player.getInventory().removeItem(ItemRegistry.get("grass"), 10000) || player.getInventory().removeItem(ItemRegistry.get("joint"), 10000) || player.getInventory().removeItem(ItemRegistry.get("vape"), 10000)) {
                        uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.police.stuffLost"), 1.5f);
                    } else {
                        uiManager.getGameUI().showInfoMessage(Assets.bundle.get("message.police.checkPassed"), 1.5f);
                    }
                }, Assets.bundle.get("dialogue.police.check"));
                this.police = new Police(Assets.bundle.get("npc.police.name"), 100, 100, x, y, texture, world, 0, 100, new Dialogue(policeNode));
                npc = this.police;
                break;

            case "kamil":
                npc = new NPC(Assets.bundle.get("npc.kamil.name"), 90, 90, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.kamil.start"))));
                break;

            case "jan":
                npc = new NPC(Assets.bundle.get("npc.jan.name"), 90, 90, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.jan.start"))));
                break;

            case "filip":
                npc = new NPC(Assets.bundle.get("npc.filip.name"), 90, 90, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.filip.start.1"), Assets.bundle.get("dialogue.filip.start.2"), Assets.bundle.get("dialogue.filip.start.3"), Assets.bundle.get("dialogue.filip.start.4"))));
                break;

            case "jason":
                npc = new NPC(Assets.bundle.get("npc.jason.name"), 90, 90, x, y, texture, world, 1, 0, 3f, 0f, 75, 100,
                    new Dialogue(new DialogueNode(() -> {
                        player.getInventory().addItem(ItemRegistry.get("money"), 20);
                        NPC jason = findNpcByName(Assets.bundle.get("npc.jason.name"));
                        jason.setDialogue(new Dialogue(new DialogueNode("...")));
                        uiManager.getGameUI().showInfoMessage("You got 20 euro", 1.5f);
                    },
                    Assets.bundle.get("dialogue.jason.start.1"), Assets.bundle.get("dialogue.jason.start.2"),
                    Assets.bundle.get("dialogue.jason.start.3"), Assets.bundle.get("dialogue.jason.start.4"),
                    Assets.bundle.get("dialogue.jason.start.5"), Assets.bundle.get("dialogue.jason.start.6"),
                    Assets.bundle.get("dialogue.jason.start.7"))));
                break;

        }

        if (npc != null) {
            allNpcs.add(npc);
            world.getNpcs().add(npc);
            System.out.println("SUCCESS: Loaded '" + npcId + "' from map in world '" + world.getName() + "'");
        }
    }

    public void update(float delta) {
        for (NPC npc : WorldManager.getCurrentWorld().getNpcs()) {
            npc.update(delta);
        }
    }

    public NPC findNpcByName(String name) {
        for (NPC npc : allNpcs) {
            if (npc.getName().equals(name)) return npc;
        }
        return null;
    }

    public void callPolice() {
        police1 = new Police(Assets.bundle.get("npc.police.name"), 100, 100, player.getX(), player.getY() - 300, Assets.getTexture("police"), WorldManager.getWorld("main"), 200, 100, new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.police.called"))));
        allNpcs.add(police1);
        WorldManager.getWorld("main").getNpcs().add(police1);
    }

    public NPC getBoss() { return boss; }
    public Police getPolice() { return police; }
    public Police getPolice1(){ return police1; }

    /** Remove NPC from the game */
    public void kill(NPC npc) {
        if (npc == police1) police1 = null;
        if (npc == police) police = null;
        if (npc == boss) boss = null;
    }
}
