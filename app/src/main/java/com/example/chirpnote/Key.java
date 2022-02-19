package com.example.chirpnote;

import java.io.Serializable;

public class Key implements Serializable {
    // Root note of the key
    public enum RootNote {
        C("C", 60),
        C_SHARP("C#", 61),
        D("D", 62),
        D_SHARP("D#", 63),
        E("E", 64),
        F("F", 65),
        F_SHARP("F#", 66),
        G("G", 67),
        G_SHARP("G#", 68),
        A("A", 69),
        A_SHARP("A#", 70),
        B("B", 71);

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

    // Type of key
    public enum Type {
        MAJOR("Major"),
        NATURAL_MINOR("Natural Minor"),
        HARMONIC_MINOR("Harmonic Minor");

        private String string;

        Type(String str){
            string = str;
        }

        @Override
        public String toString(){
            return string;
        }
    }

    private RootNote mRootNote;
    private Type mType;
    private int[] mSteps;

    public Key(RootNote root, Type type){
        mRootNote = root;
        mType = type;
        if(type == Type.MAJOR){
            mSteps = new int[]{0, 2, 2, 1, 2, 2, 2, 1};
        } else if(type == Type.NATURAL_MINOR){
            mSteps = new int[]{0, 2, 1, 2, 2, 1, 2, 2};
        } else if(type == Type.HARMONIC_MINOR){
            mSteps = new int[]{0, 2, 1, 2, 2, 1, 3, 1};
        }
    }

    /**
     * The key as a String
     * @return A String representation of this key
     */
    @Override
    public String toString(){
        return mRootNote + " " + mType;
    }

    /**
     * Gets the root note of this key
     * @return The root of this key
     */
    public RootNote getRootNote(){
        return mRootNote;
    }

    /**
     * Gets the type of this key
     * @return The key type
     */
    public Type getType(){
        return mType;
    }

    /**
     * Gets the MIDI notes of the scale associated with this key
     * @return An array of MIDI note numbers
     */
    public int[] getScaleNotes(){
        int[] arr = new int[mSteps.length];
        int current = mRootNote.getMidiNum();
        for(int i = 0; i < mSteps.length; i++){
            current += mSteps[i];
            arr[i] = current;
        }
        return arr;
    }
}
