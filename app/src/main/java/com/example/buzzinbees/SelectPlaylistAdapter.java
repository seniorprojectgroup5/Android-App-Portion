package com.example.buzzinbees;

import android.annotation.SuppressLint;
import android.content.Context;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

public class SelectPlaylistAdapter extends ArrayAdapter<Playlist> {



    FragmentActivity activity;
    MainActivity main;
    Context context;

    ImageButton listSelected;
    LinearLayout listBtn;

    Playlist viewedPlaylist;

    //boolean editState;

    ArrayList<Playlist> selectedPlaylists= new ArrayList<>();


    //constructor DO NOT DELETE
    public SelectPlaylistAdapter(Context context, ArrayList<Playlist> playlists) {
        super(context, 0, playlists);
        //this.editState = false;
    }

    /*public SelectPlaylistAdapter(Context context, Playlist p) {

        super(context, 0, p);
        this.context = context;
        this.viewedPlaylist = p;
        //this.editState = false;
    }*/

    public SelectPlaylistAdapter(Context context, ArrayList<Playlist> playlists, FragmentActivity activity) {

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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.playlist_select_view, parent, false);
            //convertView.setSelected(false);
        }

        main = (MainActivity) convertView.getContext();

        convertView.setClickable(false);

        //look up view and populate data

        TextView tvPlaylist = convertView.findViewById(R.id.txt_PlaylistTitle);

        tvPlaylist.setText(playlist.getName());


        listBtn = convertView.findViewById(R.id.playlistBox);
        listSelected = convertView.findViewById(R.id.btn_listSelect);

        listBtn.setTag(position);
        listSelected.setTag(position);

        listSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onclick for image button beside song box
                Playlist playlist = getItem(position);
                int position = (Integer) v.getTag();
//                Log.d("PLAYLIST","Song row clicked at pos "+position);

                View parent = (View) v.getParent();
                //LinearLayout l = parent.findViewById(R.id.songBox);

                ;
//                Log.d("PLAYLIST","Song :"+ playlist.toString());
                v.setSelected(!v.isSelected()); // toggle btn selection

//                Log.d("PLAYLIST","isSelected ="+v.isSelected());
                //determines current selection state and acts accordingly
                if(v.isSelected()){
                    ((ImageButton) v).setImageResource(R.drawable.ic_hexagonselectbutton_fill);
                    selectedPlaylists.add(playlist);

                }
                else{
                    ((ImageButton) v).setImageResource(R.drawable.ic_hexagonselectbutton_clear);
                    selectedPlaylists.remove(playlist);
                }
            }
        });

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on click for the song box for better user experience
                int position = (Integer) v.getTag();
//                Log.d("PLAYLIST","Song row clicked at pos "+position);

                View parent = (View) v.getParent();
                //focus on parent view of onclicked item
                ImageButton b = parent.findViewById(R.id.btn_listSelect);
                //find image button contained in parent view
                b.performClick();
                //trigger onClick for image button
            }
        });

        return convertView;
    }

}
