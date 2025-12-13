package com.mygame.dialogue.action;

import com.mygame.managers.global.save.SettingsManager;

public class CompleteEventAction implements DialogueAction {

    private final ActionContext ctx;
    private String event;

    public CompleteEventAction(ActionContext ctx, String event) {
        this.ctx = ctx;
        this.event = event;
    }

    @Override
    public void execute() {
        ctx.settings.completedDialogueEvents.add(event);
        SettingsManager.save(ctx.settings);
    }
}
