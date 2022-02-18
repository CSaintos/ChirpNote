package com.example.chirpnote;

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
        SPACE,
        STAFF5LINES,
        BARLINESINGLE,
        BARLINEFINAL,
        BARLINEREVERSEFINAL,
        GCLEF,
        FCLEF,
        NOTEWHOLE,
        NOTEHALFUP,
        NOTEHALFDOWN,
        NOTEQUARTERUP,
        NOTEQUARTERDOWN,
        NOTE8THUP,
        NOTE8THDOWN,
        NOTE16THUP,
        NOTE16THDOWN,
        NOTE32NDUP,
        NOTE32NDDOWN,
        AUGMENTATIONDOT,
    }

    /**
     * Used to retrieve the unicode of the musical syntax symbols.
     *
     * @Param syntax enum
     * @Return unicode string
     */
    public static HashMap<Syntax, String> unicode;
    {
        unicode = new HashMap<>();
        unicode.put(Syntax.SPACE, "     ");
        unicode.put(Syntax.STAFF5LINES, "\uE014");
        unicode.put(Syntax.BARLINESINGLE, "\uE030");
        unicode.put(Syntax.BARLINEFINAL, "\uE032");
        unicode.put(Syntax.BARLINEREVERSEFINAL, "\uE033");
        unicode.put(Syntax.GCLEF, "\uE050");
        unicode.put(Syntax.FCLEF, "\uE062");
        unicode.put(Syntax.NOTEWHOLE, "\uE1D2");
        unicode.put(Syntax.NOTEHALFUP, "\uE1D3");
        unicode.put(Syntax.NOTEHALFDOWN, "\uE1D4");
        unicode.put(Syntax.NOTEQUARTERUP, "\uE1D5");
        unicode.put(Syntax.NOTEQUARTERDOWN, "\uE1D6");
        unicode.put(Syntax.NOTE8THUP, "\uE1D7");
        unicode.put(Syntax.NOTE8THDOWN, "\uE1D8");
        unicode.put(Syntax.NOTE16THUP, "\uE1D9");
        unicode.put(Syntax.NOTE16THDOWN, "\uE1DA");
        unicode.put(Syntax.NOTE32NDUP, "\uE1DB");
        unicode.put(Syntax.NOTE32NDDOWN, "\uE1DC");
        unicode.put(Syntax.AUGMENTATIONDOT, "\uE1E7");
    }

    public class NoteFont {
        public NoteFont() {}
        public NoteFont(Syntax symbol, int lineNum) {
            this.symbol = symbol;
            this.lineNum = lineNum;
        }
        public Syntax symbol;
        public int lineNum;
    }

}
