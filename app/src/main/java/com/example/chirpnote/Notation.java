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
    public enum syntax {
        STAFF5LINES,
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
    public HashMap<syntax, String> unicode;
    {
        unicode = new HashMap<>();
        unicode.put(syntax.STAFF5LINES, "\uE014");
        unicode.put(syntax.GCLEF, "\uE050");
        unicode.put(syntax.FCLEF, "\uE062");
        unicode.put(syntax.NOTEWHOLE, "\uE1D2");
        unicode.put(syntax.NOTEHALFUP, "\uE1D3");
        unicode.put(syntax.NOTEHALFDOWN, "\uE1D4");
        unicode.put(syntax.NOTEQUARTERUP, "\uE1D5");
        unicode.put(syntax.NOTEQUARTERDOWN, "\uE1D6");
        unicode.put(syntax.NOTE8THUP, "\uE1D7");
        unicode.put(syntax.NOTE8THDOWN, "\uE1D8");
        unicode.put(syntax.NOTE16THUP, "\uE1D9");
        unicode.put(syntax.NOTE16THDOWN, "\uE1DA");
        unicode.put(syntax.NOTE32NDUP, "\uE1DB");
        unicode.put(syntax.NOTE32NDDOWN, "\uE1DC");
        unicode.put(syntax.AUGMENTATIONDOT, "\uE1E7");
    }

}
