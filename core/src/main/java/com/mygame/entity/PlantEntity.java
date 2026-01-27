package com.mygame.entity;

import com.badlogic.gdx.graphics.Texture;
import com.mygame.world.World;

import java.util.Map;

public class PlantEntity extends Entity {

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

    public PlantEntity(float x, float y, World world, Map<Phase, Texture> phaseTextures, float timeToNextPhase) {
        // Start with the seed texture and its dimensions
        super(75, 100, x, y, phaseTextures.get(Phase.SEED), world);
        this.phaseTextures = phaseTextures;
        this.currentPhase = Phase.SEED;
        this.growthTimer = 0f;
        this.timeToNextPhase = timeToNextPhase;
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
        if (currentPhase == Phase.HARVESTABLE) {
            // Here, you would add logic to yield an item.
            // For example, you could fire an event to create the harvested item in the world:
            // EventBus.fire(new Events.CreateItemEvent("harvested_crop", getX(), getY(), getWorld()));

            // After harvesting, reset the plant to its initial state.
            currentPhase = Phase.SEED;
            texture = phaseTextures.get(currentPhase);
            growthTimer = 0f;
        }
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }
}
