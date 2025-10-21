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


public class Main extends ApplicationAdapter {

    // === Основні ігрові об'єкти ===
    private Player player;
    private World world;
    private UIManager uiManager;
    private NpcManager npcManager;

    // === Рендеринг та графіка ===
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;

    // Текстури
    private Texture textureZoe;

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


        font = new BitmapFont();
        font.getData().setScale(2.5f);
        font.setUseIntegerPositions(false);

        camera = new OrthographicCamera();
        viewport = new FitViewport(2000, 1000, camera);
        world = new World();

        player = new Player(500, 90, 90, 200, 200, textureZoe, world);
        uiManager = new UIManager(player);
        npcManager = new NpcManager(batch, player,world,uiManager,font);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // === Оновлення ігрової логіки ===
        player.update(delta);
        uiManager.update(delta, player, npcManager.getNpcs());

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
        npcManager.render();
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
        batch.dispose();
        font.dispose();
        world.dispose();
        uiManager.dispose();
        npcManager.dispose();
    }
}
