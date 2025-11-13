package com.mygame.managers;


public class TimerManager {
    public static void setAction(Runnable action ,float time){
        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                action.run();
            }
        }, time);
    }
}
