package com.example.buzzinbees;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

public class SelectSongAdapter extends ArrayAdapter<Song> {



    FragmentActivity activity;
    MainActivity main;
    Context context;

    ImageButton songSelected;
    LinearLayout songBtn;

    Playlist viewedPlaylist;

    ArrayList<Song> selectedSongs= new ArrayList<>();


    //constructor DO NOT DELETE
    public SelectSongAdapter(Context context, ArrayList<Song> songs) {

        super(context, 0, songs);
    }

    public SelectSongAdapter(Context context, Playlist p) {

        super(context, 0, p.songsArray);
        this.context = context;
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_select_view, parent, false);
            //convertView.setSelected(false);
        }

        main = (MainActivity) convertView.getContext();

        convertView.setClickable(false);

        //look up view and populate data
        TextView tvSong = convertView.findViewById(R.id.txt_SongTitle);
        TextView tvArtist = convertView.findViewById(R.id.txt_ArtistName);
        tvSong.setText(song.songName);
        tvArtist.setText(song.songArtist);


        songBtn = convertView.findViewById(R.id.songBox);
        songSelected = convertView.findViewById(R.id.btn_songSelect);

        songBtn.setTag(position);
        songSelected.setTag(position);

        songSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onclick for image button beside song box

                int position = (Integer) v.getTag();
                Log.d("PLAYLIST","Song row clicked at pos "+position);

                View parent = (View) v.getParent();
                //LinearLayout l = parent.findViewById(R.id.songBox);

                Song song = getItem(position);
                Log.d("PLAYLIST","Song :"+ song.toString());
                v.setSelected(!v.isSelected()); // toggle btn selection

                Log.d("PLAYLIST","isSelected ="+v.isSelected());
                //determines current selection state and acts accordingly
                if(v.isSelected()){
                    ((ImageButton) v).setImageResource(R.drawable.ic_hexagonselectbutton_fill);
                    selectedSongs.add(song);

                }
                else{
                    ((ImageButton) v).setImageResource(R.drawable.ic_hexagonselectbutton_clear);
                    selectedSongs.remove(song);
                }
            }
        });

        songBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on click for the song box for better user experience
                int position = (Integer) v.getTag();
                Log.d("PLAYLIST","Song row clicked at pos "+position);

                View parent = (View) v.getParent();
                //focus on parent view of onclicked item
                ImageButton b = parent.findViewById(R.id.btn_songSelect);
                //find image button contained in parent view
                b.performClick();
                //trigger onClick for image button
            }
        });

        return convertView;
    }

}
