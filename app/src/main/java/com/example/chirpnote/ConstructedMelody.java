package com.example.chirpnote;

import android.widget.Button;

import com.leff.midi.MidiFile;

import java.io.File;
import java.io.IOException;
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
     * A MIDI melody which is recorded (constructed) by adding notes from the UI
     * @param session The session this melody is a part of
     */
    public ConstructedMelody(Session session){
        super(session);

        // Possible note durations when building a melody
        mNoteDurations = new HashMap<>();
        mNoteDurations.put(NoteDuration.WHOLE_NOTE, 1);
        mNoteDurations.put(NoteDuration.HALF_NOTE, 2);
        mNoteDurations.put(NoteDuration.QUARTER_NOTE, 4);
        mNoteDurations.put(NoteDuration.EIGHTH_NOTE, 8);
        mNoteDurations.put(NoteDuration.SIXTEENTH_NOTE, 16);
        mNoteDurations.put(NoteDuration.THIRTY_SECOND_NOTE, 32);

        mPrevTick = 0; // TODO: Move previous tick to session class (so it can persist and be saved in the database)
    }

    /**
     * Adds a note to this melody with the given duration
     * @param note The note to add
     * @param duration The note duration
     * @exception NullPointerException if the given note is null
     * @exception IllegalStateException if the note cannot be added
     */
    public void addNote(MusicNote note, NoteDuration duration) throws  NullPointerException, IllegalStateException {
        if(note == null){
            throw new NullPointerException("Cannot add a null MusicNote to the melody");
        }
        // Recording process is stopped right after it is started for a ConstructedMelody,
        // so we check if the melody has been recorded, and not if the recording process is active
        if(!isRecorded()){
            throw new IllegalStateException("Cannot add a note to the melody if the recording process has not been started");
        }

        // Read existing MIDI file
        MidiFile midiFile = null;
        try {
            midiFile = new MidiFile(new File(mFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add note to the melody
        int noteDuration = RESOLUTION * 4 / mNoteDurations.get(duration);
        midiFile.getTracks().get(1).insertNote(CHANNEL, note.getNoteNumber(), note.VELOCITY, mPrevTick, noteDuration);
        mPrevTick += noteDuration;

        // Write changes to MIDI file
        try {
            midiFile.writeToFile(new File(mFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a rest to this melody with the given duration
     * @param duration The rest duration
     * @exception IllegalStateException if the rest cannot be added
     */
    public void addRest(NoteDuration duration) throws IllegalStateException {
        // Recording process is stopped right after it is started for a ConstructedMelody,
        // so we check if the melody has been recorded, and not if the recording process is active
        if(!isRecorded()){
            throw new IllegalStateException("Cannot add a rest to the melody if the recording process has not been started");
        }
        mPrevTick += RESOLUTION * 4 / mNoteDurations.get(duration);
    }

    @Override
    public void startRecording() throws IllegalStateException {
        // Starting the recording process overwrites the previously recorded melody
        // We only want this behavior for a real time melody, so only do this once for a constructed melody
        if(!isRecorded()) {
            super.startRecording();
            // End the recording process instantly to write the MIDI file
            // All other methods will edit this MIDI file
            stopRecording();
        }
    }

    @Override
    public void stopRecording() throws IllegalStateException {
        // Stopping the recording process overwrites the previously recorded melody
        // We only want this behavior for a real time melody, so only do this once for a constructed melody
        if(!isRecorded()){
            super.stopRecording();
        }
    }
}
