package com.example.chirpnote;

import org.billthefarmer.mididriver.GeneralMidiConstants;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChirpNoteSession implements Serializable {
    // Tempo range
    public final static int MIN_TEMPO = 20;
    public final static int MAX_TEMPO = 240;

    // Resolution of MIDI tracks
    public final int RESOLUTION = 960;

    private ObjectId _id;
    private String mName;
    private Key mKey;
    private int mTempo;
    private String mMidiPath;
    public List<String> mChords;
    public List<String> mMelodyElements;
    public int mNextMelodyTick;
    private String mAudioPath;
    public List<String> mPercussionPatterns;
    public List<Integer> mTrackVolumes;
    public List<Byte> mInstruments;
    private String mUsername;

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
     * @param username The username of the user creating the session
     */
    public ChirpNoteSession(String name, Key key, int tempo, String midiPath, String audioPath, String username){
        _id = new ObjectId();
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
        mTrackVolumes = new ArrayList<>(); //{80, 80, 80, 100, 127} -> Chords, constructedMelody, recordedMelody, audio, percussion
        mTrackVolumes.add(80);
        mTrackVolumes.add(80);
        mTrackVolumes.add(80);
        mTrackVolumes.add(100);
        mTrackVolumes.add(127);
        mInstruments = new ArrayList<>(); //{piano, piano, piano} -> Chords, constructedMelody, recordedMelody
        mInstruments.add(GeneralMidiConstants.ACOUSTIC_GRAND_PIANO);
        mInstruments.add(GeneralMidiConstants.ACOUSTIC_GRAND_PIANO);
        mInstruments.add(GeneralMidiConstants.ACOUSTIC_GRAND_PIANO);
        mUsername = username;
    }

    /**
     * Gets the id of this session
     * @return The session id
     */
    public ObjectId getId(){
        return _id;
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
     * Gets the username of the user that created this session
     * @return The user's username
     */
    public String getUsername(){
        return mUsername;
    }

    /**
     * Sets the path for the active audio recording
     * @param audioPath The path of the session's audio
     */
    public void setAudioPath(String audioPath){
        mAudioPath = audioPath;
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

    /**
     * Sets the key of this session
     * @param key The key
     */
    public void setKey(Key key) {mKey = key;}

    /**
     * Sets the id of this session
     * @param id The id
     */
    public void setId(ObjectId id){
        _id = id;
    }
}
