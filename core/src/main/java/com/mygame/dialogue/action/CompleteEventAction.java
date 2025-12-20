package com.mygame.dialogue.action;

import com.mygame.game.save.SettingsManager;

public class CompleteEventAction implements DialogueAction {

    private final ActionContext ctx;
    private String event;

    public CompleteEventAction(ActionContext ctx, String event) {
        this.ctx = ctx;
        this.event = event;
    }

    @Override
    public void execute() {
        ctx.getSettings().completedDialogueEvents.add(event);
        SettingsManager.save(ctx.getSettings());
    }
}
