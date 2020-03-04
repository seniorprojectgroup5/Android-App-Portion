package com.example.buzzinbees;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

public class SelectSongAdapter extends ArrayAdapter<Song> {

    ImageButton songSelectBtn;
    FragmentActivity activity;
    MainActivity main;
    Playlist viewedPlaylist;

    ArrayList<Song> selectedSongs= new ArrayList<>();


    //constructor DO NOT DELETE
    public SelectSongAdapter(Context context, ArrayList<Song> songs) {
        super(context, 0, songs);
    }

    public SelectSongAdapter(Context context, Playlist p) {

        super(context, 0, p.songsArray);
        this.viewedPlaylist = p;
    }

    public SelectSongAdapter(Context context, ArrayList<Song> songs, FragmentActivity activity) {

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
            convertView.setSelected(false);
        }

        main = (MainActivity) convertView.getContext();

        //look up view and populate data
        TextView tvSong = convertView.findViewById(R.id.txt_SongTitle);
        TextView tvArtist = convertView.findViewById(R.id.txt_ArtistName);
        tvSong.setText(song.songName);
        tvArtist.setText(song.songArtist);

        songSelectBtn = convertView.findViewById(R.id.btn_songSelect);

        songSelectBtn.setTag(position);

        /*songSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();

                Song song = getItem(position);

                v.setSelected(!v.isSelected()); // toggle btn selection

                if(v.isSelected()){
                    ((ImageButton) v).setImageResource(R.drawable.ic_hexagonselectbutton_fill);
                    selectedSongs.add(song);
                }
                else{
                    ((ImageButton) v).setImageResource(R.drawable.ic_hexagonselectbutton_clear);
                    selectedSongs.remove(song);
                }


            }
        });*/

        return convertView;
    }

}
