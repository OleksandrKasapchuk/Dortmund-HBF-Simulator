package com.mygame.game.save;

import com.mygame.game.GameContext;
import com.mygame.quest.QuestManager;
import com.mygame.world.World;

public class DataLoader {

    public static void load(GameContext ctx, GameSettings settings){
        loadDayData(ctx, settings);
        loadFromMap(ctx, settings);
        loadInventoryAndQuests(ctx, settings);
        loadCreatedItems(ctx, settings);
    }

    private static void loadCreatedItems(GameContext ctx, GameSettings settings) {
        if (settings.createdItems != null) {
            for (GameSettings.ItemSaveData itemSaveData : settings.createdItems) {
                World world = ctx.worldManager.getWorld(itemSaveData.worldName);
                if (world != null) {
                    ctx.itemManager.createItem(itemSaveData.itemKey, itemSaveData.x, itemSaveData.y, world);
                }
            }
        }
    }

    private static void loadDayData(GameContext ctx, GameSettings settings){
        boolean isNewGame = settings.currentDay <= 0;
        ctx.dayManager.setDay(isNewGame ? 1 : settings.currentDay);
        ctx.dayManager.setTime(isNewGame ? 6f : settings.currentTime);
    }

    private static void loadFromMap(GameContext ctx, GameSettings settings){
        for (World world : ctx.worldManager.getWorlds().values()) {
            ctx.npcManager.loadNpcsFromMap(world, settings.npcStates);
            ctx.itemManager.loadItemsFromMap(world);
            ctx.zoneRegistry.loadZonesFromMap(world);
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
