
package com.teuria.songly.models;

import com.teuria.songly.notation.NotationModel;
import com.teuria.songly.notation.SimpleNotation;
import java.util.Base64;

public class Music implements NotationModel {
    private String title;
    private String artist;
    private String album;
    private String directory; 
    private String path;
    private String id;

    
    public Music (String title, String artist, String album, String directory, 
            String path) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.directory = directory;
        this.path = path;
        this.id = Base64.getEncoder().encodeToString(path.getBytes());
    }
    
    public Music() {}

    
    public String getTitle() {
        return title;
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

    public String getDirectory() {
        return directory;
    }
    
    @Override
    public SimpleNotation write() {
     SimpleNotation notation = new SimpleNotation(
             Base64.getEncoder().encodeToString(path.getBytes()));   
    
     notation.add("title", title);
     notation.add("artist", artist);
     notation.add("album", album);
     notation.add("path", path);
     notation.add("directory", directory);
     
     return notation;
    }

    @Override
    public void read(SimpleNotation reader) {
        this.title = reader.get("title");
        this.artist = reader.get("artist");
        this.album = reader.get("album");
        this.path = reader.get("path");
        this.directory = reader.get("directory");
        this.id = reader.getSectionName();
    }
    
}
