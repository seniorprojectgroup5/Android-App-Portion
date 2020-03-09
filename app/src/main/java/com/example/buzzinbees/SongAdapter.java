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

public class SongAdapter extends ArrayAdapter<Song> {

    LinearLayout songBtn;
    FragmentActivity activity;
    MainActivity main;

    Playlist viewedPlaylist;

    Spinner songMenu;

    //constructor DO NOT DELETE
    public SongAdapter(Context context, ArrayList<Song> songs) {
        super(context, 0, songs);
    }

    public SongAdapter(Context context, Playlist p) {

        super(context, 0, p.songsArray);
        this.viewedPlaylist = p;
    }

    public SongAdapter(Context context, ArrayList<Song> songs,FragmentActivity activity) {

        super(context, 0, songs);
        this.activity = activity;

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

        setSongMenuAdapter(song);

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
                if((parent.getSelectedItem().toString().equals("favourite"))||(parent.getSelectedItem().toString().equals("unfavourite"))){
                    Log.d("PLAYLIST","song:"+song.toString());
                    song.toggleFav();
                    Log.d("PLAYLIST","song favourited:"+ song.isFav);

                    parent.setSelection(0);

                    if(song.isFav){
                        Boolean b = main.arrayPlaylists.get(Constant.PLAYLIST_FAVOURITES_ID).addSong(song);
                        if (b){
                            //add was successful
                            Toast.makeText(getContext(),"Song added to Favourites",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //add was unsuccesful
                            Toast.makeText(getContext(),"[ERROR: FAILED TO ADD SONG]",Toast.LENGTH_SHORT).show();
                            song.isFav = false;
                        }
                    }
                    else{
                        Boolean b = main.arrayPlaylists.get(Constant.PLAYLIST_FAVOURITES_ID).removeSong(song);
                        if (b) {
                            //removal was successful
                            Toast.makeText(getContext(), "Song removed from Favourites", Toast.LENGTH_SHORT).show();

                        }
                        else{
                            //removal was unsuccesful
                            Toast.makeText(getContext(),"[ERROR: FAILED TO REMOVE SONG]",Toast.LENGTH_SHORT).show();
                            song.isFav = true;
                        }
                    }

                    setSongMenuAdapter(song);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(0);
            }
        });


        return convertView;
    }

    public void setSongMenuAdapter(Song s){

        if(s.isFav){
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.faved_songDropDown,android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            songMenu.setAdapter(adapter);
        }
        else{
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.songDropDown,android.R.layout.simple_spinner_item);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            songMenu.setAdapter(adapter);
        }

    }

}

