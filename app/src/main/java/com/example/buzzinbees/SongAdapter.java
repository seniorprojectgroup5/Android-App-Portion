package com.example.buzzinbees;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
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

    //constructor DO NOT DELETE
    public SongAdapter(Context context, ArrayList<Song> songs) {
        super(context, 0, songs);
    }

    public SongAdapter(Context context, ArrayList<Song> songs,FragmentActivity activity) {

        super(context, 0, songs);
        this.activity = activity;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
                main.audioManagerFragment.songPlaying = new Song(song.songName,song.songArtist,song.path);
            }
        });




        return convertView;
    }

}
