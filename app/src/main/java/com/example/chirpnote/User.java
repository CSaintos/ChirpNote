package com.example.chirpnote;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import org.bson.types.ObjectId;

public class User extends RealmObject {
    @PrimaryKey
    private ObjectId _id;
    private Boolean chordSuggestions;
    private Boolean disableChromaticNotes;
    private Boolean noteSuggestions;
    private String password;
    private String username;

    public ObjectId get_id() {
        return _id;
    }
    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public Boolean getChordSuggestions() {
        return chordSuggestions;
    }
    public void setChordSuggestions(Boolean chordSuggestions) {
        this.chordSuggestions = chordSuggestions;
    }

    public Boolean getDisableChromaticNotes() {
        return disableChromaticNotes;
    }
    public void setDisableChromaticNotes(Boolean disableChromaticNotes) {
        this.disableChromaticNotes = disableChromaticNotes;
    }

    public Boolean getNoteSuggestions() {
        return noteSuggestions;
    }
    public void setNoteSuggestions(Boolean noteSuggestions) {
        this.noteSuggestions = noteSuggestions;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
