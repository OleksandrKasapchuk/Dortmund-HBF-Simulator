package com.mygame.scenario;

import com.mygame.game.GameContext;

public class ScenarioController {
    private PfandAutomatScenario pfAe;
    private BossDeliveryScenario bossSe;
    private PoliceChaseScenario policeSe;
    private GrassSeedTableScenario grassSe;

    public void init(GameContext ctx){
        bossSe = new BossDeliveryScenario(ctx);
        bossSe.init();

        pfAe = new PfandAutomatScenario(ctx);
        pfAe.init();

        policeSe = new PoliceChaseScenario(ctx);
        policeSe.init();

        grassSe = new GrassSeedTableScenario(ctx);
    }

    public void update(){
        if (pfAe != null) pfAe.update();
        if (bossSe != null) bossSe.update();
        if (policeSe != null) policeSe.update();
        if (grassSe != null) grassSe.update();
    }

    public void draw() {
        if (pfAe != null) pfAe.draw();
        if (bossSe != null) bossSe.draw();
        if (grassSe != null) grassSe.draw();
    }
}
