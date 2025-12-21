package com.mygame.scenario;


import com.mygame.game.GameContext;


public class ScenarioController {
    public static void init(GameContext ctx){
        new BossDeliveryScenario(ctx).init();
        new PfandAutomatScenario(ctx).init();
        new PoliceChaseScenario(ctx).init();
    }
}
