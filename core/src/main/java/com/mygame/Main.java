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
    private InteractableObject bush;
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

    public enum GameState { MENU, PLAYING, PAUSED, DEATH}
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
        bush = new InteractableObject("bush",200,100,800,1800,Assets.bush,world);

        MusicManager.playMusic(Assets.startMusic, 0.4f);
    }
    public static void startGame() {
        state = GameState.PLAYING;
        MusicManager.playMusic(Assets.backMusic1, 0.2f);
        uiManager.setCurrentStage("GAME");
    }

    public static void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
            MusicManager.pauseMusic();
            uiManager.setCurrentStage("PAUSE");
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
            MusicManager.resumeMusic();
            uiManager.setCurrentStage("GAME");
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        MusicManager.update(delta);

        switch (state) {
            case MENU: renderMenu();break;
            case PAUSED: renderPaused();break;
            case PLAYING: renderGame();break;
            case DEATH: renderDeath();break;
        }
    }

    public void renderMenu() {
        uiManager.update(Gdx.graphics.getDeltaTime(), player, npcManager.getNpcs());
        uiManager.render();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startGame();
        }
    }

    public void renderGame(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            togglePause();
            return;
        }
        // === Перевірка виконання завдання від боса ===
        if (QuestManager.hasQuest("Big delivery") && player.getInventory().getAmount("grass") < 1000) {
            NPC boss = npcManager.getBoss();

            if (!uiManager.getDialogueManager().isDialogueActive()) {
                // Змінюємо тексти і блокуємо рух
                boss.setTexts(new String[]{
                    "You are not doing the task!",
                    "I told you to hide the grass, not lose it.",
                    "Now you will regret this..."
                });
                boss.setAction(() -> {
                    uiManager.getGameUI().showInfoMessage("You died", 2f);

                });


                // (опційно) після 1 секунди почати показ тексту
                com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                    @Override
                    public void run() {
                        uiManager.getDialogueManager().startForcedDialogue(boss);
                        uiManager.getDialogueUI().show();
                        player.setMovementLocked(true);
                        uiManager.getDialogueManager().forceAdvance();
                    }
                }, 2f);
            }
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
        if (spoon != null) {spoon.draw(batch);}
        bush.draw(batch);

        if (bush.isPlayerNear(player)) {
            font.draw(batch, "Press E to hide your kg", bush.x, bush.y);
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                player.getInventory().removeItem("grass", 1000);
            }
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
    }

    public void renderDeath(){}
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
