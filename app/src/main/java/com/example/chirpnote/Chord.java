package com.example.chirpnote;

import android.widget.Button;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;
import java.util.Iterator;

public class Chord {
    // Root note of the chord
    public enum RootNote {
        C("C", 48),
        C_SHARP("C#", 49),
        D("D", 50),
        D_SHARP("D#", 51),
        E("E", 52),
        F("F", 53),
        F_SHARP("F#", 54),
        G("G", 55),
        G_SHARP("G#", 56),
        A("A", 57),
        A_SHARP("A#", 58),
        B("B", 59);

        private String string;
        private int midiNum;

        RootNote(String str, int num){
            string = str;
            midiNum = num;
        }

        @Override
        public String toString(){
            return string;
        }

        public int getMidiNum(){
            return midiNum;
        }
    }
    // Chord type
    public enum Type {
        MAJOR("Major"),
        MINOR("Minor"),
        DIMINISHED("Diminished");

        private String string;

        Type(String str){
            string = str;
        }

        @Override
        public String toString(){
            return string;
        }
    }
    // Chord inversion
    public enum Inversion {
        ROOT,
        FIRST,
        SECOND,
    }
    private Type mType;
    private RootNote mRootNote;
    private int mRootNum;
    private int mOctave;
    private int mAlteration;
    private Inversion mInversion;
    private ArrayList<MusicNote> mNotes;
    private int[][] mNoteEvents;
    private boolean mPlaying;
    private int mTempo;
    private final static int RESOLUTION = 960;
    private Button mButton;
    private MidiDriver midiDriver = MidiDriver.getInstance();
    private int mRoman;

    // MIDI events for playback of chord alterations
    // Each inner int[] represents a NoteOn event to fire when playing back the chord alteration
    // [note index in mNotes, MIDI tick of event, velocity of NoteOn (velocity of 0 for NoteOff)]
    private static int[][] alteration0Events =
        {{0, 0, MusicNote.VELOCITY},
        {1, 0, MusicNote.VELOCITY},
        {2, 0, MusicNote.VELOCITY},
        {0, RESOLUTION * 4, 0},
        {1, RESOLUTION * 4, 0},
        {2, RESOLUTION * 4, 0}};
    private static int[][] alteration1Events =
        {{1, 0, MusicNote.VELOCITY},
        {2, 0, MusicNote.VELOCITY},
        {1, RESOLUTION, 0},
        {2, RESOLUTION, 0},
        {0, RESOLUTION, MusicNote.VELOCITY},
        {0, RESOLUTION * 2, 0},
        {1, RESOLUTION * 2, MusicNote.VELOCITY},
        {2, RESOLUTION * 2, MusicNote.VELOCITY},
        {1, RESOLUTION * 3, 0},
        {2, RESOLUTION * 3, 0},
        {0, RESOLUTION * 3, MusicNote.VELOCITY},
        {0, RESOLUTION * 4, 0}};
    private static int[][] alteration2Events =
        {{0, 0, MusicNote.VELOCITY},
        {0, RESOLUTION, 0},
        {1, RESOLUTION, MusicNote.VELOCITY},
        {1, RESOLUTION * 2, 0},
        {2, RESOLUTION * 2, MusicNote.VELOCITY},
        {2, RESOLUTION * 3, 0},
        {1, RESOLUTION * 3, MusicNote.VELOCITY},
        {1, RESOLUTION * 4, 0}};

    public Chord() {}

    public Chord(RootNote rootNote, Type type, int tempo, int roman) {
        mType = type;
        mRootNote = rootNote;
        mRootNum = rootNote.getMidiNum();
        buildChord();
        mTempo = tempo;
        mButton = null;
        mPlaying = false;

        mRoman = roman;
    }

    public Chord(Button viewById) {
        this.mButton = viewById;
    }

    /**
     * A chord
     * @param root The root note of this chord
     * @param chordType The type of chord this is
     * @param tempo The tempo at which to play this chord
     */
    public Chord(RootNote root, Type chordType, int tempo){
        mType = chordType;
        mRootNote = root;
        mRootNum = root.getMidiNum();
        buildChord();
        mTempo = tempo;
        mButton = null;
        mPlaying = false;
    }
    /**
     * copy constructor
     */
    public Chord(Chord c){
        mType = c.getType();
        mRootNote = c.getRootNote();
        mRootNum = c.getRootNumber();
        buildChord();
        mTempo = c.getTempo();
        mButton = c.getButton(); // TODO: Change this assignment to null once ChordsActivity is refactored
        mPlaying = false;
    }

