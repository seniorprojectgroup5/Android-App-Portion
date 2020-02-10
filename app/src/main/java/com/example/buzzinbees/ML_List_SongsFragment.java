package com.example.buzzinbees;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;


public class ML_List_SongsFragment extends Fragment {

    //song listview variables
    ArrayList<Song> arrayList;
    ListView listView;
    SongAdapter adapter;


    public ML_List_SongsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_songs, null);

        showMusic(view);
        // Inflate the layout for this fragment
        return view;
    }

    public void showMusic(View view) {
        //initialize listview and arraylist
        listView = view.findViewById(R.id.listSongs_container);
        arrayList = new ArrayList<>();

        //get the music from the device
        getMusic();

        //initialize the adapter and assign the arrrayList to it so it has data
        adapter = new SongAdapter(this.getContext(), arrayList);
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
        ContentResolver contentResolver = getContext().getContentResolver();
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

}
