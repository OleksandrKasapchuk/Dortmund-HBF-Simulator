package com.mygame.scenario;

import com.mygame.game.GameContext;

public class ScenarioController {
    private PfandAutomatScenario pfAe;
    private BossDeliveryScenario bossSe;
    private PoliceChaseScenario policeSe;

    public void init(GameContext ctx){
        bossSe = new BossDeliveryScenario(ctx);
        bossSe.init();

        pfAe = new PfandAutomatScenario(ctx);
        pfAe.init();

        policeSe = new PoliceChaseScenario(ctx);
        policeSe.init();
    }

    public void update(){
        if (pfAe != null) pfAe.update();
        if (bossSe != null) bossSe.update();
        if (policeSe != null) policeSe.update();
    }

    public void draw() {
        if (pfAe != null) pfAe.draw();
        if (bossSe != null) bossSe.draw();
    }
}
