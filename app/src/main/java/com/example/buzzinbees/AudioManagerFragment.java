package com.example.buzzinbees;
/*
This fragment handles the audio player in the app
 */

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedList;

public class AudioManagerFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    // access main activity
    MainActivity main;

    // visualizer
    private Visualizer mVisualizer;
    private MyVisualizer visualizerView;
    ByteBuffer byteBuffer;
    FloatBuffer floatBuffer;
    LinkedList<String> byteStrings;
    ImageView visHex1, visHex2, visHex3, visHex4;
    ConstraintLayout visualizerContainer;

    // seekbar
    private Runnable seekbarUpdate;
    SeekBar songSeekbar;
    Boolean isSeeking;
    Handler mHandler;

    // audio
    private boolean isPlaying;
    private MediaPlayer player;
    private MediaPlayer.OnCompletionListener completeListener;
    public Song songPlaying;
    TextView songDisplay;
    ImageButton playBack, btnNext, btnPrev, btnLoop, btnShuffle;
    Boolean isLooping, isShuffled;

    // bluetooth
    private int eff1;
    private int eff2;
    private int eff3;


    //** ONCREATE VIEW **//
    public AudioManagerFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audiomanager,container,false);
        main = (MainActivity) getActivity();

        // set up the interface to send data through bluetooth
        mListener = (OnFragmentInteractionListener) getActivity();
        eff1 = 1;
        eff2 = 2;
        eff3 = 3;



        // Visualizer set up
        //      this stores the fft bytes
        byteStrings = new LinkedList<String>();
        //      ref to entire visualizer container
        visualizerContainer = view.findViewById(R.id.VisualizerContainer);
        //      ref to drawable hexagons for visualizer
        visHex1 = view.findViewById(R.id.img_visHex1);
        visHex2 = view.findViewById(R.id.img_visHex2);
        visHex3 = view.findViewById(R.id.img_visHex3);
        visHex4 = view.findViewById(R.id.img_visHex4);
        //      ref to visualizer view (runs the canvas and draw updates)
        visualizerView = view.findViewById(R.id.visualizer);
        //      scale hexagons to proper size
        visualizerView.scaleHexagons(visHex1,Constant.HEX1SCALE);
        visualizerView.scaleHexagons(visHex2,Constant.HEX2SCALE);
        visualizerView.scaleHexagons(visHex3,Constant.HEX3SCALE);
        visualizerView.scaleHexagons(visHex4,Constant.HEX4SCALE);



        // Seekbar set up
        songSeekbar = view.findViewById(R.id.song_seekBar);
        isSeeking = false;
        mHandler = new Handler();
        //      update listener
        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    if(player != null) {
                        player.seekTo(progress);
                        Log.d("SEEK", "seeked to " + progress);
                    }
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });



        // Audio set up
        //      create media player to handle the playback
        player = null;
        //      instantiates the song
        songPlaying = new Song();
        //      create buttons
        playBack = view.findViewById(R.id.btn_PlayPause);
        btnNext = view.findViewById(R.id.btn_Next);
        btnPrev = view.findViewById(R.id.btn_Prev);
        btnLoop = view.findViewById(R.id.btn_Loop);
        btnShuffle = view.findViewById(R.id.btn_Shuffle);
        btnLoop.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
        btnShuffle.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
        //      song name display over seekbar
        songDisplay = view.findViewById(R.id.txt_SongArtistTitle);
        //      set isPlaying boolean
        isPlaying = false;
        isLooping = false;
        isShuffled = false;
        //      decide what happens when the song is done playing
        completeListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //stop the music and release the media player
                player.stop();
                player.release();
                player = null;
                //reset isPlaying to false
                isPlaying = false;
                //turn off the visualizer
                mVisualizer.setEnabled(false);
                //turn the button text to play again
                visualizerView.scaleHexagons(visHex1,Constant.HEX1SCALE);
                visualizerView.scaleHexagons(visHex2,Constant.HEX2SCALE);
                visualizerView.scaleHexagons(visHex3,Constant.HEX3SCALE);
                visualizerView.scaleHexagons(visHex4,Constant.HEX4SCALE);
                //reset hexagons

                if(!isLooping && !isShuffled){
                    //not looping, not shuffle order
                    changeSong(1);
                }
                else if (isLooping){
                    //looping one song
                    changeSong(0);
                }
                else if (isShuffled){
                    //shuffled play order
                    //shuffle case
                }

                setSongDisplay();
                playBack.performClick();
            }
        };
        //      handle play/pause button click
        playBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if a song is playing
                if (isPlaying) {
//                    Log.d("PLAY","is Playing true");
                    player.pause();
                    //if a song is playing, the button will stop playback

                    isPlaying = false;
                    mVisualizer.setEnabled(false);
                    ((ImageButton) v).setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    byteStrings.clear();//clears the byte strings
                    //reset hexagon scale
                    visualizerView.scaleHexagons(visHex1,Constant.HEX1SCALE);
                    visualizerView.scaleHexagons(visHex2,Constant.HEX2SCALE);
                    visualizerView.scaleHexagons(visHex3,Constant.HEX3SCALE);
                    visualizerView.scaleHexagons(visHex4,Constant.HEX4SCALE);

                    decideWhatEffectToSend(0);

                } else {
                    //otherwise, the play button will instantiate the song and the visualizer responding to it
                    try {
                        Log.d("Player","Set up player");
                        //instantiate the mediaplayer
                        player = new MediaPlayer();
                        // set the song to tbe played
                        player.setDataSource(songPlaying.path);

                        // create a listener to set up the seek bar, start the music, and loop the seekbar to constantly update
                        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                Log.d("SEEK", "preped to go");
                                songSeekbar.setMax(player.getDuration());
                                player.seekTo(songSeekbar.getProgress());
                                player.start();
                                changeSeekBar();
                            }
                        });

                        // create a listener that handles what happens when a song ends
                        player.setOnCompletionListener(completeListener);

                        // prepare the media player to begin playing, and record that we have started playing
                        player.prepare();
                        isPlaying = true;

                        // set up the visualizer functionality, and make it visible
                        setupVisualizerFxAndUI();
                        mVisualizer.setEnabled(true);

                        // replace the play button with the pause button
                        ((ImageButton) v).setImageResource(R.drawable.ic_pause_black_24dp);

                    } catch (IOException e) {
                        //if the "try" above fails, do this
                        e.printStackTrace();
                        //Toast.makeText(AudioManagerFragment.this, "No song to play", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //     goes to the previous song
        btnPrev.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               songSeekbar.setProgress(0);
               changeSong(-1);
               resetPlayerState();
           }
        });
        //      goes to the next song
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songSeekbar.setProgress(0);
                changeSong(1);
                resetPlayerState();
            }
        });
        //      loops through the current song again and again
        btnLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLooping){
                    isLooping = true;
                    btnLoop.setForegroundTintList(ColorStateList.valueOf(Color.BLACK));
                }
                else{
                    isLooping = false;
                    btnLoop.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
                }
            }
        });



        // Return the view to the frame
        return view;
    }




    // Handle the Media Player
    //      resets the media player and visualizer
    public void resetPlayer(){
        if(player !=null){
            if (isPlaying == true){
                player.stop();
                isPlaying = false;
            }

            player.release();
            if(mVisualizer != null){
                mVisualizer.setEnabled(false);
            }
            byteStrings.clear();//clears the byte strings
            //reset hexagon scale
            visualizerView.scaleHexagons(visHex1,Constant.HEX1SCALE);
            visualizerView.scaleHexagons(visHex2,Constant.HEX2SCALE);
            visualizerView.scaleHexagons(visHex3,Constant.HEX3SCALE);
            visualizerView.scaleHexagons(visHex4,Constant.HEX4SCALE);
        }
    }
    //      quick function to cut down on code
    public void resetPlayerState(){
        resetPlayer();
        setSongDisplay();
        playBack.performClick();
    }


    // Handle the songs
    //      updates displayed song info
    public void setSongDisplay(){
       String songInfo = songPlaying.songName + " - " + songPlaying.songArtist;
       songDisplay.setText(songInfo);
   }
    //      updates the current song from a list
    public void changeSong(int order){
        int toIndex = songPlaying.index + order;

        if (toIndex > (main.arraySongList.size()-1)){
            toIndex = 0;
        }
        else if (toIndex < 0){
            toIndex = (main.arraySongList.size()-1);
        }

        if(main.arraySongList.get(toIndex) != null){
            songPlaying = main.arraySongList.get(toIndex);
        }
        else{
            CharSequence s = "invalid song index";
            Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
        }
    }


    // Visualizer
    //      toggles visualizer visibility
    public void toggleVisualizer(int curFrag){
        if(curFrag == Constant.FRAGVAL_VISUALIZER){
            //current fragment in main activity is the visualizer page
            visualizerContainer.setVisibility(View.VISIBLE);
        }
        else{
            visualizerContainer.setVisibility(View.GONE);
        }
    }
    //      the work horse that sets up the visualizer
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
                        for(int i = 0; i<=6; i++){
                            //convert first 6 bins of fft data to string
                            Byte b = bytes[i];
                            byteStrings.add(i,b.toString());
                            //continuously replace first 6 elements of linked list with first 6 bin values
                        }

                        Float f = Float.parseFloat(byteStrings.get(3)); // init initial float


                        Float f1= f;
                        Float f2=f;
                        Float f3=f;
                        //init float values for past bin values

                        if((byteStrings.size()>7)){
                            f1 = Float.parseFloat(byteStrings.get(10));
                            decideWhatEffectToSend(eff3);
                        }
                        if((byteStrings.size()>14)){
                            f2 = Float.parseFloat(byteStrings.get(17));
                            decideWhatEffectToSend(eff2);
                        }
                        if((byteStrings.size()>21)){
                            f3 = Float.parseFloat(byteStrings.get(24));
                            decideWhatEffectToSend(eff3);
                        }

                        Log.d("Data", f1 + ", " + f2 + ", " + f3);

                        //scale hexagon in visualizer
                        visualizerView.lerpScaleHexagons(visHex1,1,f);
                        visualizerView.lerpScaleHexagons(visHex2,2,f1);
                        visualizerView.lerpScaleHexagons(visHex3,3,f2);
                        visualizerView.lerpScaleHexagons(visHex4,4,f3);
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, true);
    }

    // Bluetooth sending of effects
    //      decides what effect to send
    public void decideWhatEffectToSend(int eff){
        if(main.canSendData) {
            // TODO: clean this up so we can start sending that gud gud
            switch (eff) {
                case 1:
                    try {
                        // effect 4 is the softest tick
                        mListener.sendEffect4();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    try {
                        // effect 1 is a tick
                        mListener.sendEffect1();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    try {
                        // effect 7 is a hard tick
                        mListener.sendEffect7();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
            // add a temporary delay (mainly for debugging so we can read what is happening)
            mListener.waitToSendInfo();
        }
    }


    // Seekbar
    //      recursively updates the seekbar
    private void changeSeekBar() {
        if (player != null) {
            songSeekbar.setProgress(player.getCurrentPosition());

            if (isPlaying) {
                seekbarUpdate = new Runnable() {
                    @Override
                    public void run() {
                       // changeSeekBar();
                        if(player != null) {
                            songSeekbar.setProgress(player.getCurrentPosition());
                        }
                    }
                };
                mHandler.postDelayed(seekbarUpdate, 1000);
            }
        }
        else{
           Log.d("SEEK BAR","player is null");
        }
    }
}
