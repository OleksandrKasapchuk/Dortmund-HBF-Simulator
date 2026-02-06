package com.mygame.entity.player;

public class PlayerStatusController {
    private float hunger = 100f;        // 0..100
    private float thirst = 100f;        // 0..100
    private float vibe = 100f;        // 0..100
    private float hungerDrainPerSecond = 0.05f;
    private float thirstDrainPerSecond = 0.075f;
    private float vibeDrainPerSecond = 0.05f;


    public PlayerStatusController() {}

    public void update(float delta) {
        hunger = drain(hunger, hungerDrainPerSecond, delta);
        thirst = drain(thirst, thirstDrainPerSecond, delta);
        vibe   = drain(vibe, vibeDrainPerSecond, delta);
    }


    private float drain(float value, float drainPerSecond, float delta) {
        return clamp(value - drainPerSecond * delta);
    }

    private float clamp(float value) {
        return Math.max(0, Math.min(100, value));
    }


    public void eat(float amount) {
        if (amount < 0) return;
        hunger = Math.min(100, hunger + amount);
    }

    public void drink(float amount) {
        if (amount < 0) return;
        thirst = Math.min(100, thirst + amount);
    }

    public float getVibe() { return vibe; }
    public float getHunger() {return hunger;}
    public float getThirst() {return thirst;}

    public void addVibe(float amount) {
        if (amount < 0) return;
        vibe = Math.min(100, vibe + amount);
    }


    public boolean isStarving() {return hunger <= 0;}


    public void setHunger(float hunger) {this.hunger = hunger;}
    public void setThirst(float thirst) {this.thirst = thirst;}
    public void setVibe(float vibe) {this.vibe = vibe;}
}
