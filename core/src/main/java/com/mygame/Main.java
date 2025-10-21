package com.mygame;


import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;


public class Main extends ApplicationAdapter {

    // === Основні ігрові об'єкти ===
    private Player player;
    private ArrayList<NPC> npcs = new ArrayList<>();
    private World world;
    private UIManager uiManager;

    // === Рендеринг та графіка ===
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;

    // Текстури
    private Texture textureZoe;
    private Texture textureRyzhyi;
    private Texture textureDenys;
    private Texture textureIgo;
    private Texture textureIgo2;
    private Texture textureBaryga;
    private Texture textureChikita;
    private Texture texturePolice;


    // === Константи світу ===
    private static final int WORLD_WIDTH = 4000;
    private static final int WORLD_HEIGHT = 2000;
    public static int getWorldWidth() { return WORLD_WIDTH; }
    public static int getWorldHeight() { return WORLD_HEIGHT; }

    @Override
    public void create() {
        // === Ініціалізація базових систем ===
        batch = new SpriteBatch();
        textureZoe = new Texture("zoe.png");
        textureRyzhyi = new Texture("ryzhyi.png");
        textureDenys = new Texture("denys.png");
        textureIgo = new Texture("igo.png");
        textureIgo2 = new Texture("igo2.png");
        textureBaryga = new Texture("baryga.png");
        textureChikita = new Texture("chikita.png");
        texturePolice = new Texture("police.png");

        font = new BitmapFont();
        font.getData().setScale(2.5f);
        font.setUseIntegerPositions(false);

        camera = new OrthographicCamera();
        viewport = new FitViewport(2000, 1000, camera);
        world = new World();

        // === Гравець та UI Manager ===
        player = new Player(500, 100, 100, 200, 200, textureZoe, world);
        uiManager = new UIManager(player);

        // === NPC ===
        NPC igo = new NPC("Igo",100, 100, 500, 300, textureIgo, world, 1, 0, 3f, 0f,0,
            new String[]{"Hallo Bruder!", "Gib kosyak"});
        npcs.add(igo);

        igo.setAction(() -> {
            if (igo.getDialogueCount() == 1)
                if(player.getInventory().removeItem("kosyak", 1)) {
                    player.getInventory().addItem("Vape", 1);
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

        NPC ryzhyi = new NPC("Ryzhyi",100, 100, 1100, 500, textureRyzhyi, world, 0, 1, 1f, 2f,200,
            new String[]{"Please take 10 euro but fuck off"});
        npcs.add(ryzhyi);

        ryzhyi.setAction(() -> {
            if (ryzhyi.getDialogueCount() == 1) {
                player.getInventory().addItem("money", 10);
                uiManager.showInfoMessage("You got 10 euro",1.5f);
                ryzhyi.nextDialogueCount();
                ryzhyi.setTexts(new String[]{"I gave 10 euro why do I still see you "});
            }
        });

        NPC denys = new NPC("Denys",100, 100, 700, 700, textureDenys, world, 1, 1,2f, 1f, 100,
            new String[]{"Hello Popa!!!", "I'm not in mood to talk"});
        npcs.add(denys);

        NPC baryga = new NPC("Baryga",100, 100, 1000, 200, textureBaryga, world, 0, 1, 3f, 0f, 0,
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

        NPC chikita = new NPC("Chikita",100, 100, 1575, 200, textureChikita, world, 0, 1, 3f, 0f, 0,
            new String[]{"Gib grass du bekommen kosyak"});
        npcs.add(chikita);

        chikita.setAction(() -> {
            if (player.getInventory().removeItem("grass",1)) {
                player.getInventory().addItem("kosyak", 1);
                uiManager.showInfoMessage("You got 1 kosyak", 1.5f);
            } else {
                uiManager.showInfoMessage("Not enough grass", 1.5f);
            }
        });

        NPC police = new NPC("Police",120, 120, 575, 350, texturePolice, world, 1, 0, 3f, 6f, 75,
            new String[]{"Polizeikontrolle, haben Sie Grass?"});
        npcs.add(police);

        police.setAction(() -> {
            player.getInventory().removeItem("grass", 1);
            player.getInventory().removeItem("kosyak", 1);
        });
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // === Оновлення ігрової логіки ===
        player.update(delta);
        for (NPC npc : npcs) npc.update(delta);
        uiManager.update(delta, player, npcs);

        // === Камера слідкує за гравцем ===
        float targetX = player.x + player.width / 2f;
        float targetY = player.y + player.height / 2f;
        float cameraX = Math.max(camera.viewportWidth / 2f, Math.min(targetX, WORLD_WIDTH - camera.viewportWidth / 2f));
        float cameraY = Math.max(camera.viewportHeight / 2f, Math.min(targetY, WORLD_HEIGHT - camera.viewportHeight / 2f));
        camera.position.set(cameraX, cameraY, 0);
        camera.update();

        // === Малювання ===
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // --- Ігровий світ ---
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        world.draw(batch);
        player.draw(batch);
        for (NPC npc : npcs) {
            npc.draw(batch);
            if (npc.isPlayerNear(player)) {
                font.draw(batch, "Press E / ACT to interact", npc.x - 100, npc.y + npc.height + 40);
            }
        }
        batch.end();

        // --- UI ---
        uiManager.render();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiManager.resize(width, height);
    }

    @Override
    public void dispose() {
        // === Очищення пам’яті ===
        textureZoe.dispose();
        textureRyzhyi.dispose();
        textureDenys.dispose();
        textureIgo.dispose();
        textureBaryga.dispose();
        textureChikita.dispose();
        texturePolice.dispose();
        batch.dispose();
        font.dispose();
        world.dispose();
        uiManager.dispose();
    }
}
