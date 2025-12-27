package com.mygame.entity.item;

import com.badlogic.gdx.graphics.Texture;
import com.mygame.assets.Assets;
import com.mygame.assets.audio.SoundManager;
import com.mygame.entity.Entity;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.managers.TimerManager;
import com.mygame.world.World;
import java.util.function.Consumer;

/**
 * Represents a world item (e.g., spoon, pfand).
 * Can be interactable, solid, and have pickup radius + interaction cooldown.
 */
public class Item extends Entity {

    // --- Item properties ---
    private ItemDefinition type;
    private boolean canBePickedUp;
    private boolean solid;
    private boolean searchable;
    private boolean searched = false;
    private int distance;           // distance at which player can interact/pick up
    private String questId;

    // --- Search reward properties ---
    private String rewardItemKey;
    private int rewardAmount;

    // --- Interaction ---
    private float cooldownTimer = 0f;
    private Consumer<Player> onInteract;

    public Item(
        ItemDefinition type, int width, int height,
        float x, float y, int distance,
        Texture texture, World world,
        boolean canBePickedUp, boolean solid, boolean searchable, String questId,
        String rewardItemKey, int rewardAmount
    ) {
        super(width, height, x, y, texture, world);
        this.type = type;
        this.canBePickedUp = canBePickedUp;
        this.searchable = searchable;
        this.solid = solid;
        this.distance = distance;
        this.questId = questId;
        this.rewardItemKey = rewardItemKey;
        this.rewardAmount = rewardAmount;
    }

    @Override
    public void update(float delta) {
        // Items do not move or update by default.
    }

    /**
     * Executes the interaction logic.
     */
    public void interact(Player player) {
        if (onInteract != null) {
            System.out.println("interactiong " + this);
            onInteract.accept(player);
        } else if (searchable && !searched) {
            System.out.println("serching " + this);
            search(player);
        }
    }

    private void search(Player player) {
        searched = true;
        SoundManager.playSound(Assets.getSound("search"));
        player.setMovementLocked(true);
        // Determine item and amount
        TimerManager.setAction(() -> {
            player.setMovementLocked(false);
            if (rewardItemKey != null && !rewardItemKey.isEmpty()) {
                ItemDefinition reward = ItemRegistry.get(rewardItemKey);
                if (reward != null) {
                    player.getInventory().addItem(reward, rewardAmount);
                    EventBus.fire(new Events.MessageEvent(rewardAmount + " " + Assets.ui.format("ui.found", Assets.items.get(reward.getNameKey()) ), 2));
                }
            } else {
                EventBus.fire(new Events.MessageEvent(Assets.ui.get("ui.not_found") , 2f));
            }
        }, 2);

    }

    /**
     * Generates a unique identifier for this item based on its location and world.
     */
    public String getUniqueId() {
        return world.getName() + "_" + (int)getX() + "_" + (int)getY();
    }

    public void setOnInteract(Consumer<Player> onInteract) {
        this.onInteract = onInteract;
    }

    // --- Basic getters ---
    public String getName() { return type.getNameKey(); }
    public ItemDefinition getType() { return type; }
    public boolean canBePickedUp() { return canBePickedUp; }
    public boolean isSolid() { return solid; }
    public int getDistance() { return distance; }

    // --- Cooldown logic ---
    /**
     * Updates cooldown timer.
     */
    public void updateCooldown(float delta) {
        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
            if (cooldownTimer < 0) cooldownTimer = 0;
        }
    }

    /**
     * @return true if item can currently be interacted with.
     */
    public boolean canInteract() {
        return cooldownTimer <= 0;
    }
    /**
     * Starts cooldown after interaction.
     */
    public void startCooldown(float seconds) {
        cooldownTimer = seconds;
    }

    public String getQuestId(){ return questId; }
    public boolean isSearched() { return searched; }
    public void setSearched(boolean searched) { this.searched = searched; }
}
