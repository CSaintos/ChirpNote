package com.example.chirpnote;

import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmList;

public class ChirpNoteSession implements Serializable {
    // Tempo range
    public final static int MIN_TEMPO = 20;
    public final static int MAX_TEMPO = 240;

    // Resolution of MIDI tracks
    public final int RESOLUTION = 960;

    private String mName;
    private Key mKey;
    private int mTempo;
    private String mMidiPath;
    public ArrayList<String> mChords;
    public ArrayList<String> mMelodyElements;
    public int mNextMelodyTick;
    private String mAudioPath;
    public ArrayList<String> mPercussionPatterns;
    public ArrayList<Integer> mTrackVolumes;

    // States
    private boolean mMidiPrepared;
    private boolean mAudioRecorded;

    /**
     * A session (ChirpNote's project/song type)
     * @param name The name of this session
     * @param key The key of this session
     * @param tempo The tempo of this session
     */
    public ChirpNoteSession(String name, Key key, int tempo){
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
     * @param midiPath The file path to store the MIDI tracks at
     * @param audioPath The file path to store the audio at
     */
    public ChirpNoteSession(String name, Key key, int tempo, String midiPath, String audioPath){
        mName = name;
        mKey = key;
        if(MIN_TEMPO <= tempo && tempo <= MAX_TEMPO) {
            mTempo = tempo;
        } else {
            mTempo = 120;
        }
        mMidiPath = midiPath;
        mMidiPrepared = false;
        mChords = new ArrayList<>();
        mMelodyElements = new ArrayList<>();
        mNextMelodyTick = 0;
        mAudioPath = audioPath;
        mAudioRecorded = false;
        mPercussionPatterns = new ArrayList<>();
        mTrackVolumes = new ArrayList<>(); //{80, 80, 80, 100, 127}; // Chords, constructedMelody, recordedMelody, audio, percussion
        mTrackVolumes.add(80);
        mTrackVolumes.add(80);
        mTrackVolumes.add(80);
        mTrackVolumes.add(100);
        mTrackVolumes.add(127);
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
     * Gets the file path where the MIDI tracks are stored
     * @return The path of the MIDI file for the chords, constructed melody, and real time melody
     */
    public String getMidiPath(){
        return mMidiPath;
    }

    /**
     * Gets the file path where the audio track is stored
     * @return The path of the mp3 file for the audio track
     */
    public String getAudioPath(){
        return mAudioPath;
    }

    /**
     * Call after the MIDI file for this session has been created
     */
    public void setMidiPrepared(){
        mMidiPrepared = true;
    }

    /**
     * Call after an audio track has been recorded for this session
     */
    public void setAudioRecorded(){
        mAudioRecorded = true;
    }

    /**
     * Gets whether or not this session's MIDI file has been prepared
     * @return True if the session's MIDI file is ready to be written to
     */
    public boolean isMidiPrepared(){
        return mMidiPrepared;
    }

    /**
     * Gets whether or not this session's audio track has been recorded
     * @return True if the session's audio track has been recorded
     */
    public boolean isAudioRecorded(){
        return mAudioRecorded;
    }

    public void setKey(Key key) {mKey = key;}

    public Session toRealmSession(){
        Session realmSession = new Session();
        realmSession.set_id(new ObjectId());
        realmSession.setName(mName);
        realmSession.setKey(Key.encode(mKey));
        realmSession.setTempo(mTempo);
        realmSession.setChords(toRealmList(mChords));
        realmSession.setMelodyElements(toRealmList(mMelodyElements));
        realmSession.setNextMelodyTick(mNextMelodyTick);
        realmSession.setPercussionPatterns(toRealmList(mPercussionPatterns));
        realmSession.setTrackVolumes(toRealmList(mTrackVolumes));
        return realmSession;
    }

    private <T> RealmList<T> toRealmList(ArrayList<T> list){
        RealmList<T> realmList = new RealmList<>();
        for(T item : list){
            realmList.add(item);
        }
        return realmList;
    }
}
