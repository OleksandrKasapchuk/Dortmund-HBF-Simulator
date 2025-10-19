package com.mygame;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;

public class DialogueManager {
    private float textTimer = 0f;
    private Label dialogueLabel;
    private float textSpeed = 0.05f;
    private NPC activeNpc = null;
    private Table dialogueTable;
    private Label nameLabel;


    public DialogueManager(Table dialogueTable, Label nameLabel, Label dialogueLabel) {
        this.dialogueTable = dialogueTable;
        this.nameLabel = nameLabel;
        this.dialogueLabel = dialogueLabel;
    }
    // --- Логіка діалогів ---
    public void update(float delta, ArrayList<NPC> npcs, Player player, boolean interactPressed) {
        // 1. Обробка натискання кнопки
        if (interactPressed) {
            if (activeNpc != null) {
                // Діалог вже початий: пропускаємо анімацію або переходимо до наступної фрази
                String fullText = activeNpc.getCurrentPhrase();
                int lettersToShow = (int)(textTimer / textSpeed);

                if (lettersToShow < fullText.length()) {
                    // Анімація ще триває -> пропускаємо її
                    textTimer = fullText.length() * textSpeed; // Показуємо весь текст
                } else {
                    // Анімація завершена -> переходимо до наступної фрази
                    activeNpc.advanceDialogue();
                    textTimer = 0f; // Скидаємо таймер для нової фрази
                }
            } else {
                // Діалог неактивний: шукаємо NPC поруч, щоб почати розмову
                for (NPC npc : npcs) {
                    if (npc.isPlayerNear(player)) {
                        activeNpc = npc;
                        textTimer = 0f;
                        break;
                    }
                }
            }
        }
        // 2. Якщо гравець відійшов від активного NPC, завершуємо діалог
        if (activeNpc != null && !activeNpc.isPlayerNear(player)) {
            activeNpc.resetDialogue();
            activeNpc = null;
        }
        // 3. Якщо є активний діалог, анімуємо текст
        if (activeNpc != null) {
            if (!activeNpc.isDialogueFinished()) {
                dialogueTable.setVisible(true);
                nameLabel.setText(activeNpc.getName());
                String fullText = activeNpc.getCurrentPhrase();

                textTimer += delta;
                int lettersToShow = (int)(textTimer / textSpeed);

                if (lettersToShow > fullText.length()) {
                    lettersToShow = fullText.length();
                }
                String currentText = fullText.substring(0, lettersToShow);
                dialogueLabel.setText(currentText);
            } else {
                activeNpc.resetDialogue();
                activeNpc = null;
            }
        } else {
            // Немає активного діалогу, ховаємо вікно
            dialogueTable.setVisible(false);
        }
    }
}
