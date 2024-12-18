package com.teuria.songly.events;

import com.teuria.songly.models.Playlist;


public interface PlaylistEditOkEvent {
    void run(Playlist playlist, String name);
}
