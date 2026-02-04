package com.mygame.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygame.entity.item.Item;
import com.mygame.entity.item.ItemManager;
import com.mygame.entity.npc.NpcManager;
import com.mygame.entity.player.Player;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.DayManager;
import com.mygame.quest.QuestManager;
import com.mygame.world.WorldManager;

public class InteractionManager {
    private final SpriteBatch batch;
    private final Player player;
    private final QuestManager questManager;
    private final WorldManager worldManager;
    private final NpcManager npcManager;
    private final ItemManager itemManager;


    public InteractionManager(SpriteBatch batch, Player player, QuestManager questManager, WorldManager worldManager, NpcManager npcManager, ItemManager itemManager) {
        this.batch = batch;
        this.player = player;
        this.questManager = questManager;
        this.worldManager = worldManager;
        this.npcManager = npcManager;
        this.itemManager = itemManager;

        EventBus.subscribe(Events.InteractEvent.class, e -> handleInteraction());
    }

    private void handleInteraction() {
        for (Item item : itemManager.getAllItems()) {
            if (item.getWorld() != worldManager.getCurrentWorld()) continue;

            boolean nearPlayer = item.isPlayerNear(player, item.getDistance());
            boolean questBlocked = item.getQuestId() != null && !questManager.hasQuest(item.getQuestId());

            boolean hasSearchData =
                item.getSearchData() != null && !item.getSearchData().isSearched();

            // ті самі умови, що і в render
            if (questBlocked || !nearPlayer || !item.isInteractable()) continue;

            // 🔥 пріоритет: search > interact
            EventBus.fire(new Events.ItemInteractionEvent(item, player));
            return; // тільки один item за натиск
        }
    }
}
