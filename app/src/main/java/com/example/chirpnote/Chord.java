package com.example.chirpnote;

import android.widget.Button;

import java.util.ArrayList;

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
    private int mRoot;
    private int mOctave;
    private Inversion mInversion;
    private ArrayList<MusicNote> mNotes;
    private Button mButton;

    /**
     * A chord
     * @param root The root note of this chord
     * @param chordType The type of chord this is
     */
    public Chord(RootNote root, Type chordType){
        mNotes = new ArrayList<>();
        mType = chordType;
        mRootNote = root;
        mRoot = root.getMidiNum();
        buildChord();
        mButton = null;
    }

    /**
     * A chord that is played by a button on the UI
     * @param root The root note of this chord
     * @param chordType The type of chord this is
     * @param chordButton The button in the UI that should play this chord
     */
    public Chord(RootNote root, Type chordType, Button chordButton){
        mNotes = new ArrayList<>();
        mType = chordType;
        mRootNote = root;
        mRoot = root.getMidiNum();
        buildChord();
        mButton = chordButton;
    }

    /**
     * Adds the appropriate notes to this chord, based on the chord type and root note.
     * Only call this once, from a constructor
     */
    private void buildChord(){
        mNotes.add(new MusicNote(mRoot));
        if(mType == Type.MAJOR){
            mNotes.add(new MusicNote(mRoot + 4));
            mNotes.add(new MusicNote(mRoot + 7));
        } else if(mType == Type.MINOR){
            mNotes.add(new MusicNote(mRoot + 3));
            mNotes.add(new MusicNote(mRoot + 7));
        } else if(mType == Type.DIMINISHED){
            mNotes.add(new MusicNote(mRoot + 3));
            mNotes.add(new MusicNote(mRoot + 6));
        }
        mInversion = Inversion.ROOT;
        // Octave 1: C1-C2, 2: C2-C3, 3: C3-C4, 4: C4-C5, 5: C5-C6
        if(24 <= mRoot && mRoot < 36){
            mOctave = 1;
        } else if(36 <= mRoot && mRoot < 48){
            mOctave = 2;
        } else if(48 <= mRoot && mRoot < 60){
            mOctave = 3;
        } else if(60 <= mRoot && mRoot < 72){
            mOctave = 4;
        } else if(72 <= mRoot && mRoot < 84){
            mOctave = 5;
        }
    }

    /**
     * Gets the type of this chord
     * @return The chord type
     */
    public Type getType(){
        return mType;
    }

    /**
     * Gets the root note of this chord
     * @return The MIDI number of the root note
     */
    public int getRoot(){
        return mRoot;
    }

    /**
     * Gets the octave this chord is at
     * @return An integer representing the octave this chord is at.
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
     * Gets the Button associated with this chord
     * @return The button that plays this chord (null if this chord has no button)
     */
    public Button getButton(){
        return mButton;
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
     * @param newInv The new inversion for this chord
     */
    public void setInversion(Inversion newInv){
        if(newInv == Inversion.ROOT){
            if(mInversion == Inversion.FIRST){
                mNotes.get(0).octaveDown();
            } else if(mInversion == Inversion.SECOND){
                mNotes.get(0).octaveDown();
                mNotes.get(1).octaveDown();
            }
        } else if(newInv == Inversion.FIRST){
            if(mInversion == Inversion.ROOT){
                mNotes.get(0).octaveUp();
            } else if(mInversion == Inversion.SECOND){
                mNotes.get(1).octaveDown();
            }
        } else if(newInv == Inversion.SECOND){
            if(mInversion == Inversion.ROOT){
                mNotes.get(0).octaveUp();
                mNotes.get(1).octaveUp();
            } else if(mInversion == Inversion.FIRST){
                mNotes.get(1).octaveUp();
            }
        }
        mInversion = newInv;
    }

    /**
     * Plays this chord
     */
    public void play(){
        for(MusicNote note : mNotes){
            note.play();
        }
    }

    /**
     * Stops this chord
     */
    public void stop(){
        for(MusicNote note : mNotes){
            note.stop();
        }
    }

    /**
     * Gets a String representation of this Chord
     * @return This Chord as a String
     */
    @Override
    public String toString(){
        return mRootNote + " " + mType;
    }
}
