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

        // 1. Створюємо гравця з початковими даними, але без світу
        player = new Player(500, 80, 80, settings.playerX, settings.playerY, Assets.getTexture("zoe"), null);
        player.getStatusController().setHunger(settings.playerHunger);
        player.getStatusController().setThirst(settings.playerThirst);
        player.setState(settings.playerState);

        // 2. Створюємо реєстр менеджерів, передаючи туди вже існуючого гравця
        managerRegistry = new ManagerRegistry(batch, player, skin);
        GameContext ctx = managerRegistry.getContext();

        // 3. Ініціалізуємо інвентар гравця, використовуючи реєстр предметів з контексту
        player.getInventory().init(ctx.itemRegistry);

        // 4. Завантажуємо дані гри
        DataLoader.load(ctx, settings);

        // 5. Встановлюємо початковий світ для гравця та менеджера світів
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
