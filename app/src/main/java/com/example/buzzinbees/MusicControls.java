package com.example.buzzinbees;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

public class MusicControls extends Fragment {

    // use https://stackoverflow.com/questions/6672066/fragment-inside-fragment as a ref to set up child frags
    // setup buttons
    Button connectBluetooth, callVisualizer, musicQueue, playPauseBtn, nextBtn, prevBtn, shflBtn, loopBtn;

    SeekBar songSeekBar;

    public static MusicControls newInstance() {
        return new MusicControls();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // find all the views and set their onClick listener
        connectBluetooth = getView().findViewById(R.id.btn_Connect);
        connectBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call the bluetooth class to create the connection
            }
        });

        callVisualizer = getView().findViewById(R.id.btn_toVisualizer);
        callVisualizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call the visualizer fragment
            }
        });

        musicQueue = getView().findViewById(R.id.btn_toQueue);
        musicQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to the playlists fragment and show the current music queue
            }
        });

        playPauseBtn = getView().findViewById(R.id.btn_PlayPause);
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // switch play/pause audio
            }
        });

        nextBtn = getView().findViewById(R.id.btn_Next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to the next song in the queue
            }
        });

        prevBtn = getView().findViewById(R.id.btn_Prev);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to the previous song in the queue
            }
        });

        shflBtn = getView().findViewById(R.id.btn_Shuffle);
        shflBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // shuffle the queue
            }
        });

        loopBtn = getView().findViewById(R.id.btn_Loop);
        loopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // loop the queue (copy the current queue and add it to the end)
            }
        });

        songSeekBar = getView().findViewById(R.id.song_seekBar);


        return inflater.inflate(R.layout.music_controls_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
