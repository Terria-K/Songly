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
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class AppState {
    private static HashMap<String, Music> musicLibraries = new HashMap<>();
    private static HashMap<String, Playlist> playlistMap = new HashMap<>();
    private static LinkedList<Music> musics = new LinkedList<>();
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
    
    // adds a single music to the AppState array
    public static void addMusic(Music music) {
        musics.add(music);
        musicLibraries.put(music.getId(), music);
        rendered = false;
    }
    
    // this adds all musics from an array to the AppState array
    // used by load() function
    public static void addMusics(Collection<Music> musics) {
        for (Music music : musics) {
            addMusic(music);
        }
    }
    
    // removes a single music from the AppState array
    public static void removeMusic(Music music) {
        musics.remove(music);
        musicLibraries.remove(music.getId());
    }
    
    public static void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
        playlistMap.put(playlist.getTitle(), playlist);
    }
    
    public static Playlist getPlaylist(String playlistName) {
        return playlistMap.get(playlistName);
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
    
    public static void addMusicToPlaylist(String playlistName, Music music) {
        Playlist playlist = playlistMap.get(playlistName);
        playlist.addMusic(music);
    }
    
    public static void removeMusicToPlaylist(String playlistName, Music music) {
        Playlist playlist = playlistMap.get(playlistName);
        playlist.addMusic(music);        
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
    
    public static Collection<Music> getMusics(Playlist playlist) {
        return playlist.getSongs();
    }
    
    public static Music getMusicByIndex(int index) {
        return musics.get(index);
    }
    
    public static int getIndexFromMusic(Music music) {
        return musics.indexOf(music);
    }
    
    public static int getIndexFromMusic(Playlist playlist, Music music) {
        return playlist.getSongs().indexOf(music);
    }
    
    public static Music getMusicByIndex(Playlist playlist, int index) {
        return playlist.getSongs().get(index);
    }
    
    // this adds a folder to the music bank, then index all of the audio file
    // contains in that folder
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
