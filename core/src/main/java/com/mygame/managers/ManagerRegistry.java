package com.mygame.managers;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.entity.player.Player;
import com.mygame.game.GameContext;
import com.mygame.game.GameInputHandler;
import com.mygame.game.GameStateManager;
import com.mygame.ui.UIManager;

public class ManagerRegistry {

    private final GameContext ctx;
    private final GameInputHandler gameInputHandler;

    public ManagerRegistry(SpriteBatch batch, Player player, Skin skin, UIManager ui, GameStateManager gsm) {
        // 1. Create the context, which now creates and holds all managers
        this.ctx = new GameContext(batch, player, skin, ui, gsm);

        // 2. Create remaining objects that need the context
        this.gameInputHandler = new GameInputHandler(ctx.gsm, ctx.ui);
    }

    public void update(float delta) {
        ctx.update(delta);
    }

    public void resize(int width, int height) {
        ctx.cameraManager.resize(width, height, ctx.worldManager.getCurrentWorld());
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
