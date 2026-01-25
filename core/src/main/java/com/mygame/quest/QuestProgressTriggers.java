package com.mygame.quest;

import com.mygame.entity.item.ItemDefinition;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.npc.NPC;
import com.mygame.entity.npc.NpcManager;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.save.GameSettings;
import com.mygame.game.save.SettingsManager;
import com.mygame.world.World;
import com.mygame.world.WorldManager;

import java.util.Set;

/**
 * QuestProgressTriggers listens to game events and updates quest progress or status.
 */
public class QuestProgressTriggers {

    private final QuestManager questManager;
    private final ItemRegistry itemRegistry;
    private final NpcManager npcManager;
    private final WorldManager worldManager;

    private Set<String> talkedNpcs;
    private Set<String> visited;

    public QuestProgressTriggers(QuestManager questManager, ItemRegistry itemRegistry, NpcManager npcManager, WorldManager worldManager) {
        this.questManager = questManager;
        this.itemRegistry = itemRegistry;
        this.npcManager = npcManager;
        this.worldManager = worldManager;
        // Прогрес квестів
        GameSettings settings = SettingsManager.load();
        talkedNpcs = settings.talkedNpcs;
        visited = settings.visited;

        EventBus.subscribe(Events.DialogueFinishedEvent.class, event -> handleNpcDialogue(event.npcId()));
        EventBus.subscribe(Events.WorldChangedEvent.class, event -> handleWorldChange(event.newWorldId()));
        EventBus.subscribe(Events.InventoryChangedEvent.class, event -> handleInventoryQuest(event.item()));
        EventBus.subscribe(Events.CreateItemEvent.class, this::handleCreateItemQuest);

        // --- ОБРОБКА ЗАВЕРШЕННЯ КВЕСТУ ---
        EventBus.subscribe(Events.QuestCompletedEvent.class, event -> {
            QuestManager.Quest quest = questManager.getQuest(event.questId());
            if (quest != null && quest.getOnComplete() != null) {
                EventBus.fire(new Events.ActionRequestEvent(quest.getOnComplete()));
            }
        });
    }

    private void handleCreateItemQuest(Events.CreateItemEvent event){
        if ("firework".equals(event.itemKey())) progress("jan.firework.4");
    }
    private void handleInventoryQuest(ItemDefinition item) {
        if (item == itemRegistry.get("firework")) progress("jan.firework.1");
    }

    private void handleNpcDialogue(String npcId) {
        if (talkedNpcs.add(npcId)) progress("jason.smalltalk");
    }

    private void handleWorldChange(String worldId) {
        if (visited.add(worldId)) progress("jason.tour");

        NPC jan = npcManager.findNpcById("jan");
        World world = worldManager.getWorld("leopold");
        if (questManager.hasQuest("jan.firework.3") && jan.getWorld() != world){
            EventBus.fire(new Events.ActionRequestEvent("act.quest.jan.firework.2.active"));
        }
    }

    private void progress(String questKey) {
        QuestManager.Quest quest = questManager.getQuest(questKey);
        if (quest != null && quest.getStatus() == QuestManager.Status.ACTIVE) {
            quest.makeProgress();
        }
    }

    public Set<String> getTalkedNpcs() { return talkedNpcs; }
    public Set<String> getVisited() { return visited; }
}
