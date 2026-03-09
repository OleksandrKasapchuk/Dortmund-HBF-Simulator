package com.mygame.entity;

import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.quest.QuestManager;
import com.mygame.world.WorldManager;

public class InteractionManager {
    private final Player player;
    private final QuestManager questManager;
    private final WorldManager worldManager;
    private final ItemManager itemManager;

    public InteractionManager(Player player, QuestManager questManager, WorldManager worldManager, ItemManager itemManager) {
        this.player = player;
        this.questManager = questManager;
        this.worldManager = worldManager;
        this.itemManager = itemManager;

        EventBus.subscribe(Events.InteractEvent.class, e -> handleInteraction());
    }

    private void handleInteraction() {
        for (Item item : itemManager.getAllItems()) {
            if (item.getWorld() != worldManager.getCurrentWorld()) continue;

            boolean nearPlayer = item.isPlayerNear(player, item.getDistance());
            boolean questBlocked = item.getQuestId() != null && !questManager.hasQuest(item.getQuestId());

            // ті самі умови, що і в render
            if (questBlocked || !nearPlayer || !item.isInteractable()) continue;

            // 🔥 пріоритет: search > interact
            EventBus.fire(new Events.ItemInteractionEvent(item, player));
            return; // тільки один item за натиск
        }
    }
}
