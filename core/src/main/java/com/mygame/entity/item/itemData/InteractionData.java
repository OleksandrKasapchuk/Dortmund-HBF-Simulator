package com.mygame.entity.item.itemData;


import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;

public class InteractionData {
    private String actionId;
    private float cooldownTimer = 0f;

    public InteractionData() {}

    public InteractionData(String actionId) {
        this.actionId = actionId;
    }

    public void interact(Player player) {
        if (cooldownTimer <= 0 && actionId != null && !actionId.isEmpty()) {
            EventBus.fire(new Events.ActionRequestEvent(actionId));
            startCooldown(1.9f);
        }
    }

    public void updateCooldown(float delta) {
        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
            if (cooldownTimer < 0) cooldownTimer = 0;
        }
    }
    public void startCooldown(float seconds) {
        cooldownTimer = seconds;
    }

    public String getActionId() {
        return actionId;
    }
}
