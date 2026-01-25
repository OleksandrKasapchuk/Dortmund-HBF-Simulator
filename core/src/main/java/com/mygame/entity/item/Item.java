package com.mygame.entity.item;

import com.badlogic.gdx.graphics.Texture;
import com.mygame.entity.Entity;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.managers.TimerManager;
import com.mygame.world.World;

/**
 * Represents a world item (e.g., spoon, pfand).
 */
public class Item extends Entity {

    private ItemDefinition type;
    private boolean canBePickedUp;
    private boolean solid;
    private boolean searchable;
    private boolean searched = false;
    private int distance;
    private String questId;
    private String interactionActionId;
    private final boolean isDynamic;

    private String rewardItemKey;
    private int rewardAmount;

    private float cooldownTimer = 0f;

    public Item(
        ItemDefinition type, int width, int height,
        float x, float y, int distance,
        Texture texture, World world,
        boolean canBePickedUp, boolean solid, boolean searchable, String questId,
        String rewardItemKey, int rewardAmount, String interactionActionId, boolean isDynamic) {
        super(width, height, x, y, texture, world);
        this.type = type;
        this.canBePickedUp = canBePickedUp;
        this.searchable = searchable;
        this.solid = solid;
        this.distance = distance;
        this.questId = questId;
        this.rewardItemKey = rewardItemKey;
        this.rewardAmount = rewardAmount;
        this.interactionActionId = interactionActionId;
        this.isDynamic = isDynamic;
    }

    @Override
    public void update(float delta) {}

    public void interact(Player player) {
        if (interactionActionId != null && !interactionActionId.isEmpty()) {
            EventBus.fire(new Events.ActionRequestEvent(interactionActionId));
        } else if (searchable && !searched) {
            search(player);
        }
    }

    private void search(Player player) {
        searched = true;
        // Замість ActionRegistry.executeAction викликаємо івент
        EventBus.fire(new Events.ActionRequestEvent("act.item.search.basic"));
        TimerManager.setAction(() -> {
            EventBus.fire(new Events.ItemSearchedEvent(player, rewardItemKey, rewardAmount));
            EventBus.fire(new Events.SaveRequestEvent());
        }, 2);
    }

    public String getUniqueId() {
        return world.getName() + "_" + (int)getX() + "_" + (int)getY();
    }

    public boolean isInteractable() {
        return interactionActionId != null && !interactionActionId.isEmpty();
    }
    public ItemDefinition getType() { return type; }
    public boolean canBePickedUp() { return canBePickedUp; }
    public boolean isSolid() { return solid; }
    public int getDistance() { return distance; }

    public void updateCooldown(float delta) {
        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
            if (cooldownTimer < 0) cooldownTimer = 0;
        }
    }
    public void startCooldown(float seconds) {
        cooldownTimer = seconds;
    }

    public String getQuestId(){ return questId; }
    public boolean isSearched() { return searched; }
    public boolean isSearchable() {return searchable;}
    public void setSearched(boolean searched) { this.searched = searched; }
    public boolean isDynamic() { return isDynamic; }
}
