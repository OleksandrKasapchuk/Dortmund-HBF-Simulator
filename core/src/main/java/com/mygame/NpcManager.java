package com.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.ui.UIManager;

import java.util.ArrayList;

public class NpcManager {
    private ArrayList<NPC> npcs = new ArrayList<>();

    private NPC police;
    private NPC boss;

    private Player player;
    private SpriteBatch batch;
    private BitmapFont font;

    public NpcManager(SpriteBatch batch, Player player, World world, UIManager uiManager, BitmapFont font) {
        this.batch = batch;
        this.player = player;
        this.font = font;


        NPC igo = new NPC("Igo",90, 90, 500, 300, Assets.textureIgo, world,
            1, 0, 3f, 0f,0,150,
            new String[]{"Hi bro!", "Give me kosyak"});
        npcs.add(igo);

        igo.setAction(() -> {
            if (igo.getDialogueCount() == 1)
                if(player.getInventory().removeItem("kosyak", 1)) {
                    player.getInventory().addItem("vape", 1);
                    uiManager.getGameUI().showInfoMessage("You got 1 Vape", 1.5f);
                    QuestManager.removeQuest("Igo");
                    igo.nextDialogueCount();
                    igo.setTexts(new String[]{"Thanks bro!"});
                    Assets.lighterSound.play();

                    // Відкладена зміна текстури через Timer
                    float soundDuration = 5f; // тривалість в секундах
                    com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                        @Override
                        public void run() {
                            igo.setTexture(Assets.textureIgo2);
                        }
                    }, soundDuration);

                } else {
                    uiManager.getGameUI().showInfoMessage("Not enough kosyak", 1.5f);
                    QuestManager.addQuest(new QuestManager.Quest("Igo","Get some kosyak for igo"));
                }
        });

        NPC ryzhyi = new NPC("Ryzhyi",90, 90, 1100, 500, Assets.textureRyzhyi,
            world, 0, 1, 1f, 2f,200, 150,
            new String[]{"Please take 20 euro but fuck off"});
        npcs.add(ryzhyi);

        ryzhyi.setAction(() -> {
            if (ryzhyi.getDialogueCount() == 1) {
                player.getInventory().addItem("money", 20);
                Assets.moneySound.play(0.8f);
                uiManager.getGameUI().showInfoMessage("You got 20 euro",1.5f);
                ryzhyi.nextDialogueCount();
                ryzhyi.setTexts(new String[]{"I gave 20 euro why do I still see you "});
            }
        });

        NPC denys = new NPC("Denys",90, 90, 700, 700, Assets.textureDenys,
            world, 1, 1,2f, 1f, 100, 150,
            new String[]{"Hello!", "I'm not in mood to talk"});
        npcs.add(denys);

        NPC baryga = new NPC("Baryga",90, 90, 1000, 200, Assets.textureBaryga,
            world, 0, 1, 3f, 0f, 0,150,
            new String[]{"What do you need?", "Grass 10 euro"});
        npcs.add(baryga);

        baryga.setAction(() -> {
            if (player.getInventory().removeItem("money",10)) {
                player.getInventory().addItem("grass", 1);
                uiManager.getGameUI().showInfoMessage("You got 1g grass for 10 euro", 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage("Not enough money", 1.5f);
            }
        });

        NPC chikita = new NPC("Chikita",90, 90, 1500, 600, Assets.textureChikita,
            world, 0, 1, 3f, 0f, 0,150,
            new String[]{"Give me grass und paper and you get kosyak"});
        npcs.add(chikita);

        chikita.setAction(() -> {
            if (player.getInventory().hasItem("grass") &&  player.getInventory().hasItem("papier")) {
                player.getInventory().removeItem("grass",1);
                player.getInventory().removeItem("papier",1);
                Assets.kosyakSound.play(2f);
                com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                    @Override
                    public void run() {
                        player.getInventory().addItem("kosyak", 1);
                        uiManager.getGameUI().showInfoMessage("You got 1 kosyak", 1.5f);
                    }
                }, 1);

            } else {
                uiManager.getGameUI().showInfoMessage("Not enough grass or papier", 1.5f);
            }
        });

        police = new NPC("Police",100, 100, 400, 600, Assets.texturePolice,
            world, 1, 0, 3f, 0, 75, 100,
            new String[]{"Police check, do you have some forbidden stuff?"});
        npcs.add(police);

        police.setAction(() -> {
            if (player.getInventory().removeItem("grass", 10) |
                player.getInventory().removeItem("kosyak", 10) |
                player.getInventory().removeItem("vape", 10)) {

                uiManager.getGameUI().showInfoMessage("You lost your stuff", 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage("You passed the police check", 1.5f);
            }
        });

        NPC kioskman = new NPC("Mohammed",90, 90, 1575, 350, Assets.textureKioskMan,
            world, 1, 0, 3f, 0, 75, 100,
            new String[]{"Hi! Paper 5 euro?"});
        npcs.add(kioskman);

        kioskman.setAction(() -> {
            if (player.getInventory().removeItem("money", 5)) {
                player.getInventory().addItem("papier", 1);
                uiManager.getGameUI().showInfoMessage("You got 1 papier", 1.5f);
            } else {
                uiManager.getGameUI().showInfoMessage("Not enough money", 1.5f);
            }
        });
        NPC junky = new NPC("Junky",100, 100, 200, 300, Assets.textureJunky,
            world, 1, 0, 3f, 0, 75, 100,
            new String[]{"Do you have a spoon?"});
        npcs.add(junky);

        junky.setAction(() -> {
            if (player.getInventory().removeItem("spoon", 1)) {
                uiManager.getGameUI().showInfoMessage("You got respect from junky", 1.5f);
                QuestManager.removeQuest("Spoon");
            } else {
                QuestManager.addQuest(new QuestManager.Quest("Spoon","Find a spoon for junky"));
                uiManager.getGameUI().showInfoMessage("You do not have a spoon", 1.5f);
            }
        });

        boss = new NPC("???",100, 100, 700, 100, Assets.textureBoss,
            world, 1, 0, 3f, 0, 75, 100,
            new String[]{"DO you wanna get some money?", "I have a task for you", "You have to hide 1kg in the bush behind your house", "But remember I'll see when you are doing not what i asked"});
        npcs.add(boss);

        boss.setAction(() -> {
            QuestManager.addQuest(new QuestManager.Quest("Big delivery","Hide 1kg in the bush"));
            uiManager.getGameUI().showInfoMessage("You got 1kg grass", 1.5f);
            player.getInventory().addItem("grass", 1000);
            boss.setTexts(new String[] {"You know what to do", "So go ahead, I don't wanna wait too much"});
        });

    }

    public void render(){
        float delta = Gdx.graphics.getDeltaTime();
        for (NPC npc : npcs) npc.update(delta);
        for (NPC npc : npcs) {
            npc.draw(batch);
            if (npc.isPlayerNear(player)) {
                font.draw(batch, "Press E / ACT to interact", npc.x - 100, npc.y + npc.height + 40);
            }
        }
    }

    public ArrayList<NPC> getNpcs() {return npcs;}
    public NPC getBoss() {return boss;}
}
