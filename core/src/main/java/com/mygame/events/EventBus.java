package com.mygame.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {
    private static final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

    public static <T> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public static <T> void unsubscribe(Class<T> eventType, Consumer<T> listener) {
        List<Consumer<?>> list = listeners.get(eventType);
        if (list != null) {
            list.remove(listener);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> void fire(T event) {
        List<Consumer<?>> list = listeners.get(event.getClass());
        if (list == null) return;

        // Use a copy to avoid ConcurrentModificationException if a listener unsubscribes during fire
        for (Consumer<?> l : new ArrayList<>(list)) {
            ((Consumer<T>) l).accept(event);
        }
    }
}
