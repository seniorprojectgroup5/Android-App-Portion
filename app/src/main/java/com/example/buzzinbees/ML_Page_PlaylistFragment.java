package com.example.buzzinbees;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ML_Page_PlaylistFragment extends Fragment {

    //song listview variables
    ArrayList<Song> arrayList;
    ListView listView;
    SongAdapter adapter;

    MainActivity main;

    Playlist list;

    TextView playlistName;
    TextView numSongs;

    public ML_Page_PlaylistFragment() {
        // Required empty public constructor
    }

    public ML_Page_PlaylistFragment(Playlist p){
        list = p;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page_playlist, null);
        // Inflate the layout for this fragment
        main = (MainActivity) getActivity();

        playlistName = view.findViewById(R.id.txt_PlaylistTitle);
        playlistName.setText(list.getName());

        numSongs = view.findViewById(R.id.txt_numSongs);
        numSongs.setText(list.getSize().toString());


        if(list.songsArray.size() >0) {
            showMusic(view);
        }





        return view;


    }

    public void showMusic(View view) {
        //initialize listview and arraylist
        listView = view.findViewById(R.id.listSongs_container);
        arrayList = new ArrayList<Song>();
        Log.d("PLAYLIST",list.toString());
        //initialize the adapter and assign the arrrayList to it so it has data
        adapter = new SongAdapter(this.getContext(),list);
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

}
