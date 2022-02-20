package com.example.chirpnote;

import android.widget.Button;

import java.io.Serializable;

public class Session implements Serializable {
    // Tempo range
    public final static int MIN_TEMPO = 20;
    public final static int MAX_TEMPO = 240;

    private String mName;
    private Key mKey;
    private int mTempo;
    private Melody mMelody;
    private AudioTrack mAudio;

    /**
     * A session (ChirpNote's project/song type)
     * @param name The name of this session
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
     * For testing
     */
    public Session(String name, Key key, int tempo, Melody melody, AudioTrack audio){
        mName = name;
        mKey = key;
        if(MIN_TEMPO <= tempo && tempo <= MAX_TEMPO) {
            mTempo = tempo;
        } else {
            mTempo = 120;
        }
        mMelody = melody;
        mAudio = audio;
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

    public Melody getMelody(){
        return mMelody instanceof ConstructedMelody ? (ConstructedMelody) mMelody : (RealTimeMelody) mMelody;
    }

    public AudioTrack getAudio(){
        return mAudio;
    }
}
