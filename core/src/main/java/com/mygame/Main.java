package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygame.ui.UIManager;


public class Main extends ApplicationAdapter {
    // === Основні ігрові об'єкти ===
    private Player player;
    private InteractableObject spoon;
    private World world;
    private UIManager uiManager;
    private NpcManager npcManager;

    // === Рендеринг та графіка ===
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;

    // === Константи світу ===
    private static final int WORLD_WIDTH = 4000;
    private static final int WORLD_HEIGHT = 2000;
    public static int getWorldWidth() { return WORLD_WIDTH; }
    public static int getWorldHeight() { return WORLD_HEIGHT; }

    private enum GameState { MENU, PLAYING, PAUSED }
    private GameState state = GameState.MENU;

    @Override
    public void create() {
        Assets.load();

        batch = new SpriteBatch();

        font = new BitmapFont();
        font.getData().setScale(2.5f);
        font.setUseIntegerPositions(false);

        camera = new OrthographicCamera();
        viewport = new FitViewport(2000, 1000, camera);
        world = new World();

        player = new Player(500, 90, 90, 200, 200, Assets.textureZoe, world);
        uiManager = new UIManager(player);
        npcManager = new NpcManager(batch, player,world,uiManager,font);
        spoon = new InteractableObject("spoon", 60, 60, 500, 1800, Assets.textureSpoon, world);

        // === Музика стартового екрану ===
        Assets.startMusic.setLooping(true);
        Assets.startMusic.setVolume(0.6f);
        Assets.startMusic.play();// запускає музику
    }

    @Override
    public void render() {
        switch (state) {
            case MENU -> renderMenu();
            case PLAYING -> renderGame();
            case PAUSED -> renderPaused();

        }
    }

    public void renderMenu() {
        // === Малювання ===
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.35f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        drawCenteredText("PRESS SPACE TO START GAME", 700);
        batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            state = GameState.PLAYING;

            Assets.startMusic.stop();
            Assets.backMusic1.setLooping(true);
            Assets.backMusic1.setVolume(0.5f);
            Assets.backMusic1.play();
        }
    }

    public void renderGame(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            state = GameState.PAUSED;
            Assets.backMusic1.pause();
            return;
        }

        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            uiManager.toggleQuestTable();
        }

        player.update(delta);

        uiManager.update(delta, player, npcManager.getNpcs());

        if (spoon != null && spoon.isPlayerNear(player)) {
            player.getInventory().addItem(spoon.getName(), 1);
            spoon = null;
        }

        // === Камера слідкує за гравцем ===
        float targetX = player.x + player.width / 2f;
        float targetY = player.y + player.height / 2f;
        float cameraX = Math.max(camera.viewportWidth / 2f, Math.min(targetX, WORLD_WIDTH - camera.viewportWidth / 2f));
        float cameraY = Math.max(camera.viewportHeight / 2f, Math.min(targetY, WORLD_HEIGHT - camera.viewportHeight / 2f));
        camera.position.set(cameraX, cameraY, 0);
        camera.update();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.35f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        world.draw(batch);
        if (spoon != null) {
            spoon.draw(batch);
        }
        player.draw(batch);
        npcManager.render();
        batch.end();
        uiManager.render();
    }

    public void renderPaused() {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.35f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        drawCenteredText("PAUSED", 550);
        drawCenteredText("PRESS P TO RESUME", 450);
        batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            state = GameState.PLAYING;
            Assets.backMusic1.play();
        }
    }

    private void drawCenteredText(String text, float y) {
        font.draw(batch, text, 950 - text.length() * 10, y);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiManager.resize(width, height);
    }

    @Override
    public void dispose() {
        // === Очищення пам’яті ===
        Assets.dispose();
        batch.dispose();
        font.dispose();
        uiManager.dispose();
    }
}
