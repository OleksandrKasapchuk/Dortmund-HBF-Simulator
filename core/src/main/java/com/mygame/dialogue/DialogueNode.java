package com.mygame.dialogue;

import com.mygame.assets.Assets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DialogueNode — один екран діалогу.
 * Містить:
 *   • список фраз (текст, який друкується по черзі)
 *   • список choices (варіантів відповіді)
 *   • action, який виконується коли цей вузол був показаний або коли закінчився
 * Важливо:
 *   choose(choice) тепер ПОВЕРТАЄ nextNode!
 */
public class DialogueNode {

    /**
     * @param text     що показувати гравцю
     * @param nextNode куди переходимо
     * @param action   що виконати при виборі
     */
    public record Choice(String text, DialogueNode nextNode, Runnable action) {}

    private final List<String> texts;   // фрази вузла
    private final List<Choice> choices; // варіанти відповіді
    private final String action;      // виконується коли вузол завершується
    private boolean isForced;
    private String nextNode; // наступна нода після завершення поточної
    public DialogueNode(String... textKeys) {
        this(null, false,null, textKeys);
    }

    public DialogueNode(String onFinish, boolean isForced, String nextNode, String... textKeys) {
        this.action = onFinish;
        this.texts = (textKeys == null || textKeys.length == 0) ?
                List.of("") :
                Arrays.stream(textKeys).map(key -> {
                    try {
                        return Assets.dialogues.get(key);
                    } catch (java.util.MissingResourceException e) {
                        System.err.println("DialogueNode: Missing bundle key: \"" + key + "\"");
                        return key;
                    }
                }).collect(Collectors.toList());
        this.choices = new ArrayList<>();
        this.isForced = isForced;
        this.nextNode = nextNode;
    }

    // Новий сеттер
    public void setNextNode(String next) {
        this.nextNode = next;
    }

    // Новий геттер
    public String getNextNode() {
        return nextNode;
    }
    public void addChoice(String text, DialogueNode next, Runnable action) {
        choices.add(new Choice(text, next, action));
    }

    public List<String> getTexts() { return texts; }
    public List<Choice> getChoices() { return choices; }
    public String getAction() { return action; }
    public boolean isForced(){ return isForced; }
}
