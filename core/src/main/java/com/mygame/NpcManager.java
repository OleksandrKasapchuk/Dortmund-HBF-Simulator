package com.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;

public class NpcManager {
    private ArrayList<NPC> npcs = new ArrayList<>();
    private Texture textureRyzhyi;
    private Texture textureDenys;
    private Texture textureIgo;
    private Texture textureIgo2;
    private Texture textureBaryga;
    private Texture textureChikita;
    private Texture texturePolice;
    private Texture textureKioskMan;
    private  NPC police;

    private Player player;
    private SpriteBatch batch;
    private BitmapFont font;

    public NpcManager(SpriteBatch batch, Player player, World world, UIManager uiManager, BitmapFont font) {
        this.batch = batch;
        this.player = player;
        this.font = font;

        textureRyzhyi = new Texture("ryzhyi.png");
        textureDenys = new Texture("denys.png");
        textureIgo = new Texture("igo.png");
        textureIgo2 = new Texture("igo2.png");
        textureBaryga = new Texture("baryga.png");
        textureChikita = new Texture("chikita.png");
        texturePolice = new Texture("police.png");
        textureKioskMan = new Texture("kioskman.png");

        NPC igo = new NPC("Igo",100, 100, 500, 300, textureIgo, world,
            1, 0, 3f, 0f,0,150,
            new String[]{"Hallo Bruder!", "Gib kosyak"});
        npcs.add(igo);

        igo.setAction(() -> {
            if (igo.getDialogueCount() == 1)
                if(player.getInventory().removeItem("kosyak", 1)) {
                    player.getInventory().addItem("vape", 1);
                    uiManager.showInfoMessage("You got 1 Vape", 1.5f);
                    uiManager.updateQuestMessage("");
                    igo.nextDialogueCount();
                    igo.setTexts(new String[]{"Danke Bruder!"});
                    igo.setTexture(textureIgo2);
                } else {
                    uiManager.showInfoMessage("Not enough kosyak", 1.5f);
                    uiManager.updateQuestMessage("Get some kosyak for igo");
                }
        });

        NPC ryzhyi = new NPC("Ryzhyi",100, 100, 1100, 500, textureRyzhyi,
            world, 0, 1, 1f, 2f,200, 150,
            new String[]{"Please take 10 euro but fuck off"});
        npcs.add(ryzhyi);

        ryzhyi.setAction(() -> {
            if (ryzhyi.getDialogueCount() == 1) {
                player.getInventory().addItem("money", 20);
                uiManager.showInfoMessage("You got 20 euro",1.5f);
                ryzhyi.nextDialogueCount();
                ryzhyi.setTexts(new String[]{"I gave 20 euro why do I still see you "});
            }
        });

        NPC denys = new NPC("Denys",100, 100, 700, 700, textureDenys,
            world, 1, 1,2f, 1f, 100, 150,
            new String[]{"Hello Popa!!!", "I'm not in mood to talk"});
        npcs.add(denys);

        NPC baryga = new NPC("Baryga",100, 100, 1000, 200, textureBaryga,
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

        NPC chikita = new NPC("Chikita",100, 100, 1500, 600, textureChikita,
            world, 0, 1, 3f, 0f, 0,150,
            new String[]{"Gib grass und papier dann du bekommen kosyak"});
        npcs.add(chikita);

        chikita.setAction(() -> {
            if (player.getInventory().hasItem("grass") &&  player.getInventory().hasItem("papier")) {
                player.getInventory().removeItem("grass",1);
                player.getInventory().removeItem("papier",1);
                player.getInventory().addItem("kosyak", 1);
                uiManager.showInfoMessage("You got 1 kosyak", 1.5f);
            } else {
                uiManager.showInfoMessage("Not enough grass or papier", 1.5f);
            }
        });

        police = new NPC("Police",120, 120, 400, 600, texturePolice,
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

        NPC kioskman = new NPC("Mohammed",100, 100, 1575, 350, textureKioskMan,
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
    public void dispose(){
        textureRyzhyi.dispose();
        textureDenys.dispose();
        textureIgo.dispose();
        textureIgo2.dispose();
        textureBaryga.dispose();
        textureChikita.dispose();
        texturePolice.dispose();
    }
    public ArrayList<NPC> getNpcs() {return npcs;}
}
