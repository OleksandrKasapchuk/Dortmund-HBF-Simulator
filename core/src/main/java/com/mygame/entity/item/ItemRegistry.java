package com.mygame.entity.item;


import com.mygame.managers.ManagerRegistry;

import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    private static final Map<String, ItemType> types = new HashMap<>();

    // --- викликається один раз при старті гри ---
    public static void init(ManagerRegistry managerRegistry) {

        register(new ItemType("item.money.name", "item.money.description", null));
        register(new ItemType("item.grass.name", "item.grass.description", null));
        register(new ItemType("item.pape.name", "item.pape.description", null));
        register(new ItemType("item.joint.name", "item.joint.description", managerRegistry.getPlayerEffectManager()::applyJointEffect));
        register(new ItemType("item.ice_tea.name", "item.ice_tea.description", managerRegistry.getPlayerEffectManager()::applyIceTeaEffect));
        register(new ItemType("item.spoon.name", "item.spoon.description", null));
        register(new ItemType("item.pfand.name", "item.pfand.description", null));
        register(new ItemType("item.bush.name", "item.bush.description", null));
        register(new ItemType("item.pfandAutomat.name", "item.pfandAutomat.description", null));
        register(new ItemType("item.vape.name", "item.vape.description", null)); // Added vape
    }

    private static void register(ItemType type) {
        types.put(type.getName(), type);
    }

    public static ItemType get(String name) {
        return types.get(name);
    }
}
