package com.teuria.songly.api;

import com.teuria.songly.Database;
import com.teuria.songly.MediaPlayer;
import com.teuria.songly.models.Music;


public class MusicController {
    private Music previousMusic;
    private Music currentMusic;
    private Music nextMusic;
    
    public void play(String id) {
        int index = Database.getIndexFromMusic(Database.getMusic(id));
        previousMusic = Database.getMusicByIndex(index - 1);
        currentMusic = Database.getMusicByIndex(index);
        nextMusic = Database.getMusicByIndex(index + 1);
    }
    
    public void finished(MediaPlayer player) {
        
    }
}
