package com.mygame.ui.screenUI;


import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.Assets;

public class MenuUI extends Screen {
    private Image backgroundImage;

    public MenuUI(Skin skin){
        Stage stage = getStage();
        backgroundImage = new Image(Assets.menuBack);
        backgroundImage.setFillParent(true);

        stage.addActor(backgroundImage);
        if (Gdx.app.getType() != Application.ApplicationType.Android){
            Label menuLabel2 = new Label("PRESS ENTER TO START", skin);
            menuLabel2.setPosition(710, 750);
            menuLabel2.setFontScale(4f);
            stage.addActor(menuLabel2);
        }
    }
}
