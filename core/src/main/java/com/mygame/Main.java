package com.mygame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.Map;
import java.util.ArrayList;

public class Main extends ApplicationAdapter {

    // === Основні ігрові об'єкти ===
    private Player player;
    private ArrayList<NPC> npcs = new ArrayList<>();
    private World world;

    // === Рендеринг та графіка ===
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;
    private DialogueManager dialogueManager;

    // Текстури
    private Texture textureZoe;
    private Texture textureRyzhyi;
    private Texture textureDenys;
    private Texture textureIgo;
    private Texture textureBaryga;
    private Texture textureChikita;

    // === Інтерфейс ===
    private Stage stage;
    private Skin skin;
    private Texture dialogueBgTexture;
    private boolean actButtonJustPressed = false;
    private Label moneylabel;
    private Label infolabel;
    private Label questlabel;
    private String questMessage = "";
    private String infoMessage = "";
    private float infoMessageTimer = 0f; // скільки ще показувати повідомлення
    private Table inventoryTable;
    private boolean inventoryVisible = false;

    // === Елементи для сенсорного управління ===
    private Texture knobTexture;
    private Texture bgTexture;

    // === Константи світу ===
    private static final int WORLD_WIDTH = 4000;
    private static final int WORLD_HEIGHT = 2000;
    public static int getWorldWidth() { return WORLD_WIDTH; }
    public static int getWorldHeight() { return WORLD_HEIGHT; }

    @Override
    public void create() {
        // === Ініціалізація базових систем ===
        batch = new SpriteBatch();
        textureZoe = new Texture("ui/zoe.png");
        textureRyzhyi = new Texture("ryzhyi.png");
        textureDenys = new Texture("denys.png");
        textureIgo = new Texture("igo.png");
        textureBaryga = new Texture("baryga.png");
        textureChikita = new Texture("chikita.png");

        // Шрифт для тексту
        font = new BitmapFont();
        font.getData().setScale(2.5f);
        font.setUseIntegerPositions(false);

        // Камера та в'юпорт
        camera = new OrthographicCamera();
        viewport = new FitViewport(2000, 1000, camera);

        // Створюємо світ (фон, колізії тощо)
        world = new World();

        // === Гравець та NPC ===
        player = new Player(500, 100, 100, 200, 200, textureZoe, world);

        NPC igo = new NPC("Igo",100, 100, 500, 300, textureIgo, world, 1, 0, 3f, 0f,0,
            new String[]{"Hallo Bruder!", "Gib kosyak"});
        npcs.add(igo);

        igo.setAction(() -> {
            if (igo.getDialogueCount() == 1)
                if(player.getInventory().removeItem("kosyak", 1)) {
                    player.getInventory().addItem("Vape", 1);
                    showInfoMessage("You got 1 Vape", 1.5f);
                    questMessage = "";
                    igo.nextDialogueCount();
                    igo.setTexts(new String[]{"Danke Bruder!"});
                } else {
                    showInfoMessage("Not enough kosyak", 1.5f);
                    questMessage = "Get some kosyak for igo";
                }
        });

        NPC ryzhyi = new NPC("Ryzhyi",100, 100, 1100, 500, textureRyzhyi, world, 0, 1, 1f, 2f,200,
            new String[]{"Please take 10 euro but fuck off"});
        npcs.add(ryzhyi);

        ryzhyi.setAction(() -> {
            if (ryzhyi.getDialogueCount() == 1) {
                player.getInventory().addItem("money", 10);
                showInfoMessage("You got 10 euro",1.5f);
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
                showInfoMessage("You got 1g grass for 10 euro", 1.5f);
            } else {
                showInfoMessage("Not enough money", 1.5f);
            }
        });

        NPC chikita = new NPC("Chikita",100, 100, 1575, 200, textureChikita, world, 0, 1, 3f, 0f, 0,
            new String[]{"Gib grass du bekommen kosyak"});
        npcs.add(chikita);

        chikita.setAction(() -> {
            if (player.getInventory().removeItem("grass",1)) {
                player.getInventory().addItem("kosyak", 1);
                showInfoMessage("You got 1 kosyak", 1.5f);
            } else {
                showInfoMessage("Not enough grass", 1.5f);
            }
        });

        // === Інтерфейс ===
        stage = new Stage(new FitViewport(2000, 1000));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // === Діалогова таблиця ===
        Pixmap dialogueBg = new Pixmap(1950, 180, Pixmap.Format.RGBA8888);
        dialogueBg.setColor(new Color(0.1f, 0.1f, 0.5f, 0.6f));
        dialogueBg.fill();
        this.dialogueBgTexture = new Texture(dialogueBg);
        dialogueBg.dispose();

        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(dialogueBgTexture));

