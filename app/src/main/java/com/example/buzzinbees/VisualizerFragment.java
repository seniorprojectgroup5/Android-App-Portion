package com.example.buzzinbees;


import android.media.audiofx.Visualizer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class VisualizerFragment extends Fragment {

    private MyVisualizer visualizerView;


    ImageView visHex1;
    ImageView visHex2;
    ImageView visHex3;
    ImageView visHex4;

    float HEX1SCALE = 0.7087f;
    float HEX2SCALE = 0.5748f;
    float HEX3SCALE = 0.5354f;
    float HEX4SCALE =0.3386f;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_visualizer,container,false);

        visHex1 = view.findViewById(R.id.img_visHex1);
        visHex2 = view.findViewById(R.id.img_visHex2);
        visHex3 = view.findViewById(R.id.img_visHex3);
        visHex4 = view.findViewById(R.id.img_visHex4);

        visualizerView = view.findViewById(R.id.visualizer);

        visualizerView.scaleHexagons(visHex1,HEX1SCALE);
        visualizerView.scaleHexagons(visHex2,HEX2SCALE);
        visualizerView.scaleHexagons(visHex3,HEX3SCALE);
        visualizerView.scaleHexagons(visHex4,HEX4SCALE);


        return view;
    }



}
