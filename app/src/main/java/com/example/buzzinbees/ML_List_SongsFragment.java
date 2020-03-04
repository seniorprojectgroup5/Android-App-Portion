package com.example.buzzinbees;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
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

    MainActivity main;

    public ML_List_SongsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_songs, null);
        // Inflate the layout for this fragment


        main = (MainActivity) getActivity();
        Log.d("SONGLIST",main.allSongs.songsArray.toString());


        showMusic(view);
        Log.d("SONGLIST",main.allSongs.songsArray.toString());



        return view;
    }

    public void showMusic(View view) {
        //initialize listview and arraylist
        listView = view.findViewById(R.id.listPlaylists_container);
        arrayList = new ArrayList<Song>();

        if(!main.songsLoaded) {
            //get the music from the device
            getMusic();

            //set all songs playlist
            main.allSongs = new Playlist(getString(R.string.name_songLibraryPlaylist), Constant.PLAYLIST_ALLSONGS_ID, arrayList);

            main.songsLoaded = true; //toggle bool flag to true to prevent reloading of songs
        }

        //initialize the adapter and assign the arrrayList to it so it has data
        adapter = new SongAdapter(this.getContext(), main.allSongs.songsArray);
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

        int i = 0;

        if (songCursor != null && songCursor.moveToFirst()) {
            //get song title and artist from device
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPath = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                //generate an instance of Song
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist = songCursor.getString(songArtist);
                String currentPath = songCursor.getString(songPath);
                Song newSong = new Song(currentTitle, currentArtist,currentPath, i,false);

                //add this to the arrayList
                arrayList.add(newSong);

                i++;
            } while (songCursor.moveToNext()); //continue to the next file
        }
    }

}