        Table dialogueTable = new Table();
        dialogueTable.setSize(1950, 180);
        dialogueTable.setPosition(25, 30);
        dialogueTable.setBackground(background);

        // --- Ім’я NPC ---
        Label nameLabel = new Label("", skin);
        nameLabel.setFontScale(3f);
        nameLabel.setColor(Color.GOLD);
        nameLabel.setAlignment(Align.left);

        // --- Текст ---
        Label dialogueLabel = new Label("", skin);
        dialogueLabel.setFontScale(3f);
        dialogueLabel.setWrap(true);
        dialogueLabel.setAlignment(Align.left);

        // --- Додаємо у таблицю ---
        dialogueTable.add(nameLabel).left().padLeft(10).padBottom(20).row();
        dialogueTable.add(dialogueLabel).width(1800).padLeft(60).left();

        dialogueTable.setVisible(false);
        stage.addActor(dialogueTable);

        // --- Таблиця інвентаря ---
        inventoryTable = new Table();
        inventoryTable.setSize(1600, 800); // розмір спливаючого вікна
        inventoryTable.setPosition(stage.getViewport().getWorldWidth()/2f - 800, stage.getViewport().getWorldHeight()/2f - 400);
        inventoryTable.align(Align.topLeft).pad(20); // <--- ВИРІВНЮЄМО ВМІСТ ТАБЛИЦІ

