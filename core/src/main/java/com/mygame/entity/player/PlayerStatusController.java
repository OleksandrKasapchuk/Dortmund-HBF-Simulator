package com.mygame.entity.player;

public class PlayerStatusController {
    private float hunger = 100f;        // 0..100
    private float thirst = 100f;        // 0..100
    private float hungerDrainPerSecond = 0.05f;
    private float thirstDrainPerSecond = 0.075f;


    public PlayerStatusController() {}

    public void update(float delta) {
        hunger -= hungerDrainPerSecond * delta;
        hunger = Math.max(hunger, 0);

        thirst -= thirstDrainPerSecond * delta;
        thirst = Math.max(thirst, 0);
    }

    public void eat(float amount) { hunger = Math.min(100, hunger + amount); }

    public void drink(float amount) { thirst = Math.min(100, thirst + amount); }

    public boolean isStarving() {return hunger <= 0;}

    public int getHunger() {return (int) hunger;}

    public int getThirst() {return (int) thirst;}
}
