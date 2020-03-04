package com.example.buzzinbees;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ML_Add_NewPlaylistFragment extends Fragment {

    //song listview variables
    ArrayList<Song> arrayList;
    ListView listView;
    SelectSongAdapter adapter;

    MainActivity main;

    EditText playlistName;
    Button btnDone;

    ArrayList<Song> selectedSongs = new ArrayList<>();

    public ML_Add_NewPlaylistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_playlist, null);
        // Inflate the layout for this fragment
        main = (MainActivity) getActivity();

        playlistName = view.findViewById(R.id.editTxt_PlaylistName);
        btnDone = view.findViewById(R.id.btn_addConfirm);

        showMusic(view);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.arrayPlaylists.add(new Playlist(playlistName.getText().toString(),main.arrayPlaylists.size(),selectedSongs));
                main.currentFragment = Constant.FRAGVAL_PLAYLISTS;
                main.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new ML_List_PlaylistsFragment()).commit();
            }
        });


        return view;


    }

    public void showMusic(View view) {
        //initialize listview and arraylist
        listView = view.findViewById(R.id.listSongs_container);
        arrayList = new ArrayList<Song>();

        //initialize the adapter and assign the arrrayList to it so it has data
        adapter = new SelectSongAdapter(this.getContext(),main.allSongs.songsArray);
        //apply the adapter to the listview to show list

        listView.setAdapter(adapter);

        //on click function for song row
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageButton btnSelected = view.findViewById(R.id.btn_songSelect);
                Log.d("PLAYLIST",main.allSongs.songsArray.get(position).toString());

                view.setSelected(!view.isSelected());
                if(view.isSelected()){
                    ((ImageButton) btnSelected).setImageResource(R.drawable.ic_hexagonselectbutton_fill);
                    selectedSongs.add(main.allSongs.songsArray.get(position));
                }
                else{
                    ((ImageButton) btnSelected).setImageResource(R.drawable.ic_hexagonselectbutton_clear);
                    selectedSongs.remove(main.allSongs.songsArray.get(position));
                }
            }
        });
    }
}
