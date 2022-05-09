package com.example.chirpnote;

import java.io.Serializable;

public class ChirpNoteUser implements Serializable {
    private String mUsername;
    private String mName;
    private String mEmail;
    private String mPassword;

    /**
     * A user of ChirpNote
     */
    public ChirpNoteUser(){}

    /**
     * A user of ChirpNote
     * @param username The user's username
     * @param name The user's full name
     * @param email The user's email address
     * @param password The user's password
     */
    public ChirpNoteUser(String username, String name, String email, String password){
        mUsername = username;
        mName = name;
        mEmail = email;
        mPassword = password;
    }

    /**
     * Gets this user's username
     * @return The username
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * Gets this user's name
     * @return The full name
     */
    public String getName() {
        return mName;
    }

    /**
     * Gets this user's email
     * @return The email address
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * Gets this user's password
     * @return The password
     */
    public String getPassword() {
        return mPassword;
    }

    /**
     * Sets this user's username
     * @param mUsername The new username
     */
    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    /**
     * Sets this user's name
     * @param mName The new name
     */
    public void setName(String mName) {
        this.mName = mName;
    }

    /**
     * Sets this user's email
     * @param mEmail The new email address
     */
    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    /**
     * Sets this user's password
     * @param mPassword The new password
     */
    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }
}
