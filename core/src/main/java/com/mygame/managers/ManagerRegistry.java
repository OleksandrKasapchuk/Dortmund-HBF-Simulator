package com.mygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygame.entity.player.Player;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.item.PfandManager;
import com.mygame.entity.player.PlayerEffectManager;
import com.mygame.game.GameContext;
import com.mygame.game.GameStateManager;
import com.mygame.ui.UIManager;
import com.mygame.world.transition.TransitionManager;
import com.mygame.world.WorldManager;

public class ManagerRegistry {

    // --- Managers ---
    private UIManager uiManager;
    private NpcManager npcManager;
    private PfandManager pfandManager;
    private ItemManager itemManager;
    private TransitionManager transitionManager;
    private CameraManager cameraManager;
    private GameStateManager gameStateManager;

    // --- Core game objects ---
    private Player player;

    public ManagerRegistry(SpriteBatch batch, Player player, Skin skin) {
        this.player = player;

        cameraManager = new CameraManager(player);
        pfandManager = new PfandManager();

        uiManager = new UIManager(batch, player, skin);
        PlayerEffectManager.init(player, uiManager);

        itemManager = new ItemManager();

        npcManager = new NpcManager(player);
        transitionManager = new TransitionManager();

        gameStateManager = new GameStateManager(uiManager);
    }

    public void update(float delta) {
        npcManager.update(delta);
        cameraManager.update(delta, WorldManager.getCurrentWorld());
        itemManager.update(delta, player);
        uiManager.update(delta);
        pfandManager.update(delta);
    }

    public void resize() {
        cameraManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (uiManager != null) uiManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void dispose() {
        if (uiManager != null) uiManager.dispose();
    }

    public UIManager getUiManager() { return uiManager; }
    public GameStateManager getGameStateManager() { return gameStateManager; }
    public NpcManager getNpcManager() { return npcManager; }
    public CameraManager getCameraManager() { return cameraManager; }
    public ItemManager getItemManager() { return itemManager; }
    public TransitionManager getTransitionManager() { return transitionManager; }

    public GameContext createContext(){ return new GameContext(player, uiManager, npcManager, gameStateManager, itemManager); }
}
