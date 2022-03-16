package com.example.chirpnote;

import java.io.Serializable;

public class Key implements Serializable {

    // Root note of the key
    public enum RootNote {
        C("C", 60),
        C_SHARP("C#", 61),
//        D_FLAT("Db", 61),
        D("D", 62),
        D_SHARP("D#", 63),
//        E_FLAT("Eb", 63),
        E("E", 64),
        F("F", 65),
        F_SHARP("F#", 66),
//        G_FLAT("Gb", 66),
        G("G", 67),
        G_SHARP("G#", 68),
//        A_FLAT("Ab", 68),
        A("A", 69),
        A_SHARP("A#", 70),
//        B_FLAT("Bb", 70),
        B("B", 71);
//        C_FLAT("Cb", 71);

        private String string;
        private int midiNum;

        RootNote(RootNote rn) {
            this.string = rn.string;
            this.midiNum = rn.midiNum;
        }

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
        MINOR("Minor"),
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
    private Chord.Type[] mChordTypes;

    /**
     * A Key
     * @param root The root note of this key
     * @param type The type of key (type of scale used in this key)
     */
    public Key(RootNote root, Type type){
        mRootNote = root;
        mType = type;
        if(type == Type.MAJOR){
            mSteps = new int[]{0, 2, 2, 1, 2, 2, 2, 1};
            mChordTypes = new Chord.Type[]{Chord.Type.MAJOR, Chord.Type.MINOR, Chord.Type.MINOR,
                    Chord.Type.MAJOR, Chord.Type.MAJOR, Chord.Type.MINOR, Chord.Type.DIMINISHED, Chord.Type.MAJOR};
        } else {
            if(type == Type.NATURAL_MINOR){
                mSteps = new int[]{0, 2, 1, 2, 2, 1, 2, 2};
            } else if((type == Type.HARMONIC_MINOR) || (type == Type.MINOR)){
                mSteps = new int[]{0, 2, 1, 2, 2, 1, 3, 1};
            }
            mChordTypes = new Chord.Type[]{Chord.Type.MINOR, Chord.Type.DIMINISHED, Chord.Type.MAJOR,
                    Chord.Type.MINOR, Chord.Type.MAJOR, Chord.Type.MAJOR, Chord.Type.DIMINISHED, Chord.Type.MINOR};
        }
    }

    /**
     * Gets a String representation of this Key
     * @return This Key as a String
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

    /**
     * Gets the types of chords found in this key
     * @return All chord types in a key
     */
    public Chord.Type[] getChordTypes(){
        return mChordTypes;
    }

//    public int[] getNoteIndex()

//    public String[] getKeyAccidentals()
//    {
//        String[] keyAccidentals = new String[2];
//        if (mType == Type.MAJOR)
//        {
//            if ( (mRootNote.name() == RootNote.G.name()) || (mRootNote.name() == RootNote.F.name()) )
//            {
//                if (mRootNote.name() == RootNote.G.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "1";
//                return keyAccidentals;
//            }
//            else if ( (mRootNote.name() == RootNote.D.name()) || (mRootNote.name() == RootNote.B_FLAT.name()) )
//            {
//                if (mRootNote.name() == RootNote.D.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "2";
//                return keyAccidentals;
//            }
//            else if ( (mRootNote.name() == RootNote.A.name()) || (mRootNote.name() == RootNote.E_FLAT.name()) )
//            {
//                if (mRootNote.name() == RootNote.A.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "3";
//                return keyAccidentals;
//            }
//            else if ( (mRootNote.name() == RootNote.E.name()) || (mRootNote.name() == RootNote.A_FLAT.name()) )
//            {
//                if (mRootNote.name() == RootNote.E.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "4";
//                return keyAccidentals;
//            }
//            else if ( (mRootNote.name() == RootNote.B.name()) || (mRootNote.name() == RootNote.D_FLAT.name()) )
//            {
//                if (mRootNote.name() == RootNote.B.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "5";
//                return keyAccidentals;
//            }
//            else if ( (mRootNote.name() == RootNote.F_SHARP.name()) || (mRootNote.name() == RootNote.G_FLAT.name()) )
//            {
//                if (mRootNote.name() == RootNote.F_SHARP.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "6";
//                return keyAccidentals;
//            }
//            else if ( (mRootNote.name() == RootNote.C_SHARP.name()) || (mRootNote.name() == RootNote.C_FLAT.name()) )
//            {
//                if (mRootNote.name() == RootNote.C_SHARP.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "7";
//                return keyAccidentals;
//            }
//            else
//            {
//                keyAccidentals[0] = "neutral";
//                keyAccidentals[1] = "0";
//                return keyAccidentals;
//            }
//        }
//        else if (mType == Type.MINOR)
//        {
//            if ( (mRootNote.name() == RootNote.E.name()) || (mRootNote.name() == RootNote.D.name()) )
//            {
//                if (mRootNote.name() == RootNote.E.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "1";
//                return keyAccidentals;
//            }
//            else if ( (mRootNote.name() == RootNote.B.name()) || (mRootNote.name() == RootNote.G.name()) )
//            {
//                if (mRootNote.name() == RootNote.B.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "2";
//                return keyAccidentals;
//            }
//            else if ( (mRootNote.name() == RootNote.F_SHARP.name()) || (mRootNote.name() == RootNote.C.name()) )
//            {
//                if (mRootNote.name() == RootNote.F_SHARP.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "3";
//                return keyAccidentals;
//            }
//            else if ( (mRootNote.name() == RootNote.C_SHARP.name()) || (mRootNote.name() == RootNote.F.name()) )
//            {
//                if (mRootNote.name() == RootNote.C_SHARP.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "4";
//                return keyAccidentals;
//            }
//            else if ( (mRootNote.name() == RootNote.G_SHARP.name()) || (mRootNote.name() == RootNote.B_FLAT.name()) )
//            {
//                if (mRootNote.name() == RootNote.G_SHARP.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "5";
//                return keyAccidentals;
//            }
//            else if ( (mRootNote.name() == RootNote.D_SHARP.name()) || (mRootNote.name() == RootNote.E_FLAT.name()) )
//            {
//                if (mRootNote.name() == RootNote.D_SHARP.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "6";
//                return keyAccidentals;
//            }
//            else if ( (mRootNote.name() == RootNote.A_SHARP.name()) || (mRootNote.name() == RootNote.A_FLAT.name()) )
//            {
//                if (mRootNote.name() == RootNote.A_SHARP.name())
//                {
//                    keyAccidentals[0] = "sharp";
//                }
//                else
//                {
//                    keyAccidentals[0] = "flat";
//                }
//                keyAccidentals[1] = "7";
//                return keyAccidentals;
//            }
//            else
//            {
//                keyAccidentals[0] = "neutral";
//                keyAccidentals[1] = "0";
//                return keyAccidentals;
//            }
//        }
//        return keyAccidentals;
//    }
}
