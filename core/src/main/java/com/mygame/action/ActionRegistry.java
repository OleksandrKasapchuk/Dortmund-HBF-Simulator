package com.mygame.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.action.provider.ActionProvider;
import com.mygame.action.provider.AudioActionProvider;
import com.mygame.action.provider.CustomActionProvider;
import com.mygame.action.provider.DialogueActionProvider;
import com.mygame.action.provider.InventoryActionProvider;
import com.mygame.action.provider.ItemActionProvider;
import com.mygame.action.provider.NpcActionProvider;
import com.mygame.action.provider.PlayerActionProvider;
import com.mygame.action.provider.QuestActionProvider;
import com.mygame.action.provider.SystemActionProvider;
import com.mygame.action.provider.UiActionProvider;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.GameContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionRegistry {

    public interface ActionCreator {
        Runnable create(GameContext ctx, JsonValue data);
    }

    private final Map<String, ActionCreator> creators = new HashMap<>();
    private final Map<String, Runnable> registeredActions = new HashMap<>();
    private final List<ActionProvider> providers = new ArrayList<>();


    public void init(GameContext ctx) {
        setupProviders();
        setupCreators(ctx);
        loadActionsFromManifest(ctx);
        EventBus.subscribe(Events.ActionRequestEvent.class, event -> executeAction(event.actionId()));
    }

    private void setupProviders() {
        providers.add(new InventoryActionProvider());
        providers.add(new QuestActionProvider());
        providers.add(new DialogueActionProvider());
        providers.add(new AudioActionProvider());
        providers.add(new PlayerActionProvider());
        providers.add(new SystemActionProvider());
        providers.add(new ItemActionProvider());
        providers.add(new NpcActionProvider());
        providers.add(new UiActionProvider());
        providers.add(new CustomActionProvider());
    }

    public void registerCreator(String type, ActionCreator creator) {
        creators.put(type, creator);
    }

    public void registerAction(String name, Runnable action) {
        registeredActions.put(name, action);
    }

    private void setupCreators(GameContext ctx) {
        for (ActionProvider provider : providers) {
            provider.provide(ctx, this);
        }
    }

    private void loadActionsFromManifest(GameContext ctx) {
        JsonReader reader = new JsonReader();
        FileHandle manifestFile = Gdx.files.internal("data/actions/actions.json");

        if (!manifestFile.exists()) {
            manifestFile = Gdx.files.internal("assets/data/actions/actions.json");
        }

        if (!manifestFile.exists()) {
            Gdx.app.log("ActionRegistry", "Manifest file 'actions.json' not found. Looked in 'data/actions/' and 'assets/data/actions/'.");
            return;
        }

        try {
            String[] actionFiles = reader.parse(manifestFile).asStringArray();
            for (String fileName : actionFiles) {
                FileHandle actionFile = Gdx.files.internal("data/actions/" + fileName + ".json");
                if (!actionFile.exists()) {
                     actionFile = Gdx.files.internal("assets/data/actions/" + fileName + ".json");
                }

                if (actionFile.exists()) {
                    JsonValue root = reader.parse(actionFile);
                    for (JsonValue entry : root) {
                        registeredActions.put(entry.name(), createAction(ctx, entry));
                    }
                } else {
                    Gdx.app.log("ActionRegistry", "Action file '" + fileName + ".json' not found.");
                }
            }
        } catch (Exception e) {
            Gdx.app.log("ActionRegistry", "Could not load actions from manifest: " + e.getMessage());
        }
    }

    public Runnable createAction(GameContext ctx, JsonValue data) {
        if (data == null) return () -> {};
        if (data.isString()) {
            return registeredActions.getOrDefault(data.asString(), () -> {});
        }
        String type = data.getString("type", "");
        ActionCreator creator = creators.get(type);
        if (creator != null) {
            return creator.create(ctx, data);
        }
        Gdx.app.log("ActionRegistry", "Action creator '" + type + "' not found!");
        return () -> {};
    }

    public Runnable getAction(String name) {
        return registeredActions.get(name);
    }

    public void executeAction(String name) {
        Runnable action = registeredActions.get(name);
        if (action != null) {
            action.run();
            Gdx.app.log("ActionRegistry", "Action '" + name + "' executed!");
        } else {
            Gdx.app.log("ActionRegistry", "Action '" + name + "' not found!");
        }
    }
}
