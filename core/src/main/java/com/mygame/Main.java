package com.mygame;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygame.dialogue.Dialogue;
import com.mygame.dialogue.DialogueNode;
import com.mygame.managers.global.WorldManager;
import com.mygame.managers.global.audio.MusicManager;
import com.mygame.managers.nonglobal.NpcManager;
import com.mygame.entity.Player;
import com.mygame.ui.UIManager;

public class Main extends ApplicationAdapter {

    private static GameInitializer gameInitializer;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        Assets.load();                            // Load textures, sounds, music
        gameInitializer = new GameInitializer();
        gameInitializer.initGame();               // Initialize all game objects
        shapeRenderer = new ShapeRenderer();
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

        UIManager uiManager = gameInitializer.getManagerRegistry().getUiManager();

        switch (gameInitializer.getManagerRegistry().getGameStateManager().getState()) {
            case PLAYING:
                renderGame(delta);
                break;
            case DEATH:
                uiManager.render();
                break;
            case MENU:
            case PAUSED:
            case SETTINGS:
                uiManager.update(delta, gameInitializer.getPlayer());
                uiManager.render();
                break;
        }
    }

    private void renderGame(float delta) {
        NpcManager npcManager = gameInitializer.getManagerRegistry().getNpcManager();
        Player player = gameInitializer.getPlayer();
        player.update(delta);

        WorldManager.update(delta, player, gameInitializer.getManagerRegistry().getUiManager().isInteractPressed());

        if (player.getState() == Player.State.STONED) {
            npcManager.getPolice().setDialogue(
                new Dialogue(
                    new DialogueNode(gameInitializer.getManagerRegistry().getGameStateManager()::playerDied,
                        Assets.bundle.get("dialogue.police.stoned.1"),
                        Assets.bundle.get("dialogue.police.stoned.2"))
                )
            );
        }


        SpriteBatch batch = gameInitializer.getBatch();

        // Draw sprites
        batch.setProjectionMatrix(gameInitializer.getManagerRegistry().getCameraManager().getCamera().combined);
        batch.begin();
        gameInitializer.getManagerRegistry().update(delta);
        WorldManager.draw(batch, gameInitializer.getFont(), player);
        player.draw(batch);
        gameInitializer.getManagerRegistry().render();
        batch.end();

        // Draw shapes
        shapeRenderer.setProjectionMatrix(gameInitializer.getManagerRegistry().getCameraManager().getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (WorldManager.getCurrentWorld() != null) {
            WorldManager.getCurrentWorld().drawTransitions(shapeRenderer);
        }
        shapeRenderer.end();

        // Draw UI
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
        if (shapeRenderer != null) shapeRenderer.dispose();
        WorldManager.disposeWorlds();
        MusicManager.stopAll();
    }

    public static GameInitializer getGameInitializer() {
        return gameInitializer;
    }
}
