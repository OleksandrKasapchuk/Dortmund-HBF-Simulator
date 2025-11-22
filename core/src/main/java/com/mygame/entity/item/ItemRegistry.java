package com.mygame.entity.item;


import com.mygame.managers.ManagerRegistry;

import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    private static final Map<String, ItemType> types = new HashMap<>();

    // --- викликається один раз при старті гри ---
    public static void init(ManagerRegistry managerRegistry) {

        register(new ItemType("money", "item.money.name", "item.money.description", null));
        register(new ItemType("grass", "item.grass.name", "item.grass.description", null));
        register(new ItemType("pape", "item.pape.name", "item.pape.description", null));
        register(new ItemType("joint", "item.joint.name", "item.joint.description", managerRegistry.getPlayerEffectManager()::applyJointEffect));
        register(new ItemType("ice_tea", "item.ice_tea.name", "item.ice_tea.description", managerRegistry.getPlayerEffectManager()::applyIceTeaEffect));
        register(new ItemType("spoon", "item.spoon.name", "item.spoon.description", null));
        register(new ItemType("pfand", "item.pfand.name", "item.pfand.description", null));
        register(new ItemType("bush", "item.bush.name", "item.bush.description", null));
        register(new ItemType("pfandAutomat", "item.pfandAutomat.name", "item.pfandAutomat.description", null));
        register(new ItemType("vape", "item.vape.name", "item.vape.description", null)); // Added vape
    }

    private static void register(ItemType type) {
        types.put(type.getKey(), type);
    }

    public static ItemType get(String key) {
        return types.get(key);
    }
}
