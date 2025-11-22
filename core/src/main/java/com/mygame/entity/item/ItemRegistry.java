package com.mygame.entity.item;


import com.mygame.managers.ManagerRegistry;

import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    private static final Map<String, ItemType> types = new HashMap<>();

    // --- викликається один раз при старті гри ---
    public static void init(ManagerRegistry managerRegistry) {

        register("money", new ItemType("item.money.name", "item.money.description", null));
        register("grass", new ItemType("item.grass.name", "item.grass.description", null));
        register("pape", new ItemType("item.pape.name", "item.pape.description", null));
        register("joint", new ItemType("item.joint.name", "item.joint.description", managerRegistry.getPlayerEffectManager()::applyJointEffect));
        register("ice_tea", new ItemType("item.ice_tea.name", "item.ice_tea.description", managerRegistry.getPlayerEffectManager()::applyIceTeaEffect));
        register("spoon", new ItemType("item.spoon.name", "item.spoon.description", null));
        register("pfand", new ItemType("item.pfand.name", "item.pfand.description", null));
        register("bush", new ItemType("item.bush.name", "item.bush.description", null));
        register("pfandAutomat", new ItemType("item.pfandAutomat.name", "item.pfandAutomat.description", null));
        register("vape", new ItemType("item.vape.name", "item.vape.description", null)); // Added vape
    }

    private static void register(String key, ItemType type) {
        types.put(key, type);
    }

    public static ItemType get(String key) {
        return types.get(key);
    }
}
