package com.example.buzzinbees;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

public final class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int MEDIA_RES_ID = R.raw.sanctuarytheme;

    private TextView debugText;
    private SeekBar seekbarAudio;
    private ScrollView scrollCont;
    private PlayerAdapter playAdapt;
    private boolean isSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        initializeSeekbar();
        initializePlaybackController();
        Log.d(TAG, "onCreate:finished");

    }



    @Override
    protected void onStart() {
        super.onStart();
        playAdapt.loadMedia(MEDIA_RES_ID);
        Log.d(TAG, "onStart: create media player");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isChangingConfigurations() && playAdapt.isPlaying()) {
            Log.d(TAG, "onStop: don't release mediaplayer as screen is rotating & playing");
        } else {
            playAdapt.release();
            Log.d(TAG, "onStop: release mediaplayer");
        }
    }

    private void initializeUI() {
        //set ids to elements
        debugText = findViewById(R.id.text_debug);
        Button bPlay = findViewById(R.id.button_play);
        Button bPause = findViewById(R.id.button_pause);
        Button bReset = findViewById(R.id.button_reset);
        seekbarAudio = findViewById(R.id.seekbar_audio);
        scrollCont = findViewById(R.id.scroll_container);

        bPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAdapt.pause();
            }
        });

        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAdapt.play();
            }
        });

        bReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAdapt.reset();
            }
        });
    }

    private void initializePlaybackController() {
        MediaPlayerHolder mph = new MediaPlayerHolder(this);
        Log.d(TAG, "initializePlaybackInfoListener: created MediaPlayerHolder");
        mph.setPlaybackInfoListener(new PlaybackListener());
        playAdapt = mph;
        Log.d(TAG, "initializePlaybackController: MediaPlayerHolder progress callback set");
    }

    private void initializeSeekbar () {
        seekbarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int userSelectPosition = 0;

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeeking = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    userSelectPosition = progress;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeeking = false;
                playAdapt.seekTo(userSelectPosition);
            }
        });
    }

    public class PlaybackListener extends PlaybackInfoListener {
        @Override
        public void onDurationChanged(int duration) {
            seekbarAudio.setMax(duration);
            Log.d(TAG, String.format("setPlaybackDuration: metMax(%d)", duration));
        }

        @Override
        public void onPositionChanged(int position) {
            if(!isSeeking) {
                seekbarAudio.setProgress(position, true);
                Log.d(TAG, String.format("setPlaybackPosition: setProgress(%d)", position));
            }
        }

        @Override
        public void onStateChanged(@State int state) {
            String stateToString = PlaybackInfoListener.convertStateToString(state);
            onLogUpdated(String.format("onStateChange(%s)", stateToString));
        }

        @Override
        public void onPlaybackCompleted() {
            //empty function
        }

        @Override
        public void onLogUpdated(String message) {
            if(debugText != null) {
                debugText.append(message);
                debugText.append("\n");
                //move scroll container focus to the end
                scrollCont.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollCont.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        }
    }

}
