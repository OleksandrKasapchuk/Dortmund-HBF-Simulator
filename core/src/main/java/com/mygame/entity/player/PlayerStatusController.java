package com.mygame.entity.player;

public class PlayerStatusController {
    private float hunger = 100f;        // 0..100
    private float thirst = 100f;        // 0..100
    private float drainPerSecond = 0.5f;


    public PlayerStatusController() {}

    public void update(float delta) {
        hunger -= drainPerSecond * delta;
        hunger = Math.max(hunger, 0);

        thirst -= drainPerSecond * delta;
        thirst = Math.max(thirst, 0);
    }

    public void eat(float amount) {
        hunger = Math.min(100, hunger + amount);
    }

    public void drink(float amount) {
        thirst = Math.min(100, thirst + amount);
    }

    public boolean isStarving() {
        return hunger <= 0;
    }

    public float getHunger() {
        return hunger;
    }

    public float getThirst() {
        return thirst;
    }
}
