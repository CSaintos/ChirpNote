package com.example.chirpnote;

import android.graphics.Color;

import java.util.EnumSet;
import java.util.HashMap;

/**
 * This class is for managing music syntax as string text
 */
public class Notation {

    public Notation() {}

    /**
     * Enumeration of individual music syntax symbols
     */
    public enum Syntax {
        /** STAFF */
        STAFF_5_LINES("\uE014"),
        BARLINE_SINGLE("\uE030"),
        BARLINE_FINAL("\uE032"),
        BARLINE_REVERSE_FINAL("\uE033"),
        /** CLEF */
        SPACE_CLEF(new String(new char[7]).replace("\0", " ")),
        G_CLEF("\uE050 "),
        F_CLEF("\uE062 "),
        /** NOTE */
        SPACE_NOTE(new String(new char[5]).replace("\0", " ")),
        NOTE_WHOLE("\uE1D2"),
        NOTE_HALF_UP("\uE1D3"),
        NOTE_HALF_DOWN("\uE1D4"),
        NOTE_QUARTER_UP("\uE1D5"),
        NOTE_QUARTER_DOWN("\uE1D6"),
        NOTE_8TH_UP("\uE1D7"),
        NOTE_8TH_DOWN("\uE1D8"),
        NOTE_16TH_UP("\uE1D9"),
        NOTE_16TH_DOWN("\uE1DA"),
        NOTE_32ND_UP("\uE1DB"),
        NOTE_32ND_DOWN("\uE1DC"),
        /** REST */
        REST_WHOLE("\uE4E3"),
        REST_HALF("\uE4E4"),
        REST_QUARTER("\uE4E5"),
        REST_8TH("\uE4E6"),
        REST_16TH("\uE4E7"),
        REST_32ND("\uE4E8"),
        /** ACCIDENTAL */
        ACCIDENTAL_FLAT("\uE1E7"),
        ACCIDENTAL_NATURAL("\uE261"),
        ACCIDENTAL_SHARP("\uE262"),
        /** MISC */
        AUGMENTATION_DOT("\uE1DC"),
        EMPTY("");

        // Syntax notation categories (types)
        public static final EnumSet<Syntax> STAFF = EnumSet.range(STAFF_5_LINES, BARLINE_REVERSE_FINAL);
        public static final EnumSet<Syntax> CLEF = EnumSet.range(SPACE_CLEF, F_CLEF);
        public static final EnumSet<Syntax> NOTE = EnumSet.range(SPACE_NOTE, NOTE_32ND_DOWN);
        public static final EnumSet<Syntax> REST = EnumSet.range(REST_WHOLE, REST_32ND);
        public static final EnumSet<Syntax> ACCIDENTAL = EnumSet.range(ACCIDENTAL_FLAT, ACCIDENTAL_SHARP);
        public static final EnumSet<Syntax> MISC = EnumSet.of(AUGMENTATION_DOT);
        // Syntax notation durations
        public static final EnumSet<Syntax> WHOLE = EnumSet.of(NOTE_WHOLE, REST_WHOLE);
        public static final EnumSet<Syntax> HALF = EnumSet.of(NOTE_HALF_UP, NOTE_HALF_DOWN, REST_HALF);
        public static final EnumSet<Syntax> QUARTER = EnumSet.of(NOTE_QUARTER_UP, NOTE_QUARTER_DOWN, REST_QUARTER);
        public static final EnumSet<Syntax> EIGHTH = EnumSet.of(NOTE_8TH_UP, NOTE_8TH_DOWN, REST_8TH);
        public static final EnumSet<Syntax> SIXTEENTH = EnumSet.of(NOTE_16TH_UP, NOTE_16TH_DOWN, REST_16TH);
        public static final EnumSet<Syntax> THIRTY_SECOND = EnumSet.of(NOTE_32ND_UP, NOTE_32ND_DOWN, REST_32ND);

        public String unicode;

        Syntax(String unicode) {
            this.unicode = unicode;
        }
    }

