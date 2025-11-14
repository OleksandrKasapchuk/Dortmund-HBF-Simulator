package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.managers.GameStateManager;
import com.mygame.managers.ManagerRegistry;
import com.mygame.managers.audio.MusicManager;
import com.mygame.managers.NpcManager;
import com.mygame.entity.Player;
import com.mygame.managers.QuestManager;
import com.mygame.ui.UIManager;
import com.mygame.world.World;


public class Main extends ApplicationAdapter {
    private static Main instance;

    private Player player;
    private World world;

    private static ManagerRegistry managerRegistry;

    private SpriteBatch batch;
    private BitmapFont font;

    @Override
    public void create() {
        Assets.load();
        instance = this;
        initGame();
    }

    private void initGame() {
        MusicManager.stopAll();
        QuestManager.reset();
        if (batch!=null) batch.dispose();
        if (font!=null) font.dispose();

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2.5f);
        font.setUseIntegerPositions(false);

        world = new World();
        player = new Player(500, 80, 80, 200, 200, Assets.textureZoe, world, null);

        managerRegistry = new ManagerRegistry(batch, font, player, world);

        player.setItemManager(managerRegistry.getItemManager());

        MusicManager.playMusic(Assets.startMusic);
    }

    public static void restartGame() {instance.initGame();}

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        switch (managerRegistry.getGameStateManager().getState()) {
            case MENU: renderMenu(delta); break;
            case PLAYING: renderGame(delta); break;
            case PAUSED: renderPaused(delta); break;
            case SETTINGS: renderSettings(delta); break;
            case DEATH: renderDeath(); break;
        }
    }

    private void handleInput() {
        GameStateManager gameStateManager = managerRegistry.getGameStateManager();
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) gameStateManager.togglePause();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gameStateManager.toggleSettings();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) && gameStateManager.getState() == GameStateManager.GameState.MENU) gameStateManager.startGame();
    }

    public void renderMenu(float delta) {
        managerRegistry.getUiManager().update(delta, player, managerRegistry.getNpcManager().getNpcs());
        managerRegistry.getUiManager().render();
    }

    public void renderGame(float delta){
        NpcManager npcManager = managerRegistry.getNpcManager();
        player.update(delta);
        if (player.getState() == Player.State.STONED){npcManager.getPolice().setDialogue(new Dialogue(new DialogueNode(managerRegistry.getGameStateManager()::playerDied, "Are you stoned?", "You are caught")));}
        managerRegistry.update(delta);

        // --- World Rendering ---
        batch.begin();
        world.draw(batch);
        player.draw(batch);
        managerRegistry.render(); // Тепер малює лише об'єкти світу
        batch.end();

        // --- UI Rendering ---
        managerRegistry.getUiManager().render(); // Малюємо UI окремо
    }

    private void renderPaused(float delta) {
        UIManager uiManager = managerRegistry.getUiManager();
        uiManager.update(delta, player, managerRegistry.getNpcManager().getNpcs());
        uiManager.render();
    }

    private void renderSettings(float delta) {
        UIManager uiManager = managerRegistry.getUiManager();
        uiManager.update(delta, player, managerRegistry.getNpcManager().getNpcs());
        uiManager.render();
    }

    private void renderDeath() {managerRegistry.getUiManager().render();}

    @Override
    public void resize(int width, int height) {managerRegistry.resize();}

    @Override
    public void dispose() {
        Assets.dispose();
        batch.dispose();
        font.dispose();
        managerRegistry.dispose();
        MusicManager.stopAll();
    }
    public static ManagerRegistry getManagerRegistry(){return managerRegistry;}
}
