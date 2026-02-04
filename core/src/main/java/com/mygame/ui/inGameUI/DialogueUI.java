package com.mygame.ui.inGameUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.mygame.dialogue.DialogueNode;
import com.mygame.entity.npc.NPC;

/**
 * DialogueUI is responsible for displaying NPC dialogue in a styled window,
 * showing the NPC name, dialogue text, and interactive choice buttons.
 */
public class DialogueUI {

    private final Table dialogueTable;  // main container for dialogue UI
    private final Label nameLabel;      // displays NPC name
    private final Label dialogueLabel;  // displays dialogue text
    private final Table choiceTable;    // holds all choice buttons
    private final Skin skin;
    private final Texture dialogueBgTexture;

    private Image portraitImage;

    // Listener interface for choice selection events
    public interface ChoiceListener {
        void onChoiceSelected(DialogueNode.Choice choice);
    }

    /**
     * Constructs a DialogueUI window.
     *
     * @param skin  Skin used for labels and buttons
     * @param stage Stage to attach the dialogue UI
     * @param width Width of the dialogue window
     * @param height Height of the dialogue window
     * @param x X-coordinate of the window
     * @param y Y-coordinate of the window
     */
    public DialogueUI(Skin skin, Stage stage, int width, int height, float x, float y) {
        this.skin = skin;

        // Create semi-transparent background texture
        Pixmap bg = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        bg.setColor(new Color(0.1f, 0.1f, 0.5f, 0.6f));
        bg.fill();
        dialogueBgTexture = new Texture(bg);
        bg.dispose();

        // Initialize main dialogue table
        dialogueTable = new Table();
        dialogueTable.setSize(width, height);
        dialogueTable.setPosition(x, y);
        dialogueTable.setBackground(new TextureRegionDrawable(new TextureRegion(dialogueBgTexture)));
        dialogueTable.pad(20);

        // NPC name label
        nameLabel = new Label("", skin);
        nameLabel.setFontScale(1.5f);
        nameLabel.setColor(Color.GOLD);
        dialogueTable.add(nameLabel).align(Align.left).row();

        // Dialogue text label
        dialogueLabel = new Label("", skin);
        dialogueLabel.setFontScale(1.5f);
        dialogueLabel.setWrap(true);
        dialogueTable.add(dialogueLabel).expand().fillX().padRight(256 + 40).align(Align.left).row();


        // Table to hold choice buttons
        choiceTable = new Table();
        dialogueTable.add(choiceTable).align(Align.left).padLeft(30);

        dialogueTable.setVisible(false);  // hide by default
        stage.addActor(dialogueTable);
    }

    /**
     * Shows a dialogue window for a given NPC and dialogue node.
     * @param npc  NPC
     * @param node     DialogueNode containing text and choices
     * @param listener Callback for when a choice is selected
     */
    public void show(NPC npc, DialogueNode node, ChoiceListener listener) {
        nameLabel.setText(npc.getName());
        updateText("");  // clear dialogue text initially
        choiceTable.clear();  // remove previous choices

        showPortrait(npc.getFace_texture());

        createChoices(node, listener);
        showChoices(false);       // initially hide choices
        dialogueTable.setVisible(true);
    }

    public void showPortrait(Texture portrait) {
        if (portraitImage == null) {
            portraitImage = new Image(portrait);
            portraitImage.setSize(256, 256);
            portraitImage.setPosition(dialogueTable.getWidth() - 256 - 20, 20);
            dialogueTable.addActor(portraitImage);
        } else {
            portraitImage.setDrawable(new TextureRegionDrawable(new TextureRegion(portrait)));
        }
        portraitImage.setVisible(true);
    }

    private void createChoices(DialogueNode node, ChoiceListener listener){
        // Create buttons for each choice
        for (DialogueNode.Choice choice : node.getChoices()) {
            TextButton button = new TextButton(choice.text(), skin, "default");
            button.getLabel().setFontScale(1.5f);
            button.addListener(event -> {
                // Only respond to touchDown events
                if (event.toString().equals("touchDown")) {
                    listener.onChoiceSelected(choice);
                    return true;
                }
                return false;
            });
            choiceTable.add(button).padBottom(10).align(Align.right).row();
        }
    }
    // Updates the main dialogue text
    public void updateText(String text) { dialogueLabel.setText(text); }

    // Show or hide choice buttons
    public void showChoices(boolean show) { choiceTable.setVisible(show); }

    // Hide the entire dialogue window
    public void hide() { dialogueTable.setVisible(false); }

    // Dispose of texture resources
    public void dispose() {dialogueBgTexture.dispose();}
}