    /**
    NoteFont class for creating music notation objects
     */
    public class NoteFont {
        public Syntax symbol;
        public Syntax prefix;
        public Syntax suffix;
        public int noteLength;
        public int lineNum;
        public int color;

        public NoteFont() {
            color = Color.DKGRAY;
            symbol = Syntax.EMPTY;
            prefix = Syntax.EMPTY;
            suffix = Syntax.EMPTY;
            lineNum = -1;
            noteLength = -1;
        }
        public NoteFont(Syntax symbol, int lineNum) {
            this();
            this.symbol = symbol;
            this.lineNum = lineNum;
        }
        public NoteFont(Syntax symbol, Syntax prefix, Syntax suffix, int noteLength, int lineNum, int color) {
            this(symbol, lineNum);
            this.prefix = prefix;
            this.suffix = suffix;
            this.noteLength = noteLength;
            this.color = color;
        }
        public NoteFont(NoteFont nf) {
            this(nf.symbol, nf.prefix, nf.suffix, nf.noteLength, nf.lineNum, nf.color);
        }

        public String toString() {
            return symbol.toString();
        }
    }

    /**
     * MelodyElement struct
     * Encapsulates a melody element for Melody classes
     */
    public class MelodyElement {
        public MusicNote musicNote;
        public ConstructedMelody.NoteDuration noteDuration;
        public int[] velTick;

        public MelodyElement() {
            musicNote = new MusicNote(60);
            noteDuration = ConstructedMelody.NoteDuration.WHOLE_NOTE;
            velTick = new int[]{1, 1};
        }

        public MelodyElement(MusicNote mn, ConstructedMelody.NoteDuration nd, int[] velTick) {
            this.velTick = new int[2];
            musicNote = mn;
            noteDuration = nd;
            this.velTick[0] = velTick[0];
            this.velTick[1] = velTick[1];
        }

        public int getDurationValue() {
            int i = 1;
            switch (noteDuration) {
                case WHOLE_NOTE:
                    i = 32;
                    break;
                case HALF_NOTE:
                    i = 16;
                    break;
                case QUARTER_NOTE:
                    i = 8;
                    break;
                case EIGHTH_NOTE:
                    i = 4;
                    break;
                case SIXTEENTH_NOTE:
                    i = 2;
                    break;
                case THIRTY_SECOND_NOTE:
                    i = 1;
                    break;
            }
            return i;
        }
    }

    /**
     * MusicFontAdapter
     * An assistant class to convert NoteFonts to durations and musicNotes
     * Also works in vice versa, duration and musicNotes to NoteFonts
     */
    public class MusicFontAdapter {
        private MusicNote mn;
        private ConstructedMelody.NoteDuration nd;
        private NoteFont nf;

        public MusicFontAdapter(NoteFont nf) {
            this.nf = nf;
            int noteNumber = 60;

            if (Syntax.WHOLE.contains(nf.symbol)) {
                nd = ConstructedMelody.NoteDuration.WHOLE_NOTE;
            } else if (Syntax.HALF.contains(nf.symbol)) {
                nd = ConstructedMelody.NoteDuration.HALF_NOTE;
            } else if (Syntax.QUARTER.contains(nf.symbol)) {
                nd = ConstructedMelody.NoteDuration.QUARTER_NOTE;
            } else if (Syntax.EIGHTH.contains(nf.symbol)) {
                nd = ConstructedMelody.NoteDuration.EIGHTH_NOTE;
            } else if (Syntax.SIXTEENTH.contains(nf.symbol)) {
                nd = ConstructedMelody.NoteDuration.SIXTEENTH_NOTE;
            } else if (Syntax.THIRTY_SECOND.contains(nf.symbol)) {
                nd = ConstructedMelody.NoteDuration.THIRTY_SECOND_NOTE;
            }

            switch (this.nf.lineNum) {
                case 1:
                    noteNumber = 60;
                    break;
                case 2:
                    noteNumber = 62;
                    break;
                case 3:
                    noteNumber = 64;
                    break;
                case 4:
                    noteNumber = 65;
                    break;
                case 5:
                    noteNumber = 67;
                    break;
                case 6:
                    noteNumber = 69;
                    break;
                case 7:
                    noteNumber = 71;
                    break;
            }
            if (this.nf.prefix == Syntax.ACCIDENTAL_SHARP) {
                noteNumber++;
            }

            if (Syntax.REST.contains(nf.symbol)) {
                noteNumber = 128;
            }

            mn = new MusicNote(noteNumber);
        }

