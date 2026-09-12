package com.mygame;

public class Config {
    public static final boolean IS_DEBUG = false;

    public static String getServerUrl() {
        if (IS_DEBUG) {
            // 10.0.2.2 is the special alias to your host loopback interface (i.e., 127.0.0.1 on your PC)
            // for the Android Emulator. Use 127.0.0.1 if running on Desktop.
            return "http://127.0.0.1:8000";
        } else {
            return "https://hbf-simulator.onrender.com";
        }
    }
}
