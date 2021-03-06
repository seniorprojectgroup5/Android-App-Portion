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
//import android.util.Log;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class AudioManagerFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    // access main activity
    MainActivity main;

    // visualizer
    private Visualizer mVisualizer;
    private MyVisualizer visualizerView;
    public int qIndex;
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
    private int stopeff, eff1, eff2, eff3, eff4, eff5;

    ImageButton btnFav;

    ArrayList<Integer> shflIndex;
    int curShuffleIndex;


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
        stopeff = 0;
        eff1 = 1;
        eff2 = 2;
        eff3 = 3;
        eff4 = 4;
        eff5 = 5;



        // Visualizer set up
        //      this stores the fft bytes
        byteStrings = new LinkedList<String>();
        btnFav = view.findViewById(R.id.btn_Fav);
        curShuffleIndex = 0;
        shflIndex = new ArrayList<>();
        //create arraylist of indecies to be shuffled
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
//                        Log.d("SEEK", "seeked to " + progress);
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
                resetPlayer();
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

                boolean changed = false;

                if(!isLooping && !isShuffled){
                    //not looping, not shuffle order
                    changed = changeSong(1);
                }
                else if (isLooping){
                    //looping one song
                    changed = changeSong(0);
                }
                else if (isShuffled){
                    //shuffled play order
                    //shuffle case
                    updateShuffleIndex(1);//update index of shuffle index
                    changed = changeSong(0);

                }

                if(changed) {
                    setSongDisplay();
                    playBack.performClick();
                }
                else{
                    Toast.makeText(getContext(),"[ERROR PLAYING NEXT SONG]",Toast.LENGTH_SHORT);
                    main.playingQ = new Playlist();
                    songPlaying = new Song();
                    setSongDisplay();
                }
            }
        };

        //      handle play/pause button click
        playBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if a song is playing
                if (isPlaying) {
                   // Log.d("PLAY","is Playing true");
                    player.pause();
                    //if a song is playing, the button will stop playback

                    isPlaying = false;
                    mVisualizer.setEnabled(false);
                    ((ImageButton) v).setImageResource(R.drawable.ic_play_arrow_black_48dp);
                    byteStrings.clear();//clears the byte strings
                    //reset hexagon scale
                    visualizerView.scaleHexagons(visHex1,Constant.HEX1SCALE);
                    visualizerView.scaleHexagons(visHex2,Constant.HEX2SCALE);
                    visualizerView.scaleHexagons(visHex3,Constant.HEX3SCALE);
                    visualizerView.scaleHexagons(visHex4,Constant.HEX4SCALE);

                    decideWhatEffectToSend(stopeff);

                } else {
                    //otherwise, the play button will instantiate the song and the visualizer responding to it
                    try {
                        mListener.waitToSendInfo();
//                        Log.d("Player","Set up player");
                        //instantiate the mediaplayer
                        player = new MediaPlayer();
//                        Log.d("PLAY","Load Song");
                        if (songPlaying.index != -1) {
                            player.setDataSource(songPlaying.path);

                            // create a listener to set up the seek bar, start the music, and loop the seekbar to constantly update
                            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
//                                    Log.d("SEEK", "preped to go");
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
                            ((ImageButton) v).setImageResource(R.drawable.ic_pause_black_48dp);
                            }
                            else {
                                //to play pre loaded
                                //AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.sleepyhead); //pulling a RAW FILE, not from device storage!
                                //player.setDataSource(assetFileDescriptor);
                                main.onNavigationItemSelected(main.navigationView.getMenu().findItem(R.id.navigation_songs));
                            }

                            //navigate to song list
                        } catch (IOException e) {
                        //if the "try" above fails, do this
                        e.printStackTrace();
                        Toast.makeText(getContext(),"No Valid Songs to Play",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //     goes to the previous song
        btnPrev.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               boolean changed = false;
               if (isShuffled){
                   updateShuffleIndex(1);//update index of shuffle index
                   changed = changeSong(0);
               }
               else {
                  changed = changeSong(-1);
               }
               if(changed) {
                   resetPlayer();
                   setSongDisplay();
                   playBack.performClick();
                   changeSeekBar();
               }
               else{
                   Toast.makeText(getContext(),"[ERROR PLAYING NEXT SONG]",Toast.LENGTH_SHORT);
                   main.playingQ = new Playlist();
                   songPlaying = new Song();
                   setSongDisplay();
               }
           }
        });

        //      goes to the next song
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean changed = false;

                if (isShuffled){
                    updateShuffleIndex(1);//update index of shuffle index
                    changed = changeSong(0);
                }
                else {
                    changed = changeSong(1);
                }
                if(changed) {
                    resetPlayer();
                    setSongDisplay();
                    playBack.performClick();
                    changeSeekBar();
                }
                else{
                    Toast.makeText(getContext(),"[ERROR PLAYING NEXT SONG]",Toast.LENGTH_SHORT);
                    main.playingQ = new Playlist();
                    songPlaying = new Song();
                    setSongDisplay();
                }
            }
        });

        //      loops through the current song again and again
        btnLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLooping){
                    isLooping = true;
                    btnLoop.setColorFilter(getResources().getColor(R.color.PowderBlue));
//                    Log.d("BTN","Loop Pressed, Loop ON");
                }
                else{
                    isLooping = false;
                    btnLoop.setColorFilter(getResources().getColor(R.color.VisDark));
//                    Log.d("BTN","Loop Pressed, Loop OFF");
                }
            }
        });

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggles shuffle state on and off
//                Log.d("SHUFFLE","Playing queue:"+ main.playingQ.songsArray.toString());
//                Log.d("SHUFFLE","Playing queue size:"+ main.playingQ.songsArray.size());
                if(main.playingQ.getSize()>0) {
                    if (!isShuffled) {
                        isShuffled = true;
                        shuffleSongQueue();
                        btnShuffle.setColorFilter(getResources().getColor(R.color.PowderBlue));
//                        Log.d("BTN", "Shuffle Pressed, Shuffle ON");
                    } else {
                        isShuffled = false;
                        btnShuffle.setColorFilter(getResources().getColor(R.color.VisDark));
//                        Log.d("BTN", "Shuffle Pressed, Shuffle OFF");
                    }
                }else{
                    isShuffled = false;
                    btnShuffle.setColorFilter(getResources().getColor(R.color.VisDark));
//                    Log.d("BTN", "Shuffle Pressed, Shuffle OFF");
                }
            }
        });

        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songPlaying.toggleFav();
                //find song in original queue & all songs queue
                main.addSongToFavs(songPlaying);
            }
        });

        // Return the view to the frame
        return view;
    }

    // Handle the songs
    //      updates displayed song info
    public void setSongDisplay(){
       String songInfo = songPlaying.songName + " - " + songPlaying.songArtist;

       if(songPlaying.songName == null && songPlaying.songArtist == null){
           songInfo = "No song Playing";
       }

       songDisplay.setText(songInfo);

       if(songPlaying.isFav){
           ((ImageButton) btnFav).setImageResource(R.drawable.ic_favorite_black_24dp);
           btnFav.setColorFilter(getResources().getColor(R.color.PaleHoney));
       }
       else{
           ((ImageButton) btnFav).setImageResource(R.drawable.ic_favorite_border_black_24dp);
           btnFav.setColorFilter(getResources().getColor(R.color.VisDark));
       }



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
//        Log.d("SHUFFLE",shflIndex.toString());
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

    public boolean changeSong(int order){
        if(main.playingQ.songsArray.size()>0) {
            //ensure the Q still has songs listed in its array

            if (isShuffled) {
                order = shflIndex.get(curShuffleIndex) - qIndex;
//                Log.d("SHUFFLE", Integer.toString(order));
            }

            int toIndex = qIndex + order;
            //calculate index of next song

            if (toIndex > (main.playingQ.songsArray.size() - 1)) {
                toIndex = 0;
            } else if (toIndex < 0) {
                toIndex = (main.playingQ.songsArray.size() - 1);
            } //ensure the song queue wraps around & never reaches an out of bounds index

            if (main.playingQ.songsArray.get(toIndex) != null) {
                songPlaying = main.playingQ.songsArray.get(toIndex);
            }//ensures song at index isnt null
            else {
                CharSequence s = "invalid song index";
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
            }

            qIndex = toIndex;

            return true; // return true if able to change songs successfully
        }
        return false; // else return false
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
                        }
                        if((byteStrings.size()>14)){
                            f2 = Float.parseFloat(byteStrings.get(17));
                        }
                        if((byteStrings.size()>21)){
                            f3 = Float.parseFloat(byteStrings.get(24));
                        }

//                        Log.d("Data", f1 + ", " + f2 + ", " + f3);
                        parseFftData(f1);

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
        // check if the bluetooth is connected
        if(main.mIsBluetoothConnected){
            // used for debugging
            if(main.canSendData) {
                switch (eff) {
                    case 0:
                        try {
                            // sending 0 should stop the vibrations
                            mListener.stopEffects();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        try {
                            mListener.sendEffect1();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            mListener.sendEffect2();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        try {
                            mListener.sendEffect3();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        try {
                            mListener.sendEffect4();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case 5:
                        try {
                            mListener.sendEffect5();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                // uncomment to DEBUG
//              mListener.waitToSendInfo();
            }
        }
    }

    // logic for understanding freeform data
    public void parseFftData(float f){
        // make the float positive
        if(f<0){
            f = f/-1;
        }

        // set up data ranges and send data for those ranges
        if(f > 0.0 && f < 15.0){
            decideWhatEffectToSend(eff1);
        }else if(f > 16.0 && f < 35.0){
            decideWhatEffectToSend(eff2);
        }else if(f > 36.0 && f < 60.0){
            decideWhatEffectToSend(eff3);
        }
        else if(f > 61.0 && f < 90.0){
            decideWhatEffectToSend(eff4);
        }else{
            decideWhatEffectToSend(eff5);
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
//           Log.d("SEEK BAR","player is null");
        }
    }

    // removes the junk
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        player.release();
    }
}
