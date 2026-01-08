package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygame.assets.Assets;
import com.mygame.events.EventBus;
import com.mygame.events.Events;

/**
 * Адаптивне головне меню.
 */
public class MenuScreen extends Screen {

    public MenuScreen(Skin skin) {
        super();

        // Фонове зображення
        Image backgroundImage = new Image(Assets.getTexture("menuBack"));
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
        backgroundImage.toBack();

        // Таблиця для кнопок
        Table menuTable = new Table();

        TextButton startBtn = createButton(skin, Assets.ui.get("button.start.text"), 1.8f, () -> EventBus.fire(new Events.ActionRequestEvent("system.start")));
        TextButton newGameBtn = createButton(skin, Assets.ui.get("button.newGame.text"), 1.8f, () -> EventBus.fire(new Events.ActionRequestEvent("system.newGame")));

        menuTable.add(startBtn).width(500).height(120).padBottom(30).row();
        menuTable.add(newGameBtn).width(500).height(120).row();

        // Розміщуємо таблицю по центру екрану (або знизу, як вам подобається)
        root.add(menuTable).expand().bottom().padBottom(100);
    }
}
