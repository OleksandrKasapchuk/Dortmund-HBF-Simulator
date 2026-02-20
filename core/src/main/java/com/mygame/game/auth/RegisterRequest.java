package com.mygame.game.auth;

public class RegisterRequest {
    public String username;
    public String password;
    public String confirmPassword;

    public RegisterRequest(String username, String password, String confirmPassword) {
        this.username = username;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }
}
