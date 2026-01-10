package com.mygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.entity.player.Player;
import com.mygame.game.GameContext;
import com.mygame.game.GameInputHandler;

public class ManagerRegistry {

    private final GameContext ctx;
    private final GameInputHandler gameInputHandler;

    public ManagerRegistry(SpriteBatch batch, Player player, Skin skin) {
        // 1. Create the context, which now creates and holds all managers
        this.ctx = new GameContext(batch, player, skin);

        // 2. Create remaining objects that need the context
        this.gameInputHandler = new GameInputHandler(ctx.gsm, ctx.ui);
    }

    public void update(float delta) {
        ctx.update(delta);
    }

    public void resize() {
        ctx.cameraManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), ctx.worldManager.getCurrentWorld());
        ctx.ui.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void dispose() {
        dispose(true);
    }

    public void dispose(boolean save) {
        ctx.dispose(save);
    }

    // --- Getters --- //
    public CameraManager getCameraManager() { return ctx.cameraManager; }
    public GameInputHandler getGameInputHandler(){ return gameInputHandler; }
    public GameContext getContext(){ return ctx; }
}
