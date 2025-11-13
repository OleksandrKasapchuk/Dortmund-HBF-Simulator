package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public abstract class Screen {
    private Stage stage = new Stage(new FitViewport(2000, 1000));
    public Stage getStage() { return stage; }
    public void dispose() {stage.dispose();}
}
