package com.teuria.songly;

import com.teuria.songly.models.Music;
import com.teuria.songly.models.Playlist;
import java.util.ArrayList;
import java.util.HashMap;


public class Database {
    private static HashMap<String, Music> musicLibraries = new HashMap<>();
    private static ArrayList<Music> musics = new ArrayList<>();
    private static ArrayList<Playlist> playlists = new ArrayList<>();
    
    public static void addMusic(Music music) {
        musics.add(music);
        musicLibraries.put(music.getId(), music);
    }
    
    public static Music getMusic(String id) {
        return musicLibraries.get(id);
    }
    
    public static Music getMusicByIndex(int index) {
        return musics.get(index);
    }
    
    public static int getIndexFromMusic(Music music) {
        return musics.indexOf(music);
    }
    
    public static void save() {
        
    }
    
    public static void load() {
        
    }
}
