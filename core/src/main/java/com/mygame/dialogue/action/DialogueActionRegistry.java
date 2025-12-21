package com.mygame.dialogue.action;

import com.mygame.assets.Assets;
import com.mygame.assets.audio.MusicManager;
import com.mygame.assets.audio.SoundManager;
import com.mygame.dialogue.DialogueRegistry;
import com.mygame.dialogue.action.custom.ChikitaCraftJointAction;
import com.mygame.dialogue.action.condition.HasItemCondition;
import com.mygame.dialogue.action.custom.PoliceCheckAction;
import com.mygame.entity.item.ItemRegistry;
import com.mygame.entity.npc.Police;
import com.mygame.game.GameContext;
import com.mygame.quest.QuestManager;
import com.mygame.managers.TimerManager;


public class DialogueActionRegistry {

    public static void registerAll(GameContext ctx) {

        DialogueRegistry.registerAction("baryga_buy_grass", () -> new TradeAction(ctx, "money", "grass", 10, 1).execute());
        DialogueRegistry.registerAction("kioskman_buy_pape", () -> new TradeAction(ctx, "money", "pape", 5, 1).execute());
        DialogueRegistry.registerAction("kioskman_buy_icetea", () -> new TradeAction(ctx, "money", "ice_tea", 10, 1).execute());
        DialogueRegistry.registerAction("buy_seed", () -> new TradeAction(ctx, "money", "grass_seed", 5, 1).execute());


        DialogueRegistry.registerAction("murat_accept_quest", () -> new AddQuestAction( "chili",  false, 0, 0).execute());
        DialogueRegistry.registerAction("walter_accept_quest", () -> new AddQuestAction("wallet",  false, 0, 0).execute());
        DialogueRegistry.registerAction("igo_add_quest", () -> new AddQuestAction("igo", false, 0, 0).execute());


        DialogueRegistry.registerAction("ryzhyi_take_money", () -> {
            ctx.getInventory().addItemAndNotify(ItemRegistry.get("money"), 20);
            new SetDialogueAction(ctx, "ryzhyi", "after").execute();
            new CompleteEventAction(ctx, "ryzhyi_gave_money").execute();
        });

        DialogueRegistry.registerAction("jason_give_money", () -> {
            ctx.getInventory().addItemAndNotify(ItemRegistry.get("money"), 20);
            new AddQuestAction("jason1", true, 1, 6).execute();
            new AddQuestAction( "jason2", true, 0, 10).execute();
            new SetDialogueAction(ctx, "jason", "after").execute();
            new CompleteEventAction(ctx, "jason_gave_money").execute();
        });

        DialogueRegistry.registerAction("igo_give_vape", () -> {
            if (ctx.getInventory().trade(ItemRegistry.get("joint"), ItemRegistry.get("vape"), 1, 1)) {
                QuestManager.removeQuest("igo");
                new SetDialogueAction(ctx, "igo", "thanks").execute();
                new CompleteEventAction(ctx, "igo_gave_vape").execute();
                TimerManager.setAction(() -> ctx.npcManager.findNpcById(Assets.bundle.get("npc.igo.name")).setTexture(Assets.getTexture("igo2")), 5f);
            }
        });

        DialogueRegistry.registerAction("chikita_craft_joint", () -> new ChikitaCraftJointAction(ctx).execute());

        DialogueRegistry.registerAction("boss_accept_quest", () -> {
            ctx.getInventory().addItemAndNotify(ItemRegistry.get("grass"), 1000);
            new AddQuestAction("delivery",  false, 0, 0).execute();
            new SetDialogueAction(ctx, "boss", "after").execute();
            new CompleteEventAction(ctx, "boss_gave_quest").execute();
        });

        DialogueRegistry.registerAction("police_check", () -> new PoliceCheckAction(ctx).execute());


        DialogueRegistry.registerAction("give_vape_action", () -> {
            if (new HasItemCondition(ctx, "vape", "item.vape.name").check()) {
                new SetDialogueAction(ctx, "talahon2", "accept").execute();
            }
        });


        DialogueRegistry.registerAction("failure_action", () -> {
            new SetDialogueAction(ctx, "boss", "failure").execute();
            ctx.gsm.playerDied();
            SoundManager.playSound(Assets.getSound("gunShot"));
        });

        DialogueRegistry.registerAction("player_died", ctx.gsm::playerDied);

        DialogueRegistry.registerAction("start_police_chase", () -> {
            Police police = ctx.npcManager.getSummonedPolice();
            if (police != null) {
                new AddQuestAction("police_chase", false, 0,0).execute();

                police.startChase(ctx.player);
                ctx.ui.getGameUI().showInfoMessage(Assets.bundle.get("message.boss.chase.run"), 2f);
                MusicManager.playMusic(Assets.getMusic("backMusic4"));
                new SetDialogueAction(ctx, "summoned_police", "caught").execute();
            }
        });

        DialogueRegistry.registerAction("boss_claim_reward", () -> {
            ctx.getInventory().addItemAndNotify(ItemRegistry.get("money"), 50);
            new SetDialogueAction(ctx, "boss", "finished").execute();
            new CompleteEventAction(ctx, "boss_reward_claimed").execute();
        });
    }
}
