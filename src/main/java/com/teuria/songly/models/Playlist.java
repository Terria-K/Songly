
package com.teuria.songly.models;

import com.teuria.songly.notation.NotationModel;
import com.teuria.songly.notation.SimpleNotation;
import java.util.ArrayList;


public class Playlist implements NotationModel {

    private String title;
    private int songCount;
    private ArrayList<Music> arrayOfSong;
    
    
    @Override
    public SimpleNotation write() {
        SimpleNotation notation = new SimpleNotation("");
        notation.add("title", title);
        notation.add("songCount", songCount);
        
        ArrayList<String> strs = new ArrayList<>();
        for (Music song : arrayOfSong) {
            strs.add(song.getId());
        }
        notation.add("songs", String.join(",", strs));
        
        return notation;
    }

    @Override
    public void read(SimpleNotation reader) {
        title = reader.get("title");
        songCount = reader.getInt("songCount");
        
        // TODO implement a way to retrieve songs from playlist
        String[] songsStr = reader.get("songs").split(",");
    }
    
}
