package com.mygame.entity.player;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.mygame.assets.Assets;
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
    public enum WalkFrame {
        IDLE,
        LEFT,
        RIGHT
    }
    private WalkFrame currentWalkFrame = WalkFrame.IDLE;

    private float walkAnimTimer = 0f;
    private float walkAnimInterval = 0.1f; // швидкість анімації
    private float walkBobbingTimer = 0f; // таймер для коливань
    private final int baseHeight; // базова висота

    private boolean stepRightNext = true; // щоб чергувати ноги

    private State currentState;


    public Player(int speed, int width, int height, float x, float y, World world) {
        super(width, height, x, y,  Assets.getTexture("zoe.3d"), world);
        this.baseHeight = height;
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
        updateWalkAnimation(delta, movementController.isMoving);
        updateTexture(movementController.isMoving);
    }

    private void updateWalkAnimation(float delta, boolean isMoving) {
        if (!isMoving) {
            currentWalkFrame = WalkFrame.IDLE;
            walkAnimTimer = 0f;
            walkBobbingTimer = 0f; // Скидаємо таймер коливань
            return;
        }

        walkAnimTimer += delta;
        walkBobbingTimer += delta; // Оновлюємо таймер коливань

        if (walkAnimTimer >= walkAnimInterval) {
            walkAnimTimer = 0f;

            if (currentWalkFrame == WalkFrame.IDLE) {
                currentWalkFrame = stepRightNext ? WalkFrame.RIGHT : WalkFrame.LEFT;
            } else {
                currentWalkFrame = WalkFrame.IDLE;
                stepRightNext = !stepRightNext;
            }
        }
    }

    private void updateTexture(boolean isMoving) {
        if (!isMoving) {
            setTexture(Assets.getTexture("zoe.3d"));
            setHeight(baseHeight);
            return;
        }

        // Плавна зміна висоти за допомогою синусоїди
        float bobbingAmount = (float) (Math.sin(walkBobbingTimer * 20f) * 5f);
        setHeight(baseHeight + (int) bobbingAmount);

        switch (currentWalkFrame) {
            case LEFT:
                setTexture(Assets.getTexture("zoe.3d_left"));
                break;
            case RIGHT:
                setTexture(Assets.getTexture("zoe.3d_right"));
                break;
            default:
                // Залишаємось на останній текстурі кроку, поки не зупинимось
                // Це запобігає мерехтінню до idle текстури під час ходьби
                break;
        }
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

    @Override
    public Rectangle getBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight()*0.3f);
        return bounds;
    }
}
