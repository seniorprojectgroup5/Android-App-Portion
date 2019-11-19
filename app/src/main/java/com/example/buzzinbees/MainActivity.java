package com.example.buzzinbees;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public final class MainActivity extends AppCompatActivity {

    //permissions request code
    private static final int MY_PERMISSION_REQUEST = 1;

    //song listview variables
    ArrayList<Song> arrayList;
    ListView listView;
    SongAdapter adapter;

    //...i don't remember what this was so just leave it
    public static final String TAG = "MainActivity";

    //hard coded song data
    public static final int MEDIA_RES_ID = R.raw.sanctuarytheme;

    //music player variables
    private SeekBar seekbarAudio;
    private PlayerAdapter playAdapt;
    private boolean isSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check permissions
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
            }
        } else {
            //if permissions are granted, show everything
            showMusic();
            initializeUI();
            initializeSeekbar();
            initializePlaybackController();
        }

    }

    public void showMusic() {
        //initialize listview and arraylist
        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();

        //get the music from the device
        getMusic();

        //initialize the adapter and assign the arrrayList to it so it has data
        adapter = new SongAdapter(this, arrayList);
        //apply the adapter to the listview to show list
        listView.setAdapter(adapter);

        //on click function for song row
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //play desired song
            }
        });
    }

    public void getMusic() {
        //get access to device music info and create a "cursor" to scroll through the files
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if (songCursor != null && songCursor.moveToFirst()) {
            //get song title and artist from device
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do {
                //generate an instance of Song
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                Song newSong = new Song(currentTitle, currentArtist);

                //add this to the arrayList
                arrayList.add(newSong);
            } while (songCursor.moveToNext()); //continue to the next file
        }
    }

    //check permissions function
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case MY_PERMISSION_REQUEST: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                        showMusic();
                    }
                } else {
                    Toast.makeText(this,"No permission granted!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onStart() {
        //on start, load the media (currently loading from pre-loaded song)
        super.onStart();
        playAdapt.loadMedia(MEDIA_RES_ID);
        Log.d(TAG, "onStart: create media player");
    }

    @Override
    protected void onStop() {
        //if the application is stopped
        super.onStop();
        //check if its changing configurations (rotating) and do not stop play if so
        if(isChangingConfigurations() && playAdapt.isPlaying()) {
        } else {
            //otherwise, stop the player adapter and release it
            playAdapt.release();
        }
    }

    private void initializeUI() {
        //set ids to elements
        Button bPlay = findViewById(R.id.button_play);
        Button bPause = findViewById(R.id.button_pause);
        Button bReset = findViewById(R.id.button_reset);
        seekbarAudio = findViewById(R.id.seekbar_audio);

        //set on click listeners on buttons to functions defined later
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
        //create the media player holder, set it to listen for playback, and assign it to the player adapter
        MediaPlayerHolder mph = new MediaPlayerHolder(this);
        mph.setPlaybackInfoListener(new PlaybackListener());
        playAdapt = mph;
    }

    private void initializeSeekbar () {
        //create the seekbar and check for changes
        seekbarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
                playAdapt.seekTo(userSelectPosition);
            }
        });
    }

    public class PlaybackListener extends PlaybackInfoListener {
        //set max song playback time
        @Override
        public void onDurationChanged(int duration) {
            seekbarAudio.setMax(duration);
        }

        //set song playback progression time
        @Override
        public void onPositionChanged(int position) {
            if(!isSeeking) {
                seekbarAudio.setProgress(position, true);
            }
        }

        //check for playback state (complete, playing, paused, etc)
        @Override
        public void onStateChanged(@State int state) {
            String stateToString = PlaybackInfoListener.convertStateToString(state);
        }

        //when song playback is done
        @Override
        public void onPlaybackCompleted() {
            //when playback is done, do a thing (switch to next song)
        }
    }

}
