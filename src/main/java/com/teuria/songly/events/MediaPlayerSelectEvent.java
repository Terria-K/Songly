package com.teuria.songly.events;

import com.teuria.songly.models.Music;

public interface MediaPlayerSelectEvent {
    void select(Music music);
}
