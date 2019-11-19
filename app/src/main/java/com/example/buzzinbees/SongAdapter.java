package com.example.buzzinbees;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends ArrayAdapter<Song> {

    //constructor DO NOT DELETE
    public SongAdapter(Context context, ArrayList<Song> songs) {
        super(context, 0, songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get data for snog
        Song song = getItem(position);
        //checked if the song_view is reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_view, parent, false);
        }

        //look up view and populate data
        TextView tvSong = convertView.findViewById(R.id.txt_SongTitle);
        TextView tvArtist = convertView.findViewById(R.id.txt_ArtistName);
        tvSong.setText(song.songName);
        tvArtist.setText(song.songArtist);
        return convertView;
    }

}
