package com.mygame.entity.item;


import com.mygame.managers.ManagerRegistry;

import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    private static final Map<String, ItemType> types = new HashMap<>();

    // --- викликається один раз при старті гри ---
    public static void init(ManagerRegistry managerRegistry) {

        register(new ItemType("money",  "With this you can buy everything", null));
        register(new ItemType("grass",  "The most needed thing in this world", null));
        register(new ItemType("pape", "Is needed for making a joint", null));
        register(new ItemType("joint", "Very useful thing, makes you stoned", managerRegistry.getPlayerEffectManager()::applyJointEffect));
        register(new ItemType("ice tea", "Nice and tasty tea, sets your status back to normal while stoned", managerRegistry.getPlayerEffectManager()::applyIceTeaEffect));
        register(new ItemType("spoon", "Junky needs it", null));
        register(new ItemType("pfand",  "Just a bottle, you can get money for it at pfand automat.", null));
        register(new ItemType("bush",  "Just a bush", null));
        register(new ItemType("pfandAutomat", "Exchange machine", null));
    }

    private static void register(ItemType type) {
        types.put(type.getName(), type);
    }

    public static ItemType get(String name) {
        return types.get(name);
    }
}
