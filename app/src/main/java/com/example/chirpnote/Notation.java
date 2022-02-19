package com.example.chirpnote;

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
        public static final EnumSet<Syntax> ACCIDENTAL = EnumSet.range(ACCIDENTAL_FLAT, ACCIDENTAL_SHARP);
        public static final EnumSet<Syntax> MISC = EnumSet.of(AUGMENTATION_DOT);

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
        public int lineNum;

        public NoteFont() {
            symbol = Syntax.EMPTY;
            prefix = Syntax.EMPTY;
            suffix = Syntax.EMPTY;
            lineNum = -1;
        }
        public NoteFont(Syntax symbol, int lineNum) {
            this();
            this.symbol = symbol;
            this.lineNum = lineNum;
        }
        public NoteFont(Syntax symbol, Syntax prefix, Syntax suffix, int lineNum) {
            this(symbol, lineNum);
            this.prefix = prefix;
            this.suffix = suffix;
        }
    }

}
