package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LoadingServerScreen extends Screen {

    private Label statusLabel;

    public LoadingServerScreen(Skin skin) {
        super();
        statusLabel = createLabel(skin, "Connecting to server...", 2f);
        root.add(statusLabel);
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }
}
