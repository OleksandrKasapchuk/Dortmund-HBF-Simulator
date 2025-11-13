package com.mygame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygame.DialogueNode;

public class DialogueUI {
    private final Table dialogueTable;
    private final Label nameLabel;
    private final Label dialogueLabel;
    private final Table choiceTable;
    private final Skin skin;
    private final Texture dialogueBgTexture;

    public interface ChoiceListener { void onChoiceSelected(DialogueNode.Choice choice);}

    public DialogueUI(Skin skin, Stage stage, int width, int height, float x, float y) {
        this.skin = skin;

        Pixmap bg = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        bg.setColor(new Color(0.1f, 0.1f, 0.5f, 0.6f));
        bg.fill();
        dialogueBgTexture = new Texture(bg);
        bg.dispose();

        dialogueTable = new Table();
        dialogueTable.setSize(width, height);
        dialogueTable.setPosition(x, y);
        dialogueTable.setBackground(new TextureRegionDrawable(new TextureRegion(dialogueBgTexture)));
        dialogueTable.pad(20);

        nameLabel = new Label("", skin);
        nameLabel.setFontScale(3f);
        nameLabel.setColor(Color.GOLD);
        dialogueTable.add(nameLabel).align(Align.left).row();

        dialogueLabel = new Label("", skin);
        dialogueLabel.setFontScale(3f);
        dialogueLabel.setWrap(true);
        dialogueTable.add(dialogueLabel).expand().fillX().align(Align.left).padTop(20).row();

        choiceTable = new Table();
        dialogueTable.add(choiceTable).align(Align.left).padTop(20).padLeft(20);

        dialogueTable.setVisible(false);
        stage.addActor(dialogueTable);
    }

    public void show(String npcName, DialogueNode node, ChoiceListener listener) {
        nameLabel.setText(npcName);
        updateText("");
        choiceTable.clear();

        for (DialogueNode.Choice choice : node.getChoices()) {
            TextButton button = new TextButton(choice.text, skin, "default");
            button.getLabel().setFontScale(2.5f);
            button.addListener(event -> {
                if (event.toString().equals("touchDown")) {
                    listener.onChoiceSelected(choice);
                    return true;
                }
                return false;
            });
            choiceTable.add(button).pad(10).align(Align.right).row();
        }
        showChoices(false);
        dialogueTable.setVisible(true);
    }

    public void updateText(String text) {dialogueLabel.setText(text);}
    public void showChoices(boolean show) {choiceTable.setVisible(show);}
    public void hide() {dialogueTable.setVisible(false);}
    public void dispose() {dialogueBgTexture.dispose();}
}
