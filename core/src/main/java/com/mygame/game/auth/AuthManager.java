package com.mygame.game.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class AuthManager {

    private static Json json = new Json();

    private static String token;

    public static void login(String username, String password, HttpCallback callback) {
        LoginRequest requestObj = new LoginRequest(username, password);
        request("http://127.0.0.1:8000/auth/login/", requestObj, callback);
    }
    public static void register(String username, String password, String confirm, HttpCallback callback) {
        if (!password.equals(confirm)) return;

        RegisterRequest requestObj = new RegisterRequest(username, password, confirm);
        request("http://127.0.0.1:8000/auth/register/", requestObj, callback);
    }

    public static String getToken() {
        return token;
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
                Gdx.app.postRunnable(() ->
                    callback.onSuccess(httpResponse.getResultAsString()));
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() ->
                    callback.onFailure(t));
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
    public static void setToken(String newToken){
        token = newToken;
    }
}
