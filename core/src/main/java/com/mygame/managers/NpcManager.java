package com.mygame.managers;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.Assets;
import com.mygame.Dialogue;
import com.mygame.DialogueNode;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.managers.audio.SoundManager;
import com.mygame.ui.UIManager;
import com.mygame.world.World;
import java.util.ArrayList;

public class NpcManager {
    private final ArrayList<NPC> npcs = new ArrayList<>();
    private NPC police;
    private NPC boss;
    private NPC police1;

    private final Player player;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final World world;

    public NpcManager(SpriteBatch batch, Player player, World world, UIManager uiManager, BitmapFont font) {
        this.batch = batch;
        this.player = player;
        this.font = font;
        this.world = world;
        createNpcs(uiManager, world);
    }

    private void createNpcs(UIManager uiManager, World world) {

        // === IGO ===
        DialogueNode igoNode_start = new DialogueNode("Hi bro! Give me some joint");
        DialogueNode igoNode_thanks = new DialogueNode("Thanks bro!");
        DialogueNode igoNode_bye = new DialogueNode(() ->
        {if (!QuestManager.hasQuest("Igo")) {QuestManager.addQuest(new QuestManager.Quest("Igo", "Get some joint for igo"));}},"See ya!");

        Runnable igoAction = () -> {
            if (!QuestManager.hasQuest("Igo")) {QuestManager.addQuest(new QuestManager.Quest("Igo", "Get some joint for igo"));}
            if (player.getInventory().removeItem("joint", 1)) {
                player.getInventory().addItem("vape", 1);
                uiManager.getGameUI().showInfoMessage("You got 1 vape", 1.5f);
                QuestManager.removeQuest("Igo");
                SoundManager.playSound(Assets.lighterSound);
                NPC igo = findNpcByName("Igo");
                if (igo != null) {
                    igo.setDialogue(new Dialogue(igoNode_thanks));
                    TimerManager.setAction(() -> igo.setTexture(Assets.textureIgo2), 5f);
                }
            } else {
                uiManager.getGameUI().showInfoMessage("Not enough joint", 1.5f);
            }
        };

        igoNode_start.addChoice("Give joint", igoAction);
        igoNode_start.addChoice("Leave", igoNode_bye);

        NPC igo = new NPC("Igo", 90, 90, 500, 300, Assets.textureIgo, world, 1, 0, 3f, 0f, 0, 150,
            new Dialogue(igoNode_start));
        npcs.add(igo);

        // === RYZHYI ===
        DialogueNode ryzhyiNode_start = new DialogueNode("Please take 20 euro but fuck off");
        DialogueNode ryzhyiNode_after = new DialogueNode("I gave you 20 euro, why do I still see you?");
        Runnable ryzhyiAction = () -> {
            player.getInventory().addItem("money", 20);
            SoundManager.playSound(Assets.moneySound);
            uiManager.getGameUI().showInfoMessage("You got 20 euro", 1.5f);
            NPC ryzhyi = findNpcByName("Ryzhyi");
            if (ryzhyi != null) {
                ryzhyi.setDialogue(new Dialogue(ryzhyiNode_after));
            }
        };
        ryzhyiNode_start.addChoice("Take 20 euro", ryzhyiAction);
        NPC ryzhyi = new NPC("Ryzhyi", 90, 90, 1100, 500, Assets.textureRyzhyi, world, 0, 1, 1f, 2f, 200, 150, new Dialogue(ryzhyiNode_start));
        npcs.add(ryzhyi);

        // === DENYS ===
        NPC denys = new NPC("Denys", 90, 90, 700, 700, Assets.textureDenys, world, 1, 1, 2f, 1f, 100, 150, new Dialogue(new DialogueNode("Hello!", "I'm not in mood to talk")));
        npcs.add(denys);

        // === BARYGA ===
        DialogueNode barygaNode = new DialogueNode("What do you need?", "Grass 10 euro");
        barygaNode.addChoice("Buy grass (10 euro)", () -> {
            if (player.getInventory().removeItem("money", 10)) {
                player.getInventory().addItem("grass", 1);
                uiManager.getGameUI().showInfoMessage("You got 1g grass for 10 euro", 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage("Not enough money", 1.5f);
            }
        });
        barygaNode.addChoice("Leave", new DialogueNode("Come back if you need something."));
        NPC baryga = new NPC("Baryga", 90, 90, 1000, 200, Assets.textureBaryga, world, 0, 1, 3f, 0f, 0, 150, new Dialogue(barygaNode));
        npcs.add(baryga);

        // === CHIKITA ===
        DialogueNode chikitaNode = new DialogueNode("Give me grass and pape and I make you a joint");
        chikitaNode.addChoice("Give grass and pape", () -> {
            if (player.getInventory().hasItem("grass") && player.getInventory().hasItem("pape")) {
                player.getInventory().removeItem("grass", 1);
                player.getInventory().removeItem("pape", 1);
                player.setMovementLocked(true);
                SoundManager.playSound(Assets.kosyakSound);
                TimerManager.setAction(() -> {
                    uiManager.getGameUI().showInfoMessage("You got 1 joint", 1.5f);
                    player.setMovementLocked(false);
                }, 1f);
            } else {
                uiManager.getGameUI().showInfoMessage("Not enough grass or pape", 1.5f);
            }
        });
        chikitaNode.addChoice("Leave", () -> {});
        NPC chikita = new NPC("Chikita", 90, 90, 1500, 600, Assets.textureChikita, world, 0, 1, 3f, 0f, 0, 150, new Dialogue(chikitaNode));
        npcs.add(chikita);

        // === POLICE ===
        DialogueNode policeNode = new DialogueNode(() -> {
            if (player.getInventory().removeItem("grass", 10000) ||
                player.getInventory().removeItem("joint", 10000) ||
                player.getInventory().removeItem("vape", 10000)) {
                uiManager.getGameUI().showInfoMessage("You lost your stuff", 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage("You passed the police check", 1.5f);
            }
        },"Police check, do you have some forbidden stuff?");
        police = new NPC("Police", 100, 100, 400, 600, Assets.texturePolice, world, 1, 0, 3f, 0, 75, 100, new Dialogue(policeNode));
        npcs.add(police);

        // === KIOSKMAN ===
        DialogueNode kioskNode_start = new DialogueNode("Hi! Pape 5 euro");

        kioskNode_start.addChoice("Buy Pape (5 euro)", () -> {
            if (player.getInventory().removeItem("money", 5)) {
                player.getInventory().addItem("pape", 1);
                uiManager.getGameUI().showInfoMessage("You got 1 pape", 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage("Not enough money", 1.5f);
            }
        });

        kioskNode_start.addChoice("Buy Ice Tee (10 euro)", () -> {
            if (player.getInventory().getAmount("money") >= 10) {
                player.getInventory().removeItem("money", 10);
                player.getInventory().addItem("ice tee", 1);
                uiManager.getGameUI().showInfoMessage("You got 1 ice tee", 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage("Not enough money", 1.5f);
            }
        });

        kioskNode_start.addChoice("Leave", new DialogueNode("Okay bye!"));
        NPC kioskman = new NPC("Mohammed", 90, 90, 1575, 350, Assets.textureKioskMan, world, 1, 0, 3f, 0, 75, 100, new Dialogue(kioskNode_start));
        npcs.add(kioskman);

        // === JUNKY ===
        DialogueNode junkyNode = new DialogueNode("Do you have a spoon?");
        junkyNode.addChoice("Give a spoon", () -> {
            if (!QuestManager.hasQuest("Spoon")) {QuestManager.addQuest(new QuestManager.Quest("Spoon", "Find a spoon for junky"));}

            if (player.getInventory().removeItem("spoon", 1)) {
                uiManager.getGameUI().showInfoMessage("You got respect from junky", 1.5f);
                QuestManager.removeQuest("Spoon");
            } else {
                uiManager.getGameUI().showInfoMessage("You do not have a spoon", 1.5f);
            }
        });

        junkyNode.addChoice("Leave", () -> {
            if (!QuestManager.hasQuest("Spoon")) {QuestManager.addQuest(new QuestManager.Quest("Spoon", "Find a spoon for junky"));}
        });
        NPC junky = new NPC("Junky", 100, 100, 200, 300, Assets.textureJunky, world, 1, 0, 3f, 0, 75, 100, new Dialogue(junkyNode));
        npcs.add(junky);


        // === BOSS ===
        DialogueNode bossNode_start = new DialogueNode("Do you wanna get some money?", "I have a task for you.", "You have to hide 1kg in the bush behind your house.", "But remember! I'll see if you aren't doing what i asked for");
        DialogueNode bossNode_after = new DialogueNode("You know what to do.", "So go ahead, I don't wanna wait too much");
        bossNode_start.addChoice("Let's go", () -> {
            QuestManager.addQuest(new QuestManager.Quest("Big delivery", "Hide 1kg in the bush"));
            uiManager.getGameUI().showInfoMessage("You got 1kg grass", 1.5f);
            player.getInventory().addItem("grass", 1000);
            NPC boss = findNpcByName("???");
            if (boss != null) {
                boss.setDialogue(new Dialogue(bossNode_after));
            }
        });
        bossNode_start.addChoice("Leave", () -> {});
        boss = new NPC("???", 100, 100, 700, 100, Assets.textureBoss, world, 1, 0, 3f, 0, 75, 100, new Dialogue(bossNode_start));
        npcs.add(boss);

        // === KAMIL ===
        NPC kamil = new NPC("Kamil", 90, 90, 500, 100, Assets.textureKamil, world, 1, 0, 3f, 0, 75, 100, new Dialogue(new DialogueNode("Hello kurwa")));
        npcs.add(kamil);
    }

    public void render(float delta) {
        for (NPC npc : npcs) {
            npc.update(delta);
            npc.draw(batch);
            if (npc.isPlayerNear(player)) {
                font.draw(batch, "Press E to interact", npc.getX() - 100, npc.getY() + npc.getHeight() + 40);
            }
        }
    }

    public NPC findNpcByName(String name) {
        for (NPC npc : npcs) {
            if (npc.getName().equals(name)) {
                return npc;
            }
        }
        return null;
    }

    public boolean updatePolice() {
        if (police1 != null && !police1.followPlayer(player)) {
            npcs.remove(police1);
            police1 = null;
            return true;
        }
        return false;
    }

    public ArrayList<NPC> getNpcs() { return npcs; }
    public NPC getBoss() { return boss; }
    public NPC getPolice1() { return police1; }
    public NPC getPolice() { return police; }

    public void callPolice() {
        police1 = new NPC("Police", 100, 100, player.getX(), player.getY() - 300, Assets.texturePolice, this.world, 1, 0, 3f, 0, 200, 100, new Dialogue(new DialogueNode("What are you doing? Stop right there!")));
        npcs.add(police1);
    }
}
