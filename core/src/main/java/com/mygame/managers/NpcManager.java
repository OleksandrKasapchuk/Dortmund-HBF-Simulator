package com.mygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.Assets;
import com.mygame.Dialogue;
import com.mygame.DialogueNode;
import com.mygame.entity.NPC;
import com.mygame.entity.Player;
import com.mygame.world.World;
import com.mygame.managers.audio.SoundManager;
import com.mygame.ui.UIManager;

import java.util.ArrayList;

public class NpcManager {
    private ArrayList<NPC> npcs = new ArrayList<>();

    private NPC police;
    private NPC boss;
    private NPC police1;

    private Player player;
    private SpriteBatch batch;
    private BitmapFont font;
    private World world;

    public NpcManager(SpriteBatch batch, Player player, World world, UIManager uiManager, BitmapFont font) {
        this.batch = batch;
        this.player = player;
        this.font = font;
        this.world = world;

        DialogueNode igoNode = new DialogueNode("Hi bro! Give me some joint");

        NPC igo = new NPC("Igo",90, 90, 500, 300, Assets.textureIgo, world,
            1, 0, 3f, 0f,0,150,
            new Dialogue(igoNode));
        npcs.add(igo);
        Runnable igoAction = () -> {
            if (igo.getDialogueCount() == 1)
                if(player.getInventory().removeItem("joint", 1)) {
                    player.getInventory().addItem("vape", 1);
                    uiManager.getGameUI().showInfoMessage("You got 1 vape", 1.5f);
                    QuestManager.removeQuest("Igo");
                    igo.nextDialogueCount();
                    igo.setDialogue(new Dialogue(new DialogueNode("Thanks bro!")));
                    SoundManager.playSound(Assets.lighterSound);

                    float soundDuration = 5f;
                    com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                        @Override
                        public void run() {
                            igo.setTexture(Assets.textureIgo2);
                        }
                    }, soundDuration);

                } else {
                    uiManager.getGameUI().showInfoMessage("Not enough joint", 1.5f);
                    if (!QuestManager.hasQuest("Igo"))
                        QuestManager.addQuest(new QuestManager.Quest("Igo","Get some joint for igo"));
                }
        };
        igo.getDialogue().getCurrentNode().addChoice("Give joint", igoAction);
        igo.getDialogue().getCurrentNode().addChoice("Leave", new DialogueNode("See ya!"));


        DialogueNode ryzhyiNode = new DialogueNode("Please take 20 euro but fuck off");
        NPC ryzhyi = new NPC("Ryzhyi",90, 90, 1100, 500, Assets.textureRyzhyi,
            world, 0, 1, 1f, 2f,200, 150,
            new Dialogue(ryzhyiNode));
        npcs.add(ryzhyi);

        Runnable ryzhyiAction = () -> {
            if (ryzhyi.getDialogueCount() == 1) {
                player.getInventory().addItem("money", 20);
                SoundManager.playSound(Assets.moneySound);
                uiManager.getGameUI().showInfoMessage("You got 20 euro",1.5f);
                ryzhyi.nextDialogueCount();
                ryzhyi.setDialogue(new Dialogue(new DialogueNode("I gave 20 euro why do I still see you ")));
            }
        };
        ryzhyiNode.addChoice("Take 20 euro", ryzhyiAction);

        NPC denys = new NPC("Denys",90, 90, 700, 700, Assets.textureDenys,
            world, 1, 1,2f, 1f, 100, 150,
            new Dialogue(new DialogueNode("Hello! I'm not in mood to talk")));
        npcs.add(denys);

        DialogueNode barygaNode = new DialogueNode("What do you need? Grass 10 euro");
        NPC baryga = new NPC("Baryga",90, 90, 1000, 200, Assets.textureBaryga,
            world, 0, 1, 3f, 0f, 0,150,
            new Dialogue(barygaNode));
        npcs.add(baryga);
        Runnable barygaAction = () -> {
            if (player.getInventory().removeItem("money",10)) {
                player.getInventory().addItem("grass", 1);
                uiManager.getGameUI().showInfoMessage("You got 1g grass for 10 euro", 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage("Not enough money", 1.5f);
            }
        };
        barygaNode.addChoice("Buy grass", barygaAction);

        DialogueNode chikitaNode = new DialogueNode("Give me grass and pape and I make you a joint");
        NPC chikita = new NPC("Chikita",90, 90, 1500, 600, Assets.textureChikita,
            world, 0, 1, 3f, 0f, 0,150,
            new Dialogue(chikitaNode));
        npcs.add(chikita);
        Runnable chikitaAction = () -> {
            if (player.getInventory().hasItem("grass") &&  player.getInventory().hasItem("pape")) {
                player.getInventory().removeItem("grass",1);
                player.getInventory().removeItem("pape",1);
                player.setMovementLocked(true);
                SoundManager.playSound(Assets.kosyakSound);
                com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                    @Override
                    public void run() {
                        player.getInventory().addItem("joint", 1);
                        uiManager.getGameUI().showInfoMessage("You got 1 joint", 1.5f);
                        player.setMovementLocked(false);
                    }
                }, 1);


            } else {
                uiManager.getGameUI().showInfoMessage("Not enough grass or pape", 1.5f);
            }
        };
        chikitaNode.addChoice("give grass and pape",chikitaAction);

        DialogueNode policeNode = new DialogueNode("Police check, do you have some forbidden stuff?");
        police = new NPC("Police",100, 100, 400, 600, Assets.texturePolice,
            world, 1, 0, 3f, 0, 75, 100,
            new Dialogue(policeNode));
        npcs.add(police);
        Runnable policeAction = () -> {
            if (player.getInventory().removeItem("grass", 10000) |
                player.getInventory().removeItem("joint", 10000) |
                player.getInventory().removeItem("vape", 10000)) {

                uiManager.getGameUI().showInfoMessage("You lost your stuff", 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage("You passed the police check", 1.5f);
            }
        };
        policeNode.addChoice("OK", policeAction);


        Runnable kioskAction = () -> {
            if (player.getInventory().removeItem("money", 5)) {
                player.getInventory().addItem("pape", 1);
                uiManager.getGameUI().showInfoMessage("You got 1 pape", 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage("Not enough money", 1.5f);
            }
        };

        DialogueNode kioskNode = new DialogueNode("Hi! What do you need?");
        NPC kioskman = new NPC("Mohammed",90, 90, 1575, 350, Assets.textureKioskMan,
            world, 1, 0, 3f, 0, 75, 100,
            new Dialogue(kioskNode));
        npcs.add(kioskman);
        kioskNode.addChoice("Pape 5 euro",kioskAction);
        kioskNode.addChoice("Nothing, bye", new DialogueNode("Okay bye!"));


        DialogueNode junkyNode = new DialogueNode("Do you have a spoon?");
        NPC junky = new NPC("Junky",100, 100, 200, 300, Assets.textureJunky,
            world, 1, 0, 3f, 0, 75, 100,
            new Dialogue(junkyNode));
        npcs.add(junky);
        Runnable junkyAction = () -> {
            if (player.getInventory().removeItem("spoon", 1)) {
                uiManager.getGameUI().showInfoMessage("You got respect from junky", 1.5f);
                QuestManager.removeQuest("Spoon");
            } else {
                QuestManager.addQuest(new QuestManager.Quest("Spoon","Find a spoon for junky"));
                uiManager.getGameUI().showInfoMessage("You do not have a spoon", 1.5f);
            }
        };
        junkyNode.addChoice("Give a spoon", junkyAction);


        DialogueNode bossNode = new DialogueNode("Do you wanna get some money? " +
            "I have a task for you, You have to hide 1kg in the bush behind your house." +
            " But remember! I'll see if you aren't doing what i asked for");
        boss = new NPC("???",100, 100, 700, 100, Assets.textureBoss,
            world, 1, 0, 3f, 0, 75, 100,
            new Dialogue(bossNode));
        npcs.add(boss);

        Runnable bossAction = () -> {
            if (boss.getDialogueCount() == 1) {
                QuestManager.addQuest(new QuestManager.Quest("Big delivery", "Hide 1kg in the bush"));
                uiManager.getGameUI().showInfoMessage("You got 1kg grass", 1.5f);
                player.getInventory().addItem("grass", 1000);
                boss.setDialogue(new Dialogue(new DialogueNode("You know what to do. So go ahead, I don't wanna wait too much")));
                boss.nextDialogueCount();
            }
        };
        bossNode.addChoice("OK", bossAction);


        NPC kamil = new NPC("Kamil",90, 90, 500, 100, Assets.textureKamil,
            world, 1, 0, 3f, 0, 75, 100,
            new Dialogue(new DialogueNode("Hello kurwa")));
        npcs.add(kamil);
    }

    public void render(){
        float delta = Gdx.graphics.getDeltaTime();
        for (NPC npc : npcs) npc.update(delta);
        for (NPC npc : npcs) {
            npc.draw(batch);
            if (npc.isPlayerNear(player)) {
                font.draw(batch, "Press E / ACT to interact", npc.getX() - 100, npc.getY() + npc.getHeight() + 40);
            }
        }
    }

    public boolean updatePolice() {
        if (police1 != null && !police1.followPlayer(player)) {
            npcs.remove(police1);
            return true;
        }
        return false;
    }

    public ArrayList<NPC> getNpcs() {return npcs;}
    public NPC getBoss() {return boss;}
    public NPC getPolice1(){return police1;}
    public NPC getPolice(){return police;}

    public void callPolice(){
        police1 = new NPC("Police",100, 100, player.getX(), player.getY() - 300, Assets.texturePolice,
            world, 1, 0, 3f, 0, 200, 100,
            new Dialogue(new DialogueNode("What are you doing? Stop right there!")));
        npcs.add(police1);
    }
}
