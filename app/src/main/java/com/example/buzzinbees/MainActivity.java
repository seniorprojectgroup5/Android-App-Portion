package com.example.buzzinbees;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.widget.Toolbar;


// tutorial that was followed https://blog.stylingandroid.com/visualiser-part1/
public class MainActivity extends AppCompatActivity implements Visualizer.OnDataCaptureListener {

    private static final int REQUEST_CODE = 0;
    static final String[] PERMISSIONS = new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};

    private Visualizer viz;
    private WaveformView WFView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }
}
