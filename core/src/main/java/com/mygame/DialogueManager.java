package com.mygame;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;

public class DialogueManager {
    private float textTimer = 0f;
    private final Label dialogueLabel;
    private final float textSpeed = 0.05f;
    private NPC activeNpc = null;
    private final Table dialogueTable;
    private final Label nameLabel;
    private NPC recentlyFinishedForcedNpc = null; // NPC, з яким щойно завершився примусовий діалог


    public DialogueManager(Table dialogueTable, Label nameLabel, Label dialogueLabel) {
        this.dialogueTable = dialogueTable;
        this.nameLabel = nameLabel;
        this.dialogueLabel = dialogueLabel;
    }
    // --- Логіка діалогів ---
    public void update(float delta, ArrayList<NPC> npcs, Player player, boolean interactPressed) {
        // --- Скидання "імунітету", якщо гравець відійшов від поліцейського ---
        if (recentlyFinishedForcedNpc != null && !recentlyFinishedForcedNpc.isPlayerNear(player)) {
            recentlyFinishedForcedNpc = null;
        }

        // --- Примусовий діалог (для поліції) ---
        if (activeNpc == null) { // Перевіряємо, тільки якщо немає активного діалогу
            for (NPC npc : npcs) {
                // Починаємо діалог, тільки якщо це поліцейський і ми не щойно закінчили з ним розмову
                if (npc.getName().equals("Police") && npc.isPlayerNear(player) && npc != recentlyFinishedForcedNpc) {
                    activeNpc = npc; // Примусово починаємо діалог
                    textTimer = 0f;
                    break; // Виходимо, щоб не перевіряти інших
                }
            }
        }

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
                if (activeNpc.getName().equals("Police")) {
                    player.setMovementLocked(true);
                }
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
                activeNpc.runAction();
                activeNpc.resetDialogue();

                // Позначаємо, що ми щойно закінчили примусовий діалог
                if (activeNpc.getName().equals("Police")) {
                    recentlyFinishedForcedNpc = activeNpc;
                    player.setMovementLocked(false);
                }

                activeNpc = null;
            }
        } else {
            // Немає активного діалогу, ховаємо вікно
            dialogueTable.setVisible(false);
        }
    }
}
