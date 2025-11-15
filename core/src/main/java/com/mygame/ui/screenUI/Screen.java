package com.mygame.ui.screenUI;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * Abstract base class for all UI screens.
 * Provides a Stage with a FitViewport for UI rendering and management.
 */
public abstract class Screen {
    // Stage used to hold all UI actors for this screen
    private Stage stage = new Stage(new FitViewport(2000, 1000));

    /**
     * Returns the Stage associated with this screen.
     * @return the Stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Disposes of the Stage and its resources.
     */
    public void dispose() {
        stage.dispose();
    }
}
