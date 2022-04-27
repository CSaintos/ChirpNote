package com.example.chirpnote;

public class queryResult {

    private String songTitle;
    private String songArtist;
    private String songKey;
    private String image;

    public queryResult(String information, String songArtist, String songKey, String image) {
        this.songTitle = information;
        this.songArtist = songArtist;
        this.songKey = songKey;
        this.image = "https://itunes.apple.com/search?term=" + image.replaceAll(" ", "+").replaceAll("\n","") + "&limit=1";
    }

    public String getImage() {
        return image;
    }

    public String getSongArtist() { return songArtist; }

    public String getSongKey() {return songKey; }

    public String getSongTitle() {
        return songTitle;
    }
}
