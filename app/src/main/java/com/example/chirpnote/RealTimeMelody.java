package com.example.chirpnote;

import android.widget.Button;

import com.example.midiFileLib.src.MidiFile;
import com.example.midiFileLib.src.event.NoteOn;

import java.io.File;
import java.io.IOException;

public class RealTimeMelody extends Melody {
    private final int CHANNEL = 3;
    private long mRecordingStartTime;

    /**
     * A MIDI melody which is recorded in real time on the UI keyboard
     * @param tempo The tempo of the melody
     * @param filePath The path to store the file (of the melody recording) at
     * @param playButton The button used to start playback of the melody track
     */
    public RealTimeMelody(int tempo, String filePath, Button playButton){
        super(tempo, filePath, playButton);
    }

    /**
     * A MIDI melody which is recorded in real time on the UI keyboard
     * @param session The session this melody is a part of
     */
    public RealTimeMelody(Session session){
        super(session, session.getRealTimeMelodyPath());
    }

    @Override
    public void startRecording() throws IllegalStateException {
        super.startRecording();
        mRecordingStartTime = System.currentTimeMillis();
    }

    @Override
    public void stopRecording() throws IllegalStateException {
        super.stopRecording();
        if(mSession != null) {
            mSession.setRealTimeMelodyRecorded();
        }
    }

    /**
     * Writes a note on event for the given note to this melody. Should be called when a note is stopped.
     * (Note: Do not call, unless you manually want to add a noteOn event.
     * This happens automatically during an active recording process, if the note has been constructed with a melody)
     * @param note The note to write a note on event for
     * @return False if melody is not being recorded yet (cannot add a note if there is no active recording process)
     */
    public boolean writeNoteOn(MusicNote note){
        if(!isRecording() || note == null){
            return false;
        }
        mNoteTrack.insertEvent(new NoteOn(getCurrentTick(), CHANNEL, note.getNoteNumber(), note.VELOCITY));
        return true;
    }

    /**
     * Writes a note off event for the given note to this melody. Should be called when a note is stopped.
     * (Note: Do not call, unless you manually want to add a noteOff event.
     * This happens automatically during an active recording process, if the note has been constructed with a melody)
     * @param note The note to write a note off event for
     * @return False if melody is not being recorded, or if the given note is null
     */
    public boolean writeNoteOff(MusicNote note){
        if(!isRecording() || note == null){
            return false;
        }
        // Using a NoteOn event with a velocity of 0, instead of a NoteOff event (essentially the same thing)
        mNoteTrack.insertEvent(new NoteOn(getCurrentTick(), CHANNEL, note.getNoteNumber(), 0));
        return true;
    }

    private long getCurrentTick(){
        return (System.currentTimeMillis() - mRecordingStartTime) * getTempo()  * RESOLUTION / 60000;
    }
    
    /**
    * Quantize each note in this melody to the nearest sixteenth note
    * @exception IllegalStateException if the melody cannot be quantized
    */
    public void quantize() throws IllegalStateException {
        // Only quantize if melody is constructed using a session
        if(mSession == null){
            return;
        }
        if(!isRecorded()){
            throw new IllegalStateException("Cannot quantize the melody if it has not been recorded yet (record it first)");
        }
        // Read existing MIDI file
        MidiFile midiFile = null;
        File output = new File(mFilePath);
        try {
            midiFile = new MidiFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Quantize melody
        /*
        -Initialize HashMap to store how much a NoteOn event has shifted by, to be able to update the corresponding NoteOff event later
        -Iterate through events in the midiFile
            -Only look for NoteOn and NoteOff events (on this same CHANNEL)
            -If we hit a NoteOn event
                -Check if the event needs to be re-positioned (quantized), using some quantization technique/algorithm
                    -If it does need to be re-positioned, do so by updating the tick and delta fields
                    -Add to HashMap (map.put(noteNumber, amountChangedInTicks))
            -If we hit a NoteOff event
                -Get the delta for the corresponding NoteOn event from the HashMap (map.get(noteNumber))
                -Update the NoteOff event by that delta (same process as for NoteOn)
                -Remove this event from the HashMap
        */
        
        // Write changes to MIDI file
        try {
            midiFile.writeToFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
