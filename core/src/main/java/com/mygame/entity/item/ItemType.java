package com.mygame.entity.item;


public class ItemType {
    private String name;          // відображуване ім’я
    private String description;
    private Runnable effect;

    public ItemType(String name, String description, Runnable effect) {
        this.name = name;
        this.description = description;
        this.effect = effect;

    }

    // --- геттери ---
    public String getName() { return name; }
    public String getDescription() { return description; }

    public void apply(){effect.run();}
    public boolean isUsable(){return effect!=null;}
}
