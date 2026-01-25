package com.mygame.world.zone;


import com.badlogic.gdx.math.Rectangle;
import com.mygame.events.EventBus;
import com.mygame.events.Events;

/**
 * Represents a transition point between worlds (e.g., a doorway).
 * Contains the target world, destination coordinates, and the trigger area.
 */
public class TransitionZone extends Zone {
    public String targetWorldId;    // ID of the target world
    public float targetX;           // Destination X coordinate
    public float targetY;           // Destination Y coordinate

    public TransitionZone(String id, String targetWorldId, float targetX, float targetY, Rectangle area) {
        super(id, area);
        this.targetWorldId = targetWorldId;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public void onInteract() {
       EventBus.fire(new Events.TransitionRequestedEvent(targetWorldId, targetX, targetY));
    }
}
