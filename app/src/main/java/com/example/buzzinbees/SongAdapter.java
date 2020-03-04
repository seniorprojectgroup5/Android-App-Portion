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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter<Song> {

    LinearLayout songBtn;
    FragmentActivity activity;
    MainActivity main;

    Playlist viewedPlaylist;

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
        Song song = getItem(position);
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

        songBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch(event.getAction()){

                    case MotionEvent.ACTION_DOWN:
                        v.setBackgroundColor(Color.GRAY);
                        break;

                    case MotionEvent.ACTION_UP:
                        v.setBackgroundColor(Color.TRANSPARENT);
                        v.performClick();
                        break;

                }
                return false;
            }
        });





        return convertView;
    }

}
