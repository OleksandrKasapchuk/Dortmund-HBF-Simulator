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
    private static Main instance;

    private Player player;

    private World world;
    private static UIManager uiManager;
    private NpcManager npcManager;
    private PfandManager pfandManager;
    private ItemManager itemManager;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;

    private static final int WORLD_WIDTH = 4000;
    private static final int WORLD_HEIGHT = 2000;

    public enum GameState { MENU, PLAYING, PAUSED, DEATH}
    private static GameState state = GameState.MENU;

    @Override
    public void create() {
        instance = this;

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.5f);
        font.setUseIntegerPositions(false);
        camera = new OrthographicCamera();
        viewport = new FitViewport(2000, 1000, camera);
        Assets.load();

        initGame();
    }
    private void initGame() {
        MusicManager.stopAll();
        QuestManager.reset();

        world = new World();
        itemManager = new ItemManager(world);
        player = new Player(500, 80, 80, 200, 200, Assets.textureZoe, world, itemManager);
        if (uiManager != null) uiManager.dispose();
        uiManager = new UIManager(player);

        npcManager = new NpcManager(batch, player, world, uiManager, font);
        pfandManager = new PfandManager();

        state = GameState.MENU;
        uiManager.setCurrentStage("MENU");
        MusicManager.playMusic(Assets.startMusic, 0.4f);
    }

    public static void restartGame() {instance.initGame();}

    public static void startGame() {
        state = GameState.PLAYING;
        MusicManager.playMusic(Assets.backMusic1, 0.2f);
        uiManager.setCurrentStage("GAME");
    }

    public static void playerDied() {
        state = GameState.DEATH;
        MusicManager.stopAll();
        MusicManager.playMusic(Assets.backMusic4, 2f);
        uiManager.setCurrentStage("DEATH");
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
            case MENU: renderMenu(); break;
            case PAUSED: renderPaused(); break;
            case PLAYING: renderGame(); break;
            case DEATH: renderDeath(); break;
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
        if (QuestManager.hasQuest("Big delivery") && player.getInventory().getAmount("grass") < 1000) {
            NPC boss = npcManager.getBoss();

            if (boss != null && !uiManager.getDialogueManager().isDialogueActive()) {
                boss.setTexts(new String[]{
                    "You are not doing the task!",
                    "I told you to hide the grass, not lose it.",
                    "Now you will regret this..."
                });
                boss.setAction(() -> {
                    Main.playerDied();
                    Assets.gunShot.play();
                });

                com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                    @Override
                    public void run() {
                        boss.setX(player.getX() - 100);
                        boss.setY(player.getY());
                        uiManager.getDialogueManager().startForcedDialogue(boss);
                        uiManager.getDialogueUI().show();
                        player.setMovementLocked(true);
                        uiManager.getDialogueManager().forceAdvance();
                    }
                }, 2f);
            }
        }

        float delta = Gdx.graphics.getDeltaTime();

        player.update(delta);
        itemManager.update(player);

        uiManager.update(delta, player, npcManager.getNpcs());
        pfandManager.update(delta, player, world);

        if(npcManager.updatePolice()){
            MusicManager.playMusic(Assets.backMusic4, 0.3f);
            uiManager.getGameUI().showInfoMessage("You ran away from the police", 1.5f);
        }

        float targetX = player.getX() + player.width / 2f;
        float targetY = player.getY() + player.height / 2f;
        float cameraX = Math.max(camera.viewportWidth / 2f, Math.min(targetX, WORLD_WIDTH - camera.viewportWidth / 2f));
        float cameraY = Math.max(camera.viewportHeight / 2f, Math.min(targetY, WORLD_HEIGHT - camera.viewportHeight / 2f));
        camera.position.set(cameraX, cameraY, 0);
        camera.update();

        viewport.apply();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        world.draw(batch);

        if (QuestManager.hasQuest("Big delivery") && itemManager.getBush().isPlayerNear(player)) {
            font.draw(batch, "Press E to hide your kg", itemManager.getBush().getX(), itemManager.getBush().getY());
            if (uiManager.isInteractPressed()) {
                player.getInventory().removeItem("grass", 1000);
                QuestManager.removeQuest("Big delivery");
                Assets.bushSound.play();
                player.setMovementLocked(true);
                com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                    @Override
                    public void run() {
                        npcManager.callPolice();
                        uiManager.getDialogueManager().startForcedDialogue(npcManager.getPolice1());
                        npcManager.getPolice1().setAction(() -> {
                            npcManager.getPolice1().setFollowing(true);
                            uiManager.getGameUI().showInfoMessage("RUN", 2f);
                            MusicManager.playMusic(Assets.backMusic4, 1.5f);
                            npcManager.getPolice1().setTexts(new String[]{"You got caught!"});
                            npcManager.getPolice1().setAction(Main::playerDied);
                        });
                    }
                }, 2f);
            }
        }
        itemManager.draw(batch);

        itemManager.getPfandAutomat().updateCooldown(Gdx.graphics.getDeltaTime());

        if (itemManager.getPfandAutomat().isPlayerNear(player)) {
            font.draw(batch,"Press E to change your pfand for money",
                itemManager.getPfandAutomat().getX(),
                itemManager.getPfandAutomat().getY() + 150);

            if (uiManager.isInteractPressed() && itemManager.getPfandAutomat().canInteract()) {
                if(player.getInventory().removeItem("pfand",1)){
                    Assets.pfandAutomatSound.play();
                    com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
                        @Override
                        public void run() {
                            Assets.moneySound.play();
                            uiManager.getGameUI().showInfoMessage("You got 1 money for pfand",1f);
                            player.getInventory().addItem("money",1);
                        }
                    }, 1.9f);

                    itemManager.getPfandAutomat().startCooldown(2f);
                }
            }
        }
        player.draw(batch);
        npcManager.render();
        pfandManager.draw(batch);
        batch.end();
        uiManager.render();
        uiManager.resetButtons();
    }

    public void renderPaused() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            togglePause();
            return;
        }
        uiManager.update(Gdx.graphics.getDeltaTime(), player, npcManager.getNpcs());
        uiManager.render();
    }

    public void renderDeath(){uiManager.render();}

    @Override
    public void resize(int width, int height) {
        if (viewport != null) viewport.update(width, height, true);
        if (uiManager != null) uiManager.resize(width, height);
    }

    @Override
    public void dispose() {
        Assets.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (uiManager != null) uiManager.dispose();
        MusicManager.stopAll();
    }
    public static Main getInstance() {
        return instance;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
