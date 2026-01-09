package com.mygame.game;


import com.mygame.events.EventBus;
import com.mygame.events.Events;

public class DayManager {
    private final float DAY_LENGTH = 24;
    private int day = 1;
    public enum Phase { MORNING, DAY, EVENING, NIGHT }
    private Phase currentPhase;
    private float currentTime;

    public DayManager(){
        this.currentPhase = Phase.MORNING;
        this.currentTime = 6;
    }


    public void update(float delta) {
        currentTime += delta * 6; // баланс

        if (currentTime >= DAY_LENGTH) {
            nextDay();
        }

        updatePhase();
    }

    private void nextDay() {
        currentTime = 0f;
        day++;
        EventBus.fire(new Events.NewDayEvent(day));
        System.out.println("New day: " + day);
    }

    private void updatePhase() {
        Phase newPhase =
            currentTime < 6  ? Phase.NIGHT :
            currentTime < 12 ? Phase.MORNING :
            currentTime < 18 ? Phase.DAY : Phase.EVENING;

        if (newPhase != currentPhase) {
            currentPhase = newPhase;
            System.out.println("New phase: " + newPhase);
        }
    }

    public void sleep() {
        currentTime = 6f;
        nextDay();
    }

    public Phase getCurrentPhase(){ return currentPhase; }
    public int getDay(){ return day; }
}
