package com.mygame.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventBus {
    private static final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

    public static <T> void subscribe(Class<T> eventClass, Consumer<T> listener) {
        listeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(listener);
    }
    @SuppressWarnings("unchecked")
    public static <T> void fire(T event) {
        List<Consumer<?>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            // Копіюємо список, щоб уникнути ConcurrentModificationException
            for (Consumer<?> listener : new ArrayList<>(eventListeners)) {
                ((Consumer<T>) listener).accept(event);
            }
        }
    }

    public static void clear() {
        listeners.clear();
    }
}
