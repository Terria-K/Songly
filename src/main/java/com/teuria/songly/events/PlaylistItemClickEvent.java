package com.teuria.songly.events;

import com.teuria.songly.models.Playlist;

public interface PlaylistItemClickEvent {
    void run(Playlist playlist);
}
