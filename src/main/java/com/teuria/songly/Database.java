package com.teuria.songly;

import com.teuria.songly.models.Music;
import com.teuria.songly.models.Playlist;
import com.teuria.songly.notation.SimpleNotation;
import com.teuria.songly.notation.SimpleNotationReader;
import com.teuria.songly.notation.SimpleNotationWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Database {
    private static HashMap<String, Music> musicLibraries = new HashMap<>();
    private static ArrayList<Music> musics = new ArrayList<>();
    private static ArrayList<Playlist> playlists = new ArrayList<>();
    private static ArrayList<String> folders = new ArrayList<>();
    private static boolean rendered = false;
    private static MediaPlayer player;
    
    public static MediaPlayer initPlayer() {
        player = MediaPlayer.init();
        return player;
    }
    
    public static MediaPlayer getPlayer() {
        return player;
    }
    
    public static void addMusic(Music music) {
        musics.add(music);
        musicLibraries.put(music.getId(), music);
        rendered = false;
    }
    
    public static void addMusics(Collection<Music> musics) {
        for (Music music : musics) {
            addMusic(music);
        }
    }
    
    public static void removeMusic(Music music) {
        musics.remove(music);
        musicLibraries.remove(music.getId());
    }
    
    public static void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
    }
    
    public static Collection<Playlist> getPlaylists() {
        return playlists;
    }
    
    public static boolean hasPlaylist(String name) {
        for (Playlist playlist : playlists) {
            if (playlist.getTitle().contains(name)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static void removePlaylist(Playlist playlist) {
        playlists.remove(playlist);
    }
    
    public static void setAsRendered() {
        rendered = true;
    }
    
    public static boolean isRendered() {
        return rendered;
    }
    
    public static Music getMusic(String id) {
        return musicLibraries.get(id);
    }

    public static Collection<Music> getMusics() {
        return musics;
    }
    
    public static Music getMusicByIndex(int index) {
        return musics.get(index);
    }
    
    public static int getIndexFromMusic(Music music) {
        return musics.indexOf(music);
    }
    
    public static void addAndIndex(String folder) {
        if (folders.contains(folder)) {
            return;
        }
        folders.add(folder);
        Path p = Paths.get(folder);
      
        try (Stream<Path> mp = Files.list(p)) {
            addMusics(
                mp.filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.toString().endsWith("mp3") || 
                                file.toString().endsWith("m4a") || 
                                file.toString().endsWith("opus") ||
                                file.toString().endsWith("ogg"))
                    .map(t -> player.toMusic(p, t))
                    .collect(Collectors.toList())
            );
        } catch (IOException e) {

        }
    }
    
    public static Collection<String> getFolders() {
        return folders;
    }
    
    public static void removeFolder(String folder) {
        folders.remove(folder);
        
        ArrayList<Music> toRemove = new ArrayList<>();
        
        for (Music music : musics) {
            if (music.getDirectory().equals(folder)) {                
                toRemove.add(music);
            }
        }
        
        for (Music music : toRemove) {
            removeMusic(music);
        }
    }
    
    public static void save() {
        SimpleNotationWriter writer = new SimpleNotationWriter(); 
        for (String folder : folders) {
            SimpleNotation notation = new SimpleNotation(folder);
            writer.write(notation);
        }
        writer.save("folder.nb");
        writer.clear();
        
        for (Music music : musics) {
            SimpleNotation notation = music.write();
            writer.write(notation);
        }
        
        writer.save("songs.nb");
        writer.clear();
        
        for (Playlist playlist : playlists) {
            SimpleNotation notation = playlist.write();
            writer.write(notation);
        }
        
        writer.save("playlist.nb");
        writer.clear();
    }
    
    public static void load() {
        SimpleNotationReader reader = new SimpleNotationReader();
        if (Files.exists(Paths.get("folder.nb"))) {
            reader.loadFile("folder.nb");
            for (SimpleNotation notation : reader.getAll()) {
                folders.add(notation.getSectionName());
            }            
        }
        
        if (Files.exists(Paths.get("songs.nb"))) {
            reader.loadFile("songs.nb");
            for (SimpleNotation notation : reader.getAll()) {
                Music music = new Music();
                music.read(notation);
                addMusic(music);
            }
        }
        
        if (Files.exists(Paths.get("playlist.nb"))) {
            reader.loadFile("playlist.nb");
            for (SimpleNotation notation : reader.getAll()) {
                Playlist playlist = new Playlist();
                playlist.read(notation);
                addPlaylist(playlist);
            }
        }
    }
}
