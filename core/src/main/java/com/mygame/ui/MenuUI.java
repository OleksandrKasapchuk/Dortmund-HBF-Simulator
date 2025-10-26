package com.mygame.ui;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MenuUI {
    private Stage stage;
    private final Label menuLabel1;
    private final Label menuLabel2;

    public MenuUI(Skin skin){
        stage = new Stage(new FitViewport(2000, 1000));
        menuLabel1 = new Label("DORTMUND HBF SIMULATOR", skin);
        menuLabel1.setPosition(700, 600);
        menuLabel1.setFontScale(4f);
        stage.addActor(menuLabel1);

        menuLabel2 = new Label("PRESS ENTER TO START", skin);
        menuLabel2.setPosition(710, 500);
        menuLabel2.setFontScale(4f);
        stage.addActor(menuLabel2);
    }
    public Stage getStage() { return stage; }
    public void dispose() {stage.dispose();}
}