    /**
     * A chord that is played by a button on the UI
     * @param root The root note of this chord
     * @param chordType The type of chord this is
     * @param tempo The tempo at which to play this chord
     * @param chordButton The button in the UI that should play this chord
     */
    public Chord(RootNote root, Type chordType, int tempo, Button chordButton){
        mType = chordType;
        mRootNote = root;
        mRootNum = root.getMidiNum();
        buildChord();
        mTempo = tempo;
        mButton = chordButton;
        mPlaying = false;
    }

    /**
     * Adds the appropriate notes to this chord, based on the chord type and root note.
     * Only call this once, from a constructor
     */
    private void buildChord(){
        mNotes = new ArrayList<>();
        // mNotes.add(new MusicNote(mRootNum - 12)); // TODO: Add support for more notes in a chord
        mNotes.add(new MusicNote(mRootNum));
        if(mType == Type.MAJOR){
            mNotes.add(new MusicNote(mRootNum + 4));
            mNotes.add(new MusicNote(mRootNum + 7));
        } else if(mType == Type.MINOR){
            mNotes.add(new MusicNote(mRootNum + 3));
            mNotes.add(new MusicNote(mRootNum + 7));
        } else if(mType == Type.DIMINISHED){
            mNotes.add(new MusicNote(mRootNum + 3));
            mNotes.add(new MusicNote(mRootNum + 6));
        }
        mInversion = Inversion.ROOT;
        // Octave 1: C1-C2, 2: C2-C3, 3: C3-C4, 4: C4-C5, 5: C5-C6
        if(24 <= mRootNum && mRootNum < 36){
            mOctave = 1;
        } else if(36 <= mRootNum && mRootNum < 48){
            mOctave = 2;
        } else if(48 <= mRootNum && mRootNum < 60){
            mOctave = 3;
        } else if(60 <= mRootNum && mRootNum < 72){
            mOctave = 4;
        } else if(72 <= mRootNum && mRootNum < 84){
            mOctave = 5;
        }
        mAlteration = 0;
        mNoteEvents = alteration0Events;
    }

    /**
     * Gets the type of this chord
     * @return The chord type
     */
    public Type getType(){
        return mType;
    }

    /**
     * Gets the MIDI number of the root note of this chord
     * @return The MIDI number of the root note
     */
    public int getRootNumber(){
        return mRootNum;
    }

    /**
     * Gets the root note of this chord
     * @return The root note
     */
    public RootNote getRootNote(){
        return mRootNote;
    }

    /**
     * Gets the tempo at which this chord is played
     * @return The tempo in bpm
     */
    public int getTempo(){
        return mTempo;
    }

    /**
     * Gets the octave this chord is at
     * @return An int from 1-5 representing the octave this chord is at.
     * The octave is determined by the root note of the chord.
     * A chord in octave 1 has a root note in the range [C1, C2).
     * A chord in octave 5 has a root note in the range [C5, C6).
     */
    public int getOctave(){
        return mOctave;
    }

    /**
     * Gets the inversion this chord is in
     * @return The chord inversion
     */
    public Inversion getInversion(){
        return mInversion;
    }

    /**
     * Gets the alteration this chord is in
     * @return The chord alteration
     */
    public int getAlteration(){
        return mAlteration;
    }

    /**
     * Gets the MIDI numbers for the notes in this chord
     * @return An array of the note numbers
     */
    public int[] getNoteNumbers(){
        int[] nums = new int[mNotes.size()];
        for(int i = 0; i < nums.length; i++){
            nums[i] = mNotes.get(i).getNoteNumber();
        }
        return nums;
    }

    /**
     * Gets the MusicNotes in this chord
     * @return An array of the MusicNote
     */
    public MusicNote[] getMusicNotes(){
        MusicNote[] notes = new MusicNote[mNotes.size()];
        for(int i = 0; i < notes.length; i++){
            notes[i] = mNotes.get(i);
        }
        return notes;
    }

    /**
     * Gets the NoteOn events used to playback this chord
     * @return An array of events, with each event in the following format:
     *          [note number, MIDI tick, velocity]
     */
    public int[][] getNoteEvents(){
        int[][] events = new int[mNoteEvents.length][];
        for(int i = 0; i < mNoteEvents.length; i++){
            int[] temp = mNoteEvents[i];
            events[i] = new int[]{mNotes.get(temp[0]).getNoteNumber(), temp[1], temp[2]};
        }
        return events;
    }

    /**
     * Gets the Button associated with this chord
     * @return The button that plays this chord (null if this chord has no button)
     */
    public Button getButton(){
        return mButton;
    }

