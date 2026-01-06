package com.mygame.scenario;

import com.mygame.game.GameContext;

public class ScenarioController {
    private BossDeliveryScenario bossSe;
    private PoliceChaseScenario policeSe;

    public ScenarioController(GameContext ctx){
        bossSe = new BossDeliveryScenario(ctx);
        bossSe.init();

        policeSe = new PoliceChaseScenario(ctx);
        policeSe.init();

    }

    public void update(){
        if (policeSe != null) policeSe.update();
    }
}
