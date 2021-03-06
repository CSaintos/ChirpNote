package com.example.chirpnote;

import com.example.midiFileLib.src.MidiFile;
import com.example.midiFileLib.src.event.NoteOn;

import com.example.chirpnote.Notation.NoteFont;
import com.example.chirpnote.Notation.MelodyElement;
import com.example.chirpnote.Notation.MusicFontAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private static HashMap<NoteDuration, Integer> mNoteDurations = new HashMap<>();
    public final static Notation notation = new Notation(); // for getElement
    public final static int CHANNEL = 2;
    public int mElementIndex = 0;

    /**
     * A MIDI melody which is recorded (constructed) by adding notes from the UI
     * @param session The session this melody is a part of
     */
    public ConstructedMelody(ChirpNoteSession session){
        super(session, session.getMidiPath());

        // Possible note durations when building a melody
        mNoteDurations = new HashMap<>();
        mNoteDurations.put(NoteDuration.WHOLE_NOTE, 1);
        mNoteDurations.put(NoteDuration.HALF_NOTE, 2);
        mNoteDurations.put(NoteDuration.QUARTER_NOTE, 4);
        mNoteDurations.put(NoteDuration.EIGHTH_NOTE, 8);
        mNoteDurations.put(NoteDuration.SIXTEENTH_NOTE, 16);
        mNoteDurations.put(NoteDuration.THIRTY_SECOND_NOTE, 32);
    }

    /**
     * Adds a note to this melody with the given duration, at the given position
     * @param note The note to add
     * @param duration The note duration
     * @param position The position in the melody to add the note (replaces existing notes/rests at this position)
     * @exception NullPointerException if the given note is null
     * @exception IllegalStateException if the note cannot be added to the melody at this time
     */
    public void addNote(MusicNote note, NoteDuration duration, int position) throws NullPointerException, IllegalStateException {
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
        File output = new File(mFilePath);
        try {
            midiFile = new MidiFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Add note to the melody
        int noteDuration = getDurationTicks(duration);
        if(position < mSession.mMelodyElements.size()){
            // Add to the middle of the melody
            int[] tickRange = removeAtPosition(position, midiFile);
            midiFile.getTracks().get(1).insertNote(CHANNEL, note.getNoteNumber(), note.VELOCITY, tickRange[0], noteDuration);
            mSession.mMelodyElements.set(position, encodeNote(note, duration, tickRange[0]));
            updateFromPosition(position, tickRange, noteDuration, midiFile);
        } else {
            // Add to the end of the melody
            midiFile.getTracks().get(1).insertNote(CHANNEL, note.getNoteNumber(), note.VELOCITY, mSession.mNextMelodyTick, noteDuration);
            mSession.mMelodyElements.add(encodeNote(note, duration, mSession.mNextMelodyTick));
            mSession.mNextMelodyTick += noteDuration;
        }

        // Write changes to MIDI file
        try {
            midiFile.writeToFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a rest to this melody with the given duration
     * @param duration The rest duration
     * @param position The position in the melody to add the rest (replaces existing notes/rests at this position)
     * @exception IllegalStateException if the rest cannot be added to the melody at this time
     */
    public void addRest(NoteDuration duration, int position) throws IllegalStateException {
        // Recording process is stopped right after it is started for a ConstructedMelody,
        // so we check if the melody has been recorded, and not if the recording process is active
        if(!isRecorded()){
            throw new IllegalStateException("Cannot add a rest to the melody if the recording process has not been started");
        }

        // Add rest to the melody
        int restDuration = getDurationTicks(duration);
        if(position < mSession.mMelodyElements.size()){ // Add to the middle of the melody
            // Read existing MIDI file
            MidiFile midiFile = null;
            try {
                midiFile = new MidiFile(new File(mFilePath));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Add rest
            int[] tickRange = removeAtPosition(position, midiFile);
            mSession.mMelodyElements.set(position, encodeRest(duration, tickRange[0]));
            updateFromPosition(position, tickRange, restDuration, midiFile);

            // Write changes to MIDI file
            try {
                midiFile.writeToFile(new File(mFilePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Add to the end of the melody
            mSession.mMelodyElements.add(encodeRest(duration, mSession.mNextMelodyTick));
            mSession.mNextMelodyTick += restDuration;
        }
    }

    /**
     * Temporary method used for testing
     * Get's the melody element given the position
     * @param position int index
     * @return NotFont representation of the element
     */
    public NoteFont getElement(int position) {
        NoteFont nf = notation.new NoteFont();
        MusicFontAdapter mfAdapter;
        if (position < mSession.mMelodyElements.size()) {
            System.out.println(mSession.mMelodyElements.get(position));
            MelodyElement me = decodeElement(mSession.mMelodyElements.get(position));
            mfAdapter = notation.new MusicFontAdapter(me.musicNote, me.noteDuration);
            nf = mfAdapter.getNoteFont();
        }
        return nf;
    }

    /**
     * Gets the length of the given NoteDuration in MIDI ticks
     * @param duration The duration to compute the ticks of
     * @return The duration's MIDI ticks
     */
    public static int getDurationTicks(NoteDuration duration){
        return RESOLUTION * 4 / mNoteDurations.get(duration);
    }

    /**
     * Gets the index (in the enumeration) of the given NoteDuration
     * @param duration The duration to get the index of
     * @return The duration's index
     */
    private int getDurationIdx(NoteDuration duration){
        return (int) (Math.log(mNoteDurations.get(duration))/Math.log(2));
    }

    /**
     * Removes the element (note/rest) at the given position
     * @param position The position to remove at
     * @param midiFile The MIDI file to update
     * @return The MIDI tick range of the removed element
     */
    private int[] removeAtPosition(int position, MidiFile midiFile){
        String encodedElement = mSession.mMelodyElements.get(position);
        int tickOn = Integer.parseInt(encodedElement.substring(7));
        int tickOff = tickOn + getDurationTicks(NoteDuration.values()[Integer.parseInt(encodedElement.substring(6, 7))]);
        int[] tickRange = new int[]{tickOn, tickOff};

        // Rest in given position
        if(encodedElement.startsWith("128")){
            return tickRange;
        }
        // Note in given position
        int pitch = removeLeadingZeroes(encodedElement.substring(0, 3));
        int velocity = removeLeadingZeroes(encodedElement.substring(3, 6));

        // Remove NoteOn and NoteOff events from MIDI file to remove the note
        midiFile.getTracks().get(1).removeNoteOnEvent(new NoteOn(tickRange[0], CHANNEL, pitch, velocity));
        // A NoteOn event with velocity 0 was used instead of a NoteOff event
        // See: https://github.com/LeffelMania/android-midi-lib/issues/10
        midiFile.getTracks().get(1).removeNoteOnEvent(new NoteOn(tickRange[1], CHANNEL, pitch, 0));

        return tickRange;
    }

    /**
     * Removes any leading zeroes from the given String
     * @param str The String to remove leading zeroes from
     * @return The trimmed String as an Integer
     */
    private int removeLeadingZeroes(String str){
        return Integer.parseInt(str.replaceFirst("^0+(?!$)", ""));
    }

    /**
     * Updates the melody from the given position.
     * Adds rests to pad a smaller duration, or overwrites existing notes/rests to make space for a larger duration
     * @param position The position to update from
     * @param tickRange The tick range of the element that used to exist at this position
     * @param newDuration The duration of the new element
     * @param midiFile The MIDI file to update
     */
    private void updateFromPosition(int position, int[] tickRange, int newDuration, MidiFile midiFile){
        int endTick = tickRange[0] + newDuration;
        if(endTick < tickRange[1]){
            // Add rests after given position, when the duration is less than the duration of the old element (note/rest)
            for(NoteDuration nd : NoteDuration.values()){
                if(endTick + getDurationTicks(nd) <= tickRange[1]){
                    mSession.mMelodyElements.add(++position, encodeRest(nd, endTick));
                    endTick += getDurationTicks(nd);
                }
                if(endTick == tickRange[1]) break;
            }
        } else if(endTick > tickRange[1]){
            // Overwrite the next elements (notes/rests) to make space for the new element
            position++;
            do {
                tickRange = removeAtPosition(position, midiFile);
                mSession.mMelodyElements.remove(position);
            } while(endTick > tickRange[1]);
        }
    }

    /**
     * Decodes a Melody Element String into a MelodyElement struct and returns it.
     * The struct can be used to access any attributes that is stored within a melody element, ex:
     *   melodyElement.musicNote; // retrieves the MusicNote variable
     *   melodyElement.noteDuration; // retrieves the NoteDuration variable
     *   melodyElement.velTick; // retrieves the velocity, tick array pair
     *
     * @param element melody element string representation
     * @return MelodyElement struct
     */
    public MelodyElement decodeElement(String element) {
        MelodyElement me = notation.new MelodyElement();
        //Log.d("Decode element length", ""+element.length());

        if (element.length() >= 8) {
            try {
                //Log.d("Decode", ""+element.charAt(6));
                NoteDuration noteDuration = NoteDuration.values()[Character.getNumericValue(element.charAt(6))];
                MusicNote musicNote = new MusicNote(Integer.parseInt(element.substring(0, 3)));
                int[] velTick = {Integer.parseInt(element.substring(3, 6)), Character.getNumericValue(element.charAt(7))};

                me = notation.new MelodyElement(musicNote, noteDuration, velTick);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return me;
    }

    /*
	The string encoding for melody elements (notes/rests) is defined as follows:
	char 0-2: note MIDI number (range of 000-127, 128 if rest)
	char 3-5: note velocity (range of 000-127)
	char 6: note duration (0 = whole, 1 = half, 2 = quarter, 3 = eighth, 4 = sixteenth, 5 = thirty-second)
	                        2^5        2^4      2^3           2^2        2^1            2^0
	char 7 to last char: note tick (the position of the note in the melody, in terms of MIDI ticks for the MIDI file)
	*/

    /**
     * getMeasure
     * Reads from mMelodyElements from session starting from mElementIndex
     * and constructs a measure
     * @return encoded melody string[] equivalent to a measure
     */
    public String[] getMeasure() {
        ArrayList<String> measure = new ArrayList<>();
        int measureDuration = 0;
        int index = mElementIndex;
        boolean val = true;

        if (mSession.mMelodyElements.size() == 0) { // this would be an error
            return new String[0];
        }

        while (measureDuration < 32 && val) {
            String encodedElement = mSession.mMelodyElements.get(index);
            Notation.MelodyElement me = decodeElement(encodedElement);
            measureDuration += me.getDurationValue();
            if (measureDuration > 32) {
                val = false;
            } else {
                measure.add(encodedElement);
            }
            index++;
        }
        return measure.toArray(new String[0]);
    }

    /**
     * Sets mElementIndex to the start of the next measure
     */
    public void nextMeasure()
    {
        int index = mElementIndex;
        index += getMeasure().length;
        if (mSession.mMelodyElements.size() > index) {
            mElementIndex = index;
        }
    }

    /**
     * Sets mElementIndex to the start of the previous measure
     */
    public void previousMeasure() {
        int measureDuration = 0;
        int index = mElementIndex - 1;
        boolean val = true;

        if (index > 0) {
            while (measureDuration < 32 && val) {
                String encodedElement = mSession.mMelodyElements.get(index);
                Notation.MelodyElement me = decodeElement(encodedElement);
                measureDuration += me.getDurationValue();

                if (measureDuration >= 32) {
                    val = false;
                } else {
                    index--;
                }
            }
            mElementIndex = index;
        }
    }

    /**
     * getElementIndex
     * @return mElementIndex
     */
    public int getElementIndex() {
        return mElementIndex;
    }

    /**
     * Encodes the given note as a String
     * @param note The note to encode
     * @param duration The duration of the note
     * @param tick The MIDI tick at which this note occurs in the melody
     * @return The note encoding
     */
    private String encodeNote(MusicNote note, NoteDuration duration, int tick){
        return padNumber(note.getNoteNumber()) + padNumber(note.VELOCITY) + getDurationIdx(duration) + tick;
    }

    /**
     * Adds leading zeroes to the given number
     * @param num The number to pad
     * @return The padded number as a String
     */
    private String padNumber(int num){
        if(num < 10) return "00" + num;
        if(num < 100) return "0" + num;
        return "" + num;
    }

    /**
     * Encodes the given rest as a String
     * @param duration The duration of the rest
     * @param tick The MIDI tick at which this rest occurs in the melody
     * @return The rest encoding
     */
    private String encodeRest(NoteDuration duration, int tick){
        return "128" + "000" + getDurationIdx(duration) + tick;
    }

    public boolean isRecorded(){
        return mSession.isMidiPrepared();
    }

    @Override
    /**
     * Prepares the MIDI file to begin constructing this melody
     * Note: Do not call this method if this melody was obtained from a Mixer instance
     */
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
            if(mSession != null) {
                mSession.setMidiPrepared();
            }
        }
    }
}
