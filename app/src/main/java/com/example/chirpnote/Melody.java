package com.example.chirpnote;

import android.widget.Button;

import com.example.midiFileLib.src.MidiFile;
import com.example.midiFileLib.src.MidiTrack;
import com.example.midiFileLib.src.event.NoteOn;
import com.example.midiFileLib.src.event.meta.Tempo;
import com.example.midiFileLib.src.event.meta.TimeSignature;
import com.example.midiFileLib.src.util.MidiProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

abstract class Melody implements Track {
    // States
    protected boolean mRecording;
    private boolean mRecorded;

    // For recording the melody
    public final int RESOLUTION = 960;
    private int mBPM;
    private MidiTrack mTempoTrack;
    protected MidiTrack mNoteTrack;
    protected String mFilePath;

    // For playback
    private MidiProcessor mMidiProcessor;
    private MidiEventHandler mMidiEventHandler;
    private Button mPlayButton;

    protected ChirpNoteSession mSession;

    /**
     * A MIDI melody track
     * @param tempo The tempo of the melody
     * @param filePath The path to store the file (of the melody recording) at
     * @param playButton The button used to start playback of the melody track
     */
    public Melody(int tempo, String filePath, Button playButton){
        mRecording = false;
        mRecorded = false;

        mBPM = tempo;
        mFilePath = filePath;

        mPlayButton = playButton;
    }

    /**
     * A MIDI melody track
     * @param session The session this melody is a part of
     * @param filePath The file path to store the melody at
     */
    public Melody(ChirpNoteSession session, String filePath){
        mRecording = false;
        mSession = session;
        mFilePath = filePath;
    }

    /**
     * Gets whether or not this melody is currently being played back
     * @return True if the melody is being played
     */
    @Override
    public boolean isPlaying(){
        return mMidiProcessor != null && mMidiProcessor.isRunning();
    }

    /**
     * Gets whether or not this melody is currently being recorded
     * @return True if the melody is being recorded
     */
    @Override
    public boolean isRecording(){
        return mRecording;
    }

    /**
     * Gets whether or not this melody has been recorded
     * @return True if the melody has been recorded
     */
    public boolean isRecorded(){
        return mRecorded;
    }

    /**
     * Gets the tempo of this melody
     * @return The BPM of this melody
     */
    public int getTempo(){
        return mSession == null ? mBPM : mSession.getTempo();
    }

    /**
     * Starts the recording process for this MIDI melody
     * @exception IllegalStateException if the recording process cannot be started at this time
     */
    @Override
    public void startRecording() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot start the recording process when the melody is already being recorded");
        }
        if(isPlaying()){
            throw new IllegalStateException("Cannot start the recording process when the melody is being played back");
        }
        mRecording = true;

        // Setup MIDI tracks
        mTempoTrack = new MidiTrack();
        mNoteTrack = new MidiTrack();

        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
        mTempoTrack.insertEvent(ts);

        // Tempo setup
        Tempo tempo = new Tempo();
        tempo.setBpm(getTempo());
        mTempoTrack.insertEvent(tempo);
    }

    /**
     * Stops the recording process for this MIDI melody
     * @exception IllegalStateException if the recording process cannot be stopped at this time
     */
    @Override
    public void stopRecording() throws IllegalStateException {
        if(!isRecording()){
            throw new IllegalStateException("Cannot stop the recording process if there is no active recording process (start recording first)");
        }
        mRecorded = true;
        ArrayList<MidiTrack> tracks = new ArrayList<>();
        tracks.add(mTempoTrack);
        tracks.add(mNoteTrack);

        // Write tracks to MIDI file
        MidiFile midiFile = new MidiFile(RESOLUTION, tracks);
        try {
            midiFile.writeToFile(new File(mFilePath));
        } catch(IOException e) {
            System.err.println(e);
        }
        mRecording = false;
    }

    /**
     * Plays back this melody
     * @exception IllegalStateException if the melody cannot be played at this time
     */
    @Override
    public void play() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot play the melody when there is an active recording process (stop recording first)");
        }
        if(!isRecorded()){
            throw new IllegalStateException("Cannot play the melody if it has not been recorded yet (record it first)");
        }
        if(isPlaying()){
            throw new IllegalStateException("Cannot play the melody if it is already being played (stop playback first)");
        }
        MidiFile midiFile = null;
        try {
            midiFile = new MidiFile(new File(mFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMidiEventHandler = new MidiEventHandler("MelodyPlayback", mPlayButton);
        mMidiProcessor = new MidiProcessor(midiFile);
        mMidiProcessor.registerEventListener(mMidiEventHandler, NoteOn.class);
        mMidiProcessor.start();
    }

    /**
     * Stops playback of this melody
     * @exception IllegalStateException if the melody cannot be stopped at this time
     */
    @Override
    public void stop() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot stop the melody when there is an active recording process (stop recording first)");
        }
        if(!isPlaying()){
            throw new IllegalStateException("Cannot stop the melody if it is not being played (start playback first)");
        }
        mMidiProcessor.reset();
    }

    /**
     * For testing (TODO: Remove play button from all Tracks and move to a new class that globally controls playback of tracks)
     */
    public void setPlayButton(Button button){
        mPlayButton = button;
    }
}
