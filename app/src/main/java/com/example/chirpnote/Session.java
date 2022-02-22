package com.example.chirpnote;

import java.io.Serializable;

public class Session implements Serializable {
    // Tempo range
    public final static int MIN_TEMPO = 20;
    public final static int MAX_TEMPO = 240;

    private String mName;
    private Key mKey;
    private int mTempo;
    private String mMelodyPath;
    private String mAudioPath;

    // States
    private boolean mMelodyRecorded;
    private boolean mAudioRecorded;

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
     * A session (ChirpNote's project/song type)
     * @param name The name of this session
     * @param key The key of this session
     * @param tempo The tempo of this session
     * @param melodyPath The file path to store the melody at
     * @param audioPath The file path to store the audio at
     */
    public Session(String name, Key key, int tempo, String melodyPath, String audioPath){
        mName = name;
        mKey = key;
        if(MIN_TEMPO <= tempo && tempo <= MAX_TEMPO) {
            mTempo = tempo;
        } else {
            mTempo = 120;
        }
        mMelodyPath = melodyPath;
        mMelodyRecorded = false;
        mAudioPath = audioPath;
        mAudioRecorded = false;
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

    /**
     * Gets the file path where the melody is stored
     * @return The file path of the MIDI file for the melody
     */
    public String getMelodyPath(){
        return mMelodyPath;
    }

    /**
     * Gets the file path where the audio track is stored
     * @return The file path of the mp4 file for the audio track
     */
    public String getAudioPath(){
        return mAudioPath;
    }

    /**
     * Call after a melody has been recorded for this session
     */
    public void setMelodyRecorded(){
        mMelodyRecorded = true;
    }

    /**
     * Call after an audio track has been recorded for this session
     */
    public void setAudioRecorded(){
        mAudioRecorded = true;
    }

    /**
     * Gets whether or not this session's melody has been recorded
     * @return True if the session's melody has been recorded
     */
    public boolean isMelodyRecorded(){
        return mMelodyRecorded;
    }

    /**
     * Gets whether or not this session's audio track has been recorded
     * @return True if the session's audio track has been recorded
     */
    public boolean isAudioRecorded(){
        return mAudioRecorded;
    }
}
