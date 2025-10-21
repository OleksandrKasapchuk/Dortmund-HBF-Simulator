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
    public boolean removeItem(String itemName, int count) {
        if (!items.containsKey(itemName)) return false;

        int current = items.get(itemName);
        if (current <= count) {
            items.remove(itemName); // видаляємо весь стек
        } else {
            items.put(itemName, current - count);
        }
        return true;
    }


    // Перевірити наявність
    public boolean hasItem(String itemName) {return items.containsKey(itemName);}

    // Отримати кількість предмета
    public int getAmount(String itemName) {return items.getOrDefault(itemName, 0);}
    public Map<String, Integer> getItems() {return items;}
}
