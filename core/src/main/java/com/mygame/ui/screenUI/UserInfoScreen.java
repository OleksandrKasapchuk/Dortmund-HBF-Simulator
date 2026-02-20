package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.mygame.assets.Assets;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.auth.AuthManager;

public class UserInfoScreen extends Screen {


    public UserInfoScreen(Skin skin){
        super();

        // --- TOP BAR ---
        Table topBar = new Table();

        TextButton backBtn = createButton(skin, Assets.ui.get("button.back.text"), 1.8f, () -> EventBus.fire(new Events.ActionRequestEvent("act.system.account")));
        Label titleLabel = createLabel(skin, Assets.ui.get("ui.account.title"), 3f);


        topBar.add(backBtn).left().pad(50);
        topBar.add(titleLabel).center().expandX();
        topBar.add().width(backBtn.getPrefWidth());

        // --- USER INFO ---
        Table userTable = new Table();
        userTable.left().padLeft(40).padTop(30);

        Label usernameLabel = createLabel(skin, Assets.ui.get("ui.auth.username") + ": " + AuthManager.getUsername(), 1.5f);
        userTable.add(usernameLabel).padBottom(50).row();

        userTable.add(usernameLabel).left().row();

        // --- ROOT LAYOUT ---
        root.top().left();
        root.add(topBar).expandX().fillX().row();
        root.add(userTable).expand().top().left();
    }
}
