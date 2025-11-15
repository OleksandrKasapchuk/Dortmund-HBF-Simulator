package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.dialogue.Dialogue;
import com.mygame.dialogue.DialogueNode;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.managers.nonglobal.NpcManager;
import com.mygame.entity.Player;
import com.mygame.ui.UIManager;

public class Main extends ApplicationAdapter {

    private static GameInitializer gameInitializer;

    @Override
    public void create() {
        Assets.load();                            // Load textures, sounds, music
        gameInitializer = new GameInitializer();
        gameInitializer.initGame();               // Initialize all game objects
    }

    public static void restartGame() {
        if (gameInitializer != null) {
            gameInitializer.initGame();
        }
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameInitializer.getGameInputHandler().handleInput();

        switch (gameInitializer.getManagerRegistry().getGameStateManager().getState()) {
            case MENU: renderMenu(delta); break;
            case PLAYING: renderGame(delta); break;
            case PAUSED: renderPaused(delta); break;
            case SETTINGS: renderSettings(delta); break;
            case DEATH: renderDeath(); break;
        }
    }

    private void renderMenu(float delta) {
        UIManager uiManager = gameInitializer.getManagerRegistry().getUiManager();
        uiManager.update(delta, gameInitializer.getPlayer(), gameInitializer.getManagerRegistry().getNpcManager().getNpcs());
        uiManager.render();
    }

    private void renderGame(float delta) {
        NpcManager npcManager = gameInitializer.getManagerRegistry().getNpcManager();
        Player player = gameInitializer.getPlayer();
        player.update(delta);

        // Example death dialogue trigger
        if (player.getState() == Player.State.STONED) {
            npcManager.getPolice().setDialogue(
                new Dialogue(
                    new DialogueNode(gameInitializer.getManagerRegistry().getGameStateManager()::playerDied,
                        "Are you stoned?", "You are caught")
                )
            );
        }

        SpriteBatch batch = gameInitializer.getBatch();

        batch.begin();
        gameInitializer.getManagerRegistry().update(delta);
        gameInitializer.getWorld().draw(batch);
        player.draw(batch);
        gameInitializer.getManagerRegistry().render();
        batch.end();

        gameInitializer.getManagerRegistry().getUiManager().render();
    }

    private void renderPaused(float delta) {
        UIManager uiManager = gameInitializer.getManagerRegistry().getUiManager();
        uiManager.update(delta, gameInitializer.getPlayer(), gameInitializer.getManagerRegistry().getNpcManager().getNpcs());
        uiManager.render();
    }

    private void renderSettings(float delta) {
        UIManager uiManager = gameInitializer.getManagerRegistry().getUiManager();
        uiManager.update(delta, gameInitializer.getPlayer(), gameInitializer.getManagerRegistry().getNpcManager().getNpcs());
        uiManager.render();
    }

    private void renderDeath() {
        gameInitializer.getManagerRegistry().getUiManager().render();
    }

    @Override
    public void resize(int width, int height) {
        gameInitializer.getManagerRegistry().resize();
    }

    @Override
    public void dispose() {
        Assets.dispose();
        if (gameInitializer != null) gameInitializer.dispose();
        MusicManager.stopAll();
    }

    public static GameInitializer getGameInitializer() {
        return gameInitializer;
    }
}
