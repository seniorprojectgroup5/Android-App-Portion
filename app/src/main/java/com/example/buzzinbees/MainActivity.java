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

    //create variables
    private boolean isPlaying;
    private MediaPlayer player;
    private MyVisualizer visualizerView;
    private Visualizer mVisualizer;
    private MediaPlayer.OnCompletionListener completeListener;

    //check permissions
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
        //check permissions function call
        askPermission();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create media player to handle the playback
        player = new MediaPlayer();


        //create play button
        final Button playBack = findViewById(R.id.btnPlayRec);
        visualizerView = findViewById(R.id.visualizer);

        //set isPlaying boolean
        isPlaying = false;

        //decide what happens when the song is done playing
        completeListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //stop the music and release the media player
                player.stop();
                player.release();
                //reset isPlaying to false
                isPlaying = false;
                //turn off the visualizer
                mVisualizer.setEnabled(false);
                //turn the button text to play again
                playBack.setText("Play");
            }
        };

        playBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if a song is playing
                if (isPlaying) {
                    //if a song is playing, the button will stop playback
                    player.stop();
                    player.release();
                    isPlaying = false;
                    mVisualizer.setEnabled(false);
                    ((Button) v).setText("Play");
                } else {
                    //otherwise, the play button will instantiate the song and the visualizer responding to it
                    try {
                        //instantiate the mediaplayer
                        player = new MediaPlayer();
                        //assign the song to play
                        AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.wowwow); //pulling a RAW FILE, not from device storage!
                        player.setDataSource(assetFileDescriptor);
                        //check for song completion
                        player.setOnCompletionListener(completeListener);
                        player.prepare();
                        //set up visualizer function
                        setupVisualizerFxAndUI();
                        //start playback
                        player.start();
                        mVisualizer.setEnabled(true);
                        isPlaying = true;
                        //change button text to stop
                        ((Button) v).setText("Stop");
                    } catch (IOException e) {
                        //if the "try" above fails, do this
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "No song to play", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void setupVisualizerFxAndUI() {
        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(player.getAudioSessionId());

        //set up the visualizer
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);

        //set up the data capture for waveform data and fft data
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                        //update the visualizer view with the waveform
                        visualizerView.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                        //code for fft data capture, likely a print to screen to start
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }


}