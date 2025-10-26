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
    private static UIManager uiManager;
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

    public enum GameState { MENU, PLAYING, PAUSED }
    private static GameState state = GameState.MENU;

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

        player = new Player(500, 80, 80, 200, 200, Assets.textureZoe, world);
        uiManager = new UIManager(player);
        npcManager = new NpcManager(batch, player,world,uiManager,font);
        spoon = new InteractableObject("spoon", 60, 60, 500, 1800, Assets.textureSpoon, world);

        MusicManager.playMusic(Assets.startMusic, 0.4f);
    }
    public static void startGame() {
        state = GameState.PLAYING;
        MusicManager.playMusic(Assets.backMusic1, 0.2f);
        uiManager.setCurrentStage(2);
    }

    public static void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
            MusicManager.pauseMusic();
            uiManager.setCurrentStage(3);
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
            MusicManager.resumeMusic();
            uiManager.setCurrentStage(2);
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        MusicManager.update(delta);

        switch (state) {
            case MENU:
                renderMenu();
                break;
            case PAUSED:
                renderPaused();
                break;
            case PLAYING:
                renderGame();
                break;

        }
    }

    public void renderMenu() {
        uiManager.update(Gdx.graphics.getDeltaTime(), player, npcManager.getNpcs());
        uiManager.render();
        batch.begin();
        font.draw(batch,"DORTMUND HBF SIMULATOR", 700, 600);
        font.draw(batch,"PRESS ENTER TO START", 710, 500);
        batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startGame();
        }
    }

    public void renderGame(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            togglePause();
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

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(0.1f,0.1f, 0.35f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            togglePause();
            return;
        }
        uiManager.update(Gdx.graphics.getDeltaTime(), player, npcManager.getNpcs());
        uiManager.render();
        batch.begin();
        font.draw(batch,"GAME PAUSED", 825, 600);
        font.draw(batch,"PRESS P TO RESUME", 775, 500);
        batch.end();
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
        MusicManager.stopAll();
    }
}
