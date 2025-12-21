package com.mygame.dialogue.action;

import com.mygame.game.GameContext;
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
        ctx.getSettings().completedDialogueEvents.add(event);
        SettingsManager.save(ctx.getSettings());
    }
}
