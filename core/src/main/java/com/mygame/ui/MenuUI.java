package com.mygame.ui;


import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygame.Assets;

public class MenuUI {
    private Stage stage;
    private final Label menuLabel2;
    private Image backgroundImage;

    public MenuUI(Skin skin){
        stage = new Stage(new FitViewport(2000, 1000));

        backgroundImage = new Image(Assets.menuBack);
        backgroundImage.setFillParent(true);

        stage.addActor(backgroundImage);

        menuLabel2 = new Label("PRESS ENTER TO START", skin);
        menuLabel2.setPosition(710, 750);
        menuLabel2.setFontScale(4f);
        stage.addActor(menuLabel2);
    }
    public Stage getStage() { return stage; }
    public void dispose() {stage.dispose();}
}
