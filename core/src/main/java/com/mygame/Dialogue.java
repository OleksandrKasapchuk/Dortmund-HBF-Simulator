package com.mygame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dialogue {
    private final DialogueNode startNode;
    private DialogueNode currentNode;

    public Dialogue(DialogueNode startNode) {
        this.startNode = startNode;
        this.currentNode = startNode;
    }

    public DialogueNode getCurrentNode() {
        return currentNode;
    }

    public void choose(DialogueNode.Choice choice) {
        // Виконуємо дію, якщо вона є
        if (choice.action != null) {
            choice.action.run();
        }

        // Переходимо до наступного вузла, якщо він є
        if (choice.nextNode != null) {
            currentNode = choice.nextNode;
        } else {
            // Якщо наступного вузла немає, діалог міг завершитись.
            // Якщо вибір не мав дії, і не мав наступного вузла, можливо, варто скинути діалог?
            // Поки що, якщо немає nextNode, діалог просто залишається на поточному вузлі
            // або завершується, якщо була дія. Це буде контролювати DialogueManager.
        }
    }

    /**
     * Скидає діалог до початкового стану.
     */
    public void reset() {
        this.currentNode = startNode;
    }
}
