package com.mygame.ui.screenUI;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.mygame.entity.Player;

public class GameUI extends Screen {
    private final Label moneyLabel;
    private final Label infoLabel;
    private float infoMessageTimer = 0f;

    public GameUI(Skin skin, Player player){
        Stage stage = getStage();

        moneyLabel = new Label("Money: " + player.getMoney(), skin);
        moneyLabel.setPosition(1700, 925);
        moneyLabel.setFontScale(3f);
        stage.addActor(moneyLabel);

        infoLabel = new Label("", skin);
        infoLabel.setColor(Color.GOLD);
        infoLabel.setAlignment(Align.center);
        infoLabel.setFontScale(4f);
        infoLabel.setPosition(stage.getViewport().getWorldWidth() / 2f, 850, Align.center);
        stage.addActor(infoLabel);
        infoLabel.setVisible(false);
    }
    public void updateMoney(int money) {moneyLabel.setText("Money: " + money);}

    public void showInfoMessage(String message, float duration) {
        infoLabel.setText(message);
        infoLabel.setVisible(true);
        infoMessageTimer = duration;
    }
    public void update(float delta) {
        if (infoMessageTimer > 0) {
            infoMessageTimer -= delta;
            if (infoMessageTimer <= 0) infoLabel.setVisible(false);
        }
    }
}
