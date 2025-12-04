package com.mygame.managers.nonglobal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.mygame.Assets;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.entity.Police;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.game.GameSettings;
import com.mygame.game.SettingsManager;
import com.mygame.managers.global.DialogueRegistry;
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
    private final ArrayList<NPC> npcs = new ArrayList<>();
    private final Player player;
    private final UIManager uiManager;
    private final DialogueRegistry dialogueRegistry;

    // Direct references for special NPCs if needed
    private Police police;
    private NPC boss;
    private Police summonedPolice; // For the summoned police

    public NpcManager(Player player, UIManager uiManager) {
        this.player = player;
        this.uiManager = uiManager;
        this.dialogueRegistry = new DialogueRegistry();
        registerDialogueActions();
    }

    private void registerDialogueActions() {
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
                findNpcByName(Assets.bundle.get("npc.igo.name")).setDialogue(dialogueRegistry.getDialogue("igo", "thanks"));
                TimerManager.setAction(() -> findNpcByName(Assets.bundle.get("npc.igo.name")).setTexture(Assets.getTexture("igo2")), 5f);
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
            findNpcByName(Assets.bundle.get("npc.ryzhyi.name")).setDialogue(dialogueRegistry.getDialogue("ryzhyi", "after"));
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
            NPC bossRef = findNpcByName(Assets.bundle.get("npc.boss.name"));
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
            NPC jason = findNpcByName(Assets.bundle.get("npc.jason.name"));
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

        NPC npc;
        GameSettings settings = SettingsManager.load();
        List<String> completedEvents = settings.completedDialogueEvents;

        DialogueNode initialDialogue = dialogueRegistry.getInitialDialogue(npcId.toLowerCase());

        String npcName;
        try {
            npcName = Assets.bundle.get("npc." + npcId.toLowerCase() + ".name");
        } catch (Exception e) {
            npcName = npcId; // Fallback to id
        }

        int directionX = props.get("directionX", 0, Integer.class);
        int directionY = props.get("directionY", 0, Integer.class);
        float pauseTime = props.get("pauseTime", 0f, Float.class);
        float moveTime = props.get("moveTime", 0f, Float.class);
        int speed = props.get("speed", 50, Integer.class);
        int distance = props.get("distance", 150, Integer.class);

        if (npcId.equalsIgnoreCase("police")) {
            this.police = new Police(npcName, 100, 100, x, y, texture, world, 0, 100, initialDialogue);
            npc = this.police;
        } else {
            npc = new NPC(npcName, 100, 100, x, y, texture, world, directionX, directionY, pauseTime, moveTime, speed, distance, initialDialogue);
        }

        if (npcId.equalsIgnoreCase("igo") && completedEvents.contains("igo_gave_vape")) {
            npc.setDialogue(dialogueRegistry.getDialogue("igo", "thanks"));
            npc.setTexture(Assets.getTexture("igo2"));
        }
        if (npcId.equalsIgnoreCase("ryzhyi") && completedEvents.contains("ryzhyi_gave_money")) {
            npc.setDialogue(dialogueRegistry.getDialogue("ryzhyi", "after"));
        }
        if (npcId.equalsIgnoreCase("boss")) {
            this.boss = npc;
            if (completedEvents.contains("boss_gave_quest")) {
                npc.setDialogue(dialogueRegistry.getDialogue("boss", "after"));
            }
        }
        if (npcId.equalsIgnoreCase("jason") && completedEvents.contains("jason_gave_money")) {
            npc.setDialogue(dialogueRegistry.getDialogue("jason", "after"));
        }

        npcs.add(npc);
        world.getNpcs().add(npc);
        System.out.println("SUCCESS: Loaded '" + npcId + "' from map in world '" + world.getName() + "'");
    }

    public void update(float delta) {
        for (NPC npc : WorldManager.getCurrentWorld().getNpcs()) {
            npc.update(delta);
        }
    }

    public NPC findNpcByName(String name) {
        for (NPC npc : npcs) {
            if (npc.getName().equals(name)) return npc;
        }
        return null;
    }

    public void callPolice() {
        summonedPolice = new Police(Assets.bundle.get("npc.police.name"), 100, 100, player.getX(), player.getY() - 300, Assets.getTexture("police"), WorldManager.getWorld("main"), 200, 100, new DialogueNode("dialogue.police.called"));
        npcs.add(summonedPolice);
        WorldManager.getWorld("main").getNpcs().add(summonedPolice);
    }

    public NPC getBoss() { return boss; }
    public Police getPolice() { return police; }
    public Police getSummonedPolice(){ return summonedPolice; }

    public void kill(NPC npc) {
        if (npc == summonedPolice) summonedPolice = null;
        if (npc == police) police = null;
        if (npc == boss) boss = null;
    }
}
