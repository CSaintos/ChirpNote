package com.example.chirpnote;

import android.widget.Button;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;

public class MusicNote {
    private int mMidiNumber;
    private Button mButton;
    private RealTimeMelody mMelody;
    // The velocity is a default value for all music notes, as most Android device do not have
    // a pressure sensor in the display, so we have no way of obtaining the velocity for all devices
    public final int VELOCITY = 110;

    /**
     * A music note on the UI keyboard (no recording of this note is planned)
     * @param noteNumber The MIDI number of that note.
     *                   For all MIDI numbers and their associated notes,
     *                   see: https://www.inspiredacoustics.com/en/MIDI_note_numbers_and_center_frequencies
     * @param noteButton The button in the UI that should play this note
     */
    public MusicNote(int noteNumber, Button noteButton){
        mMidiNumber = noteNumber;
        mButton = noteButton;
        mMelody = null;
    }

    /**
     * A music note on the UI keyboard (this note's events will be recorded to the given melody)
     * @param noteNumber The MIDI number of that note.
     *                   For all MIDI numbers and their associated notes,
     *                   see: https://www.inspiredacoustics.com/en/MIDI_note_numbers_and_center_frequencies
     * @param noteButton The button in the UI that should play this note
     * @param melody The melody where this note's events will be recorded
     */
    public MusicNote(int noteNumber, Button noteButton, RealTimeMelody melody){
        mMidiNumber = noteNumber;
        mButton = noteButton;
        mMelody = melody;
    }

    /**
     * Gets the MIDI number associated with this music note
     * @return The MIDI number of this note
     */
    public int getNoteNumber(){
        return mMidiNumber;
    }

    /**
     * Gets the Button associated with this music note
     * @return The button that plays this note
     */
    public Button getButton(){
        return mButton;
    }

    /**
     * Plays this music note
     * @param midiDriver The MIDI driver to create MIDI events with
     */
    public void playNote(MidiDriver midiDriver){
        midiDriver.write(new byte[]{MidiConstants.NOTE_ON, (byte) mMidiNumber, (byte) VELOCITY});
        if(mMelody != null){
            mMelody.writeNoteOn(this);
        }
    }

    /**
     * Stops this music note
     * @param midiDriver The MIDI driver to create MIDI events with
     */
    public void stopNote(MidiDriver midiDriver){
        midiDriver.write(new byte[]{MidiConstants.NOTE_OFF, (byte) mMidiNumber, (byte) 0x00});
        if(mMelody != null){
            mMelody.writeNoteOff(this);
        }
    }
}
