package com.mygame.action.provider;

import com.mygame.action.ActionRegistry;
import com.mygame.game.GameContext;
import com.mygame.world.zone.PlaceZone;

public class QuestActionProvider implements ActionProvider {
    @Override
    public void provide(GameContext context, ActionRegistry registry) {
        registry.registerCreator("quest.start", (c, data) -> () -> c.questManager.startQuest(data.getString("id")));
        registry.registerCreator("quest.complete", (c, data) -> () -> c.questManager.completeQuest(data.getString("id")));
        registry.registerCreator("zone.set.enabled", (c, data) -> () -> c.zoneRegistry.getZone(data.getString("id")).setEnabled(data.getBoolean("enabled")));

        registry.registerCreator("zone.enable_next_available", (c, data) -> () -> {
            String prefix = data.getString("prefix");
            c.zoneRegistry.getZones().stream()
                    .filter(zone -> !zone.isEnabled() && zone.getId().startsWith(prefix) && (zone instanceof PlaceZone placeZone) && !placeZone.isOccupied())
                    .findFirst()
                    .ifPresent(zone -> zone.setEnabled(true));
        });
    }
}
