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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {

    LinearLayout playlistBtn;
    FragmentActivity activity;
    MainActivity main;


    //constructor DO NOT DELETE
    public PlaylistAdapter(Context context, ArrayList<Playlist> playlists) {
        super(context, 0, playlists);
    }

    public PlaylistAdapter(Context context, ArrayList<Playlist> playlists,FragmentActivity activity) {

        super(context, 0, playlists);
        this.activity = activity;

    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //get data for snog
        Playlist playlist = getItem(position);
        //checked if the song_view is reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.playlists_view, parent, false);
        }

        main = (MainActivity) convertView.getContext();

        //look up view and populate data
        TextView tvPlaylist = convertView.findViewById(R.id.txt_PlaylistTitle);

        tvPlaylist.setText(playlist.getName());

        playlistBtn = convertView.findViewById(R.id.playlistBox);

        playlistBtn.setTag(position);

        playlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();

                Playlist playlist = getItem(position);


                if(playlist.songsArray.size() > 0) {
                    main.playingQ = playlist;
                    main.audioManagerFragment.songPlaying = main.playingQ.songsArray.get(0);
                    main.audioManagerFragment.setSongDisplay();
                    main.audioManagerFragment.qIndex = 0;

                    main.onNavigationItemSelected(main.navigationView.getMenu().findItem(R.id.navigation_visualizer));

                    main.audioManagerFragment.resetPlayer();
                    main.audioManagerFragment.playBack.performClick();
                }
                else{
                    Toast.makeText(getContext(),"Cannot play empty Playlist",Toast.LENGTH_SHORT).show();
                }
            }
        });

        playlistBtn.setOnTouchListener(new View.OnTouchListener() {
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

        ImageButton btnGoToPlaylist = convertView.findViewById(R.id.btn_goToPlaylist);
        btnGoToPlaylist.setTag(position);
        btnGoToPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = (Integer) v.getTag();

                Playlist playlist = getItem(position);
                Log.d("PLAYLIST",playlist.toString());

                main.currentFragment = Constant.FRAGVAL_PLAYLISTPAGE;
                main.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                        new ML_Page_PlaylistFragment(playlist)).commit();

            }
        });





        return convertView;
    }

}
