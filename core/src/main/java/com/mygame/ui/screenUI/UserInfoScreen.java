package com.mygame.ui.screenUI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mygame.Config;
import com.mygame.assets.Assets;
import com.mygame.events.EventBus;
import com.mygame.events.Events;
import com.mygame.game.DayManager;
import com.mygame.game.auth.AuthManager;
import com.mygame.game.auth.LeaderboardEntry;

public class UserInfoScreen extends Screen {

    private final Table leaderboardTable = new Table();
    private final Skin skin;

    public UserInfoScreen(Skin skin, DayManager dayManager){
        super();
        this.skin = skin;

        // --- TOP BAR ---
        Table topBar = new Table();

        TextButton backBtn = createButton(skin, Assets.ui.get("button.back.text"), 1.8f, () -> EventBus.fire(new Events.ActionRequestEvent("act.system.account")));

        Label titleLabel = createLabel(skin, Assets.ui.get("ui.account.title"), 3f);

        topBar.add(backBtn).left().pad(50);
        topBar.add(titleLabel).center().expandX();
        topBar.add().width(backBtn.getPrefWidth());

        // --- USER INFO ---
        Table userTable = new Table();
        userTable.left().padLeft(40).padTop(30);

        Label usernameLabel = createLabel(skin, Assets.ui.get("ui.auth.username") + ": " + AuthManager.getUsername(), 1.5f);
        userTable.add(usernameLabel).padBottom(50).left().row();

        Label dayLabel = createLabel(skin, Assets.ui.format("ui.day", dayManager.getDay()), 1.5f  );
        userTable.add(dayLabel).padBottom(50).left().row();

        // --- LEADERBOARD ---
        leaderboardTable.top().left().padLeft(20).padTop(30); // менший відступ

        // --- ROOT ---
        root.top().left();
        root.add(topBar).expandX().fillX().row();

        Table contentTable = new Table();
        contentTable.left().top();
        contentTable.add(userTable).left().top();
        contentTable.add(leaderboardTable).left().top().padLeft(100);
        root.add(contentTable).expand().fill().row();

        // initial load
        showLoading();
        loadLeaderboard();
    }

    // ---------- UI STATES ----------

    private void showLoading() {
        leaderboardTable.clear();
        addHeader();

        Label loading = createLabel(skin, "Loading...", 1.2f);
        leaderboardTable.add(loading).colspan(2).left().padTop(10).row();
    }

    private void showError() {
        leaderboardTable.clear();
        addHeader();

        Label error = createLabel(skin, "Failed to load leaderboard", 1.2f);
        leaderboardTable.add(error).colspan(2).left().padTop(10).row();
    }

    private void addHeader() {
        Label lbTitle = createLabel(skin, "Leaderboard", 2f);

        TextButton refreshBtn = createButton(skin, "Refresh", 1.2f, () -> {
            showLoading();
            loadLeaderboard();
        });

        leaderboardTable.add(lbTitle).left();
        leaderboardTable.add(refreshBtn).right().padLeft(20).row();
    }

    // ---------- NETWORK ----------

    private void loadLeaderboard() {
        String url = Config.getServerUrl() + "/api/leaderboard/";
        System.out.println("REQUEST URL: " + url);

        Net.HttpRequest request = new HttpRequestBuilder()
            .newRequest()
            .method(Net.HttpMethods.GET)
            .url(url)
            .header("Accept", "application/json")
            .build();

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                int status = httpResponse.getStatus().getStatusCode();
                String jsonText = httpResponse.getResultAsString();

                System.out.println("STATUS: " + status);
                System.out.println("JSON: " + jsonText);

                if (status != 200 || jsonText == null || jsonText.isEmpty()) {
                    Gdx.app.postRunnable(UserInfoScreen.this::showError);
                    return;
                }

                Json json = new Json();
                Array<LeaderboardEntry> data =
                    json.fromJson(Array.class, LeaderboardEntry.class, jsonText);

                if (data == null) data = new Array<>();

                Array<LeaderboardEntry> finalData = data;
                Gdx.app.postRunnable(() -> populateLeaderboard(finalData));
            }

            @Override
            public void failed(Throwable t) {
                t.printStackTrace();
                Gdx.app.postRunnable(UserInfoScreen.this::showError);
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(UserInfoScreen.this::showError);
            }
        });
    }

    // ---------- POPULATE ----------

    private void populateLeaderboard(Array<LeaderboardEntry> entries) {
        leaderboardTable.clear();
        addHeader();

        if (entries == null || entries.size == 0) {
            Label empty = createLabel(skin, "No players yet", 1.2f);
            leaderboardTable.add(empty).colspan(2).left().padTop(10).row();
            return;
        }

        int rank = 1;
        for (LeaderboardEntry e : entries) {

            Label rankLabel = createLabel(skin, String.valueOf(rank), 1.2f);

            Label nameLabel = createLabel(skin, e.username + " (" + e.currentDay + " day)", 1.2f);

            // зменшена відстань між рангом і імʼям
            leaderboardTable.add(rankLabel).width(30).left(); // раніше було 40
            leaderboardTable.add(nameLabel).left().padLeft(5).padBottom(6).row();

            rank++;
        }
    }
}
