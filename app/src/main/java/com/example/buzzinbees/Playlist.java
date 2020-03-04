package com.example.buzzinbees;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class Playlist {

    //playlist object variables
    private String name;
    private int ID;
    private int size;

    public ArrayList<Song> songsArray = new ArrayList<>();

    public Playlist(){
        //null constructor
        this.name = null;
        this.ID = -1;
        this.size = -1;
    }

    public Playlist(String n,int id,ArrayList<Song> songs){
        //constructor with variables passed
        this.name = n;
        this.ID =  id;
        //System.arraycopy(songs,0,songsArray,0,songs.size());
        this.songsArray = (ArrayList<Song>)songs.clone();
        this.size = songsArray.size()-1;
    }

    public void setName(String n){
        this.name = n;
    }
    public String getName(){
        return this.name;
    }
    public void setID(int n){
        this.ID = n;
    }
    public Integer getID(){
        return this.ID;
    }

    public void setSize(){
        if(songsArray.size() == 0){
            this.size = 0;
        }
        else {
            this.size = songsArray.size()-1;
        }
    }
    public Integer getSize(){
        return this.size;
    }

    public boolean addSong(Song s){
        //add individual song to the song array
        try{
            this.songsArray.add(s);
        }
        catch (ConcurrentModificationException e){
            return false; // if error occurs return false
        }

        return true;//else will return true
    }

    public boolean addSong(ArrayList<Song> s){
        //add individual song to the song array
        try {
            this.songsArray.addAll(s);
        }
        catch (ConcurrentModificationException e){
            return false; // if error occurs return false
        }
        return true;
    }

    public boolean removeSong(Song s){
        //add individual song to the song array
        try{
            if(this.songsArray.contains(s)){
                //ensure array contains song item
                this.songsArray.remove(s);
            }
            else {
                return false;
            }
        }
        catch (ConcurrentModificationException e){
            return false; // if error occurs return false
        }
        return true;//else will return true
    }

    public boolean removeSong(ArrayList<Song> s, Context context){
        //add individual song to the song array
        boolean r = false;
        try {
            for(int i = 0; i < s.size();i++){
                //loop through the passed array & remove songs individually
                r = this.removeSong(s.get(i));
                if(!r){
                    Toast.makeText(context,"Song could not be removed",Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (ConcurrentModificationException e){
            return false; // if error occurs return false
        }
        return true;
    }



}
