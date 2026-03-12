package com.mygame;

public class Config {
    public static final boolean IS_DEBUG = true;

    public static String getServerUrl() {
        if (IS_DEBUG) {
            return "http://127.0.0.1:8000";
        } else {
            return "https://hbf-simulator-backend.onrender.com";
        }
    }
}
