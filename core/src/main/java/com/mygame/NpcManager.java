package com.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.ui.UIManager;

import java.util.ArrayList;

public class NpcManager {
    private ArrayList<NPC> npcs = new ArrayList<>();

    private  NPC police;

    private Player player;
    private SpriteBatch batch;
    private BitmapFont font;

    public NpcManager(SpriteBatch batch, Player player, World world, UIManager uiManager, BitmapFont font) {
        this.batch = batch;
        this.player = player;
        this.font = font;


        NPC igo = new NPC("Igo",100, 100, 500, 300, Assets.textureIgo, world,
            1, 0, 3f, 0f,0,150,
            new String[]{"Hallo Bruder!", "Gib kosyak"});
        npcs.add(igo);

        igo.setAction(() -> {
            if (igo.getDialogueCount() == 1)
                if(player.getInventory().removeItem("kosyak", 1)) {
                    player.getInventory().addItem("vape", 1);
                    uiManager.showInfoMessage("You got 1 Vape", 1.5f);
                    QuestManager.removeQuest("Igo");
                    igo.nextDialogueCount();
                    igo.setTexts(new String[]{"Danke Bruder!"});
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
                    uiManager.showInfoMessage("Not enough kosyak", 1.5f);
                    QuestManager.addQuest(new QuestManager.Quest("Igo","Get some kosyak for igo"));
                }
        });

        NPC ryzhyi = new NPC("Ryzhyi",100, 100, 1100, 500, Assets.textureRyzhyi,
            world, 0, 1, 1f, 2f,200, 150,
            new String[]{"Please take 20 euro but fuck off"});
        npcs.add(ryzhyi);

        ryzhyi.setAction(() -> {
            if (ryzhyi.getDialogueCount() == 1) {
                player.getInventory().addItem("money", 20);
                Assets.moneySound.play(0.5f);
                uiManager.showInfoMessage("You got 20 euro",1.5f);
                ryzhyi.nextDialogueCount();
                ryzhyi.setTexts(new String[]{"I gave 20 euro why do I still see you "});
            }
        });

        NPC denys = new NPC("Denys",100, 100, 700, 700, Assets.textureDenys,
            world, 1, 1,2f, 1f, 100, 150,
            new String[]{"Hello Popa!!!", "I'm not in mood to talk"});
        npcs.add(denys);

        NPC baryga = new NPC("Baryga",100, 100, 1000, 200, Assets.textureBaryga,
            world, 0, 1, 3f, 0f, 0,150,
            new String[]{"Bruder was brauchst du?", "Grass 10 Euro"});
        npcs.add(baryga);

        baryga.setAction(() -> {
            if (player.getInventory().removeItem("money",10)) {
                player.getInventory().addItem("grass", 1);
                uiManager.showInfoMessage("You got 1g grass for 10 euro", 1.5f);
            } else {
                uiManager.showInfoMessage("Not enough money", 1.5f);
            }
        });

        NPC chikita = new NPC("Chikita",100, 100, 1500, 600, Assets.textureChikita,
            world, 0, 1, 3f, 0f, 0,150,
            new String[]{"Gib grass und papier dann du bekommen kosyak"});
        npcs.add(chikita);

        chikita.setAction(() -> {
            if (player.getInventory().hasItem("grass") &&  player.getInventory().hasItem("papier")) {
                player.getInventory().removeItem("grass",1);
                player.getInventory().removeItem("papier",1);
                Assets.kosyakSound.play(0.6f);
                com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                    @Override
                    public void run() {
                        player.getInventory().addItem("kosyak", 1);
                        uiManager.showInfoMessage("You got 1 kosyak", 1.5f);
                    }
                }, 1);

            } else {
                uiManager.showInfoMessage("Not enough grass or papier", 1.5f);
            }
        });

        police = new NPC("Police",120, 120, 400, 600, Assets.texturePolice,
            world, 1, 0, 3f, 0, 75, 100,
            new String[]{"Polizeikontrolle, haben Sie Grass?"});
        npcs.add(police);

        police.setAction(() -> {
            if (player.getInventory().removeItem("grass", 10) |
                player.getInventory().removeItem("kosyak", 10) |
                player.getInventory().removeItem("vape", 10)) {

                uiManager.showInfoMessage("You lost your stuff", 1.5f);
            } else {
                uiManager.showInfoMessage("You passed the Polizeikontrolle", 1.5f);
            }
        });

        NPC kioskman = new NPC("Mohammed",100, 100, 1575, 350, Assets.textureKioskMan,
            world, 1, 0, 3f, 0, 75, 100,
            new String[]{"Hallo! Was wollen Sie?"});
        npcs.add(kioskman);

        kioskman.setAction(() -> {
            if (player.getInventory().removeItem("money", 5)) {
                player.getInventory().addItem("papier", 1);
                uiManager.showInfoMessage("You got 1 papier", 1.5f);
            } else {
                uiManager.showInfoMessage("You enough money", 1.5f);
            }
        });
        NPC junky = new NPC("Junky",100, 100, 200, 300, Assets.textureJunky,
            world, 1, 0, 3f, 0, 75, 100,
            new String[]{"Hast du mal nen Loffel?"});
        npcs.add(junky);

        junky.setAction(() -> {
            if (player.getInventory().removeItem("spoon", 1)) {
                uiManager.showInfoMessage("You got respect from junky", 1.5f);
                QuestManager.removeQuest("Spoon");
            } else {
                QuestManager.addQuest(new QuestManager.Quest("Spoon","Find a spoon for junky"));
                uiManager.showInfoMessage("You do not have a spoon", 1.5f);
            }
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
}
