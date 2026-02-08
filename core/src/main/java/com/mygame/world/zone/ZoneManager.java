package com.mygame.world.zone;

import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.managers.TimerManager;
import com.mygame.world.WorldManager;

public class ZoneManager {

    private static final float TRANSITION_COOLDOWN = 0.5f; // Cooldown in seconds
    private float cooldownTimer = 0f;
    private boolean inZone = false;
    private Player player;
    private WorldManager worldManager;
    private Zone activeZone;


    public ZoneManager(WorldManager worldManager, Player player){
        this.worldManager = worldManager;
        this.player = player;
        EventBus.subscribe(Events.InteractEvent.class, e -> handleInteraction());
        EventBus.subscribe(Events.TransitionRequestedEvent.class, this::handleTransition);
    }

    private void handleTransition(Events.TransitionRequestedEvent e) {
        if (cooldownTimer > 0) return;
        if (player == null) return;

        EventBus.fire(new Events.OverlayEvent(0.8f, true));
        TimerManager.setAction(() -> {
            worldManager.setCurrentWorld(e.targetWorldId());
            player.setX(e.targetX());
            player.setY(e.targetY());
            player.setWorld(worldManager.getCurrentWorld());
            cooldownTimer = TRANSITION_COOLDOWN; // Start cooldown
        }, 0.2f);
    }

    public void update(float delta) {
        if (cooldownTimer > 0) {
            cooldownTimer -= delta;
            inZone = false;
            return;
        }

        if (worldManager.getCurrentWorld() == null || player == null) return;

        activeZone = null;
        for (Zone zone : worldManager.getCurrentWorld().getZones()) {
            if (zone.getArea().overlaps(player.getBounds())) {
                activeZone = zone;
                break;
            }
        }

        inZone = activeZone != null;
    }

    private void handleInteraction() {
        if (inZone && activeZone != null && activeZone.isEnabled()) {
            activeZone.onInteract();
        }
    }

    public Zone getActiveZone() {
        return activeZone;
    }
    public boolean isInZone(){ return inZone; }
}
