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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AudioManagerFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public static final int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000;

    // old seekbar stuff
    private ScheduledExecutorService exec;
    private Runnable seekbarPositionUpdateTask;

    // new seek bar stuff
    private Runnable seekbarUpdate;


    //create variables
    private int effectID;
    private int currentEffectID;

    MainActivity main;

    private boolean isPlaying;
    private MediaPlayer player;

    private Visualizer mVisualizer;
    private MyVisualizer visualizerView;

    private MediaPlayer.OnCompletionListener completeListener;

    public Song songPlaying;

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


    public AudioManagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_audiomanager,container,false);

        main = (MainActivity) getActivity();

//        Log.d("PLAY","AUDIO MANAGER LOADED ");


        mListener = (OnFragmentInteractionListener) getActivity();
        effectID = 0;
        currentEffectID = 0;


        byteStrings = new LinkedList<String>();
        //this stores the fft bytes

        player = new MediaPlayer();
        //create media player to handle the playback

        //lets make a song
        songPlaying = new Song();

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
        mHandler = new Handler();

        //set isPlaying boolean
        isPlaying = false;
        isLooping = false;
        isShuffled = false;

        // TODO: new seekbar stuff
        //player = new MediaPlayer();

        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



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

       playBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("PLAY","play click");
                //check if a song is playing
                if (isPlaying) {
//                    Log.d("PLAY","is Playing true");
                    //if a song is playing, the button will stop playback
                    player.stop();
                    player.release();
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
//                        Log.d("PLAY"," Try to play music");
                        //instantiate the mediaplayer
                        // todo: uncomment below later
                        player = new MediaPlayer();
//                        Log.d("PLAY"," Create Media Player");
                        //assign the song to play
//                        Log.d("PLAY","Load Song");
                        player.setDataSource(songPlaying.path);

                        // todo mediaplayer attempt
                        //player = MediaPlayer.create(getContext(), songPlaying.path);
                        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                songSeekbar.setMax(player.getDuration());
                                player.start();
                                changeSeekBar();
                            }
                        });
                       //to play pre loaded
                        //AssetFileDescriptor assetFileDescriptor = getResources().openRawResourceFd(R.raw.sleepyhead); //pulling a RAW FILE, not from device storage!
                        //player.setDataSource(assetFileDescriptor);

//                        Log.d("PLAY","Set Song");
                        //check for song completion
                        player.setOnCompletionListener(completeListener);
//                        Log.d("PLAY","CompletionListenr");
                        player.prepare();
//                        Log.d("PLAY","Song Prepared");
                        //set up visualizer function
                        setupVisualizerFxAndUI();
                        //start playback
                        //player.start();
//                        Log.d("PLAY","Song Started");
                        mVisualizer.setEnabled(true);
                        isPlaying = true;

                        //SEEKBAR STUFF
                        //songSeekbar.setMax(player.getDuration()/1000); //set bar to length of song
                        // todo: new seekbar things


                        // TODO: old seekbar things
                        // initializeSeekbar();
                        //startUpdatingCallbackWithPosition();



