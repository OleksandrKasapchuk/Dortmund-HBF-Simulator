package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygame.assets.Assets;
import com.mygame.events.EventBus;
import com.mygame.events.Events;

/**
 * Адаптивний екран паузи.
 */
public class PauseScreen extends Screen {

    public PauseScreen(Skin skin) {
        super(); // Ініціалізує root table

        // Заголовок
        Label title = createLabel(skin, Assets.ui.get("pause.title"), 3f);
        root.add(title).padBottom(100).row();

        // Основне меню кнопок
        Table menu = new Table();

        TextButton resumeBtn = createButton(skin, Assets.ui.get("button.resume.text"), 1.8f, () -> EventBus.fire(new Events.ActionRequestEvent("system.pause")));
        TextButton settingsBtn = createButton(skin, Assets.ui.get("settings.title"), 1.8f, () -> EventBus.fire(new Events.ActionRequestEvent("system.settings")));

        TextButton menuBtn = createButton(skin, Assets.ui.get("button.exit.text"), 1.8f, () -> EventBus.fire(new Events.ActionRequestEvent("system.menu")));

        menu.add(resumeBtn).width(500).height(100).padBottom(30).row();
        menu.add(settingsBtn).width(500).height(100).padBottom(30).row();
        menu.add(menuBtn).width(500).height(100).row();

        root.add(menu).center().row();
    }
}