        // фон (прозорий синій прямокутник)
        Pixmap pixmap = new Pixmap(1600, 800, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.1f, 0.1f, 0.7f, 0.4f));
        pixmap.fill();
        inventoryTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))));
        pixmap.dispose();

        inventoryTable.setVisible(false);
        stage.addActor(inventoryTable);

        // === Менеджер діалогів ===
        this.dialogueManager = new DialogueManager(dialogueTable, nameLabel, dialogueLabel);

        // --- Гроші ---
        moneylabel = new Label("Money: " + player.getMoney(), skin);
        moneylabel.setPosition(1700, 925);
        moneylabel.setFontScale(3f);
        stage.addActor(moneylabel);

        // --- Інформація ---
        infolabel = new Label("", skin);
        infolabel.setColor(Color.GOLD);
        infolabel.setAlignment(Align.center);
        infolabel.setFontScale(4f);
        infolabel.setPosition(stage.getViewport().getWorldWidth() / 2f - infolabel.getWidth() / 2f, 850);
        stage.addActor(infolabel);

        questlabel = new Label("Mission: " + questMessage, skin);
        questlabel.setPosition(stage.getViewport().getWorldWidth() / 2f - infolabel.getWidth() / 2f, 925);
        questlabel.setColor(Color.ORANGE);
        questlabel.setAlignment(Align.center);
        questlabel.setFontScale(4f);
        stage.addActor(questlabel);
        // === Сенсорне керування (для Android) ===
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            Gdx.input.setInputProcessor(stage);

            // Кнопка руху (джойстик)
            Pixmap knobPixmap = new Pixmap(50, 50, Pixmap.Format.RGBA8888);
            knobPixmap.setColor(Color.WHITE);
            knobPixmap.fillCircle(25, 25, 25);
            knobTexture = new Texture(knobPixmap);
            knobPixmap.dispose();

            Pixmap bgPixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
            bgPixmap.setColor(new Color(0.3f, 0.3f, 0.3f, 0.5f));
            bgPixmap.fillCircle(50, 50, 50);
            bgTexture = new Texture(bgPixmap);
            bgPixmap.dispose();

            Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
            touchpadStyle.knob = new TextureRegionDrawable(new TextureRegion(knobTexture));
            touchpadStyle.background = new TextureRegionDrawable(new TextureRegion(bgTexture));

            Touchpad touchpad = new Touchpad(10, touchpadStyle);
            touchpad.setBounds(150, 150, 200, 200);
            stage.addActor(touchpad);
            player.touchpad = touchpad;

            // Кнопка взаємодії
            TextButton actButton = new TextButton("ACT", skin);
            actButton.setSize(150, 150);
            actButton.setPosition(1800, 150);
            actButton.getLabel().setFontScale(4f);

            actButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    actButtonJustPressed = true;
                    return true;
                }
            });
            stage.addActor(actButton);

            // --- Кнопка інвентаря ---
            TextButton inventoryButton = new TextButton("INV", skin);
            inventoryButton.setSize(150, 150);
            inventoryButton.setPosition(1800, 325); // правий нижній кут
            inventoryButton.getLabel().setFontScale(3f);

            inventoryButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    inventoryVisible = !inventoryVisible; // перемикаємо видимість
                    inventoryTable.setVisible(inventoryVisible);

                    if (inventoryVisible) {
                        updateInventoryTable(); // оновлюємо список предметів
                    }
                    return true;
                }
            });

            stage.addActor(inventoryButton);
        }
    }

    // --- приватний метод для повідомлень ---
    private void showInfoMessage(String message, float duration) {
        infoMessage = message;
        infoMessageTimer = duration;
        infolabel.setText(infoMessage);
        infolabel.setVisible(true);
    }

    private void updateInventoryTable() {
        inventoryTable.clear(); // очищаємо попередні Label

        Label titleLabel = new Label("INVENTORY", skin);
        titleLabel.setFontScale(4f);
        titleLabel.setColor(Color.GOLD);
        inventoryTable.add(titleLabel).pad(30).row();

        Inventory inv = player.getInventory();

        for (Map.Entry<String, Integer> entry : inv.getItems().entrySet()) {
            String itemName = entry.getKey();

            Label itemLabel = new Label(itemName + ": ", skin);
            itemLabel.setFontScale(3f);

            Label countLabel = new Label(String.valueOf(entry.getValue()), skin);
            countLabel.setFontScale(3f);

            inventoryTable.add(itemLabel).left().padTop(20).padLeft(30);
            inventoryTable.add(countLabel).left().padTop(20).row();
        }
    }
    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // === Оновлення логіки ===
        player.update(delta);
        for (NPC npc : npcs) npc.update(delta);

        moneylabel.setText("Money: " + player.getMoney());
        questlabel.setText("Mission: " + questMessage);
        // Оновлюємо таймер повідомлення
        if (infoMessageTimer > 0) {
            infoMessageTimer -= delta;
            if (infoMessageTimer <= 0) {
                infolabel.setVisible(false);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            inventoryVisible = !inventoryVisible;
            inventoryTable.setVisible(inventoryVisible);

            if (inventoryVisible) {
                updateInventoryTable();
            }
        }
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
        viewport.apply();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        world.draw(batch);
        player.draw(batch);



        // Малюємо NPC і підказки
        for (NPC npc : npcs) {
            npc.draw(batch);
            if (npc.isPlayerNear(player)) {
                font.draw(batch, "Press E / ACT to interact", npc.x - 100, npc.y + npc.height + 40);
            }
        }
        boolean interactPressed = Gdx.input.isKeyJustPressed(Input.Keys.E) || actButtonJustPressed;
        dialogueManager.update(delta, npcs, player, interactPressed);

        batch.end();

        // === Рендеринг UI ===
        stage.act(delta);
        stage.draw();

        // Скидаємо прапорець натискання кнопки в кінці
        actButtonJustPressed = false;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
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
        batch.dispose();
        font.dispose();
        stage.dispose();
        skin.dispose();
        world.dispose();
        dialogueBgTexture.dispose();
        if (knobTexture != null) knobTexture.dispose();
        if (bgTexture != null) bgTexture.dispose();
    }
}
