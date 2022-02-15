package com.example.chirpnote;

import java.io.Serializable;

public class Session implements Serializable {
    // Tempo range
    public final static int MIN_TEMPO = 20;
    public final static int MAX_TEMPO = 240;
    // Keys
    public enum Key {
        C_MAJOR("C Major"),
        C_SHARP_MAJOR("C# Major"),
        D_MAJOR("D Major"),
        D_SHARP_MAJOR("D# Major"),
        E_MAJOR("E Major"),
        F_MAJOR("F Major"),
        F_SHARP_MAJOR("F# Major"),
        G_MAJOR("G Major"),
        G_SHARP_MAJOR("G# Major"),
        A_MAJOR("A Major"),
        A_SHARP_MAJOR("A# Major"),
        B_MAJOR("B Major");

        private String string;

        Key(String str){
            string = str;
        }

        @Override
        public String toString(){
            return string;
        }
    }

    public enum Note {
        C("C"),
        C_SHARP("C#"),
        D("D"),
        D_SHARP("D#"),
        E("E"),
        F("F"),
        F_SHARP("F#"),
        G("G"),
        G_SHARP("G#"),
        A("A"),
        A_SHARP("A#"),
        B("B");

        private String string;

        Note(String str){
            string = str;
        }

        @Override
        public String toString(){
            return string;
        }
    }

    public enum KeyType {
        MAJOR("MAJOR"),
        MINOR("MINOR");

        private String string;

        KeyType(String str){
            string = str;
        }

        @Override
        public String toString(){
            return string;
        }
    }

    private String mName;
    private Key mKey;
    private int mTempo;

    private Note mNote;
    private KeyType mKeyType;

    /**
     * A session (ChirpNote's project/song type)
     * @param name The name of this session
     * @param key The key of this session
     * @param tempo The tempo of this session
     */
    public Session(String name, Key key, int tempo){
        mName = name;
        mKey = key;
        if(MIN_TEMPO <= tempo && tempo <= MAX_TEMPO) {
            mTempo = tempo;
        } else {
            mTempo = 120;
        }
    }

    /**
     * TESTING
     */
    public Session(String name, Note note, KeyType keyType, int tempo){
        mNote = note;
        mKeyType = keyType;
        if(MIN_TEMPO <= tempo && tempo <= MAX_TEMPO) {
            mTempo = tempo;
        } else {
            mTempo = 120;
        }
    }

    public int getNoteValue()
    {
        if (mNote == Note.C)
        {
            return 60;
        }
        else if (mNote == Note.D)
        {
            return 62;
        }
        else
        {
            return 64;
        }
    }


    /**
     * Gets the name of this session
     * @return The session name
     */
    public String getName(){
        return mName;
    }

    /**
     * Gets the key of this session
     * @return The session key
     */
    public Key getKey(){
        return mKey;
    }

    /**
     * Gets the tempo of this session
     * @return The session tempo
     */
    public int getTempo(){
        return mTempo;
    }

    public Note getNote() {return mNote;}

    public KeyType getKeyType() {return mKeyType;}



    public void setNote(Note note) {mNote = note;}

    public void setKeyType(KeyType keyType) {mKeyType = keyType;}
}
