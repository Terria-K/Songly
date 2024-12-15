package com.teuria.songly;

import com.sun.jna.NativeLibrary;
import com.teuria.songly.events.MediaPlayerSelectEvent;
import com.teuria.songly.events.MediaPlayerTimeEvent;
import com.teuria.songly.models.Music;
import com.teuria.songly.models.Playlist;
import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private long currentTime;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
    private Date currentDate = new Date(0);
    
    private Music previousMusic;
    private Music currentMusic;
    private Music nextMusic;
    
    private Playlist currentPlaylist;
    
    private MediaPlayerSelectEvent selectEvent;
    private MediaPlayerTimeEvent timeEvent;
    
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
                public void timeChanged(
                        uk.co.caprica.vlcj.player.base.MediaPlayer player2,
                        long time) {
                    player.currentTime = time;
                    player.timeEvent.run(time);
                }
                        
                @Override
                public void playing(
                        uk.co.caprica.vlcj.player.base.MediaPlayer player2) {
                    player.currentTime = 0;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            player.selectEvent.select(player.currentMusic);
                        }
                    });
                }
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
    
    public void addSelectEvent(MediaPlayerSelectEvent selectEvent) {
        this.selectEvent = selectEvent;
    }

    public void addTimeEvent(MediaPlayerTimeEvent timeEvent) {
        this.timeEvent = timeEvent;
    }
    
    public void setPlaylist(Playlist playlist) {
        this.currentPlaylist = playlist;
    }
    
    public void selectAndPlay(Music music) {
        if (!isInitialized) {
            return;
        }
        
        if (currentPlaylist == null) {
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
        } else {
            int index = AppState.getIndexFromMusic(currentPlaylist, music);
            if (index - 1 >= 0) {
                previousMusic = AppState.getMusicByIndex(
                        currentPlaylist, index - 1);
            } else {
                previousMusic = null;
            }
            currentMusic = AppState.getMusicByIndex(currentPlaylist, index);

            if (index + 1 < AppState.getMusics(currentPlaylist).size()) {
                nextMusic = AppState.getMusicByIndex(
                        currentPlaylist, index + 1);
            } else {
                nextMusic = null;
            }
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
        MetaData meta = getSongMetadata(path);
        String title = meta.get(Meta.TITLE);
        String artist = meta.get(Meta.ARTIST);
        String album = meta.get(Meta.ALBUM);
        return new Music(title, artist, album, dir.toString(), path.toString());
    }
    
    public long getTimeEnd() {
        return mediaPlayer.status().length();
    }
    
    public long getCurrentTime() {
        return currentTime;
    }
    
    public String getTimeEndFormatted() {
        long time = getTimeEnd();
        Date date = new Date(time);

        return dateFormat.format(date);            
    }
    
    public String getCurrentTimeFormatted() {
        long time = getCurrentTime();
        currentDate.setTime(time);
        return dateFormat.format(currentDate);
    }
    
    public void seek(long time) {
        mediaPlayer.controls().setTime(time);
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
    
    public EmbeddedMediaPlayer getPlayer() {
        return mediaPlayer;
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
