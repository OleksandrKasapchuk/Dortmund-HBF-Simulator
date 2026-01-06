package com.mygame.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.assets.Assets;
import com.mygame.entity.player.Player;
import com.mygame.managers.ManagerRegistry;
import com.mygame.quest.QuestManager;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.ui.load.SkinLoader;
import com.mygame.world.WorldManager;
import com.mygame.assets.audio.MusicManager;
import com.mygame.world.World;

public class GameInitializer {

    private Player player;
    private SpriteBatch batch;
    private Skin skin;
    private ManagerRegistry managerRegistry;

    public void initGame() {
        if (managerRegistry != null) managerRegistry.dispose();
        if (batch != null) batch.dispose();
        MusicManager.stopAll();

        batch = new SpriteBatch();
        WorldManager.init();
        skin = SkinLoader.loadSkin();

        GameSettings settings = SettingsManager.load();

        player = new Player(500, 80, 80, settings.playerX, settings.playerY, Assets.getTexture("zoe"), null);

        managerRegistry = new ManagerRegistry(batch, player, skin);
        GameContext ctx = managerRegistry.getContext();

        player.getInventory().init(ctx.itemRegistry);

        // Встановлюємо світ
        World startWorld = WorldManager.getWorld(settings.currentWorldName != null ? settings.currentWorldName : "main");
        player.setWorld(startWorld);
        WorldManager.setCurrentWorld(startWorld);

        // Load inventory data
        if (settings.inventory != null)
            settings.inventory.forEach((itemKey, amount) -> player.getInventory().addItem(ctx.itemRegistry.get(itemKey), amount));

        // Відновлюємо прогрес квестів
        if (settings.activeQuests != null) {
            settings.activeQuests.forEach((key, saveData) -> {
                QuestManager.Quest q = ctx.questManager.getQuest(key);
                if (q != null) {
                    q.setStatus(saveData.status);
                    q.setProgress(saveData.progress);
                }
            });
        }

        MusicManager.playMusic(Assets.getMusic("start"));
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
