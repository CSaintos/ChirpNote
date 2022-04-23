package com.example.chirpnote;

public class queryResult {

    private String information;
    private String image;

    public queryResult(String information, String image) {
        this.information = information;
        this.image = "https://itunes.apple.com/search?term=" + image.replaceAll(" ", "+") + "&limit=1";
    }

    public String getImage() {
        return image;
    }

    public String getInformation() {
        return information;
    }
}
