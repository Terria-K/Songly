package com.teuria.songly.events;

import com.teuria.songly.models.Playlist;

public interface PlaylistItemDeleteEvent {
    void run(Playlist playlist);
}
