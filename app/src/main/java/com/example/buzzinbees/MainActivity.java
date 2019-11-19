package com.example.buzzinbees;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean isPlaying;
    private MediaPlayer player;
    private MyVisualizer visualizerView;
    private Visualizer mVisualizer;
    private MediaPlayer.OnCompletionListener completeListener;


    List<String> permissions = new ArrayList<String>();

    private boolean askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int RECORD_AUDIO = checkSelfPermission(Manifest.permission.RECORD_AUDIO );
            if (RECORD_AUDIO != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 1);
            } else
                return false;
        } else
            return false;
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            boolean result = true;
            for (int i = 0; i < permissions.length; i++) {
                result = result && grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
            if (!result) {
                Toast.makeText(this, "..", Toast.LENGTH_LONG).show();
            } else {
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        askPermission();

        Log.d("CRASH", "starts onCreate");
        super.onCreate(savedInstanceState);
        Log.d("CRASH", "Load instance");
        setContentView(R.layout.activity_main);
        Log.d("CRASH", "Loaded XML");

        player = new MediaPlayer();
        Log.d("CRASH", "Initialized MediaPlayer");


        final Button record = findViewById(R.id.btntoggelRec);
        final Button playBack = findViewById(R.id.btnPlayRec);
        Log.d("CRASH", "Loaded buttons");
        visualizerView = findViewById(R.id.visualizer);
        Log.d("CRASH", "Loaded visualizer");

        isPlaying = false;



        completeListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("CRASH", "completion listener started");
                player.stop();
                player.release();
                isPlaying = false;
                record.setEnabled(true);
                mVisualizer.setEnabled(false);
                playBack.setText("Play");
            }
        };

        playBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CRASH", "button clicked");
                if (isPlaying) {
                    Log.d("CRASH", "isPlaying true");
                    player.stop();
                    player.release();
                    Log.d("CRASH", "mediaplayer released");
                    isPlaying = false;
                    mVisualizer.setEnabled(false);
                    ((Button) v).setText("Play");
                } else {
                    Log.d("CRASH", "isPlaying false");
                    try {
                        Log.d("CRASH", "start of process");
                        player = new MediaPlayer();
                        Log.d("CRASH", "player defined");
                        AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.sanctuarytheme);
                        Log.d("CRASH", "song file assigned to variable");
                        player.setDataSource(assetFileDescriptor);
                        Log.d("CRASH", "data source assigned");
                        player.setOnCompletionListener(completeListener);
                        Log.d("CRASH", "set completion listener");
                        player.prepare();
                        Log.d("CRASH", "prepared media player");
                        setupVisualizerFxAndUI();
                        Log.d("CRASH", "set up visualizer");
                        player.start();
                        mVisualizer.setEnabled(true);
                        isPlaying = true;
                        ((Button) v).setText("Stop");
                        Toast.makeText(MainActivity.this, "PlayBack Started", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "No song to play", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void setupVisualizerFxAndUI() {

        Log.d("CRASH", "start vis function");
        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(player.getAudioSessionId());
        Log.d("CRASH", "get audio session");
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
        Log.d("CRASH", "set capture size");
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                        visualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }


}