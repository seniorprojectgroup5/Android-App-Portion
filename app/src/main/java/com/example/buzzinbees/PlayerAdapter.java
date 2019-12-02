package com.example.buzzinbees;

public interface PlayerAdapter {
    void loadMedia(int resourceID);
    void release();
    boolean isPlaying();
    void play();
    void reset();
    void pause();
    void initializeProgressCallback();
    void seekTo(int position);
}
