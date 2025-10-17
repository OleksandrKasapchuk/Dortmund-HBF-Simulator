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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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
    private Texture texture;
    private BitmapFont font;
    private DialogueManager dialogueManager;

    // === Інтерфейс ===
    private Stage stage;
    private Skin skin;
    private Label dialogueLabel;
    private Texture dialogueBgTexture;
    private boolean actButtonJustPressed = false;


    // === Елементи для сенсорного управління ===
    private Texture knobTexture;
    private Texture bgTexture;

    // === Константи світу ===
    private static final int WORLD_WIDTH = 4000;
    private static final int WORLD_HEIGHT = 2000;

    //
    public static int getWorldWidth() { return WORLD_WIDTH; }
    public static int getWorldHeight() { return WORLD_HEIGHT; }

    @Override
    public void create() {
        // === Ініціалізація базових систем ===
        batch = new SpriteBatch();
        texture = new Texture("ui/zoe.png");

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
        player = new Player(500, 100, 100, 200, 200, texture, world);

        npcs.add(new NPC(100, 100, 500, 300, texture, world, 1, 0,
            new String[]{"Hello Pisiunchyk! How are you doing?", "Oh, I'm sorry, I forgot you can't answer me", "nevermind, just fuck off"}));
        npcs.add(new NPC(90, 90, 1100, 500, texture, world, 0, 1,
            new String[]{"Hello Zhopa!!!","I have nothing to say", "I guess..."}));
        npcs.add(new NPC(90, 90, 700, 700, texture, world, 1, 1,
            new String[]{"Hello Popa!!!", "I'm not in mood to talk"}));

        // === Інтерфейс ===
        stage = new Stage(new FitViewport(2000, 1000));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        // Діалогове вікно
        dialogueLabel = new Label("", skin);
        dialogueLabel.setFontScale(3f);
        dialogueLabel.setSize(1950, 150);
        dialogueLabel.setPosition(25, 50);
        dialogueLabel.setAlignment(Align.center);
        dialogueLabel.setVisible(false);

        // Фон діалогу (Pixmap -> Texture)
        Pixmap dialogueBg = new Pixmap(1950, 150, Pixmap.Format.RGBA8888);
        dialogueBg.setColor(new Color(0.1f, 0.1f, 0.5f, 0.6f));
        dialogueBg.fillRectangle(0, 0, 1950, 150);
        dialogueBgTexture = new Texture(dialogueBg);
        dialogueBg.dispose();
        dialogueLabel.getStyle().background = new TextureRegionDrawable(new TextureRegion(dialogueBgTexture));

        stage.addActor(dialogueLabel);

        dialogueManager = new DialogueManager(dialogueLabel);
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
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // === Оновлення логіки ===
        player.update(delta);
        for (NPC npc : npcs) npc.update(delta);

        // === Камера слідкує за гравцем ===
        float targetX = player.x + player.width / 2f;
        float targetY = player.y + player.height / 2f;

        float cameraX = Math.max(camera.viewportWidth / 2f,
            Math.min(targetX, WORLD_WIDTH - camera.viewportWidth / 2f));
        float cameraY = Math.max(camera.viewportHeight / 2f,
            Math.min(targetY, WORLD_HEIGHT - camera.viewportHeight / 2f));

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

        // Скидаємо прапорець натискання кнопки в кінці кадру
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
        texture.dispose();
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
