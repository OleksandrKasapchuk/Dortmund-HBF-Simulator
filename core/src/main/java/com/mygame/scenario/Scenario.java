package com.mygame.scenario;

public interface Scenario {
    void init();
    default void update() {}
}
