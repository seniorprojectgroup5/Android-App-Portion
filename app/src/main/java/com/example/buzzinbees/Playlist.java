package com.example.buzzinbees;

import java.lang.ref.Reference;
import java.util.ArrayList;

public class Playlist {

    //playlist object variables
    public String name;
    public int numSongs;
    public int ID;

    public ArrayList<Song> songsArray = new ArrayList<>();

    public Playlist(){
        //null constructor
        this.name = null;
        this.numSongs = -1;
        this.ID = -1;
    }

    public Playlist(String n, int numS, int id){
        //constructor with variables passed
        this.name = null;
        this.numSongs = numS;
        this.ID =  id;
    }



}
