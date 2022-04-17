package com.example.chirpnote;

import com.example.midiFileLib.src.MidiFile;
import com.example.midiFileLib.src.MidiTrack;
import com.example.midiFileLib.src.event.NoteOff;
import com.example.midiFileLib.src.event.NoteOn;
import com.example.midiFileLib.src.event.meta.Tempo;
import com.example.midiFileLib.src.event.meta.TimeSignature;
import com.example.midiFileLib.src.util.MidiProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PercussionTrack implements Track {
    // States
    private boolean mRecording;

    // For writing to MIDI file
    public final int RESOLUTION = 960;
    private MidiTrack mTempoTrack;
    protected MidiTrack mNoteTrack;
    private String mFilePath;

    // For playback
    private MidiProcessor mMidiProcessor;
    private MidiEventHandler mMidiEventHandler;

    private Session mSession;
    public final static int CHANNEL = 9;

    public PercussionTrack(Session session){
        mRecording = false;
        mSession = session;
        mFilePath = session.getMidiPath();
        mMidiEventHandler = new MidiEventHandler("PercussionPlayback");
    }

    /**
     * Gets whether or not this percussion track is currently being played back
     * @return True if the percussion track is being played
     */
    @Override
    public boolean isPlaying(){
        return mMidiProcessor != null && mMidiProcessor.isRunning();
    }

    /**
     * Gets whether or not this percussion track is currently being recorded
     * @return True if the percussion track is being recorded
     */
    @Override
    public boolean isRecording(){
        return mRecording;
    }

    /**
     * Gets whether or not this chord track has been recorded
     * @return True if the chord track has been recorded
     */
    public boolean isRecorded(){
        return mSession.isMidiPrepared();
    }

    /**
     * Starts the recording process for this percussion track
     * Note: Do not call this method if this percussion track was obtained from a Mixer instance
     * @exception IllegalStateException if the recording process cannot be started
     */
    @Override
    public void startRecording() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot start the recording process when the percussion track is already being recorded");
        }
        if(isPlaying()){
            throw new IllegalStateException("Cannot start the recording process when the percussion track is being played back");
        }
        if(isRecorded()) {
            // Do not start the recording process more than once, as this will overwrite the entire percussion track
            // We only want this behavior for a RealTimeMelody
            return;
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
        tempo.setBpm(mSession.getTempo());
        mTempoTrack.insertEvent(tempo);

        // Stop the recording to write the MIDI file instantly (to add percussion patterns, we will edit the MIDI file)
        stopRecording();
    }

    /**
     * Stops the recording process for this percussion track
     * @exception IllegalStateException if the recording process cannot be stopped
     */
    @Override
    public void stopRecording() throws IllegalStateException {
        if(!isRecording()){
            throw new IllegalStateException("Cannot stop the recording process if there is no active recording process (start recording first)");
        }
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
        mSession.setMidiPrepared();
    }

    /**
     * Plays back this percussion track
     * @exception IllegalStateException if the percussion track cannot be played
     */
    @Override
    public void play() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot play the percussion track when there is an active recording process (stop recording first)");
        }
        if(!isRecorded()){
            throw new IllegalStateException("Cannot play the percussion track if it has not been recorded yet (record it first)");
        }
        if(isPlaying()){
            throw new IllegalStateException("Cannot play the percussion track if it is already being played (stop playback first)");
        }
        MidiFile midiFile = null;
        try {
            midiFile = new MidiFile(new File(mFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMidiProcessor = new MidiProcessor(midiFile);
        mMidiProcessor.registerEventListener(mMidiEventHandler, NoteOn.class);
        mMidiProcessor.registerEventListener(mMidiEventHandler, NoteOff.class);
        mMidiProcessor.start();
    }

    /**
     * Stops playback of this percussion track
     * @exception IllegalStateException if the percussion track cannot be stopped
     */
    @Override
    public void stop() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot stop the percussion track when there is an active recording process (stop recording first)");
        }
        if(!isPlaying()){
            throw new IllegalStateException("Cannot stop the percussion track if it is not being played (start playback first)");
        }
        mMidiProcessor.reset();
    }
}
