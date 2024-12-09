
package com.teuria.songly.models;

import com.teuria.songly.notation.NotationModel;
import com.teuria.songly.notation.SimpleNotation;
import java.util.Base64;

public class Music implements NotationModel {
    private String title;
    private String song;
    private String artist;
    private String album;
    private String path;
    private String id;

    public String getTitle() {
        return title;
    }

    public String getSong() {
        return song;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getPath() {
        return path;
    }

    public String getId() {
        return id;
    }
    
    public Music (String title, String song, String artist, String album, 
            String path, String id) {
        this.title = title;
        this.song = song;
        this.artist = artist;
        this.album = album;
        this.path = path;
        this.id = id;
    }

    @Override
    public SimpleNotation write() {
     SimpleNotation notation = new SimpleNotation(
             Base64.getEncoder().encodeToString(id.getBytes()));   
    
     notation.add("title", title);
     notation.add("song", song);
     notation.add("artist", artist);
     notation.add("album", album);
     notation.add("path", path);
     
     return notation;
    }
    

    @Override
    public void read(SimpleNotation reader) {
        this.title = reader.get("title");
        this.song = reader.get("song");
        this.artist = reader.get("artist");
        this.album = reader.get("album");
        this.path = reader.get("path");
        this.id = reader.getSectionName();
    }
    
}
