package com.mygame.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.game.save.DataLoader;
import com.mygame.managers.ManagerRegistry;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.ui.load.SkinLoader;
import com.mygame.assets.audio.MusicManager;
import com.mygame.world.World;

public class GameInitializer {

    private Player player;
    private SpriteBatch batch;
    private Skin skin;
    private ManagerRegistry managerRegistry;

    public void initGame() {
        EventBus.clear();

        if (managerRegistry != null) managerRegistry.dispose(false);
        if (batch != null) batch.dispose();

        MusicManager.init();

        batch = new SpriteBatch();

        skin = SkinLoader.loadSkin();

        GameSettings settings = SettingsManager.load();

        player = new Player(500, 80, 80, settings.playerX, settings.playerY, Assets.getTexture("zoe"), null);

        managerRegistry = new ManagerRegistry(batch, player, skin);
        GameContext ctx = managerRegistry.getContext();

        player.getInventory().init(ctx.itemRegistry);

        DataLoader.load(ctx, settings);

        // Встановлюємо світ
        World startWorld = ctx.worldManager.getWorld(settings.currentWorldName != null ? settings.currentWorldName : "main");
        player.setWorld(startWorld);
        ctx.worldManager.setCurrentWorld(startWorld);
    }

    public ManagerRegistry getManagerRegistry() { return managerRegistry; }
    public SpriteBatch getBatch() { return batch; }
    public GameContext getContext() { return managerRegistry.getContext(); }

    public void dispose() {
        if (managerRegistry != null) managerRegistry.dispose();
        if (batch != null) batch.dispose();
        if (skin != null) skin.dispose();
    }
}
