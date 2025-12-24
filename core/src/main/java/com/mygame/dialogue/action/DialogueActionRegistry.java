package com.mygame.dialogue.action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.mygame.assets.Assets;
import com.mygame.assets.audio.MusicManager;
import com.mygame.assets.audio.SoundManager;
import com.mygame.dialogue.DialogueRegistry;
import com.mygame.dialogue.action.condition.HasItemCondition;
import com.mygame.dialogue.action.custom.ChikitaCraftJointAction;
import com.mygame.dialogue.action.custom.PoliceCheckAction;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.npc.Police;
import com.mygame.game.GameContext;
import com.mygame.quest.QuestManager;
import com.mygame.managers.TimerManager;

import java.util.HashMap;
import java.util.Map;

public class DialogueActionRegistry {

    private interface ActionCreator {
        Runnable create(GameContext ctx, JsonValue data);
    }

    private static final Map<String, ActionCreator> creators = new HashMap<>();

    static {
        creators.put("trade", (ctx, data) -> () -> new TradeAction(ctx,
                data.getString("from"), data.getString("to"),
                data.getInt("fromAmount"), data.getInt("toAmount")).execute());

        creators.put("add_quest", (ctx, data) -> () -> new AddQuestAction(
                data.getString("id"),
                data.getBoolean("progressable", false),
                data.getInt("progress", 0),
                data.getInt("max", 0)).execute());

        creators.put("remove_quest", (ctx, data) -> () -> QuestManager.removeQuest(data.getString("id")));

        creators.put("set_dialogue", (ctx, data) -> () -> new SetDialogueAction(ctx,
                data.getString("npc"), data.getString("node")).execute());

        creators.put("complete_event", (ctx, data) -> () -> new CompleteEventAction(ctx,
                data.getString("id")).execute());

        creators.put("add_item", (ctx, data) -> () -> ctx.getInventory().addItemAndNotify(
                ItemRegistry.get(data.getString("id")),
                data.getInt("amount", 1)));

        creators.put("play_sound", (ctx, data) -> () -> SoundManager.playSound(Assets.getSound(data.getString("id"))));

        creators.put("player_died", (ctx, data) -> ctx.gsm::playerDied);

        creators.put("composite", (ctx, data) -> () -> {
            for (JsonValue subAction : data.get("actions")) {
                createAction(ctx, subAction).run();
            }
        });

        creators.put("timer", (ctx, data) -> () -> {
            float delay = data.getFloat("delay", 1f);
            JsonValue actionData = data.get("action");
            TimerManager.setAction(() -> createAction(ctx, actionData).run(), delay);
        });

        creators.put("set_texture", (ctx, data) -> () -> {
            var npc = ctx.npcManager.findNpcById(Assets.bundle.get(data.getString("npcNameKey")));
            if (npc != null) npc.setTexture(Assets.getTexture(data.getString("texture")));
        });

        creators.put("conditional_trade", (ctx, data) -> () -> {
            if (ctx.getInventory().trade(ItemRegistry.get(data.getString("from")),
                    ItemRegistry.get(data.getString("to")),
                    data.getInt("fromAmount"), data.getInt("toAmount"))) {
                if (data.has("onSuccess")) {
                    createAction(ctx, data.get("onSuccess")).run();
                }
            }
        });

        creators.put("if_has_item", (ctx, data) -> () -> {
            if (new HasItemCondition(ctx, data.getString("itemId"), data.getString("itemNameKey")).check()) {
                if (data.has("action")) {
                    createAction(ctx, data.get("action")).run();
                }
            }
        });

        // Specific complex ones mapped to types
        creators.put("chikita_craft", (ctx, data) -> () -> new ChikitaCraftJointAction(ctx).execute());
        creators.put("police_check", (ctx, data) -> () -> new PoliceCheckAction(ctx).execute());
        creators.put("start_police_chase", (ctx, data) -> () -> {
            Police police = ctx.npcManager.getSummonedPolice();
            if (police != null) {
                new AddQuestAction("chase", false, 0, 0).execute();
                police.startChase(ctx.player);
                ctx.ui.getGameUI().showInfoMessage(Assets.bundle.get("message.boss.chase.run"), 2f);
                MusicManager.playMusic(Assets.getMusic("backMusic4"));
                new SetDialogueAction(ctx, "summoned_police", "caught").execute();
            }
        });
    }

    public static void registerAll(GameContext ctx) {
        loadActionsFromJson(ctx);
    }

    private static void loadActionsFromJson(GameContext ctx) {
        try {
            JsonReader reader = new JsonReader();
            JsonValue root = reader.parse(Gdx.files.internal("data/dialogues/actions.json"));
            for (JsonValue entry : root) {
                DialogueRegistry.registerAction(entry.name(), createAction(ctx, entry));
            }
        } catch (Exception e) {
            Gdx.app.log("DialogueActionRegistry", "Could not load actions.json: " + e.getMessage());
        }
    }

    public static Runnable createAction(GameContext ctx, JsonValue data) {
        if (data == null) return () -> {};
        String type = data.getString("type", "");
        ActionCreator creator = creators.get(type);
        if (creator != null) {
            return creator.create(ctx, data);
        }
        return () -> {};
    }
}
