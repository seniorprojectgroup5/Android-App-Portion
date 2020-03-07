package com.example.buzzinbees;


import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    TextView pageTitle;
    EditText playlistName;
    Button btnDone;

    Playlist editingPlaylist;

    boolean isEditing;


    ArrayList<Song> selectedSongs = new ArrayList<>();

    public ML_Add_NewPlaylistFragment() {
        // Required empty public constructor
        this.isEditing = false;

    }
    public ML_Add_NewPlaylistFragment(boolean editState, Playlist p) {
        // Required empty public constructor
        this.isEditing = editState;
        this.editingPlaylist = p;

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
        pageTitle = view.findViewById(R.id.txt_PageTitle);
        playlistName.setEnabled(true);
        playlistName.setClickable(true);

        if(isEditing){
            pageTitle.setText(getResources().getString(R.string.title_musicLibrary_editPlaylist));
            playlistName.setText(editingPlaylist.getName());
            if(editingPlaylist.getID()==Constant.PLAYLIST_FAVOURITES_ID){
                playlistName.setEnabled(false);
                playlistName.setClickable(false);
                //removes ability to edit name of default playlist
            }
        }



        showMusic(view);
        Log.d("PLAYLIST","Add new playlist: music showing");



        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Log.d("PLAYLIST", "done btn click registered");

                if(isEditing) {
                    //editing an existing playlist
                    editingPlaylist.setName(playlistName.getText().toString());
                    //change name to text in edittext field

                    if (adapter.selectedSongs.size()>0){
                        new AlertDialog.Builder(getContext())
                                .setTitle("Confirm Changes")
                                .setMessage("Remove selected songs from this playlist?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        editingPlaylist.removeSong(adapter.selectedSongs, getContext());
                                        //removing songs

                                        Toast.makeText(getContext(), "Songs removed from playlist", Toast.LENGTH_SHORT).show();
                                        //toast to user
                                        main.currentFragment = Constant.FRAGVAL_PLAYLISTPAGE;
                                        main.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                                                new ML_Page_PlaylistFragment(editingPlaylist)).commit();
                                        //nav back to playlist page

                                    }})
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                    else{
                        main.currentFragment = Constant.FRAGVAL_PLAYLISTPAGE;
                        main.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                                new ML_Page_PlaylistFragment(editingPlaylist)).commit();
                        //nav back to playlist page
                    }

                }
                else{
                    //Creating a new playlist
                    main.arrayPlaylists.add(new Playlist(playlistName.getText().toString(), main.arrayPlaylists.size(), adapter.selectedSongs));
                    //add new playlist to array of playlists
                    Log.d("PLAYLIST", "Playlist Added: " + main.arrayPlaylists.get(main.arrayPlaylists.size() - 1).toString());
                    Toast.makeText(getContext(), "New Playlist Added", Toast.LENGTH_SHORT);
                    main.currentFragment = Constant.FRAGVAL_PLAYLISTS;
                    main.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                            new ML_List_PlaylistsFragment()).commit();
                    //travel back to playlists page
                }
            }
        });

        playlistName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d("PLAYLIST", "focus loosed");
                    // Do whatever you want here
                } else {
                    Log.d("PLAYLIST", "focused");
                }
            }
        });

        return view;

    }

    public void showMusic(View view) {
        //initialize listview and arraylist
        listView = view.findViewById(R.id.listSongs_container);
        arrayList = new ArrayList<Song>();

        //initialize the adapter and assign the arrrayList to it so it has data
        if(!isEditing) {
            adapter = new SelectSongAdapter(this.getContext(), main.allSongs.songsArray);
        }
        else {
            adapter = new SelectSongAdapter(this.getContext(),editingPlaylist);
        }
        //apply the adapter to the listview to show list

        listView.setAdapter(adapter);

        //on click function for song row
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song s = (Song) parent.getItemAtPosition(position);
                Log.d("PLAYLIST","ML_newPlaylist: Song selected in arrayadapter:"+ s.toString());
            }
        });

    }
}
