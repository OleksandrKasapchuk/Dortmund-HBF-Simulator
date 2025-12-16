package com.mygame.dialogue;

public class TypeWriterController {


    private static final float TEXT_SPEED = 0.05f;

    private float timer;

    public void reset() {
        timer = 0f;
    }

    public String update(String fullText, float delta) {
        timer += delta;
        int letters = (int) (timer / TEXT_SPEED);
        return letters >= fullText.length()
            ? fullText
            : fullText.substring(0, letters);
    }

    public boolean isFinished(String fullText) {
        return (int)(timer / TEXT_SPEED) >= fullText.length();
    }
}
