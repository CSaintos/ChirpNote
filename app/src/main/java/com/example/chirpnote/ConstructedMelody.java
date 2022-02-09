package com.example.chirpnote;

import android.widget.Button;

import java.util.HashSet;

public class ConstructedMelody extends Melody {
    private HashSet<Integer> mNoteDurations;
    private int mPrevTick;
    private final int CHANNEL = 1;

    /**
     * A MIDI melody which is recorded (constructed) by adding notes from the UI
     * @param tempo The tempo of the melody
     * @param filePath The path to store the file (of the melody recording) at
     * @param playButton The button used to start playback of the melody track
     */
    public ConstructedMelody(int tempo, String filePath, Button playButton){
        super(tempo, filePath, playButton);

        // Possible note durations when building a melody
        mNoteDurations = new HashSet<>();
        mNoteDurations.add(1); // Whole note
        mNoteDurations.add(2); // Half note
        mNoteDurations.add(4); // Quarter note
        mNoteDurations.add(8); // Eighth note
        mNoteDurations.add(16); // Sixteenth note
        mNoteDurations.add(32); // Thirty-second note

        mPrevTick = 0;
    }

    /**
     * Adds a note to this melody with the given duration
     * @param note The note to add
     * @param duration The note duration (1 = whole note, 2 = half, 4 = quarter, etc...)
     * @return False if not currently recording a melody, or the note duration is invalid
     */
    public boolean addNote(MusicNote note, int duration){
        if(!super.isRecording() || note == null || !mNoteDurations.contains(duration)){
            return false;
        }
        int noteDuration = RESOLUTION * 4 / duration;
        mNoteTrack.insertNote(CHANNEL, note.getNoteNumber(), note.VELOCITY, mPrevTick, noteDuration);
        mPrevTick += noteDuration;
        return true;
    }

    @Override
    public boolean stopRecording() {
        mPrevTick = 0;
        return super.stopRecording();
    }
}
