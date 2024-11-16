package com.teuria.songly.notation;

/**
 * An interface that dictates what a model should write and read.
 */
public interface NotationModel {
    SimpleNotation write();
    void read(SimpleNotation reader);
}
