package com.example.chirpnote.activities;

import static com.example.chirpnote.Notation.Syntax;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chirpnote.BLL;
import com.example.chirpnote.ConstructedMelody;
import com.example.chirpnote.Key;
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.Notation;
import com.example.chirpnote.Notation.NoteFont;
import com.example.chirpnote.R;
import com.example.chirpnote.ChirpNoteSession;
import com.google.android.material.navigation.NavigationView;

import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.util.ArrayList;

public class MelodyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Notation notation = new Notation();

    private TextView[] staffLines;
    private RadioButton[] noteLengthButtons;
    private Button leftButton;
    private Button rightButton;
    private Button restButton;
    private Button playButton;
    private Button navLeftButton;
    private Button navRightButton;
    private Button octUpButton;
    private Button octDownButton;
    private TextView melodyText;
    private TextView gclefText;
    private TextView octaveText;

    private BLL<NoteFont> noteList;
    private BLL.ListIterator itr;
    private NoteFont currentNote; // can store any symbol
    private NoteFont currentDuration; // only stores rest length
    private MidiDriver midiDriver;
    private ConstructedMelody consMelody;
    private Notation.MusicFontAdapter mfAdapter;
    private ChirpNoteSession session;
    private Key key;
    private int octNum;
    private int melodyPosition;

    //private Key currentKey;
    private ArrayList<MusicNote> keyButtons;
    private ArrayList<MusicNote> pianoKeys;

    private int barLength;
    private int maxBarLength;

    // navigation drawer
    private DrawerLayout drawer;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melody);

        // navigation menu
        Toolbar toolbar = findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // end of navigation menu

        // Init noteList
        noteList = new BLL<>();
        itr = noteList.listIterator();

        // Init note constraint vars
        barLength = 0;
        maxBarLength = 32;

        // Init session
        // TODO: get key from Session Activity
        key = new Key(Key.RootNote.C, Key.Type.MAJOR);
        octNum = 4;

        // TODO: get session from Session Activity

        String basePath = this.getFilesDir().getPath();
        session = new ChirpNoteSession("Name", key, 120,
                basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3");

        // Init Constructed Melody
        consMelody = new ConstructedMelody(session);
        consMelody.startRecording();
        midiDriver = MidiDriver.getInstance();
        melodyPosition = 0;

        // Initialize buttons
        leftButton = (Button) findViewById(R.id.melodyleftbutton);
        rightButton = (Button) findViewById(R.id.melodyrightbutton);
        restButton = (Button) findViewById(R.id.melodyrestbutton);
        playButton = (Button) findViewById(R.id.melodyPlayButton);
        navLeftButton = (Button) findViewById(R.id.melodynavleft);
        navRightButton = (Button) findViewById(R.id.melodynavright);
        octUpButton = (Button) findViewById(R.id.melodyoctupbutton);
        octDownButton = (Button) findViewById(R.id.melodyoctdownbutton);

        // Initialize piano keys
        pianoKeys = getPianoKeys();

        // Initialize note suggestions
        keyButtons = new ArrayList<>();
        for (int i = 0; i < key.getScaleNotes().length-1; i++)
        {
            // TODO: Think of a better way to do this
            int rootIdx = (/*currentKey*/key.getScaleNotes()[i] - 60) % 12;
            /** arraylist of all chords that belong to the current key based on the type of chord
             * it takes in the root note of the chord and type of chord
             */
            keyButtons.add(pianoKeys.get(rootIdx));
        }

        Button noteSuggestButton = findViewById(R.id.noteSuggestion);
        noteSuggestButton.setClickable(true);

        noteSuggestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noteSuggestButton.isSelected())
                {
                    noteSuggestButton.setSelected(false);
                    for (int i = 0; i < keyButtons.size(); i++)
                    {
                        keyButtons.get(i).getButton().setSelected(false);
                    }
                }
                else
                {
                    noteSuggestButton.setSelected(true);
                    for (int i = 0; i < keyButtons.size(); i++)
                    {
                        keyButtons.get(i).getButton().setSelected(true);
                    }
                }
            }
        });

        // Initialize text views
        melodyText = (TextView) findViewById(R.id.stafftextview);
        gclefText = (TextView) findViewById(R.id.gcleftextview);
        octaveText = (TextView) findViewById(R.id.melodyoctindicator);

        // Initialize all staff line text views
        staffLines = new TextView[] {
                (TextView) findViewById(R.id.spaceN1textview),
                (TextView) findViewById(R.id.line0textview),
                (TextView) findViewById(R.id.space0textview),
                (TextView) findViewById(R.id.line1textview),
                (TextView) findViewById(R.id.space1textview),
                (TextView) findViewById(R.id.line2textview),
                (TextView) findViewById(R.id.space2textview),
                (TextView) findViewById(R.id.line3textview),
                (TextView) findViewById(R.id.space3textview),
                (TextView) findViewById(R.id.line4textview),
                (TextView) findViewById(R.id.space4textview),
                (TextView) findViewById(R.id.line5textview),
                (TextView) findViewById(R.id.space5textview),
                (TextView) findViewById(R.id.line6textview),
                (TextView) findViewById(R.id.space6textview),
        };

        // Initialize noteLengthButtons
        noteLengthButtons = new RadioButton[] {
                (RadioButton) findViewById(R.id.melodywholeradiobutton),
                (RadioButton) findViewById(R.id.melodyhalfradiobutton),
                (RadioButton) findViewById(R.id.melody4thradiobutton),
                (RadioButton) findViewById(R.id.melody8thradiobutton),
                (RadioButton) findViewById(R.id.melody16thradiobutton),
                (RadioButton) findViewById(R.id.melody32ndradiobutton)
        };

        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /**
         * Same as setting the keyboard listeners,
         *  just with a rest instead
         */
        restButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                NoteFont replacedSymbol = notation.new NoteFont(currentNote);

                // Set rest symbol
                currentNote.symbol = currentDuration.symbol;

                // Set noteLength
                currentNote.noteLength = currentDuration.noteLength;

                // Locate vertical line staff position
                if (currentDuration.symbol == Syntax.REST_WHOLE) {
                    currentNote.lineNum = 9;
                } else {
                    currentNote.lineNum = 7;
                }

                // remove accidentals
                currentNote.prefix = Syntax.EMPTY;
                currentNote.suffix = Syntax.EMPTY;

                replaceElement(currentNote, replacedSymbol);
                /*
                Log.d("BarLength, before", Integer.toString(barLength));
                // apply note constraint
                int tempBarLength = barLength - replacedSymbol.noteLength + currentNote.noteLength;
                if (tempBarLength <= maxBarLength) {
                    barLength = tempBarLength;
                    // Set rest to noteList
                    itr.set(notation.new NoteFont(currentNote));
                    // Set rest to constructedMelody
                    mfAdapter = notation.new MusicFontAdapter(currentNote);
                    consMelody.addRest(mfAdapter.getNoteDuration(), melodyPosition);
                } else {
                    currentNote = replacedSymbol;
                }
                Log.d("BarLength, after", Integer.toString(barLength));
                 */

                displayText();
            }
        });

        /**
         *
         */
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //toConstructedMelody();
                if (consMelody.isPlaying()) consMelody.stop();
                consMelody.play();
            }
        });

        /**
         * Navigate to the left note
         */
        navLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itr.hasPrevious()) {
                    currentNote = notation.new NoteFont((NoteFont) itr.get());
                    currentNote.color = Color.DKGRAY;
                    itr.set(notation.new NoteFont(currentNote));
                    itr.previous();
                    currentNote = notation.new NoteFont((NoteFont) itr.get());
                    currentNote.color = Color.BLUE;
                    itr.set(notation.new NoteFont(currentNote));

                    melodyPosition--;
                }

                Log.d("NoteList2", currentNote.symbol.toString());

                displayText();
            }
        });

        /**
         * Navigate to the right note
         */
        navRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itr.hasNext()) {
                    currentNote = notation.new NoteFont((NoteFont) itr.get());
                    currentNote.color = Color.DKGRAY;
                    itr.set(notation.new NoteFont(currentNote));
                    itr.next();
                    currentNote = notation.new NoteFont((NoteFont) itr.get());
                    currentNote.color = Color.BLUE;
                    itr.set(notation.new NoteFont(currentNote));

                    melodyPosition++;
                } else if (((NoteFont) itr.get()).symbol != Syntax.EMPTY) {
                    currentNote = notation.new NoteFont((NoteFont) itr.get());
                    currentNote.color = Color.DKGRAY;
                    itr.set(notation.new NoteFont(currentNote));
                    currentNote = notation.new NoteFont();
                    currentNote.color = Color.BLUE;
                    currentNote.noteLength = 0;
                    itr.insertAfter(notation.new NoteFont(currentNote));
                    itr.next();

                    melodyPosition++;
                }
                Log.d("NoteList2", ((NoteFont) itr.get()).symbol.toString());

                displayText();
            }
        });

        octDownButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (octNum > 3) {
                    octNum--;

                    for (MusicNote mn : pianoKeys) {
                        mn.octaveDown();
                    }

                    displayOctaveText();
                }
            }
        });

        octUpButton.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               if (octNum < 5) {
                   octNum++;

                   for (MusicNote mn : pianoKeys) {
                       mn.octaveUp();
                   }

                   displayOctaveText();
               }
           }
        });

        /**
        * Iterate through all noteLengthButtons and implement their
        *  onClickListener to set the currentNote to the proper note of length
         */
        for (int i = 0; i < noteLengthButtons.length; i++) {
            int finalI = i;
            noteLengthButtons[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (finalI) {
                        case 0:
                            currentDuration.symbol = Syntax.REST_WHOLE;
                            currentDuration.noteLength = 32;
                            break;
                        case 1:
                            currentDuration.symbol = Syntax.REST_HALF;
                            currentDuration.noteLength = 16;
                            break;
                        case 2:
                            currentDuration.symbol = Syntax.REST_QUARTER;
                            currentDuration.noteLength = 8;
                            break;
                        case 3:
                            currentDuration.symbol = Syntax.REST_8TH;
                            currentDuration.noteLength = 4;
                            break;
                        case 4:
                            currentDuration.symbol = Syntax.REST_16TH;
                            currentDuration.noteLength = 2;
                            break;
                        case 5:
                            currentDuration.symbol = Syntax.REST_32ND;
                            currentDuration.noteLength = 1;
                            break;
                    }
                }
            });
        }

        // Initialize staff
        initStaffText();
        //initClefText();
        initNoteText();
        displayOctaveText();


        // Setup listeners for each piano key
        // Attaches each key to a specifc line on the staff
        for (int i = 0; i < pianoKeys.size(); i++) {
            int finalI = i;

            pianoKeys.get(finalI).getButton().setOnTouchListener(new OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        pianoKeys.get(finalI).play();

                        NoteFont replacedSymbol = notation.new NoteFont(currentNote);

                        // FIXME check duration - it assumes note tail
                        switch (currentDuration.symbol) {
                            case REST_WHOLE:
                                currentNote.symbol = Syntax.NOTE_WHOLE;
                                break;
                            case REST_HALF:
                                currentNote.symbol = Syntax.NOTE_HALF_UP;
                                break;
                            case REST_QUARTER:
                                currentNote.symbol = Syntax.NOTE_QUARTER_UP;
                                break;
                            case REST_8TH:
                                currentNote.symbol = Syntax.NOTE_8TH_UP;
                                break;
                            case REST_16TH:
                                currentNote.symbol = Syntax.NOTE_16TH_UP;
                                break;
                            case REST_32ND:
                                currentNote.symbol = Syntax.NOTE_32ND_UP;
                                break;
                        }

                        // Apply noteLength
                        currentNote.noteLength = currentDuration.noteLength;

                        // FIXME check "pitch"
                        switch (finalI) {
                            case 0: case 1:
                                currentNote.lineNum = 1;
                                break;
                            case 2: case 3:
                                currentNote.lineNum = 2;
                                break;
                            case 4:
                                currentNote.lineNum = 3;
                                break;
                            case 5: case 6:
                                currentNote.lineNum = 4;
                                break;
                            case 7: case 8:
                                currentNote.lineNum = 5;
                                break;
                            case 9: case 10:
                                currentNote.lineNum = 6;
                                break;
                            case 11:
                                currentNote.lineNum = 7;
                                break;
                        }

                        // Check for accidental
                        switch (finalI) {
                            case 0: case 2: case 4: case 5: case 7: case 9: case 11:
                                currentNote.prefix = Syntax.EMPTY;
                                currentNote.suffix = Syntax.EMPTY;
                                break;
                            case 1: case 3: case 6: case 8: case 10:
                                currentNote.prefix = Syntax.ACCIDENTAL_SHARP;
                                currentNote.suffix = Syntax.EMPTY;
                                break;
                        }

                        /*
                        Implement Note/Rest Replacement
                         */
                        replaceElement(currentNote, replacedSymbol);

                        /*
                        Log.d("BarLength, before", Integer.toString(barLength));
                        // note length constraint checking
                        int tempBarLength = barLength + currentDuration.noteLength - replacedSymbol.noteLength;
                        //Log.d("tempbarLength barLength", Integer.toString(barLength));
                        //Log.d("tempbarLength currentDuration.notelength", Integer.toString(currentDuration.noteLength));
                        //Log.d("tempbarLength replacedSymbol.noteLength", Integer.toString(replacedSymbol.noteLength));
                        if (tempBarLength <= maxBarLength) {
                            // set note to noteList
                            barLength = tempBarLength;
                            itr.set(notation.new NoteFont(currentNote));
                            // set note to constructed melody
                            mfAdapter = notation.new MusicFontAdapter(currentNote);
                            consMelody.addNote(mfAdapter.getMusicNote(), mfAdapter.getNoteDuration(), melodyPosition);
                        } else {
                            currentNote = replacedSymbol;
                        }
                        Log.d("BarLength, after", Integer.toString(barLength));
                         */

                        // Double check the note
                        //Log.d("NoteList", currentNote.symbol.toString() + " " + Integer.toString(currentNote.lineNum));

                        displayText();

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        pianoKeys.get(finalI).stop();
                    }
                    return true;
                }
            });
        }

        displayText();

    }

    /**
    Initializes the display text with the staff and adds a temporary
    element into noteList
     */
    private void initStaffText() {
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        ssb.append(Syntax.BARLINE_SINGLE.unicode);
        for (int i = 0; i < 16; i++) {
            ssb.append(Syntax.STAFF_5_LINES.unicode);
        }
        ssb.append(Syntax.BARLINE_SINGLE.unicode);

        melodyText.setText(ssb);
    }

    // Do not implement like this. The clef should not be in the note list
    private void initClefText() {
        // FIXME ? this might need to be implemented differently later
        SpannableString clef = new SpannableString(Syntax.G_CLEF.unicode);
        clef.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, clef.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        gclefText.setText(clef);
        // FIXME ? end of fixme

        itr.insertAfter(notation.new NoteFont(Syntax.SPACE_CLEF, 5));
        if (itr.hasNext()) itr.next();
        Log.d("NoteList 2", ((NoteFont) itr.get()).symbol.toString());
    }

    private void initNoteText() {
        // Default noteLength and noteLengthButton
        noteLengthButtons[0].toggle(); // set Whole Note not length button
        currentDuration = notation.new NoteFont(Syntax.REST_WHOLE, -1);
        currentNote = notation.new NoteFont(currentDuration.symbol, 9);
        currentNote.color = Color.BLUE;
        currentDuration.noteLength = 32;
        currentNote.noteLength = currentDuration.noteLength;
        barLength = 0;

        // Add default note to staff
        itr.insertAfter(notation.new NoteFont(currentNote));
        if (itr.hasNext()) itr.next(); // if there was a clef there (supposedly)
        currentNote = notation.new NoteFont((NoteFont) itr.get());

        // Add defaults note to constructed melody
        mfAdapter = notation.new MusicFontAdapter(currentNote);
        consMelody.addRest(mfAdapter.getNoteDuration(), melodyPosition);
        Log.d("NoteList2", ((NoteFont) itr.get()).symbol.toString() + " " + ((NoteFont) itr.get()).noteLength);
    }

    // FIXME theres a bug concerning consMelody and noteList compatibility
    private void replaceElement(NoteFont nf, NoteFont replaced) {
        int tempBarLength = barLength + nf.noteLength - replaced.noteLength;
        int elementLength = 0;

        Log.d("replace Element", "Attempt: " + replaced.toString() + " to " + nf.toString());

        if (tempBarLength > maxBarLength) {
            Log.d("replace Element", "failed to replace");
            nf = replaced;
        } else {
            boolean replaceable = true;
            if (nf.noteLength > replaced.noteLength) {
                Log.d("replace Element", "small to big");
                int numNexts = 0;
                NoteFont temp;
                elementLength = replaced.noteLength;
                while (elementLength < nf.noteLength && itr.hasNext()) {
                    itr.next();
                    temp = (NoteFont) itr.get();
                    numNexts++;
                    elementLength += temp.noteLength;
                }

                for (int i = 0; i < numNexts; i++) {
                    itr.previous();
                }

                if (elementLength == nf.noteLength) { // if replaceable
                    itr.set(notation.new NoteFont(nf));

                    for (; numNexts > 0; numNexts--) {
                        itr.next();
                        itr.removeThenBefore();
                    }
                } else {
                    replaceable = false;
                    Log.d("replace Element", "did not replace");
                }
                // eight, eight, half, replace first eight with half, not allowed
                // whole, replace with 32nd: 32 32 16 8 4 2
            } else if (nf.noteLength < replaced.noteLength) {
                Log.d("replace Element", "big to small");
                NoteFont temp;
                elementLength = nf.noteLength;
                int offset = melodyPosition;

                itr.set(notation.new NoteFont(nf));

                while (elementLength < replaced.noteLength) {
                    Log.d("replace Element", "el: " + elementLength + ". replace: " + replaced.noteLength);

                    if (elementLength + 32 <= replaced.noteLength) {
                        temp = notation.new NoteFont(Syntax.REST_WHOLE, Syntax.EMPTY, Syntax.EMPTY, 32, 9, Color.DKGRAY);
                    } else if (elementLength + 16 <= replaced.noteLength) {
                        temp = notation.new NoteFont(Syntax.REST_HALF, Syntax.EMPTY, Syntax.EMPTY, 16, 7, Color.DKGRAY);
                    } else if (elementLength + 8 <= replaced.noteLength) {
                        temp = notation.new NoteFont(Syntax.REST_QUARTER, Syntax.EMPTY, Syntax.EMPTY, 8, 7, Color.DKGRAY);
                    } else if (elementLength + 4 <= replaced.noteLength) {
                        temp = notation.new NoteFont(Syntax.REST_8TH, Syntax.EMPTY, Syntax.EMPTY, 4, 7, Color.DKGRAY);
                    } else if (elementLength + 2 <= replaced.noteLength) {
                        temp = notation.new NoteFont(Syntax.REST_16TH, Syntax.EMPTY, Syntax.EMPTY, 2, 7, Color.DKGRAY);
                    } else {
                        temp = notation.new NoteFont(Syntax.REST_32ND, Syntax.EMPTY, Syntax.EMPTY, 1, 7, Color.DKGRAY);
                    }
                    offset++;

                    // insert into noteList
                    itr.insertAfter(temp);
                    elementLength += temp.noteLength;

                    // insert element to constructed melody
                    mfAdapter = notation.new MusicFontAdapter(temp);
                    if (Notation.Syntax.REST.contains(temp.symbol)) {
                        consMelody.addRest(mfAdapter.getNoteDuration(), offset);
                    }

                }

            } else {
                Log.d("replace Element", "same size");
                itr.set(notation.new NoteFont(nf));
            }
            if (replaceable) {
                // set element to constructed melody
                mfAdapter = notation.new MusicFontAdapter(nf);
                if (Notation.Syntax.REST.contains(nf.symbol)) {
                    consMelody.addRest(mfAdapter.getNoteDuration(), melodyPosition);
                } else if (Notation.Syntax.NOTE.contains(nf.symbol)) {
                    consMelody.addNote(mfAdapter.getMusicNote(), mfAdapter.getNoteDuration(), melodyPosition);
                }
            }
        }
    }

    private void displayOctaveText() {
        Spannable octave = new SpannableString(key.getRootNote().toString() + Integer.toString(octNum));
        octaveText.setText(octave);
    }

    /**
    Displays text from the noteList in the order given.
     */
    private void displayText() {
        StringBuffer[] sb = new StringBuffer[staffLines.length];
        SpannableStringBuilder[] ssb = new SpannableStringBuilder[staffLines.length];
        // Initialize string buffers
        for (int i = 0; i < staffLines.length; i++) {
            ssb[i] = new SpannableStringBuilder();
        }

        int strIdx = 0;
        // Add notes to noteList
        BLL.ListIterator itr2 = noteList.listIterator();
        for (int k = 0; k < noteList.size(); k++) { // FIXME idk how
            NoteFont nf = notation.new NoteFont((NoteFont)itr2.get());
            Log.d("displayText", nf.symbol.toString());
            if (itr2.hasNext()) itr2.next();

            int strLen = 0; // FIXME inefficient use of code
            for (int i = 0; i < staffLines.length; i++) {
                strLen = 0;
                if (nf.lineNum == i) { // If this is the line where the note belongs
                    if (nf.prefix != Syntax.EMPTY) {
                        ssb[i].append(nf.prefix.unicode);
                        ssb[i].setSpan(new ForegroundColorSpan(nf.color), strIdx, strIdx + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        strLen += 1;
                    }
                    ssb[i].append(nf.symbol.unicode);
                    ssb[i].setSpan(new ForegroundColorSpan(nf.color), strIdx, strIdx + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    strLen += 1;
                } else {
                    if (nf.prefix != Syntax.EMPTY) {
                        ssb[i].append(Syntax.SPACE_NOTE.unicode);
                        ssb[i].setSpan(new ForegroundColorSpan(nf.color), strIdx, strIdx + 1/*Syntax.SPACE_NOTE.unicode.length()*/, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        strLen += 1; //Syntax.SPACE_NOTE.unicode.length();
                    }
                    if (Syntax.CLEF.contains(nf.symbol)) {
                        ssb[i].append(Syntax.SPACE_CLEF.unicode);
                        ssb[i].setSpan(new ForegroundColorSpan(nf.color), strIdx, strIdx + Syntax.SPACE_CLEF.unicode.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        strLen += Syntax.SPACE_CLEF.unicode.length();
                    } else {
                        ssb[i].append(Syntax.SPACE_NOTE.unicode);
                        ssb[i].setSpan(new ForegroundColorSpan(nf.color), strIdx, strIdx + 1/*Syntax.SPACE_NOTE.unicode.length()*/, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        strLen += 1; //Syntax.SPACE_NOTE.unicode.length();
                    }
                }
            }

            strIdx += strLen;
        }

        // reset test in staffLines text views
        for (int i = 0; i < staffLines.length; i++) {
            //ssb[i].setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, ssb[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            staffLines[i].setText(ssb[i]);
        }
    }

    /**
     * Temporary method to construct melody
     */
    private void toConstructedMelody() {
        ConstructedMelody constructedMelody = new ConstructedMelody(session);
        constructedMelody.startRecording();
        int position = 0;

        BLL.ListIterator itr2 = noteList.listIterator();
        for (int i = 0; i < noteList.size(); i++) {
            NoteFont nf = (NoteFont)itr2.get();
            if (itr2.hasNext()) itr2.next();

            Notation.MusicFontAdapter mfAdapter = notation.new MusicFontAdapter(nf);
            if (Notation.Syntax.REST.contains(nf.symbol)) {
                constructedMelody.addRest(mfAdapter.getNoteDuration(), position);
            } else if (Notation.Syntax.NOTE.contains(nf.symbol)) {
                constructedMelody.addNote(mfAdapter.getMusicNote(), mfAdapter.getNoteDuration(), position);
            }

            position++;
        }

        constructedMelody.play();
        constructedMelody.stopRecording();
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();
        midiDriver.setReverb(ReverbConstants.OFF);
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }

    private ArrayList<MusicNote> getPianoKeys()
    {
        ArrayList<MusicNote> keys = new ArrayList<>();

        keys.add(new MusicNote(60, (Button) findViewById(R.id.melodynoteCbutton)));
        keys.add(new MusicNote(61, (Button) findViewById(R.id.melodynoteCsharpbutton)));
        keys.add(new MusicNote(62, (Button) findViewById(R.id.melodynoteDbutton)));
        keys.add(new MusicNote(63, (Button) findViewById(R.id.melodynoteDsharpbutton)));
        keys.add(new MusicNote(64, (Button) findViewById(R.id.melodynoteEbutton)));
        keys.add(new MusicNote(65, (Button) findViewById(R.id.melodynoteFbutton)));
        keys.add(new MusicNote(66, (Button) findViewById(R.id.melodynoteFsharpbutton)));
        keys.add(new MusicNote(67, (Button) findViewById(R.id.melodynoteGbutton)));
        keys.add(new MusicNote(68, (Button) findViewById(R.id.melodynoteGsharpbutton)));
        keys.add(new MusicNote(69, (Button) findViewById(R.id.melodynoteAbutton)));
        keys.add(new MusicNote(70, (Button) findViewById(R.id.melodynoteAsharpbutton)));
        keys.add(new MusicNote(71, (Button) findViewById(R.id.melodynoteBbutton)));

        return keys;
    }

    /**
     * Logic for adding notes with note constraint
     *
     * Let staff be blank, by default, there should be a whole rest, and the note length is 0
     * 1. We add a whole note, the whole rest is replaced by the whole note, and the note length turns 32 - maxed
     *  1. Say we choose a different whole note, the note length remains the same,
     *  and the note changes to the new note.
     *  2. Say we choose a half note and insert it, the whole note switches over to that half note, the second half
     *  of the bar is a half rest, the note length is now 16.
     *  3. If we choose a quarter note, it is followed by quarter rest and a half rest, note length is 8.
     *  4. Eigth note, followed by a eigth rest, quarter rest, and half rest. Note length = 4.
     *  5. Sixteength note, followed by a sixteength rest, eight rest, quarter rest, and half rest. Note length = 2.
     *  6. So on so forth for 32nd.
     */

    // code for navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                redirectActivity(this, HomeScreenActivity.class);
                break;
            case R.id.nav_profile:
                redirectActivity(this, UserProfileActivity.class);
                break;
            case R.id.nav_music_theory:
                redirectActivity(this, MusicTheoryInfoActivity.class);
                break;
            case R.id.nav_overview:
                Toast.makeText(this, "Overview", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_melody:
                // Just close the drawer since we're already on this activity
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_chords:
                redirectActivity(this, InsertChordsActivity.class);
                break;
            case R.id.nav_percussion:
                redirectActivity(this, PercussionActivity.class);
                break;
            case R.id.nav_keyboard:
                redirectActivity(this, KeyboardActivity.class);
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    // method to activate intent
    private static void redirectActivity(Activity activity, Class aClass) {

        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

}