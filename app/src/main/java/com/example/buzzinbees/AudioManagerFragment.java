package com.example.buzzinbees;
/*
This fragment handles the audio player in the app
 */

import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import java.util.Arrays;
import java.util.LinkedList;

public class AudioManagerFragment extends Fragment {

    //create variables
    private boolean isPlaying;
    private MediaPlayer player;

    private Visualizer mVisualizer;
    private MyVisualizer visualizerView;

    private MediaPlayer.OnCompletionListener completeListener;

    ByteBuffer byteBuffer;
    FloatBuffer floatBuffer;
    LinkedList<String> byteStrings;


    ImageView visHex1;
    ImageView visHex2;
    ImageView visHex3;
    ImageView visHex4;

    ConstraintLayout visualizerContainer;

    float HEX1SCALE = 0.7087f;
    float HEX2SCALE = 0.5748f;
    float HEX3SCALE = 0.5354f;
    float HEX4SCALE =0.3386f;

    public AudioManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_audiomanager,container,false);


        Log.d("PLAY","AUDIO MANAGER LOADED ");


        byteStrings = new LinkedList<String>();

        //create media player to handle the playback
        player = new MediaPlayer();


        //create play button
        ImageButton playBack = view.findViewById(R.id.btn_PlayPause);

        visualizerContainer = view.findViewById(R.id.VisualizerContainer);

        visHex1 = view.findViewById(R.id.img_visHex1);
        visHex2 = view.findViewById(R.id.img_visHex2);
        visHex3 = view.findViewById(R.id.img_visHex3);
        visHex4 = view.findViewById(R.id.img_visHex4);

        visualizerView = view.findViewById(R.id.visualizer);

        visualizerView.scaleHexagons(visHex1,HEX1SCALE);
        visualizerView.scaleHexagons(visHex2,HEX2SCALE);
        visualizerView.scaleHexagons(visHex3,HEX3SCALE);
        visualizerView.scaleHexagons(visHex4,HEX4SCALE);


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
                //playBack.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
            }
        };




       playBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PLAY","play click");
                //check if a song is playing
                if (isPlaying) {
                    Log.d("PLAY","is Playing true");
                    //if a song is playing, the button will stop playback
                    player.stop();
                    player.release();
                    isPlaying = false;
                    mVisualizer.setEnabled(false);
                    ((ImageButton) v).setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    byteStrings.clear();//clears the byte strings
                    //reset hexagon scale NEEDS TO BE MOVED
                    visualizerView.lerpScaleHexagons(visHex1,1,HEX1SCALE);
                    visualizerView.lerpScaleHexagons(visHex2,2,HEX2SCALE);
                    visualizerView.lerpScaleHexagons(visHex3,3,HEX3SCALE);
                    visualizerView.lerpScaleHexagons(visHex4,4,HEX4SCALE);
                } else {
                    //otherwise, the play button will instantiate the song and the visualizer responding to it
                    try {
                        Log.d("PLAY"," Try to play music");
                        //instantiate the mediaplayer
                        player = new MediaPlayer();
                        Log.d("PLAY"," Create Media Player");
                        //assign the song to play
                        AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.sleepyhead); //pulling a RAW FILE, not from device storage!
                        Log.d("PLAY","Load Song");
                        player.setDataSource(assetFileDescriptor);
                        Log.d("PLAY","Set Song");
                        //check for song completion
                        player.setOnCompletionListener(completeListener);
                        Log.d("PLAY","CompletionListenr");
                        player.prepare();
                        Log.d("PLAY","Song Prepared");
                        //set up visualizer function
                        setupVisualizerFxAndUI();
                        //start playback
                        player.start();
                        Log.d("PLAY","Song Started");
                        mVisualizer.setEnabled(true);
                        isPlaying = true;
                        Log.d("PLAY","IsPlaying True");
                        //change button text to stop
                        ((ImageButton) v).setImageResource(R.drawable.ic_pause_black_24dp);
                    } catch (IOException e) {
                        //if the "try" above fails, do this
                        e.printStackTrace();
                        //Toast.makeText(AudioManagerFragment.this, "No song to play", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        return view;
    }



    private void setupVisualizerFxAndUI() {
        // Create the Visualizer object and attach it to our media player.
        mVisualizer = new Visualizer(player.getAudioSessionId());

        //set up the visualizer
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);

        mVisualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_NONE);

        //set up the data capture for waveform data and fft data
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                        //update the visualizer view with the waveform
                        visualizerView.updateVisualizer(bytes);
                        //visualizerView.scaleHexagons(visHex1,visHex2,visHex3,visHex4);

                    }

                    public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                        //code for fft data capture, likely a print to screen to start
                        //base frequencies 60-250 hz

                        Log.d("BYTES", Arrays.toString(bytes));

                        for(int i = 0; i<=6; i++){
                            //convert first 6 bins of fft data to string
                            Byte b = bytes[i];
                            byteStrings.add(i,b.toString());
                            //continuously replace first 6 elements of linked list with first 6 bin values
                        }
                        Log.d("FFT",byteStrings.toString());



                        Float f = Float.parseFloat(byteStrings.get(3)); // init initial float


                        Float f1= f;
                        Float f2=f;
                        Float f3=f;
                        //init float values for past bin values

                        if((byteStrings.size()>7)){
                            f1 = Float.parseFloat(byteStrings.get(10));
                        }
                        if((byteStrings.size()>14)){
                            f2 = Float.parseFloat(byteStrings.get(17));
                        }
                        if((byteStrings.size()>21)){
                            f3 = Float.parseFloat(byteStrings.get(24));
                        }


                        //scale hexagon in visualizer
                        visualizerView.lerpScaleHexagons(visHex1,1,f);
                        visualizerView.lerpScaleHexagons(visHex2,2,f1);
                        visualizerView.lerpScaleHexagons(visHex3,3,f2);
                        visualizerView.lerpScaleHexagons(visHex4,4,f3);
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, true);
    }



}
