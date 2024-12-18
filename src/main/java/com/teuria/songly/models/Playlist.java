
package com.teuria.songly.models;

import com.teuria.songly.AppState;
import com.teuria.songly.notation.NotationModel;
import com.teuria.songly.notation.SimpleNotation;
import java.util.ArrayList;
import java.util.LinkedList;


public class Playlist implements NotationModel {

    private String title;
    private int songCount;
    private LinkedList<Music> songs;

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public int getSongCount() {
        return songCount;
    }

    public LinkedList<Music> getSongs() {
        return songs;
    }

    public Playlist(String title, int songCount, LinkedList<Music> songs) {
        this.title = title;
        this.songCount = songCount;
        this.songs = songs;
    }
    
    public Playlist(String title, int songCount) {
        this.title = title;
        this.songCount = songCount;
        this.songs = new LinkedList<>();
    }
    
    public Playlist() {
        this.songs = new LinkedList<>();
    }
    
    public void addMusic(Music music) {
        if (!songs.contains(music)) {
            songs.add(music);
            songCount += 1;
        }
    }
    
    public void removeMusic(Music music) {
        if (songs.remove(music)) {
            songCount -= 1;
        }
    }
    
    @Override
    public SimpleNotation write() {
        SimpleNotation notation = new SimpleNotation(title);
        notation.add("songCount", songCount);
        
        ArrayList<String> strs = new ArrayList<>();
       
        if (songs.size() != 0) {
            for (Music song : songs) {
                strs.add(song.getId());
            }
        }
        notation.add("songs", String.join(",", strs));
        
        return notation;
    }

    @Override
    public void read(SimpleNotation reader) {
        title = reader.getSectionName();
        songCount = reader.getInt("songCount");
        
        String[] songsStr = reader.get("songs").split(",");
        
        LinkedList<Music> songs = new LinkedList<>();
        
        if (songCount != 0) {
            for (String id : songsStr) {
                songs.add(AppState.getMusic(id));
            }   
        }
        
        this.songs = songs;
    }
    
}
