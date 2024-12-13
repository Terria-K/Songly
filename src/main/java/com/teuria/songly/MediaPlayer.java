package com.teuria.songly;

import com.sun.jna.NativeLibrary;
import com.teuria.songly.api.MusicController;
import com.teuria.songly.models.Music;
import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import javax.swing.SwingUtilities;
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.media.Media;
import uk.co.caprica.vlcj.media.MediaEventAdapter;
import uk.co.caprica.vlcj.media.MediaEventListener;
import uk.co.caprica.vlcj.media.MediaParsedStatus;
import uk.co.caprica.vlcj.media.Meta;
import uk.co.caprica.vlcj.media.MetaData;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class MediaPlayer {
    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;
    private boolean isInitialized;
    private boolean paused;
    
    private Music previousMusic;
    private Music currentMusic;
    private Music nextMusic;
    
    
    
    public MediaPlayer() {
        NativeLibrary.addSearchPath(
                RuntimeUtil.getLibVlcLibraryName(), "native");
        NativeLibrary.addSearchPath(
                RuntimeUtil.getLibVlcCoreLibraryName(), "native");
        // Privacy compliant code: --no-metadata-network-access
    }
    
    public static MediaPlayer init() {
        MediaPlayer player = new MediaPlayer();
        
        File file = new File("native");
        if (System.getProperty("os.name").startsWith("Windows")) {
            if (file.exists() && file.isDirectory()) {
                player.factory = 
                        new MediaPlayerFactory("--no-metadata-network-access");
                player.mediaPlayer = 
                        player.factory.mediaPlayers().newEmbeddedMediaPlayer();
                player.isInitialized = true;
            } else {
                player.isInitialized = false;
            }
        } else {
            player.factory = 
                    new MediaPlayerFactory("--no-metadata-network-access");
            player.mediaPlayer = 
                    player.factory.mediaPlayers().newEmbeddedMediaPlayer();
            player.isInitialized = true;  
            
            player.mediaPlayer.events().addMediaPlayerEventListener(
                    new MediaPlayerEventAdapter() {
                
                @Override
                public void finished(
                        uk.co.caprica.vlcj.player.base.MediaPlayer player2) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            player.finished();
                        }
                    });
                }
            });
        }
        
        return player;
    }
    
    public boolean isInitialized() {
        return isInitialized;
    }
    
    
    public void selectAndPlay(Music music) {
        if (!isInitialized) {
            return;
        }
        int index = AppState.getIndexFromMusic(music);
        if (index - 1 >= 0) {
            previousMusic = AppState.getMusicByIndex(index - 1);
        } else {
            previousMusic = null;
        }
        currentMusic = AppState.getMusicByIndex(index);
        
        if (index + 1 < AppState.getMusics().size()) {
            nextMusic = AppState.getMusicByIndex(index + 1);
        } else {
            nextMusic = null;
        }
        mediaPlayer.media().play(music.getPath());
    }
    
    public void play() {
        if (!isInitialized) {
            return;
        }
        paused = false;
        mediaPlayer.controls().play();
    }
    
    public void stop() {
        if (!isInitialized) {
            return;
        }
        mediaPlayer.controls().stop();
    }
    
    public void pause() {
        if (!isInitialized) {
            return;
        }
        paused = true;
        mediaPlayer.controls().pause();
    }
    
    public boolean isPaused() {
        if (!isInitialized) {
            return true;
        }
        
        return paused;
    }

    public void previous() {
        if (!isInitialized) {
            return;
        }
        
        if (previousMusic == null) {
            return;
        }
        
        selectAndPlay(previousMusic);
    }
    
    public void next() {
        if (!isInitialized) {
            return;
        }
        
        if (nextMusic == null) {
            return;
        }
        
        selectAndPlay(nextMusic);
    }
    
    public void finished() {
        if (nextMusic == null) {
            return;
        }
        selectAndPlay(nextMusic);
    }
    
    public Music toMusic(Path dir, Path path) {
        MetaData testMeta = getSongMetadata(path);
        String title = testMeta.get(Meta.TITLE);
        String artist = testMeta.get(Meta.ARTIST);
        String album = testMeta.get(Meta.ALBUM);
        return new Music(title, artist, album, dir.toString(), path.toString());
    }
    
    public MetaData getSongMetadata(Path path) {
        Media media = null;
        try {
            media = parseMedia(path.toString());
            // FIXME: could be dangerous to return the MetaData itself while
            // it is released.
            return media.meta().asMetaData();
        } catch (InterruptedException _e) {
            return null;
        } finally {
            media.release(); // assuming that the MetaData is not a part of the 
                             // Media chunk pointer, we should be good using it
                             // after.
        }
    }
    
    private Media parseMedia(String path) throws InterruptedException {
        final Media media = factory.media().newMedia(path);
        // prevents race condition
        final CountDownLatch latch = new CountDownLatch(1);
        
        MediaEventListener listener = new MediaEventAdapter() {
            @Override
            public void mediaParsedChanged(
                    Media media, 
                    MediaParsedStatus newStatus) {
                latch.countDown();
            }
        };
        
        try {
            media.events().addMediaEventListener(listener);
            if (media.parsing().parse()) {
                latch.await();
                return media;
            } else {
                return null;
            }
        } finally {
            media.events().removeMediaEventListener(listener);
        }
    }
}