//                        Log.d("PLAY","IsPlaying True");
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

       btnPrev.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               changeSong(-1);
               resetPlayer();
               setSongDisplay();
               playBack.performClick();
           }
       });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSong(1);
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
                    btnLoop.setForegroundTintList(ColorStateList.valueOf(Color.BLACK));
                }
                else{
                    isLooping = false;
                    btnLoop.setForegroundTintList(ColorStateList.valueOf(Color.GRAY));
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

            // todo: seek bar things
           stopUpdatingCallbackWithPosition(true);
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

//                        Log.d("BYTES", Arrays.toString(bytes));

                        for(int i = 0; i<=6; i++){
                            //convert first 6 bins of fft data to string
                            Byte b = bytes[i];
                            byteStrings.add(i,b.toString());
                            //continuously replace first 6 elements of linked list with first 6 bin values
                        }
//                        Log.d("FFT",byteStrings.toString());


                        Float f = Float.parseFloat(byteStrings.get(3)); // init initial float


                        Float f1= f;
                        Float f2=f;
                        Float f3=f;
                        //init float values for past bin values

                        if((byteStrings.size()>7)){
                            f1 = Float.parseFloat(byteStrings.get(10));
                            effectID = 1;
                            decideWhatEffectToSend(effectID);
                        }
                        if((byteStrings.size()>14)){
                            f2 = Float.parseFloat(byteStrings.get(17));
                            effectID = 4;
                            decideWhatEffectToSend(effectID);
                        }
                        if((byteStrings.size()>21)){
                            f3 = Float.parseFloat(byteStrings.get(24));
                            effectID = 7;
                            decideWhatEffectToSend(effectID);
                        }


                        //scale hexagon in visualizer
                        visualizerView.lerpScaleHexagons(visHex1,1,f);
                        visualizerView.lerpScaleHexagons(visHex2,2,f1);
                        visualizerView.lerpScaleHexagons(visHex3,3,f2);
                        visualizerView.lerpScaleHexagons(visHex4,4,f3);
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, true);
    }


    // send in 300 chunks per second
    public void decideWhatEffectToSend(int i){
//        if(i == currentEffectID){
//            return;
//        }else {
//            if (i == 0) {
//                return;
//            }
//            if (i == 1) {
//                currentEffectID = 1;
//                try {
//                    mListener.sendEffect1();
//                    Log.d("Eff - ", "1");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            if (i == 4) {
//                currentEffectID = 4;
//                try {
//                    mListener.sendEffect4();
//                    Log.d("Eff - ", "4");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            if (i == 7) {
//                currentEffectID = 7;
//                try {
//                    mListener.sendEffect7();
//                    Log.d("Eff - ", "7");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    // TODO: actually make the seekbar work
    private void changeSeekBar(){
       songSeekbar.setProgress(player.getCurrentPosition());

       if(player.isPlaying()){
           seekbarUpdate = new Runnable(){
               @Override
               public void run(){
                   changeSeekBar();
               }
           };
           mHandler.postDelayed(seekbarUpdate, 1000);
       }
    }



    /// TODO: "maybe get rid of idk" shoved code -------------------------------------------------------------------------
    private void initializeSeekbar () {
        //create the seekbar and check for changes
        songSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //set initial seekbar position
            int userSelectPosition = 0;

            //when the user touches the seekbar, start tracking it
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            //if the user moves their finger on the seekbar, check their progress
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    userSelectPosition = progress;
                }
            }

            //when the user lifts their finger from the seekbar, change the song playback time and set the seekbar to that final point
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                seekTo(userSelectPosition);
            }
        });
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public void release() {
//        if(player != null) {
//            logToUI("release() and mediaplayer = null");
//            player.release();
//            player = null;
//        }
//    }
//
//    @Override
//    public boolean isPlaying() {
//        if(player != null) {
//            return player.isPlaying();
//        }
//        return false;
//    }
//
//    @Override
//    public void play() {
//        if(player != null && !player.isPlaying()) {
//            player.start();
//            startUpdatingCallbackWithPosition();
//        }
//    }
//
//    @Override
//    public void reset() {
//        if(player != null) {
//            logToUI("playbackReset()");
//            player.reset();
//            stopUpdatingCallbackWithPosition(true);
//        }
//    }
//
//    @Override
//    public void pause() {
//        if(player != null && player.isPlaying()) {
//            player.pause();
//        }
//    }


    public void seekTo(int position) {
        if(player != null) {
            logToUI(String.format("seekTo() %d ms", position));
            player.seekTo(position);
        }
    }

    private void startUpdatingCallbackWithPosition() {
        if(exec == null) {
            exec = Executors.newSingleThreadScheduledExecutor();
        }
        if(seekbarPositionUpdateTask == null) {
            seekbarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }
        exec.scheduleAtFixedRate(seekbarPositionUpdateTask,0,PLAYBACK_POSITION_REFRESH_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }


    //Reports media playback position to PlaybackProgressCallback
    private void stopUpdatingCallbackWithPosition (boolean resetUIPlaybackPosition) {
        if(exec != null) {
            exec.shutdownNow();
            exec = null;
            seekbarPositionUpdateTask = null;
        }
    }

    private void updateProgressCallbackTask() {
        if(player != null && player.isPlaying()) {
            int currentPosition = player.getCurrentPosition();
        }
    }

    public void initializeProgressCallback() {
        final int duration = player.getDuration();
    }

    private void logToUI(String message) {
    }

}
