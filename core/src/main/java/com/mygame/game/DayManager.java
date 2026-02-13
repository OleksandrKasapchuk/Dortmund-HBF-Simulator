package com.mygame.game;


import com.mygame.assets.Assets;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.managers.TimerManager;

public class DayManager {
    private final float DAY_LENGTH = 24;
    private int day;

    public enum Phase {
        MORNING("phase.morning"),
        DAY("phase.day"),
        EVENING("phase.evening"),
        NIGHT("phase.night");

        private final String localizationKey;

        Phase(String localizationKey) {
            this.localizationKey = localizationKey;
        }
        public String getLocalizationKey() {
            return localizationKey;
        }

    }
    private Phase currentPhase;
    private float currentTime;


    public DayManager(){
        this.currentPhase = Phase.MORNING;
        this.currentTime = 6f;
    }

    public void update(float delta) {
        currentTime += delta * 0.1f; // баланс

        if (currentTime >= DAY_LENGTH) {
            nextDay();
        }

        updatePhase();
    }

    private void nextDay() {
        currentTime = 0f;
        setDay(++day);
    }

    public void setTime(float time){ currentTime = time; }
    public void setDay(int day){
        this.day = day;
        EventBus.fire(new Events.NewDayEvent(day));
        EventBus.fire(new Events.SaveRequestEvent());
    }


    private void updatePhase() {
        Phase newPhase =
            currentTime < 6  ? Phase.NIGHT :
            currentTime < 12 ? Phase.MORNING :
            currentTime < 18 ? Phase.DAY : Phase.EVENING;

        if (newPhase != currentPhase) {
            currentPhase = newPhase;
            EventBus.fire(new Events.PhaseChangedEvent(newPhase));
        }
    }

    public void sleep() {
        if(currentPhase == Phase.MORNING || currentPhase == Phase.DAY) {
            EventBus.fire(new Events.MessageEvent(Assets.messages.get("message.sleep.not_allowed")));
            return;
        }
        EventBus.fire(new Events.OverlayEvent(0.5f, true));
        TimerManager.setAction(() -> {
            if (currentPhase == Phase.EVENING) nextDay();
            currentTime = 6f;
        }, 0.5f);

    }

    public float getCurrentTime(){ return currentTime; }
    public Phase getCurrentPhase(){ return currentPhase; }
    public int getDay(){ return day; }
}
