package com.example.chirpnote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Session implements Serializable {
    // Tempo range
    public final static int MIN_TEMPO = 20;
    public final static int MAX_TEMPO = 240;

    private String mName;
    private Key mKey;
    private int mTempo;
    public List<String> mChords;
    private String mChordsPath;
    private String mConstructedMelodyPath;
    private String mRealTimeMelodyPath;
    public List<String> mMelodyElements;
    public int mNextMelodyTick;
    private String mAudioPath;

    // States
    private boolean mChordsRecorded;
    private boolean mConstructedMelodyRecorded;
    private boolean mRealTimeMelodyRecorded;
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
     * @param chordsPath The file path to store the chords at
     * @param constructedMelodyPath The file path to store the constructed melody at
     * @param realTimeMelodyPath The file path to store the real time melody at
     * @param audioPath The file path to store the audio at
     */
    public Session(String name, Key key, int tempo, String chordsPath, String constructedMelodyPath, String realTimeMelodyPath, String audioPath){
        mName = name;
        mKey = key;
        if(MIN_TEMPO <= tempo && tempo <= MAX_TEMPO) {
            mTempo = tempo;
        } else {
            mTempo = 120;
        }
        mChords = new ArrayList<>();
        mChordsPath = chordsPath;
        mChordsRecorded = false;
        mConstructedMelodyPath = constructedMelodyPath;
        mRealTimeMelodyPath = realTimeMelodyPath;
        mConstructedMelodyRecorded = false;
        mRealTimeMelodyRecorded = false;
        mMelodyElements = new ArrayList<>();
        mNextMelodyTick = 0;
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
     * Gets the file path where the chords are stored
     * @return The file path of the MIDI file for the chords
     */
    public String getChordsPath(){
        return mChordsPath;
    }

    /**
     * Gets the file path where the constructed melody is stored
     * @return The file path of the MIDI file for the constructed melody
     */
    public String getConstructedMelodyPath(){
        return mConstructedMelodyPath;
    }

    /**
     * Gets the file path where the real time melody is stored
     * @return The file path of the MIDI file for the real time melody
     */
    public String getRealTimeMelodyPath(){
        return mRealTimeMelodyPath;
    }

    /**
     * Gets the file path where the audio track is stored
     * @return The file path of the mp3 file for the audio track
     */
    public String getAudioPath(){
        return mAudioPath;
    }

    /**
     * Call after a chords has been recorded (inserted) for this session
     */
    public void setChordsRecorded(){
        mChordsRecorded = true;
    }

    /**
     * Call after a constructed melody has been recorded for this session
     */
    public void setConstructedMelodyRecorded(){
        mConstructedMelodyRecorded = true;
    }

    /**
     * Call after a real time melody has been recorded for this session
     */
    public void setRealTimeMelodyRecorded(){
        mRealTimeMelodyRecorded = true;
    }

    /**
     * Call after an audio track has been recorded for this session
     */
    public void setAudioRecorded(){
        mAudioRecorded = true;
    }

    /**
     * Gets whether or not this session's chord track has been recorded
     * @return True if the session's chord track has been recorded
     */
    public boolean areChordsRecorded(){
        return mChordsRecorded;
    }

    /**
     * Gets whether or not this session's constructed melody has been recorded
     * @return True if the session's constructed melody has been recorded
     */
    public boolean isConstructedMelodyRecorded(){
        return mConstructedMelodyRecorded;
    }

    /**
     * Gets whether or not this session's real time melody has been recorded
     * @return True if the session's real time melody has been recorded
     */
    public boolean isRealTimeMelodyRecorded(){
        return mRealTimeMelodyRecorded;
    }

    /**
     * Gets whether or not this session's audio track has been recorded
     * @return True if the session's audio track has been recorded
     */
    public boolean isAudioRecorded(){
        return mAudioRecorded;
    }

    public void setKey(Key key) {mKey = key;}
}
