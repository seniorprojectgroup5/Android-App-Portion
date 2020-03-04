package com.example.buzzinbees;

public class Song {

    //create custom "song" variable type
    public String songName;
    public String songArtist;
    public String path;
    public int index;
    public boolean isFav;

    public Song (){
        this.songArtist = null;
        this.songName = null;
        this.path = null;
        this.index = -1;
        this.isFav = false;
    }

    public Song(String songName, String songArtist, String path, int index, boolean f) {
        this.songName = songName;
        this.songArtist = songArtist;
        this.path = path;
        this.index = index;
        this.isFav = f;
    }

    public void toggleFav(){

        if(this.isFav){
            this.isFav = false;
        }
        else{
            this.isFav = true;
        }
    }

    public String toString(){

        String s = this.songName + ", " +this.songArtist+", " +this.path+", Faved:"+this.isFav;
        return s;
    }

}