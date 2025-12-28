package com.mygame.scenario;

import com.mygame.game.GameContext;

public class ScenarioController {
    private BossDeliveryScenario bossSe;
    private PoliceChaseScenario policeSe;

    public void init(GameContext ctx){
        bossSe = new BossDeliveryScenario();
        bossSe.init();

        policeSe = new PoliceChaseScenario(ctx);
        policeSe.init();

    }

    public void update(){
        if (policeSe != null) policeSe.update();
    }
}
