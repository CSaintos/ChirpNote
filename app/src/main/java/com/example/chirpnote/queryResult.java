package com.example.chirpnote;

import android.view.View;

public class queryResult {

    private String songTitle;
    private String songArtist;
    private String songKey;
    private String image;
    public Boolean isImageLoaded;
    public View storeView;

    /**
     * A queryResult with relevant song information
     * @param title the title of the song
     * @param songArtist the artist of the song
     * @param songKey the key of the song
     * @param image the string representation of the query required for image search using public itunes web API
     */
    public queryResult(String title, String songArtist, String songKey, String image) {
        this.songTitle = title;
        this.songArtist = songArtist;
        this.songKey = songKey;
        this.image = "https://itunes.apple.com/search?term=" + image.replaceAll(" ", "+").replaceAll("\n","") + "&limit=1";
        isImageLoaded = false;
        storeView = null;
    }

    /**
     * Get the image string
     * @return the image string
     */
    public String getImage() {
        return image;
    }

    /**
     * Get the artist
     * @return the artist
     */
    public String getSongArtist() { return songArtist; }

    /**
     * Get the song key featuring major and minor
     * @return the song key
     */
    public String getSongKey() {return songKey; }

    /**
     * Get the title of the song
     * @return the title
     */
    public String getSongTitle() {
        return songTitle;
    }
    public void setImageLoaded(Boolean imageLoaded) {
        isImageLoaded = imageLoaded;
    }
}
