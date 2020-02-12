package com.example.buzzinbees;

public class Song {

    //create custom "song" variable type
    public String songName;
    public String songArtist;
    public String path;
    public int index;

    public Song (){
        this.songArtist = null;
        this.songName = null;
        this.path = null;
        this.index = -1;
    }

    public Song(String songName, String songArtist, String path, int index) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.path = path;
        this.index = index;
    }

}