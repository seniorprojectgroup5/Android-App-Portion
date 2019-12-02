package com.example.buzzinbees;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class MediaPlayerHolder implements PlayerAdapter {

    public static final int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000;

    private final Context context;
    private MediaPlayer mediaplayer;
    private int rID;
    private PlaybackInfoListener pil;
    private ScheduledExecutorService exec;
    private Runnable seekbarPositionUpdateTask;

    public MediaPlayerHolder(ML_List_SongsFragment con) {
        context = con.getContext();
    }


    //sets up mediaplayer instance
    private void initializeMediaPlayer () {
        if (mediaplayer == null) {
            mediaplayer = new MediaPlayer();
            mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopUpdatingCallbackWithPosition(true);
                    logToUI("MediaPlayer playback completed");
                    if(mediaplayer != null) {
                        pil.onStateChanged(PlaybackInfoListener.State.COMPLETED);
                        pil.onPlaybackCompleted();
                    }
                }
            });

            logToUI("mediaplayer = new MediaPlayer()");
        }
    }

    public void setPlaybackInfoListener(PlaybackInfoListener listener) {
        pil = listener;
    }


    //implements playback control
    @Override
    public void loadMedia(int resourceID) {
        rID = resourceID;

        initializeMediaPlayer();

        AssetFileDescriptor assetFileDescriptor = context.getResources().openRawResourceFd(rID);

        try {
            logToUI("load() {1. setDataSource}");
            mediaplayer.setDataSource(assetFileDescriptor);
        } catch (Exception e) {
            logToUI(e.toString());
        }

        try {
            logToUI("load() {2. prepare}");
            mediaplayer.prepare();
        } catch (Exception e) {
            logToUI(e.toString());
        }

        initializeProgressCallback();
        logToUI("initializeProgressCallback");
    }

    @Override
    public void release() {
        if(mediaplayer != null) {
            logToUI("release() and mediaplayer = null");
            mediaplayer.release();
            mediaplayer = null;
        }
    }

    @Override
    public boolean isPlaying() {
        if(mediaplayer != null) {
            return mediaplayer.isPlaying();
        }
        return false;
    }

    @Override
    public void play() {
        if(mediaplayer != null && !mediaplayer.isPlaying()) {
            logToUI(String.format("playbackStart() %s", context.getResources().getResourceEntryName(rID)));
            mediaplayer.start();
            if (pil != null) {
                pil.onStateChanged(PlaybackInfoListener.State.PLAYING);
            }
            startUpdatingCallbackWithPosition();
        }
    }

    @Override
    public void reset() {
        if(mediaplayer != null) {
            logToUI("playbackReset()");
            mediaplayer.reset();
            loadMedia(rID);
            if(pil != null) {
                pil.onStateChanged(PlaybackInfoListener.State.RESET);
            }
            stopUpdatingCallbackWithPosition(true);
        }
    }

    @Override
    public void pause() {
        if(mediaplayer != null && mediaplayer.isPlaying()) {
            mediaplayer.pause();
            if (pil != null) {
                pil.onStateChanged(PlaybackInfoListener.State.PAUSED);
            }
        }
    }

    @Override
    public void seekTo(int position) {
        if(mediaplayer != null) {
            logToUI(String.format("seekTo() %d ms", position));
            mediaplayer.seekTo(position);
        }
    }

    private void startUpdatingCallbackWithPosition() {
        if(exec == null) {
            exec = Executors.newSingleThreadScheduledExecutor();
        }
        if(seekbarPositionUpdateTask == null) {
            seekbarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }
        exec.scheduleAtFixedRate(seekbarPositionUpdateTask,0,PLAYBACK_POSITION_REFRESH_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }


    //Reports media playback position to PlaybackProgressCallback
    private void stopUpdatingCallbackWithPosition (boolean resetUIPlaybackPosition) {
        if(exec != null) {
            exec.shutdownNow();
            exec = null;
            seekbarPositionUpdateTask = null;
            if(resetUIPlaybackPosition && pil != null) {
                pil.onPositionChanged(0);
            }
        }
    }

    private void updateProgressCallbackTask() {
        if(mediaplayer != null && mediaplayer.isPlaying()) {
            int currentPosition = mediaplayer.getCurrentPosition();
            if(pil != null) {
                pil.onPositionChanged(currentPosition);
            }
        }
    }

    @Override
    public void initializeProgressCallback() {
        final int duration = mediaplayer.getDuration();
        if(pil != null) {
            pil.onDurationChanged(duration);
            pil.onPositionChanged(0);
            logToUI(String.format("firing setPlaybackDuration(%d sec)", TimeUnit.MILLISECONDS.toSeconds(duration)));
            logToUI("firing setPlaybackPosition(0)");
        }
    }

    private void logToUI(String message) {
        if(pil != null) {
            pil.onLogUpdated(message);
        }
    }

}
