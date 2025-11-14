package com.mygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygame.entity.Player;
import com.mygame.ui.UIManager;
import com.mygame.world.World;


public class ManagerRegistry {
    private UIManager uiManager;
    private NpcManager npcManager;
    private PfandManager pfandManager;
    private ItemManager itemManager;
    private PlayerEffectManager playerEffectManager;
    private CameraManager cameraManager;
    private static GameStateManager gameStateManager;
    private EventManager eventManager;

    private World world;
    private Player player;
    private SpriteBatch batch;

    public ManagerRegistry(SpriteBatch batch, BitmapFont font, Player player, World world){
        this.batch = batch;
        this.world = world;
        this.player = player;

        itemManager = new ItemManager(world);
        uiManager = new UIManager(player);

        playerEffectManager = new PlayerEffectManager(player, uiManager);
        playerEffectManager.registerEffects();

        cameraManager = new CameraManager(4000, 2000);

        npcManager = new NpcManager(batch, player, world, uiManager, font);
        pfandManager = new PfandManager();

        gameStateManager = new GameStateManager(uiManager);
        eventManager = new EventManager(player,npcManager,uiManager,itemManager,batch,font);

        uiManager.setCurrentStage("MENU");
        player.getInventory().setOnInventoryChanged(() -> {
            if (uiManager.getInventoryUI().isVisible()) {
                uiManager.getInventoryUI().update(player);
            }
        });
        resize();
    }
    public void update(float delta) {
        npcManager.update(delta);
        cameraManager.update(player, batch);
        itemManager.update(player);
        uiManager.update(delta, player, npcManager.getNpcs());
        pfandManager.update(delta, player, world);
        eventManager.update(delta);
        uiManager.resetButtons();
    }

    public void render(){
        // Спочатку малюємо всі об'єкти світу (спрайти)
        itemManager.draw(batch);
        pfandManager.draw(batch);

        // Потім малюємо NPC та текстові підказки до подій.
        // Порядок тут важливий, щоб текст малювався поверх.
        npcManager.render();
        eventManager.render();
    }

    public UIManager getUiManager(){return uiManager;}
    public GameStateManager getGameStateManager(){return gameStateManager;}
    public ItemManager getItemManager(){return itemManager;}
    public NpcManager getNpcManager(){return npcManager;}
    public CameraManager getCameraManager() {return cameraManager;}

    public void resize(){
        cameraManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (uiManager != null) uiManager.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    public void dispose(){uiManager.dispose();}
}
