package com.mygame.game.save.data;

public class ClientSaveData {

    public float musicVolume;
    public float soundVolume;
    public boolean muteAll;
    public String language;

    public ClientSaveData(){
        this.language = "en";
        this.musicVolume = 1.0f;
        this.soundVolume = 1.0f;
        this.muteAll = false;
    }

}
