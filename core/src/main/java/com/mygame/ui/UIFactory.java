package com.mygame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.Assets;

import java.util.Map;

/**
 * A generic factory for creating UI components from JSON configuration files.
 */
public class UIFactory {

    /**
     * Creates and adds buttons to stages based on a JSON configuration file.
     *
     * @param jsonFile        The FileHandle for the JSON configuration.
     * @param skin            The skin to use for the buttons.
     * @param stages          A map of stage names to Stage objects.
     * @param actionListeners A map where keys are action names from JSON and values are their corresponding listeners.
     * @param table           (Optional) The table to add the buttons to.
     */
    public static void createButtonsFromJson(FileHandle jsonFile, Skin skin, Map<String, Stage> stages, Map<String, InputListener> actionListeners, Table table) {
        JsonReader jsonReader = new JsonReader();
        JsonValue base = jsonReader.parse(jsonFile);
        JsonValue buttons = base.get("buttons");

        for (JsonValue buttonValue : buttons.iterator()) {
            String action = buttonValue.getString("action");
            String textKey = buttonValue.getString("textKey", buttonValue.getString("text", "")); // Fallback to text
            String stageName = buttonValue.getString("stage");

            Stage targetStage = stages.get(stageName);
            InputListener listener = actionListeners.get(action);

            if (targetStage == null || listener == null) {
                Gdx.app.log("UIFactory", "Warning: Could not create button with action '" + action + "'. Stage or listener not found.");
                continue;
            }

            TextButton button = createButton(textKey, buttonValue.getFloat("fontScale"), skin, listener);

            if (table != null) {
                table.add(button)
                     .width(buttonValue.getFloat("width"))
                     .height(buttonValue.getFloat("height"))
                     .padBottom(buttonValue.getFloat("padBottom", 0));
                if (buttonValue.getBoolean("row", false)) {
                    table.row();
                }
            } else {
                button.setSize(buttonValue.getFloat("width"), buttonValue.getFloat("height"));
                float x = buttonValue.getFloat("x");
                float y = buttonValue.getFloat("y");

                if (buttonValue.getBoolean("y_from_top", false)) {
                    y = targetStage.getViewport().getWorldHeight() - y;
                }
                if (buttonValue.getBoolean("center_x", false)) {
                    x = targetStage.getViewport().getWorldWidth() / 2 + x;
                }
                button.setPosition(x, y);
                targetStage.addActor(button);
            }
        }
    }

    private static TextButton createButton(String text, float fontScale, Skin skin, InputListener listener) {
        TextButton button = new TextButton(text.startsWith("button.") || text.startsWith("settings.") ? Assets.bundle.get(text) : text, skin);
        button.getLabel().setFontScale(fontScale);
        button.addListener(listener);
        return button;
    }
}