    public void setButton(Button button)
    {
        this.mButton = button;
    }

    public int getRoman()
    {
        return mRoman;
    }

    /**
     * Increases the octave of this chord
     */
    public void octaveUp(){
        if(mOctave < 5){
            for(MusicNote note : mNotes){
                note.octaveUp();
            }
            mOctave++;
        }
    }

    /**
     * Decreases the octave of this chord
     */
    public void octaveDown(){
        if(mOctave > 1){
            for(MusicNote note : mNotes){
                note.octaveDown();
            }
            mOctave--;
        }
    }

    /**
     * Sets the inversion for this chord
     * @param newInv The inversion to set this chord to
     */
    public void setInversion(Inversion newInv){
        ArrayList<MusicNote> temp = new ArrayList<>();
        switch(newInv){
            case ROOT:
                if(mInversion == Inversion.FIRST){
                    temp.add(mNotes.get(2));
                    temp.add(mNotes.get(0));
                    temp.add(mNotes.get(1));
                    temp.get(0).octaveDown();
                    mNotes = temp;
                } else if(mInversion == Inversion.SECOND){
                    temp.add(mNotes.get(1));
                    temp.add(mNotes.get(2));
                    temp.add(mNotes.get(0));
                    temp.get(1).octaveDown();
                    temp.get(2).octaveDown();
                    mNotes = temp;
                }
                mInversion = newInv;
                break;
            case FIRST:
                if(mInversion == Inversion.ROOT){
                    temp.add(mNotes.get(1));
                    temp.add(mNotes.get(2));
                    temp.add(mNotes.get(0));
                    temp.get(2).octaveUp();
                    mNotes = temp;
                } else if(mInversion == Inversion.SECOND){
                    temp.add(mNotes.get(2));
                    temp.add(mNotes.get(0));
                    temp.add(mNotes.get(1));
                    temp.get(0).octaveDown();
                    mNotes = temp;
                }
                mInversion = newInv;
                break;
            case SECOND:
                if(mInversion == Inversion.ROOT){
                    temp.add(mNotes.get(2));
                    temp.add(mNotes.get(0));
                    temp.add(mNotes.get(1));
                    temp.get(1).octaveUp();
                    temp.get(2).octaveUp();
                    mNotes = temp;
                } else if(mInversion == Inversion.FIRST){
                    temp.add(mNotes.get(1));
                    temp.add(mNotes.get(2));
                    temp.add(mNotes.get(0));
                    temp.get(2).octaveUp();
                    mNotes = temp;
                }
                mInversion = newInv;
                break;
        }
    }

    /**
     * Sets the alteration for this chord
     * @param alt The alteration number to set this chord to
     */
    public void setAlteration(int alt){
        switch(alt){
            case 1:
                mNoteEvents = alteration1Events;
                mAlteration = alt;
                break;
            case 2:
                mNoteEvents = alteration2Events;
                mAlteration = alt;
                break;
            default:
                mNoteEvents = alteration0Events;
                mAlteration = 0;
        }
    }

    /**
     * Plays this chord
     */
    public void play(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                mPlaying = true;
                int i = 0;
                int[] noteEvent = mNoteEvents[i++];
                while(i < mNoteEvents.length && mPlaying){
                    if(noteEvent[1] > getRelativeTick(start)){
                        continue;
                    }
                    midiDriver.write(new byte[]{MidiConstants.NOTE_ON, (byte) mNotes.get(noteEvent[0]).getNoteNumber(), (byte) noteEvent[2]});
                    noteEvent = mNoteEvents[i++];
                }
                // Handle the last note event
                while(noteEvent[1] > getRelativeTick(start)){
                    continue;
                }
                midiDriver.write(new byte[]{MidiConstants.NOTE_ON, (byte) mNotes.get(noteEvent[0]).getNoteNumber(), (byte) noteEvent[2]});
                mPlaying = false;
            }
        }).start();
    }

    private long getRelativeTick(long start){
        return (System.currentTimeMillis() - start) * mTempo  * RESOLUTION / 60000;
    }

    /**
     * Stops this chord
     */
    public void stop(){
        mPlaying = false;
        for(MusicNote note : mNotes){
            midiDriver.write(new byte[]{MidiConstants.NOTE_OFF, (byte) note.getNoteNumber(), (byte) note.VELOCITY});
        }
    }

    /**
     * Gets a String representation of this Chord
     * @return This Chord as a String
     */
    @Override
    public String toString(){
        return mRootNote + " " + mType;// + "testing Chord toString()";
    }
}
