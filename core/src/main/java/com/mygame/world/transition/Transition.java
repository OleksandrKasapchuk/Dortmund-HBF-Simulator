package com.mygame.world.transition;


import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;

/**
 * Represents a transition point between worlds (e.g., a doorway).
 * Contains the target world, destination coordinates, and the trigger area.
 */
public class Transition {
    public String targetWorldId;    // ID of the target world
    public float targetX;           // Destination X coordinate
    public float targetY;           // Destination Y coordinate
    public Rectangle area;          // The area that triggers the transition

    public Transition(String targetWorldId, float targetX, float targetY, Rectangle area) {
        this.targetWorldId = targetWorldId;
        this.targetX = targetX;
        this.targetY = targetY;
        this.area = area;
    }

    public void drawDebug(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.WHITE); // колір зони
        shapeRenderer.rect(area.x, area.y, area.width, area.height);
    }

}
