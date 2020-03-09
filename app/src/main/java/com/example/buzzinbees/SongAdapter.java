package com.example.buzzinbees;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {

    LinearLayout songBtn;
    FragmentActivity activity;
    MainActivity main;

    Playlist viewedPlaylist;

    Spinner songMenu;

    List<String> listNotFav = new ArrayList<String>();
    int listsize;
    List<String> listFav = new ArrayList<String>();


    //constructor DO NOT DELETE
    public SongAdapter(Context context, ArrayList<Song> songs) {
        super(context, 0, songs);
        listNotFav.add(Constant.MENULIST_FAV);
        listNotFav.add(Constant.MENULIST_ADDTOPLAY);
        listNotFav.add("");
        listsize = listNotFav.size() - 1;

        listFav.add(Constant.MENULIST_UNFAV);
        listFav.add(Constant.MENULIST_ADDTOPLAY);
        listFav.add("");
    }

    public SongAdapter(Context context, Playlist p) {

        super(context, 0, p.songsArray);
        this.viewedPlaylist = p;

        listNotFav.add(Constant.MENULIST_FAV);
        listNotFav.add(Constant.MENULIST_ADDTOPLAY);
        listNotFav.add("");
        listsize = listNotFav.size() - 1;

        listFav.add(Constant.MENULIST_UNFAV);
        listFav.add(Constant.MENULIST_ADDTOPLAY);
        listFav.add("");
    }

    public SongAdapter(Context context, ArrayList<Song> songs,FragmentActivity activity) {

        super(context, 0, songs);
        this.activity = activity;
        listNotFav.add(Constant.MENULIST_FAV);
        listNotFav.add(Constant.MENULIST_ADDTOPLAY);
        listNotFav.add("");
        listsize = listNotFav.size() - 1;

        listFav.add(Constant.MENULIST_UNFAV);
        listFav.add(Constant.MENULIST_ADDTOPLAY);
        listFav.add("");
    }




    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //get data for snog
        final Song song = getItem(position);

        //checked if the song_view is reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_view, parent, false);
        }

        main = (MainActivity) convertView.getContext();

        //look up view and populate data
        TextView tvSong = convertView.findViewById(R.id.txt_SongTitle);
        TextView tvArtist = convertView.findViewById(R.id.txt_ArtistName);
        tvSong.setText(song.songName);
        tvArtist.setText(song.songArtist);

        songMenu = (Spinner) convertView.findViewById(R.id.spn_songDropDown);
        songMenu.setTag(position);

        setSongMenuAdapter(song,songMenu);

        songBtn = convertView.findViewById(R.id.songBox);

        songBtn.setTag(position);

        songBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();

                Song song = getItem(position);
                main.audioManagerFragment.songPlaying = song;
                main.audioManagerFragment.setSongDisplay();
                main.audioManagerFragment.qIndex = position;

                //set playingQueue value
                switch(main.currentFragment){
                    //check value of current fragment
                    case Constant.FRAGVAL_SONGS:{
                        main.playingQ = main.allSongs;
                        //if playing from all songs page, clone array of all songs
                        break;
                    }
                    case Constant.FRAGVAL_PLAYLISTPAGE:{
                        main.playingQ = viewedPlaylist;
                        break;
                    }
                }

                main.onNavigationItemSelected(main.navigationView.getMenu().findItem(R.id.navigation_visualizer));
                main.navigationView.setCheckedItem(R.id.navigation_visualizer);

                main.audioManagerFragment.resetPlayer();
                main.audioManagerFragment.playBack.performClick();
            }
        });



        songMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Song song = getItem((Integer)parent.getTag());
                //get song from selected item
                if((parent.getSelectedItem().toString().equals(Constant.MENULIST_FAV))||(parent.getSelectedItem().toString().equals(Constant.MENULIST_UNFAV))){

                    Log.d("PLAYLIST","song:"+song.toString());
                    song.toggleFav();
                    Log.d("PLAYLIST","song favourited:"+ song.isFav);

                    main.addSongToFavs(song);

                    setSongMenuAdapter(song,(Spinner)parent);

                    parent.setSelection(listsize);
                }
                if (parent.getSelectedItem().toString().equals(Constant.MENULIST_ADDTOPLAY)){
                    Log.d("PLAYLIST","Add to playlist clicked at index"+position);
                    Log.d("PLAYLIST","song:"+song.toString());
                    parent.setSelection(listsize);
                    main.currentFragment = Constant.FRAGVAL_SONGTOPLAYLIST;
                    main.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                            new ML_Add_ToPlaylistFragment(song)).commit();
                    main.navigationView.setCheckedItem(R.id.navigation_songs);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(listsize);
            }
        });


        return convertView;
    }

    public void setSongMenuAdapter(Song s,Spinner spinner){

        if(s.isFav){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, listFav) {
                @Override
                public int getCount() {
                    return(listsize); // Truncate the list
                }
            };
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner

            spinner.setAdapter(adapter);
        }
        else{
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, listNotFav) {
                @Override
                public int getCount() {
                    return(listsize); // Truncate the list
                }
            };
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner

            spinner.setAdapter(adapter);
        }

        spinner.setSelection(listsize);

    }


}

