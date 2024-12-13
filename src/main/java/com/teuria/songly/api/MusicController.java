package com.teuria.songly.api;

import com.teuria.songly.AppState;
import com.teuria.songly.MediaPlayer;
import com.teuria.songly.models.Music;


public class MusicController {
    private Music previousMusic;
    private Music currentMusic;
    private Music nextMusic;
    
    public void play(String id) {
        int index = AppState.getIndexFromMusic(AppState.getMusic(id));
        previousMusic = AppState.getMusicByIndex(index - 1);
        currentMusic = AppState.getMusicByIndex(index);
        nextMusic = AppState.getMusicByIndex(index + 1);
    }
    
    public void finished(MediaPlayer player) {
        
    }
}
