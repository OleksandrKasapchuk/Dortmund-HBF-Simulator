package com.mygame;

import java.util.LinkedHashMap;
import java.util.Map;

public class Inventory {
    private Map<String, Integer> items; // itemName -> quantity

    public Inventory() {
        items = new LinkedHashMap<>();
    }

    // Додати предмет
    public void addItem(String itemName, int amount) {
        items.put(itemName, items.getOrDefault(itemName, 0) + amount);
    }

    // Забрати предмет
    public boolean removeItem(String itemName, int amount) {
        if (items.containsKey(itemName)) {
            int current = items.get(itemName);
            if (current >= amount) {
                items.put(itemName, current - amount);
                if (items.get(itemName) == 0) {
                    items.remove(itemName);
                }
                return true; // вистачає
            }
        }
        return false; // не вистачає кількості або предмета
    }

    // Перевірити наявність
    public boolean hasItem(String itemName) {return items.containsKey(itemName);}

    // Отримати кількість предмета
    public int getAmount(String itemName) {return items.getOrDefault(itemName, 0);}
    public Map<String, Integer> getItems() {return items;}
}
