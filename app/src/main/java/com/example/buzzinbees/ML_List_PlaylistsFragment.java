package com.example.buzzinbees;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;



/**
 * A simple {@link Fragment} subclass.
 */
public class ML_List_PlaylistsFragment extends Fragment {


    //listview variables
    ArrayList<Song> arrayList;
    ListView listView;
    PlaylistAdapter adapter;

    MainActivity main;

    ImageButton addPlaylist;

    public ML_List_PlaylistsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_playlists, null);
        // Inflate the layout for this fragment

        main = (MainActivity) getActivity();

        showPlaylists(view);

        addPlaylist = view.findViewById(R.id.btn_addPlaylist);

        addPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                main.currentFragment = Constant.FRAGVAL_NEWPLAYLIST;
                main.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new ML_Add_NewPlaylistFragment()).commit();


            }
        });



        return view;
    }

    public void showPlaylists(View view) {
        //initialize listview and arraylist
        listView = view.findViewById(R.id.listPlaylists_container);
        arrayList = new ArrayList<Song>();


        //initialize the adapter and assign the arrrayList to it so it has data
        adapter = new PlaylistAdapter(this.getContext(), main.arrayPlaylists);
        //apply the adapter to the listview to show list

        listView.setAdapter(adapter);

        //on click function for song row
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //play desired playlist
            }
        });
    }



}
