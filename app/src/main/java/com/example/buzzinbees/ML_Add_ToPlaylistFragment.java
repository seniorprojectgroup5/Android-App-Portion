package com.example.buzzinbees;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ML_Add_ToPlaylistFragment extends Fragment {

    ArrayList<Song> arrayList;
    ListView listView;
    SelectPlaylistAdapter adapter;

    MainActivity main;

    TextView pageTitle;
    EditText playlistName;
    Button btnDone;

    Song songToAdd;

    public ML_Add_ToPlaylistFragment() {
        // Required empty public constructor
    }

    public ML_Add_ToPlaylistFragment(Song s) {
        // Required empty public constructor
        songToAdd = s;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_to_playlist, null);
        // Inflate the layout for this fragment
        main = (MainActivity) getActivity();


        btnDone = view.findViewById(R.id.btn_addConfirm);

        showMusic(view);
        Log.d("PLAYLIST","Add new playlist: music showing");


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log.d("PLAYLIST", "done btn click registered");


                if(adapter.selectedPlaylists.size()>0){
                    boolean b = false;
                    for(int i =0; i <adapter.selectedPlaylists.size();i++){
                        //loop through selected arrays, add song to each array selected by user
                        b = adapter.selectedPlaylists.get(i).addSong(songToAdd);
                        if (adapter.selectedPlaylists.get(i).getID() == Constant.PLAYLIST_FAVOURITES_ID){
                            songToAdd.toggleFav();
                        }
                        if (b){
                            Toast.makeText(getContext(),"Song added to Playlist(s)",Toast.LENGTH_SHORT);
                        }
                        else{
                            Toast.makeText(getContext(),"Error adding Song to Playlist(s)",Toast.LENGTH_SHORT);
                        }
                    }

                }
                main.currentFragment = Constant.FRAGVAL_SONGS;
                main.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new ML_List_SongsFragment()).commit();
                main.navigationView.setCheckedItem(R.id.navigation_songs);
                //nav back to song page


            }
        });



        return view;
    }


    public void showMusic(View view) {
        //initialize listview and arraylist
        listView = view.findViewById(R.id.listPlaylists_container);
        arrayList = new ArrayList<Song>();

        //initialize the adapter and assign the arrrayList to it so it has data

        adapter = new SelectPlaylistAdapter(this.getContext(), main.arrayPlaylists);

        //apply the adapter to the listview to show list

        listView.setAdapter(adapter);

        //on click function for song row
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Song s = (Song) parent.getItemAtPosition(position);
               // Log.d("PLAYLIST","ML_newPlaylist: Song selected in arrayadapter:"+ s.toString());
            }
        });

    }

}
