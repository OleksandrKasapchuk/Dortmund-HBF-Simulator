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
import com.mygame.ui.screenUI.GameUI;
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

        InventoryManager inventory = player.getInventory();
        GameUI gameUI = uiManager.getGameUI();


        switch (npcId.toLowerCase()) {
            case "igo":
                DialogueNode igoNodeStart = new DialogueNode(Assets.bundle.get("dialogue.igo.start"));
                DialogueNode igoNodeThanks = new DialogueNode(Assets.bundle.get("dialogue.igo.thanks"));
                igoNodeStart.addChoice(Assets.bundle.get("dialogue.igo.choice.give"), () -> {
                    if (inventory.removeItem(ItemRegistry.get("joint"), 1)) {
                        inventory.addItem(ItemRegistry.get("vape"), 1);
                        uiManager.showEarned(1, Assets.bundle.get("item.vape.name"));

                        QuestManager.removeQuest("igo");
                        settings.completedDialogueEvents.add("igo_gave_vape");
                        SettingsManager.save(settings);
                        findNpcByName(Assets.bundle.get("npc.igo.name")).setDialogue(new Dialogue(igoNodeThanks));
                        TimerManager.setAction(() -> findNpcByName(Assets.bundle.get("npc.igo.name")).setTexture(Assets.getTexture("igo2")), 5f);
                    } else {
                        uiManager.showNotEnough(Assets.bundle.format("item.joint.name"));
                    }
                });
                igoNodeStart.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), () -> { if (!QuestManager.hasQuest("igo")) QuestManager.addQuest(new QuestManager.Quest("igo", "quest.igo.name", "quest.igo.description")); });
                npc = new NPC(Assets.bundle.get("npc.igo.name"), 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 0, 150, new Dialogue(completedEvents.contains("igo_gave_vape") ? igoNodeThanks : igoNodeStart));
                if (completedEvents.contains("igo_gave_vape")) npc.setTexture(Assets.getTexture("igo2"));
                break;

            case "ryzhyi":
                DialogueNode ryzhyiNodeStart = new DialogueNode(Assets.bundle.get("dialogue.ryzhyi.start"));
                DialogueNode ryzhyiNodeAfter = new DialogueNode(Assets.bundle.get("dialogue.ryzhyi.after"));
                ryzhyiNodeStart.addChoice(Assets.bundle.get("dialogue.ryzhyi.choice.take"), () -> {
                    player.addMoney(20);
                    uiManager.showEarned(20, Assets.bundle.get("item.money.name"));

                    settings.completedDialogueEvents.add("ryzhyi_gave_money");
                    SettingsManager.save(settings);
                    findNpcByName(Assets.bundle.get("npc.ryzhyi.name")).setDialogue(new Dialogue(ryzhyiNodeAfter));
                });
                npc = new NPC(Assets.bundle.get("npc.ryzhyi.name"), 100, 100, x, y, texture, world, 0, 1, 1f, 2f, 200, 150, new Dialogue(completedEvents.contains("ryzhyi_gave_money") ? ryzhyiNodeAfter : ryzhyiNodeStart));
                break;

            case "denys":
                npc = new NPC(Assets.bundle.get("npc.denys.name"), 100, 100, x, y, texture, world, 0, 1, 2f, 1f, 100, 150, new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.denys.start"), Assets.bundle.get("dialogue.denys.declined"))));
                break;

            case "baryga":
                DialogueNode barygaNode = new DialogueNode(Assets.bundle.get("dialogue.baryga.start.1"), Assets.bundle.get("dialogue.baryga.start.2"));
                barygaNode.addChoice(Assets.bundle.get("dialogue.baryga.choice.buy"), () -> {
                    if (inventory.removeItem(ItemRegistry.get("money"), 10)) {
                        inventory.addItem(ItemRegistry.get("grass"), 1);
                        uiManager.showEarned(1, Assets.bundle.get("item.grass.name"));
                    } else {
                        uiManager.showNotEnough(Assets.bundle.get("item.money.name"));
                    }
                });
                barygaNode.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), new DialogueNode(Assets.bundle.get("dialogue.baryga.bye")));
                npc = new NPC(Assets.bundle.get("npc.baryga.name"), 100, 100, x, y, texture, world, 0, 1, 3f, 0f, 0, 150, new Dialogue(barygaNode));
                break;

            case "chikita":
                DialogueNode chikitaNode = new DialogueNode(Assets.bundle.get("dialogue.chikita.start"));
                chikitaNode.addChoice(Assets.bundle.get("dialogue.chikita.choice.give"), () -> {
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
                chikitaNode.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), () -> {});
                npc = new NPC(Assets.bundle.get("npc.chikita.name"), 100, 100, x, y, texture, world, 0, 1, 3f, 0f, 0, 150, new Dialogue(chikitaNode));
                break;

            case "kioskman":
                DialogueNode kioskNodeStart = new DialogueNode(Assets.bundle.get("dialogue.kioskman.start"));
                kioskNodeStart.addChoice(Assets.bundle.get("dialogue.kioskman.choice.buyPape"), () -> {
                    if (inventory.removeItem(ItemRegistry.get("money"), 5)) {
                        inventory.addItem(ItemRegistry.get("pape"), 1);
                        uiManager.showEarned(1, Assets.bundle.get("item.pape.name"));
                    } else {
                        uiManager.showNotEnough(Assets.bundle.get("item.money.name"));
                    }
                });
                kioskNodeStart.addChoice(Assets.bundle.get("dialogue.kioskman.choice.buyIceTea"), () -> {
                    if (inventory.getAmount(ItemRegistry.get("money")) >= 10) {
                        inventory.removeItem(ItemRegistry.get("money"), 10);

                        inventory.addItem(ItemRegistry.get("ice_tea"), 1);
                        uiManager.showEarned(1, Assets.bundle.get("item.ice_tea.name"));
                    } else {
                        uiManager.showNotEnough(Assets.bundle.get("item.money.name"));
                    }
                });
                kioskNodeStart.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), new DialogueNode(Assets.bundle.get("dialogue.kioskman.bye")));
                npc = new NPC(Assets.bundle.get("npc.kioskman.name"), 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(kioskNodeStart));
                break;

            case "junky":
                DialogueNode junkyNode = new DialogueNode(Assets.bundle.get("dialogue.junky.start"));
                junkyNode.addChoice(Assets.bundle.get("dialogue.junky.choice.give"), () -> {
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
                junkyNode.addChoice(Assets.bundle.get("dialogue.igo.choice.leave"), () -> { if (!QuestManager.hasQuest("spoon")) QuestManager.addQuest(new QuestManager.Quest("spoon", "quest.spoon.name", "quest.spoon.description")); });
                npc = new NPC(Assets.bundle.get("npc.junky.name"), 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(junkyNode));
                break;

            case "boss":
                DialogueNode bossNodeStart = new DialogueNode(Assets.bundle.get("dialogue.boss.start.1"), Assets.bundle.get("dialogue.boss.start.2"), Assets.bundle.get("dialogue.boss.start.3"), Assets.bundle.get("dialogue.boss.start.4"));
                DialogueNode bossNodeAfter = new DialogueNode(Assets.bundle.get("dialogue.boss.after.1"), Assets.bundle.get("dialogue.boss.after.2"));
                bossNodeStart.addChoice(Assets.bundle.get("dialogue.boss.choice.accept"), () -> {
                    QuestManager.addQuest(new QuestManager.Quest("delivery", "quest.delivery.name", "quest.delivery.description"));

                    inventory.addItem(ItemRegistry.get("grass"), 1000);
                    uiManager.showEarned(1000, Assets.bundle.get("item.grass.name"));

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
                    if (inventory.removeItem(ItemRegistry.get("grass"), 10000) || inventory.removeItem(ItemRegistry.get("joint"), 10000) || inventory.removeItem(ItemRegistry.get("vape"), 10000)) {
                        gameUI.showInfoMessage(Assets.bundle.get("message.police.stuffLost"), 1.5f);
                    } else {
                        gameUI.showInfoMessage(Assets.bundle.get("message.police.checkPassed"), 1.5f);
                    }
                }, Assets.bundle.get("dialogue.police.check"));
                this.police = new Police(Assets.bundle.get("npc.police.name"), 100, 100, x, y, texture, world, 0, 100, new Dialogue(policeNode));
                npc = this.police;
                break;

            case "kamil":
                npc = new NPC(Assets.bundle.get("npc.kamil.name"), 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.kamil.start"))));
                break;

            case "jan":
                npc = new NPC(Assets.bundle.get("npc.jan.name"), 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.jan.start"))));
                break;

            case "filip":
                npc = new NPC(Assets.bundle.get("npc.filip.name"), 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.filip.start.1"), Assets.bundle.get("dialogue.filip.start.2"), Assets.bundle.get("dialogue.filip.start.3"), Assets.bundle.get("dialogue.filip.start.4"))));
                break;

            case "jason":
                DialogueNode jasonEndNode = new DialogueNode("...");
                DialogueNode jasonStartNode = new DialogueNode(() -> {

                    player.addMoney(20);
                    uiManager.showEarned(20, Assets.bundle.get("item.money.name"));

                    NPC jason = findNpcByName(Assets.bundle.get("npc.jason.name"));
                    jason.setDialogue(new Dialogue(jasonEndNode));


                    settings.completedDialogueEvents.add("jason_gave_money");
                    SettingsManager.save(settings);
                },  Assets.bundle.get("dialogue.jason.start.1"), Assets.bundle.get("dialogue.jason.start.2"),
                    Assets.bundle.get("dialogue.jason.start.3"), Assets.bundle.get("dialogue.jason.start.4"),
                    Assets.bundle.get("dialogue.jason.start.5"));

                npc = new NPC(Assets.bundle.get("npc.jason.name"), 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100,
                    new Dialogue(completedEvents.contains("jason_gave_money") ? jasonEndNode : jasonStartNode));
                break;

            case "murat":
                DialogueNode muratNode = new DialogueNode(Assets.bundle.get("dialogue.murat.start.1"), Assets.bundle.get("dialogue.murat.start.2"), Assets.bundle.get("dialogue.murat.start.3"), Assets.bundle.get("dialogue.murat.start.4"), Assets.bundle.get("dialogue.murat.start.5"), Assets.bundle.get("dialogue.murat.start.6"));
                muratNode.addChoice(Assets.bundle.get("dialogue.murat.choice.accept"), () -> QuestManager.addQuest(new QuestManager.Quest("chili", "quest.chili.name", "quest.chili.description")));
                muratNode.addChoice(Assets.bundle.get("dialogue.murat.choice.decline"), () -> {});
                npc = new NPC(Assets.bundle.get("npc.murat.name"), 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(muratNode));
                break;

            case "talahon1":
                npc = new NPC("talahon1", 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.talahon1.start.1"), Assets.bundle.get("dialogue.talahon1.start.2"), Assets.bundle.get("dialogue.talahon1.start.3"))));
                break;
            case "talahon2":
                npc = new NPC("talahon2", 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(new DialogueNode(Assets.bundle.get("dialogue.talahon2.start.1"), Assets.bundle.get("dialogue.talahon2.start.2"), Assets.bundle.get("dialogue.talahon2.start.3"))));
                break;

            case "walter":
                DialogueNode walterNode = new DialogueNode(Assets.bundle.get("dialogue.walter.start.1"), Assets.bundle.get("dialogue.walter.start.2"), Assets.bundle.get("dialogue.walter.start.3"), Assets.bundle.get("dialogue.walter.start.4"), Assets.bundle.get("dialogue.walter.start.5"), Assets.bundle.get("dialogue.walter.start.6"));
                walterNode.addChoice(Assets.bundle.get("dialogue.walter.choice.help"), () -> QuestManager.addQuest(new QuestManager.Quest("wallet", "quest.wallet.name", "quest.wallet.description")));
                walterNode.addChoice(Assets.bundle.get("dialogue.walter.choice.ignore"), () -> {});
                npc = new NPC(Assets.bundle.get("npc.walter.name"), 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(walterNode));
                break;

            case "dmitri":
                DialogueNode dmitriNode = new DialogueNode(Assets.bundle.get("dialogue.dmitri.start.1"), Assets.bundle.get("dialogue.dmitri.start.2"), Assets.bundle.get("dialogue.dmitri.start.3"), Assets.bundle.get("dialogue.dmitri.start.4"), Assets.bundle.get("dialogue.dmitri.start.5"), Assets.bundle.get("dialogue.dmitri.start.6"), Assets.bundle.get("dialogue.dmitri.start.7"));
                npc = new NPC(Assets.bundle.get("npc.dmitri.name"), 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(dmitriNode));
                break;

            case "jamal":
                DialogueNode jamalNode = new DialogueNode(Assets.bundle.get("dialogue.jamal.start.1"), Assets.bundle.get("dialogue.jamal.start.2"), Assets.bundle.get("dialogue.jamal.start.3"), Assets.bundle.get("dialogue.jamal.start.4"), Assets.bundle.get("dialogue.jamal.start.5"), Assets.bundle.get("dialogue.jamal.start.6"));
                jamalNode.addChoice(Assets.bundle.get("dialogue.jamal.choice.give_money"), () -> {
                    if (inventory.removeItem(ItemRegistry.get("money"), 5)) {
                        gameUI.showInfoMessage(Assets.bundle.get("message.jamal.thanks"), 2f);
                    } else {
                        uiManager.showNotEnough(Assets.bundle.get("item.money.name"));
                    }
                });
                jamalNode.addChoice(Assets.bundle.get("dialogue.jamal.choice.ignore"), () -> {});
                npc = new NPC(Assets.bundle.get("npc.jamal.name"), 100, 100, x, y, texture, world, 1, 0, 3f, 0f, 75, 100, new Dialogue(jamalNode));
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

    public void kill(NPC npc) {
        if (npc == police1) police1 = null;
        if (npc == police) police = null;
        if (npc == boss) boss = null;
    }
}
