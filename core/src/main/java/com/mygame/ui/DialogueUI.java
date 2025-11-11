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
    private final Texture dialogueBgTexture;
    private final Table choiceTable;
    private final Skin skin;

    public interface ChoiceListener {
        void onChoice(DialogueNode.Choice choice);
    }

    public DialogueUI(Skin skin, Stage stage, int width, int height, float x, float y) {
        this.skin = skin;

        Pixmap dialogueBg = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        dialogueBg.setColor(new Color(0.1f, 0.1f, 0.5f, 0.6f));
        dialogueBg.fill();
        dialogueBgTexture = new Texture(dialogueBg);
        dialogueBg.dispose();

        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(dialogueBgTexture));

        dialogueTable = new Table();
        dialogueTable.setSize(width, height);
        dialogueTable.setPosition(x, y);
        dialogueTable.setBackground(background);

        nameLabel = new Label("", skin);
        nameLabel.setFontScale(3f);
        nameLabel.setColor(Color.GOLD);
        nameLabel.setAlignment(Align.left);

        dialogueLabel = new Label("", skin);
        dialogueLabel.setFontScale(3f);
        dialogueLabel.setWrap(true);
        dialogueLabel.setAlignment(Align.left);

        dialogueTable.add(nameLabel).left().padLeft(10).padBottom(20).row();
        dialogueTable.add(dialogueLabel).width(width - 150).padLeft(60).left().row();

        choiceTable = new Table();
        dialogueTable.add(choiceTable).padTop(20);
        dialogueTable.setVisible(false);
        stage.addActor(dialogueTable);
    }

    public void show(String npcName, DialogueNode node, ChoiceListener listener) {
        nameLabel.setText(npcName);
        dialogueLabel.setText(node.getText());
        dialogueTable.setVisible(true);
        choiceTable.clear();

        for (DialogueNode.Choice choice : node.getChoices()) {
            TextButton button = new TextButton(choice.text, skin, "default");
            button.getLabel().setFontScale(2.5f);
            button.pad(10, 20, 10, 20);
            button.addListener(event -> {
                if (event.toString().equals("touchDown")) {
                    listener.onChoice(choice);
                    return true;
                }
                return false;
            });
            choiceTable.add(button).padTop(10).row();
        }
    }

    public void hide() {
        dialogueTable.setVisible(false);
        choiceTable.clear();
    }

    public void dispose() {dialogueBgTexture.dispose();}
}
