package com.example.buzzinbees;
/*
This fragment handles the audio player in the app
 */

import android.os.Bundle;

import androidx.annotation.DrawableRes;
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
    private MediaPlayer.OnCompletionListener completeListener;

    ByteBuffer byteBuffer;
    FloatBuffer floatBuffer;
    LinkedList<String> byteStrings;



    public AudioManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_audiomanager,container,false);

        byteStrings = new LinkedList<String>();

        //create media player to handle the playback
        player = new MediaPlayer();


        //create play button
        final ImageButton playBack = view.findViewById(R.id.btn_PlayPause);




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
                playBack.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
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
                    ((Button) v).setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                    byteStrings.clear();//clears the byte strings
                    //reset hexagon scale NEEDS TO BE MOVED
                    /*visualizerView.lerpScaleHexagons(visHex1,1,HEX1SCALE);
                    visualizerView.lerpScaleHexagons(visHex2,2,HEX2SCALE);
                    visualizerView.lerpScaleHexagons(visHex3,3,HEX3SCALE);
                    visualizerView.lerpScaleHexagons(visHex4,4,HEX4SCALE);*/
                } else {
                    //otherwise, the play button will instantiate the song and the visualizer responding to it
                    try {
                        //instantiate the mediaplayer
                        player = new MediaPlayer();
                        //assign the song to play
                        AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.gorrilaz); //pulling a RAW FILE, not from device storage!
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
                        ((Button) v).setBackgroundResource(R.drawable.ic_pause_black_24dp);
                    } catch (IOException e) {
                        //if the "try" above fails, do this
                        e.printStackTrace();
                        //Toast.makeText(AudioManagerFragment.this, "No song to play", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return inflater.inflate(R.layout.fragment_audiomanager, container, false);
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
                        //visualizerView.updateVisualizer(bytes);
                        //CALL TO OPEN VISUALZIER TO UPDATE IT

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
                       /* visualizerView.lerpScaleHexagons(visHex1,1,f);
                        visualizerView.lerpScaleHexagons(visHex2,2,f1);
                        visualizerView.lerpScaleHexagons(visHex3,3,f2);
                        visualizerView.lerpScaleHexagons(visHex4,4,f3);*/
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, true);
    }




}
