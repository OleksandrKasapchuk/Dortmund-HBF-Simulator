package com.mygame.dialogue.action;

import com.mygame.game.GameContext;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;

public class CompleteEventAction implements DialogueAction {

    private final GameContext ctx;
    private String event;

    public CompleteEventAction(GameContext ctx, String event) {
        this.ctx = ctx;
        this.event = event;
    }

    @Override
    public void execute() {
        // Завантажуємо актуальні налаштування з файлу
        GameSettings settings = SettingsManager.load();

        // Додаємо подію, якщо її ще немає
        if (!settings.completedDialogueEvents.contains(event)) {
            settings.completedDialogueEvents.add(event);
            // Одразу зберігаємо зміни у файл
            SettingsManager.save(settings);
            System.out.println("EVENT COMPLETED AND SAVED: " + event);
        }
    }
}
