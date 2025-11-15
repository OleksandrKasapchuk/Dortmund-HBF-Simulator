package com.mygame.managers.global;

import com.badlogic.gdx.utils.Timer;

/**
 * TimerManager allows scheduling delayed actions in the game.
 * It uses LibGDX's Timer under the hood to run a Runnable after a specified delay.
 */
public class TimerManager {

    /**
     * Schedules a Runnable to be executed after a given delay in seconds.
     *
     * @param action The Runnable action to execute
     * @param time   Delay in seconds before executing the action
     */
    public static void setAction(Runnable action, float time) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                action.run();
            }
        }, time);
    }
}
