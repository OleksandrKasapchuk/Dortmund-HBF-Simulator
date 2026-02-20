package com.mygame.game.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.mygame.game.GameInitializer;
import com.mygame.game.save.SettingsManager;

public class AuthManager {

    private static final String PREF_NAME = "MyGameSession";
    private static final String TOKEN_KEY = "token";
    private static final String USERNAME_KEY = "username";

    private static Json json = new Json();
    private static String token;
    private static String username;
    private static GameInitializer gameInitializer;

    static {
        loadSession();
    }

    public static void init(GameInitializer gameInitializer) {
        AuthManager.gameInitializer = gameInitializer;
    }

    private static void loadSession() {
        Preferences prefs = Gdx.app.getPreferences(PREF_NAME);
        token = prefs.getString(TOKEN_KEY, null);
        username = prefs.getString(USERNAME_KEY, null);
    }

    public static void login(String username, String password, HttpCallback callback) {
        LoginRequest requestObj = new LoginRequest(username, password);
        request("https://hbf-simulator-backend.onrender.com/auth/login/", requestObj, callback);
    }

    public static void register(String username, String password, String confirm, HttpCallback callback) {
        if (!password.equals(confirm)) return;

        RegisterRequest requestObj = new RegisterRequest(username, password, confirm);
        request("https://hbf-simulator-backend.onrender.com/auth/register/", requestObj, callback);
    }

    public static String getToken() {
        return token;
    }

    public static String getUsername() {
        return username;
    }

    public static <T> void request(String url, T requestObj, HttpCallback callback){
        json.setOutputType(JsonWriter.OutputType.json);
        String body = json.toJson(requestObj);

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(url);
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Accept", "application/json");
        request.setContent(body);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {

            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                byte[] raw = httpResponse.getResult();
                String text = new String(raw);
                Gdx.app.postRunnable(() -> {
                    // Спочатку викликаємо callback, щоб зберегти токен
                    callback.onSuccess(text);
                });
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> callback.onFailure(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(callback::onCancelled);
            }
        });
    }

    public interface HttpCallback {
        void onSuccess(String response);
        void onFailure(Throwable t);
        void onCancelled();
    }

    public static void setSession(String newToken, String newUsername) {
        token = newToken;
        username = newUsername;
        Preferences prefs = Gdx.app.getPreferences(PREF_NAME);
        prefs.putString(TOKEN_KEY, newToken);
        prefs.putString(USERNAME_KEY, newUsername);
        prefs.flush();
    }

    public static void logout() {
        token = null;
        username = null;
        SettingsManager.resetSettings();
        Preferences prefs = Gdx.app.getPreferences(PREF_NAME);
        prefs.remove(TOKEN_KEY);
        prefs.remove(USERNAME_KEY);
        prefs.flush();
        gameInitializer.initGame();
    }

    public static boolean hasToken(){
        return token != null && !token.isEmpty();
    }
}
