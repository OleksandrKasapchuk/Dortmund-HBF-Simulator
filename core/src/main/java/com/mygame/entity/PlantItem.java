package com.mygame.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemDefinition;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.world.World;

import java.util.Map;

public class PlantItem extends Item {

    public enum Phase {
        SEED,
        SPROUT,
        FLOWERING,
        HARVESTABLE
    }

    private Phase currentPhase;
    private float growthTimer;
    private final float timeToNextPhase; // Time in seconds for each phase

    private final Map<Phase, Texture> phaseTextures;

    public PlantItem(String id, ItemDefinition type, float x, float y, World world, Map<Phase, Texture> phaseTextures,
                     float timeToNextPhase) {
        // Start with the seed texture and its dimensions
        super(id, type, 75, 100, x, y, phaseTextures.get(Phase.SEED), world, false, true, null, true);

        this.phaseTextures = phaseTextures;
        this.currentPhase = Phase.SEED;
        this.growthTimer = 0f;
        this.timeToNextPhase = timeToNextPhase;
        this.bounds = new Rectangle(x, y, 75, 50);
    }

    @Override
    public void update(float delta) {
        // Only grow if the plant is not yet ready for harvest
        if (currentPhase != Phase.HARVESTABLE) {
            growthTimer += delta;
            if (growthTimer >= timeToNextPhase) {
                advancePhase();
                growthTimer = 0f; // Reset timer for the new phase
            }
        }
    }

    private void advancePhase() {
        int nextPhaseOrdinal = currentPhase.ordinal() + 1;
        if (nextPhaseOrdinal < Phase.values().length) {
            currentPhase = Phase.values()[nextPhaseOrdinal];
            // Update texture to reflect the new growth phase
            texture = phaseTextures.get(currentPhase);
        }
    }

    public void harvest() {
        System.out.println("plant");
        if (!isInteractable())  return;
        System.out.println("harvested");
        EventBus.fire(new Events.HarvestPlantEvent(this));
    }

    public boolean isInteractable(){
        return currentPhase == Phase.HARVESTABLE;
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }
}
