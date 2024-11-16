package com.teuria.songly;

import com.sun.jna.NativeLibrary;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.media.Media;
import uk.co.caprica.vlcj.media.MediaEventAdapter;
import uk.co.caprica.vlcj.media.MediaEventListener;
import uk.co.caprica.vlcj.media.MediaParsedStatus;
import uk.co.caprica.vlcj.media.Meta;
import uk.co.caprica.vlcj.media.MetaData;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class MediaPlayer {
    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;
    private boolean isInitialized;
    
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
        }
        
        return player;
    }
    
    public boolean isInitialized() {
        return isInitialized;
    }
    
    
    public void selectAndPlay(String path) {
        if (!isInitialized) {
            return;
        }
        mediaPlayer.media().play(path);
    }
    
    public void play(String path) {
        if (!isInitialized) {
            return;
        }
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
        mediaPlayer.controls().pause();
    }
    
    public void test(String path) {
        MetaData testMeta = getSongMetadata(path);
        System.out.println(testMeta.get(Meta.TITLE));
    }
    
    public MetaData getSongMetadata(String path) {
        Media media = null;
        try {
            media = parseMedia(path);
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
