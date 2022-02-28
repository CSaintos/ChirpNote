package com.example.chirpnote;

import android.widget.Button;

import com.example.chirpnote.midiLib.src.event.NoteOff;
import com.example.chirpnote.midiLib.src.event.NoteOn;

public class RealTimeMelody extends Melody {
    private final int CHANNEL = 0;
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

    @Override
    public void startRecording() throws IllegalStateException {
        super.startRecording();
        mRecordingStartTime = System.currentTimeMillis();
    }

    /**
     * Writes a note on event for the given note to this melody. Should be called when a note is stopped.
     * (Note: Do not call, unless you manually want to add a noteOn event.
     * This happens automatically during an active recording process, if the note has been constructed with a melody)
     * @param note The note to write a note on event for
     * @return False if melody is not being recorded yet (cannot add a note if there is no active recording process)
     */
    public boolean writeNoteOn(MusicNote note){
        if(!super.isRecording() || note == null){
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
        if(!super.isRecording() || note == null){
            return false;
        }
        mNoteTrack.insertEvent(new NoteOff(getCurrentTick(), CHANNEL, note.getNoteNumber(), 0));
        return true;
    }

    private long getCurrentTick(){
        return (System.currentTimeMillis() - mRecordingStartTime) * super.getTempo()  * super.RESOLUTION / 60000;
    }
}
