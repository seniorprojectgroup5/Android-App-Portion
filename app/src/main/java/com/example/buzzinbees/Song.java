package com.example.buzzinbees;

public class Song {

    //create custom "song" variable type
    public String songName;
    public String songArtist;
    public String path;

    public Song (){
        this.songArtist = null;
        this.songName = null;
        this.path = null;
    }

    public Song(String songName, String songArtist, String path) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.path = path;
    }

}