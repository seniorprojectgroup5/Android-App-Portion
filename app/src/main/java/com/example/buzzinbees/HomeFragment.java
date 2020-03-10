package com.example.buzzinbees;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class HomeFragment extends Fragment {

    MainActivity main;

    Button btnToQueue;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home,null);

        main = (MainActivity) getActivity();

        btnToQueue = view.findViewById(R.id.btn_toQueue);

        int val = main.playingQ.getID();


        if ((val == Constant.PLAYLIST_ALLSONGS_ID)||(val >= Constant.PLAYLIST_FAVOURITES_ID)){
            btnToQueue.setText(main.playingQ.getName());
        }else{
            btnToQueue.setText(getString(R.string.var_RecentPlayedQueue));
        }

        btnToQueue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(main.playingQ.getID()){
                    case Constant.PLAYLIST_ALLSONGS_ID:{
                        main.currentFragment = Constant.FRAGVAL_SONGS;
                        main.onNavigationItemSelected(main.navigationView.getMenu().findItem(R.id.navigation_songs));
                        main.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                                new ML_List_SongsFragment()).commit();
                        break;
                    }
                    default:{

                        main.currentFragment = Constant.FRAGVAL_PLAYLISTPAGE;
                        main.onNavigationItemSelected(main.navigationView.getMenu().findItem(R.id.navigation_playlists));
                        main.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer_main,
                                new ML_Page_PlaylistFragment(main.playingQ)).commit();

                        break;
                    }
                }
            }
        });

        return view;
    }

}
