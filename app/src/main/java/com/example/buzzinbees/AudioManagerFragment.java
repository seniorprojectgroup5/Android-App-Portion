package com.example.buzzinbees;
/*
This fragment handles the audio player in the app
 */

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AudioManagerFragment extends Fragment {

    //create variables


    MainActivity main;

    private boolean isPlaying;
    private MediaPlayer player;

    private Visualizer mVisualizer;
    private MyVisualizer visualizerView;

    private MediaPlayer.OnCompletionListener completeListener;

    public Song songPlaying;
    public int qIndex;

    ByteBuffer byteBuffer;
    FloatBuffer floatBuffer;
    LinkedList<String> byteStrings;

    ImageView visHex1;
    ImageView visHex2;
    ImageView visHex3;
    ImageView visHex4;

    SeekBar songSeekbar;
    Boolean isSeeking;

    Handler mHandler;


    ConstraintLayout visualizerContainer;

    TextView songDisplay;
    ImageButton playBack;
    ImageButton btnNext;
    ImageButton btnPrev;

    ImageButton btnLoop;
    ImageButton btnShuffle;

    Boolean isLooping;
    Boolean isShuffled;

    ArrayList<Integer> shflIndex;
    int curShuffleIndex;


    public AudioManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_audiomanager,container,false);

        main = (MainActivity) getActivity();

        Log.d("PLAY","AUDIO MANAGER LOADED ");


        byteStrings = new LinkedList<String>();
        //this stores the fft bytes

        player = new MediaPlayer();
        //create media player to handle the playback

        //lets make a song
        songPlaying = new Song();

        shflIndex = new ArrayList<>();
        //create arraylist of indecies to be shuffled
        curShuffleIndex = 0;

        //create buttons
        playBack = view.findViewById(R.id.btn_PlayPause);
        btnNext = view.findViewById(R.id.btn_Next);
        btnPrev = view.findViewById(R.id.btn_Prev);

        btnLoop = view.findViewById(R.id.btn_Loop);
        btnShuffle = view.findViewById(R.id.btn_Shuffle);

        btnLoop.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
        btnShuffle.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));

        visualizerContainer = view.findViewById(R.id.VisualizerContainer);
        //ref to entire visualizer container

        visHex1 = view.findViewById(R.id.img_visHex1);
        visHex2 = view.findViewById(R.id.img_visHex2);
        visHex3 = view.findViewById(R.id.img_visHex3);
        visHex4 = view.findViewById(R.id.img_visHex4);
        //ref to drawable hexagons for visualizer

        visualizerView = view.findViewById(R.id.visualizer);
        //ref to visualizer view (runs the canvas and draw updates)

        visualizerView.scaleHexagons(visHex1,Constant.HEX1SCALE);
        visualizerView.scaleHexagons(visHex2,Constant.HEX2SCALE);
        visualizerView.scaleHexagons(visHex3,Constant.HEX3SCALE);
        visualizerView.scaleHexagons(visHex4,Constant.HEX4SCALE);
        //scale hexagons to proper size

        //song name display over seekbar
        songDisplay = view.findViewById(R.id.txt_SongArtistTitle);

        //import seekbar
        songSeekbar = view.findViewById(R.id.song_seekBar);
        isSeeking = false;

        //set isPlaying boolean
        isPlaying = false;
        isLooping = false;
        isShuffled = false;

        //decide what happens when the song is done playing
        completeListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //stop the music and release the media player
                resetPlayer();
                //player.stop();
                //player.release();
                //reset isPlaying to false
                //isPlaying = false;
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
                    updateShuffleIndex(1);//update index of shuffle index
                    changeSong(0);
                    //int iSHFL = shflIndex.get(curShuffleIndex);//get index value of next song to be played
                }

                setSongDisplay();
                playBack.performClick();
            }
        };

       playBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PLAY","play click");
                //check if a song is playing
                if (isPlaying) {
                   // Log.d("PLAY","is Playing true");
                    player.pause();
                    //if a song is playing, the button will stop playback
                    //player.stop();
                    //player.release();
                    isPlaying = false;
                    mVisualizer.setEnabled(false);
                    ((ImageButton) v).setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    byteStrings.clear();//clears the byte strings
                    //reset hexagon scale
                    visualizerView.scaleHexagons(visHex1,Constant.HEX1SCALE);
                    visualizerView.scaleHexagons(visHex2,Constant.HEX2SCALE);
                    visualizerView.scaleHexagons(visHex3,Constant.HEX3SCALE);
                    visualizerView.scaleHexagons(visHex4,Constant.HEX4SCALE);
                } else {
                    //otherwise, the play button will instantiate the song and the visualizer responding to it
                    try {
                        Log.d("PLAY"," Try to play music");
                        //instantiate the mediaplayer
                        player = new MediaPlayer();
                        Log.d("PLAY"," Create Media Player");
                        //assign the song to play
                        Log.d("PLAY","Load Song");
                        if (songPlaying.index != -1) {
                            player.setDataSource(songPlaying.path);

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

                        //SEEKBAR STUFF

                        songSeekbar.setMax(player.getDuration()/1000); //set bar to length of song

                        mHandler = new Handler();

                        Log.d("PLAY","IsPlaying True");
                        //change button text to stop
                        ((ImageButton) v).setImageResource(R.drawable.ic_pause_black_24dp);
                        }
                        else {
                            //to play pre loaded
                            //AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.sleepyhead); //pulling a RAW FILE, not from device storage!
                            //player.setDataSource(assetFileDescriptor);

                            main.onNavigationItemSelected(main.navigationView.getMenu().findItem(R.id.navigation_songs));
                            //navigate to song list
                        }
                    } catch (IOException e) {
                        //if the "try" above fails, do this
                        e.printStackTrace();
                        Toast.makeText(getContext(),"No Valid Songs to Play",Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

       btnPrev.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (isShuffled){
                   updateShuffleIndex(1);//update index of shuffle index
                   changeSong(0);
               }
               else {
                   changeSong(-1);
               }
               resetPlayer();
               setSongDisplay();
               playBack.performClick();
           }
       });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShuffled){
                    updateShuffleIndex(1);//update index of shuffle index
                    changeSong(0);
                }
                else {
                    changeSong(1);
                }
                resetPlayer();
                setSongDisplay();
                playBack.performClick();
            }
        });

        btnLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLooping){
                    isLooping = true;
                    btnLoop.setColorFilter(getResources().getColor(R.color.PowderBlue));
                    Log.d("BTN","Loop Pressed, Loop ON");
                }
                else{
                    isLooping = false;
                    btnLoop.setColorFilter(getResources().getColor(R.color.VisDark));
                    Log.d("BTN","Loop Pressed, Loop OFF");
                }

            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggles shuffle state on and off
                Log.d("SHUFFLE","Playing queue:"+ main.playingQ.songsArray.toString());
                Log.d("SHUFFLE","Playing queue size:"+ main.playingQ.songsArray.size());
                if(main.playingQ.getSize()>0) {
                    if (!isShuffled) {
                        isShuffled = true;
                        shuffleSongQueue();
                        btnShuffle.setColorFilter(getResources().getColor(R.color.PowderBlue));
                        Log.d("BTN", "Shuffle Pressed, Shuffle ON");
                    } else {
                        isShuffled = false;
                        btnShuffle.setColorFilter(getResources().getColor(R.color.VisDark));
                        Log.d("BTN", "Shuffle Pressed, Shuffle OFF");
                    }
                }else{
                    isShuffled = false;
                    btnShuffle.setColorFilter(getResources().getColor(R.color.VisDark));
                    Log.d("BTN", "Shuffle Pressed, Shuffle OFF");
                }
            }
        });


        return view;
    }

   /* public void run(){
        if(player != null){
            int mCurPos = player.getCurrentPosition()/1000;
            songSeekbar.setProgress(mCurPos);
        }
        mHandler.postDelayed(this,1000);
    }*/

   public void setSongDisplay(){

       String songInfo = songPlaying.songName + " - " + songPlaying.songArtist;
       songDisplay.setText(songInfo);

   }


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


    public void toggleVisualizer(int curFrag){
        if(curFrag == Constant.FRAGVAL_VISUALIZER){
            //current fragment in main activity is the visualizer page
            visualizerContainer.setVisibility(View.VISIBLE);
        }
        else{
            visualizerContainer.setVisibility(View.GONE);
        }
    }

    public void changeSong(int order){


        if(isShuffled){
            order = shflIndex.get(curShuffleIndex) - qIndex;
            Log.d("SHUFFLE", Integer.toString(order));
        }

        int toIndex= qIndex + order;
        //calculate index of next song

        if (toIndex > (main.playingQ.songsArray.size()-1)){
           toIndex = 0;
        }
        else if (toIndex < 0){
           toIndex = (main.playingQ.songsArray.size()-1);
        } //ensure the song queue wraps around & never reaches an out of bounds index

        if(main.playingQ.songsArray.get(toIndex) != null){
           songPlaying = main.playingQ.songsArray.get(toIndex);
        }//ensures song at index isnt null
        else{
           CharSequence s = "invalid song index";
           Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
        }

        qIndex = toIndex;

    }

    public void updateShuffleIndex(int n){
       //updates the index of the shuffle array if shuffle is active
        curShuffleIndex += n;
        if(curShuffleIndex > (shflIndex.size()-1)){
            curShuffleIndex = 0;
        }
        else if (curShuffleIndex  < 0){
            curShuffleIndex  = (shflIndex.size()-1);
        }
    }

    public void shuffleSongQueue(){
        if (shflIndex != null){
            shflIndex.clear();
            //clear array

            for (int i = 0; i < main.playingQ.songsArray.size(); i++){
                shflIndex.add(i); // fill shlfIndex with indecies of songs stored in arraylist
            }
            Collections.shuffle(shflIndex); // shuffle array

            curShuffleIndex = 0;
        }
        Log.d("SHUFFLE",shflIndex.toString());
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
