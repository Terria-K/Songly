package com.teuria.songly.events;

import com.teuria.songly.models.Music;

public interface MusicItemClickEvent {
    void run(Music music);
}
