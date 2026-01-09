package com.mygame.game.save;

import com.mygame.game.GameContext;
import com.mygame.quest.QuestManager;
import com.mygame.world.World;

public class DataLoader {

    public static void load(GameContext ctx, GameSettings settings){
        loadDayData(ctx, settings);
        loadFromMap(ctx);
        loadInventoryAndQuests(ctx, settings);
    }

    private static void loadDayData(GameContext ctx, GameSettings settings){
        ctx.dayManager.setDay(settings.currentDay>0 ? settings.currentDay : 1);
        ctx.dayManager.setTime(settings.currentTime);
    }

    private static void loadFromMap(GameContext ctx){
        for (World world : ctx.worldManager.getWorlds().values()) {
            ctx.npcManager.loadNpcsFromMap(world);
            ctx.itemManager.loadItemsFromMap(world);
            ctx.transitionManager.loadTransitionsFromMap(world);
        }
    }

    private static void loadInventoryAndQuests(GameContext ctx, GameSettings settings){
        if (settings.inventory != null)
            settings.inventory.forEach((itemKey, amount) -> ctx.player.getInventory().addItem(ctx.itemRegistry.get(itemKey), amount));

        if (settings.activeQuests != null) {
            settings.activeQuests.forEach((key, saveData) -> {
                QuestManager.Quest q = ctx.questManager.getQuest(key);
                if (q != null) {
                    q.setStatus(saveData.status);
                    q.setProgress(saveData.progress);
                }
            });
        }
    }
}
