package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygame.assets.Assets;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameStateManager;
import com.mygame.game.auth.AuthManager;

/**
 * Адаптивне головне меню.
 */
public class MenuScreen extends Screen {

    public MenuScreen(Skin skin, GameStateManager gsm) {
        super();

        // Фонове зображення
        Image backgroundImage = new Image(Assets.getTexture("menuBack"));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        backgroundImage.toBack();

        // Таблиця для кнопок
        Table menuTable = new Table();

        TextButton startBtn = createButton(skin, Assets.ui.get("button.start.text"), 1.8f, () -> EventBus.fire(new Events.ActionRequestEvent("act.system.start")));
        TextButton newGameBtn = createButton(skin, Assets.ui.get("button.newGame.text"), 1.8f, () -> EventBus.fire(new Events.ActionRequestEvent("act.system.newGame")));
        TextButton accountBtn = createButton(skin, Assets.ui.get("button.account.text"), 1.8f, () -> EventBus.fire(new Events.ActionRequestEvent("act.system.account")));

        TextButton logoutBtn = createButton(skin, "Logout", 1.8f, () -> {
            AuthManager.logout();
            gsm.setState(GameStateManager.GameState.AUTH);
        });

        menuTable.add(startBtn).width(450).height(100).padBottom(20).row();
        menuTable.add(newGameBtn).width(450).height(100).padBottom(20).row();
        menuTable.add(accountBtn).width(450).height(100).padBottom(20).row();
        menuTable.add(logoutBtn).width(450).height(100).row();

        // Розміщуємо таблицю по центру екрану (або знизу, як вам подобається)
        root.add(menuTable).expand().bottom().left().padBottom(50).padLeft(150);
    }
}
