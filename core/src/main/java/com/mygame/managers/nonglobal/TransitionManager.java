package com.mygame.managers.nonglobal;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.world.Transition;
import com.mygame.world.World;

public class TransitionManager {

    /**
     * Loads transitions from a specific world's Tiled map layer named "transitions".
     * @param world The world from which to load transitions.
     */
    public void loadTransitionsFromMap(World world) {
        MapLayer transitionLayer = world.getMap().getLayers().get("transitions");
        if (transitionLayer == null) {
            return; // No transitions layer, which is fine.
        }

        for (MapObject object : transitionLayer.getObjects()) {
            MapProperties props = object.getProperties();

            if (props.containsKey("targetWorldId") && props.containsKey("targetX") && props.containsKey("targetY")) {
                try {
                    float x = props.get("x", Float.class);
                    float y = props.get("y", Float.class);
                    float width = props.get("width", Float.class);
                    float height = props.get("height", Float.class);
                    Rectangle rect = new Rectangle(x, y, width, height);

                    String targetWorldId = props.get("targetWorldId", String.class);
                    Object xObj = props.get("targetX");
                    Object yObj = props.get("targetY");
                    float targetX = Float.parseFloat(String.valueOf(xObj).replace(',', '.'));
                    float targetY = Float.parseFloat(String.valueOf(yObj).replace(',', '.'));

                    world.getTransitions().add(new Transition(targetWorldId, targetX, targetY, rect));
                    System.out.println("SUCCESS: TransitionManager loaded transition to '" + targetWorldId + "' in world '" + world.getName() + "'");
                } catch (Exception e) {
                    System.err.println("CRITICAL ERROR loading a transition object: " + e.getMessage());
                }
            }
        }
    }
}