        public MusicFontAdapter(MusicNote mn, ConstructedMelody.NoteDuration nd) {
            this.mn = mn;
            this.nd = nd;
            this.nf = new NoteFont();

            int noteNumber = this.mn.getNoteNumber();

            switch (noteNumber) {
                case 61:
                case 63:
                case 66:
                case 68:
                case 70:
                case 72:
                    this.nf.prefix = Syntax.ACCIDENTAL_SHARP;
                    break;
            }

            if (60 == noteNumber || 61 == noteNumber) {
                this.nf.lineNum = 1;
            } else if (62 == noteNumber || 63 == noteNumber) {
                this.nf.lineNum = 2;
            } else if (64 == noteNumber) {
                this.nf.lineNum = 3;
            } else if (65 == noteNumber || 66 == noteNumber) {
                this.nf.lineNum = 4;
            } else if (67 == noteNumber || 68 == noteNumber) {
                this.nf.lineNum = 5;
            } else if (69 == noteNumber || 70 == noteNumber) {
                this.nf.lineNum = 6;
            } else {
                this.nf.lineNum = 7;
            }

            if (128 == noteNumber) {
                switch (this.nd) {
                    case WHOLE_NOTE:
                        this.nf.symbol = Syntax.REST_WHOLE;
                        this.nf.lineNum = 9;
                        break;
                    case HALF_NOTE:
                        this.nf.symbol = Syntax.REST_HALF;
                        break;
                    case QUARTER_NOTE:
                        this.nf.symbol = Syntax.REST_QUARTER;
                        break;
                    case EIGHTH_NOTE:
                        this.nf.symbol = Syntax.REST_8TH;
                        break;
                    case SIXTEENTH_NOTE:
                        this.nf.symbol = Syntax.REST_16TH;
                        break;
                    case THIRTY_SECOND_NOTE:
                        this.nf.symbol = Syntax.REST_32ND;
                        break;
                }
            } else {
                switch (this.nd) {
                    case WHOLE_NOTE:
                        this.nf.symbol = Syntax.NOTE_WHOLE;
                        break;
                    case HALF_NOTE:
                        this.nf.symbol = Syntax.NOTE_HALF_UP;
                        break;
                    case QUARTER_NOTE:
                        this.nf.symbol = Syntax.NOTE_QUARTER_UP;
                        break;
                    case EIGHTH_NOTE:
                        this.nf.symbol = Syntax.NOTE_8TH_UP;
                        break;
                    case SIXTEENTH_NOTE:
                        this.nf.symbol = Syntax.NOTE_16TH_UP;
                        break;
                    case THIRTY_SECOND_NOTE:
                        this.nf.symbol = Syntax.NOTE_32ND_UP;
                        break;
                }
            }

            switch(this.nd) {
                case WHOLE_NOTE:
                    nf.noteLength = 32;
                    break;
                case HALF_NOTE:
                    nf.noteLength = 16;
                    break;
                case QUARTER_NOTE:
                    nf.noteLength = 8;
                    break;
                case EIGHTH_NOTE:
                    nf.noteLength = 4;
                    break;
                case SIXTEENTH_NOTE:
                    nf.noteLength = 2;
                    break;
                case THIRTY_SECOND_NOTE:
                    nf.noteLength = 1;
                    break;
            }
        }

        public NoteFont getNoteFont() {
            return nf;
        }

        public MusicNote getMusicNote() {
            return mn;
        }

        public ConstructedMelody.NoteDuration getNoteDuration() {
            return nd;
        }
    }

}
