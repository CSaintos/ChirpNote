package com.example.chirpnote;

import android.widget.Button;

import java.util.HashMap;

public class ConstructedMelody extends Melody {
    // Durations for notes
    public enum NoteDuration {
        WHOLE_NOTE,
        HALF_NOTE,
        QUARTER_NOTE,
        EIGHTH_NOTE,
        SIXTEENTH_NOTE,
        THIRTY_SECOND_NOTE,
    }
    private HashMap<NoteDuration, Integer> mNoteDurations;
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
        mNoteDurations = new HashMap<>();
        mNoteDurations.put(NoteDuration.WHOLE_NOTE, 1);
        mNoteDurations.put(NoteDuration.HALF_NOTE, 2);
        mNoteDurations.put(NoteDuration.QUARTER_NOTE, 4);
        mNoteDurations.put(NoteDuration.EIGHTH_NOTE, 8);
        mNoteDurations.put(NoteDuration.SIXTEENTH_NOTE, 16);
        mNoteDurations.put(NoteDuration.THIRTY_SECOND_NOTE, 32);

        mPrevTick = 0;
    }

    /**
     * Adds a note to this melody with the given duration
     * @param note The note to add
     * @param duration The note duration (1 = whole note, 2 = half, 4 = quarter, etc...)
     * @return False if not currently recording a melody
     */
    public boolean addNote(MusicNote note, NoteDuration duration){
        if(!super.isRecording() || note == null){
            return false;
        }
        int noteDuration = RESOLUTION * 4 / mNoteDurations.get(duration);
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
