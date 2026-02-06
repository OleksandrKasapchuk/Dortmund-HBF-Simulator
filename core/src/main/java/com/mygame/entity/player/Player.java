package com.mygame.entity.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.mygame.entity.Entity;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.npc.NpcManager;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.save.SettingsManager;
import com.mygame.world.World;


// Player entity controlled by user
public class Player extends Entity {

    public Touchpad touchpad;

    private final InventoryManager inventory = new InventoryManager();
    private ItemManager itemManager;
    private NpcManager npcManager;

    private boolean isMovementLocked = false;
    private PlayerMovementController movementController;
    private PlayerStatusController statusController;
    private PlayerEffectController effectController;

    public enum State {
        NORMAL("player.state.normal"),
        STONED("player.state.stoned");

        private final String localizationKey;

        State(String localizationKey) {
            this.localizationKey = localizationKey;
        }

        public String getLocalizationKey() {
            return localizationKey;
        }
    }

    private State currentState;


    public Player(int speed, int width, int height, float x, float y, Texture texture, World world) {
        super(width, height, x, y, texture, world);
        this.statusController = new PlayerStatusController();
        this.movementController = new PlayerMovementController();
        this.currentState = SettingsManager.load().playerState;
        this.effectController = new PlayerEffectController();
    }

    // Lock/unlock movement (used for dialogues, cutscenes etc.)
    public void setMovementLocked(boolean locked) {this.isMovementLocked = locked;}
    public void setItemManager(ItemManager itemManager) {this.itemManager = itemManager;}
    public void setNpcManager(NpcManager npcManager) {this.npcManager = npcManager;}

    // --- UPDATE METHOD ---
    @Override
    public void update(float delta) {
        statusController.update(delta);
        effectController.update(this, delta);
        if (isMovementLocked) return;
        movementController.update(this, delta);
    }

    public Item getCollidingSolidItem(Rectangle rect) {
        for (Item item : itemManager.getAllItems()) {
            if (!item.isSolid()) continue;
            if (item.getWorld() != world) continue;
            if (rect.overlaps(item.getBounds())) {
                return item;
            }
        }
        return null;
    }

    public NPC getCollidingNpc(Rectangle rect) {
        for (NPC npc : npcManager.getNpcs()) {
            if (npc.getWorld() != world) continue;
            if (rect.overlaps(npc.getBounds())) {
                return npc;
            }
        }
        return null;
    }

    public InventoryManager getInventory() {return inventory;}

    // State setters
    public void setState(Player.State state) {
        this.currentState = state;
        EventBus.fire(new Events.PlayerStateChangedEvent(state));
        EventBus.fire(new Events.SaveRequestEvent());
    }

    public State getState() { return currentState; }

    public PlayerStatusController getStatusController() {return statusController;}
    public PlayerMovementController getMovementController(){return movementController;}
}
