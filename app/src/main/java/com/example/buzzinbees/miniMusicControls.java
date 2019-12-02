package com.example.buzzinbees;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;


public class miniMusicControls extends Fragment {

    //hard coded song data
    public static final int MEDIA_RES_ID = R.raw.sanctuarytheme;

    //music player variables
    private SeekBar seekbarAudio;
    private PlayerAdapter playAdapt;
    private boolean isSeeking = false;

    public miniMusicControls() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mini_music_controls, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
