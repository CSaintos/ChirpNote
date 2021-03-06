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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chirpnote.BLL;
import com.example.chirpnote.ChirpNoteSession;
import com.example.chirpnote.ChirpNoteUser;
import com.example.chirpnote.ConstructedMelody;
import com.example.chirpnote.Key;
import com.example.chirpnote.Mixer;
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.Notation;
import com.example.chirpnote.Notation.NoteFont;
import com.example.chirpnote.R;
import com.example.chirpnote.Session;
import com.google.android.material.navigation.NavigationView;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;

import io.realm.Realm;

public class MelodyActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static Realm realm;
    private static ChirpNoteUser user;

    private Notation notation = new Notation();

    private TextView[] staffLines;
    private RadioButton[] noteLengthButtons;
    private Button leftButton;
    private Button rightButton;
    private Button restButton;
    //private Button playButton;
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
    private static ChirpNoteSession session;
    private Key key;
    private static Mixer mixer;
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
        hideSystemBars();

        user = (ChirpNoteUser) getIntent().getSerializableExtra("user");
        realm = Realm.getDefaultInstance();

        // navigation menu
        Toolbar toolbar = findViewById(R.id.nav_toolbar);
        toolbar.setTitle("");
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

        // play and stop button
        ImageView navPlayButton = findViewById(R.id.nav_play_button);
        navPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MelodyActivity.this, "Play", Toast.LENGTH_SHORT).show();
                if (mixer.areTracksPlaying()) {
                    mixer.stopTracks();
                }
                mixer.playTracks();
            }
        });
        ImageView navStopButton = findViewById(R.id.nav_stop_button);
        navStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(InsertChordsActivity.this, "Stop", Toast.LENGTH_SHORT).show();
                if (mixer.areTracksPlaying()) {
                    mixer.stopTracks();
                }
            }
        }); //end play and stop

        // Init noteList
        noteList = new BLL<>();
        itr = noteList.listIterator();

        // Init note constraint vars
        barLength = 0;
        maxBarLength = 32;

        // Init session
        session = (ChirpNoteSession) getIntent().getSerializableExtra("session");
        octNum = 4;
        String basePath = this.getFilesDir().getPath();
        if (session == null) {
            session = new ChirpNoteSession("Name", new Key(Key.RootNote.C, Key.Type.MAJOR), 120,
                    basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3");
        }
        key = session.getKey();
        mixer = new Mixer(session);

        Log.d("SessionRetrieval", "elements size " + session.mMelodyElements.size());

        // Init Constructed Melody
        consMelody = mixer.constructedMelody;

        midiDriver = MidiDriver.getInstance();

        melodyPosition = 0;
        consMelody.mElementIndex = melodyPosition;

        // Initialize buttons
        leftButton = (Button) findViewById(R.id.melodyleftbutton);
        rightButton = (Button) findViewById(R.id.melodyrightbutton);
        restButton = (Button) findViewById(R.id.melodyrestbutton);
        //playButton = (Button) findViewById(R.id.melodyPlayButton);
        navLeftButton = (Button) findViewById(R.id.melodynavleft);
        navRightButton = (Button) findViewById(R.id.melodynavright);
        octUpButton = (Button) findViewById(R.id.melodyoctupbutton);
        octDownButton = (Button) findViewById(R.id.melodyoctdownbutton);

        // Initialize piano keys
        pianoKeys = getPianoKeys();

        // Initialize note suggestions
        keyButtons = new ArrayList<>();
        for (int i = 0; i < key.getScaleNotes().length - 1; i++) {
            int rootIdx = (/*currentKey*/key.getScaleNotes()[i] - 60) % 12;
            /** arraylist of all chords that belong to the current key based on the type of chord
             * it takes in the root note of the chord and type of chord
             */
            keyButtons.add(pianoKeys.get(rootIdx));
        }

//        Button noteSuggestButton = findViewById(R.id.noteSuggestion);
//        noteSuggestButton.setClickable(true);
//
//        noteSuggestButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (noteSuggestButton.isSelected()) {
//                    noteSuggestButton.setSelected(false);
//                    for (int i = 0; i < keyButtons.size(); i++) {
//                        keyButtons.get(i).getButton().setSelected(false);
//                    }
//                } else {
//                    noteSuggestButton.setSelected(true);
//                    for (int i = 0; i < keyButtons.size(); i++) {
//                        keyButtons.get(i).getButton().setSelected(true);
//                    }
//                }
//            }
//        });
        if (session.getNoteSuggestionFlag() == true)
        {
            for (int i = 0; i < keyButtons.size(); i++)
            {
                keyButtons.get(i).getButton().setSelected(true);
            }
        }
        else
        {
            for (int i = 0; i < keyButtons.size(); i++)
            {
                keyButtons.get(i).getButton().setSelected(false);
            }
        }

        // Initialize text views
        melodyText = (TextView) findViewById(R.id.stafftextview);
        gclefText = (TextView) findViewById(R.id.gcleftextview);
        octaveText = (TextView) findViewById(R.id.melodyoctindicator);

        // Initialize all staff line text views
        staffLines = new TextView[]{
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
        noteLengthButtons = new RadioButton[]{
                (RadioButton) findViewById(R.id.melodywholeradiobutton),
                (RadioButton) findViewById(R.id.melodyhalfradiobutton),
                (RadioButton) findViewById(R.id.melody4thradiobutton),
                (RadioButton) findViewById(R.id.melody8thradiobutton),
                (RadioButton) findViewById(R.id.melody16thradiobutton),
                (RadioButton) findViewById(R.id.melody32ndradiobutton)
        };

        /**
         * Navigate to the previous measure
         */
        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                consMelody.previousMeasure();
                melodyPosition = consMelody.getElementIndex();
                constructNoteList(consMelody.getMeasure());
                displayText();
                // For testing purposes only
                for (String str : consMelody.getMeasure()) {
                    Notation.MelodyElement me = consMelody.decodeElement(str);
                    Notation.MusicFontAdapter mf = notation.new MusicFontAdapter(me.musicNote, me.noteDuration);
                    Notation.NoteFont nf = mf.getNoteFont();
                    Log.d("getConstructedMeasure", nf.toString());
                }
                // end of testing
            }
        });

        /**
         * Navigate to the next measure
         */
        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                consMelody.nextMeasure();
                melodyPosition = consMelody.getElementIndex();
                constructNoteList(consMelody.getMeasure());
                displayText();
            }
        });

        /**
         * Same as setting the keyboard listeners,
         *  just with a rest instead
         */
        restButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentNote == null) currentNote = notation.new NoteFont();
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

                if (session.mMelodyElements.size() != 0)
                    replaceElement(currentNote, replacedSymbol);

                displayText();
            }
        });

        /**
         * Old play button
         * TODO remove
         */
        /*
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mixer.areTracksPlaying()) mixer.stopTracks();
                mixer.playTracks();
            }
        });
         */

        /**
         * Navigate to the left note on the staff
         */
        navLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (session.mMelodyElements.size() != 0) {
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


                    Log.d("NavLeft noteFont", ((NoteFont) itr.get()).symbol.toString());
                    if (currentNote.symbol != Syntax.EMPTY) Log.d("ConsMelody", "position " +
                            melodyPosition + ", element " + consMelody.getElement(melodyPosition).toString());

                    displayText();
                }
            }
        });

        /**
         * Navigate to the right note on the staff
         * Fixme after navigating right, you shouldn't be able to add notes after completing the note constraint, but it happens
         */
        navRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session.mMelodyElements.size() != 0) {

                    if (currentNote.symbol != Syntax.EMPTY) Log.d("Nav ConsMelody", "position " +
                            melodyPosition + ", element " + consMelody.getElement(melodyPosition).toString());


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


                    Log.d("NavRight noteFont", ((NoteFont) itr.get()).symbol.toString());
                    if (currentNote.symbol != Syntax.EMPTY) Log.d("ConsMelody", "position " +
                            melodyPosition + ", element " + consMelody.getElement(melodyPosition).toString());

                    displayText();
                }
            }
        });

        /**
         * OctaveDownButton Listener
         * sets the octave down for the view and logically
         */
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

        /**
         * OctaveUpButton listener
         * sets the octave up for the view and logically
         */
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

            pianoKeys.get(finalI).getButton().setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        pianoKeys.get(finalI).play();

                        if (currentNote == null) currentNote = notation.new NoteFont(); // if empty staff
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
                            case 0:
                            case 1:
                                currentNote.lineNum = 1;
                                break;
                            case 2:
                            case 3:
                                currentNote.lineNum = 2;
                                break;
                            case 4:
                                currentNote.lineNum = 3;
                                break;
                            case 5:
                            case 6:
                                currentNote.lineNum = 4;
                                break;
                            case 7:
                            case 8:
                                currentNote.lineNum = 5;
                                break;
                            case 9:
                            case 10:
                                currentNote.lineNum = 6;
                                break;
                            case 11:
                                currentNote.lineNum = 7;
                                break;
                        }

                        // Check for accidental
                        switch (finalI) {
                            case 0:
                            case 2:
                            case 4:
                            case 5:
                            case 7:
                            case 9:
                            case 11:
                                currentNote.prefix = Syntax.EMPTY;
                                currentNote.suffix = Syntax.EMPTY;
                                break;
                            case 1:
                            case 3:
                            case 6:
                            case 8:
                            case 10:
                                currentNote.prefix = Syntax.ACCIDENTAL_SHARP;
                                currentNote.suffix = Syntax.EMPTY;
                                break;
                        }

                        // replace note on staff with new note
                        if (session.mMelodyElements.size() != 0)
                            replaceElement(currentNote, replacedSymbol);

                        // Double check the note
                        //Log.d("NoteList", currentNote.symbol.toString() + " " + Integer.toString(currentNote.lineNum));

                        // update the staff text view
                        displayText();

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        // stop playing the note
                        pianoKeys.get(finalI).stop();
                    }
                    return true;
                }
            });
        }

        // lastly after all is initialized, display the staff text/notes
        displayText();
    }

    /**
     * Initializes the display text with the staff and adds a temporary
     * element into noteList
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

    // TODO remove
    private void tempInitSessionMeasures() {
        currentNote = notation.new NoteFont(currentDuration.symbol, Syntax.EMPTY, Syntax.EMPTY, 32, 9, Color.DKGRAY);

        mfAdapter = notation.new MusicFontAdapter(currentNote);
        int offset = melodyPosition;

        for (; offset < melodyPosition + 4; offset++) {
            consMelody.addRest(mfAdapter.getNoteDuration(), offset);
        }

        Log.d("consMelody size", Integer.toString(session.mMelodyElements.size()));
    }

    /**
     * Initialize noteList from session melody elements
     */
    private void initNoteText() {
        // Default noteLength and noteLengthButton
        noteLengthButtons[0].toggle(); // set Whole Note not length button
        currentDuration = notation.new NoteFont(Syntax.REST_WHOLE,Syntax.EMPTY, Syntax.EMPTY, 32, -1, Color.DKGRAY);

        barLength = 0;

        //tempInitSessionMeasures();
        constructNoteList(consMelody.getMeasure());

    }

    /**
     * replaceElement
     *
     * replaces the current noteFont with the replaced one.
     * - respects note constraint
     * - adds rests where needed
     * - replaces multiple notes where needed
     * - needs noteList iterator to be at currentNote location
     *
     * FIXME consMelody and noteList compatibility (idk if i fixed this already, should have been more descriptive)
     * @param nf noteFont to place
     * @param replaced noteFont to replaced (used for reference of information)
     */
    private void replaceElement(NoteFont nf, NoteFont replaced) {
        /*
        if (nf.symbol == Syntax.EMPTY) {
            Log.d("replace Element", "Empty Staff Case");
            return; // empty staff case
        }
         */
        int tempBarLength = barLength + nf.noteLength - replaced.noteLength;
        int elementLength = 0;
        int offset = melodyPosition;

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
                }
            } else {
                Log.d("replace Element", "same size");
                itr.set(notation.new NoteFont(nf));
            }
            if (replaceable) { // when adding notes, must be done chronologically
                // set element to constructed melody
                mfAdapter = notation.new MusicFontAdapter(nf);
                if (Notation.Syntax.REST.contains(nf.symbol)) {
                    consMelody.addRest(mfAdapter.getNoteDuration(), melodyPosition);
                } else if (Notation.Syntax.NOTE.contains(nf.symbol)) {
                    consMelody.addNote(mfAdapter.getMusicNote(), mfAdapter.getNoteDuration(), melodyPosition);
                }
                //Log.d("ConsMelody", consMelody.getElement(melodyPosition).toString());
                Log.d("ConsMelody", "position " + melodyPosition + ", element " +
                        consMelody.getElement(melodyPosition).toString());

                // insert rests to constructed melody
                int offset2 = melodyPosition;
                while (offset > offset2) {
                    itr.next();
                    offset2++;
                    mfAdapter = notation.new MusicFontAdapter((NoteFont) itr.get());
                    //Log.d("replace element adapter duration", mfAdapter.getNoteDuration().toString());
                    Log.d("ConsMelody b4", "position " + offset2 + ", element " + consMelody.getElement(offset2).toString());
                    consMelody.addRest(mfAdapter.getNoteDuration(), offset2);
                    Log.d("ConsMelody", "position " + offset2 + ", element " + consMelody.getElement(offset2).toString());
                }
                // reset iterator
                for (; offset2 > melodyPosition; offset2--) {
                    itr.previous();
                }
            }
        }
    }

    /**
     * Displays the octave onto the octave indicator
     */
    private void displayOctaveText() {
        Spannable octave = new SpannableString(key.getRootNote().toString() + Integer.toString(octNum));
        octaveText.setText(octave);
    }

    /**
     * Displays text from the noteList in the order given.
     * In other words, displays note fonts onto the staff.
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
            NoteFont nf = notation.new NoteFont((NoteFont) itr2.get());
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
     * ConstructNoteList
     *
     * Using the encoded string of melody elements,
     * build the noteList to place onto the staff.
     *
     * @param measure encoded string of melody elements
     */
    private void constructNoteList(String[] measure) {
        Log.d("ConstructNoteList", "measure length: " + measure.length);
        Log.d("ConstructNoteList", "elements length: " + session.mMelodyElements.size()); // FIXME not retrieving
        if (measure.length == 0) return;
        noteList = new BLL<>();
        itr = noteList.listIterator();

        for (String encodedElement : measure) {
            Notation.MelodyElement me = consMelody.decodeElement(encodedElement);
            Notation.MusicFontAdapter mf = notation.new MusicFontAdapter(me.musicNote, me.noteDuration);
            Notation.NoteFont nf = notation.new NoteFont(mf.getNoteFont());
            Log.d("constructNoteList", nf.toString());
            itr.insertAfter(nf);
            if (itr.hasNext()) itr.next();
        }

        itr.goToBegin();
        currentNote = notation.new NoteFont((NoteFont) itr.get());
        currentNote.color = Color.BLUE;
        itr.set(notation.new NoteFont(currentNote));
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();
        mixer.syncWithSession();
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }

    private ArrayList<MusicNote> getPianoKeys() {
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
     * <p>
     * Let staff be blank, by default, there should be a whole rest, and the note length is 0
     * 1. We add a whole note, the whole rest is replaced by the whole note, and the note length turns 32 - maxed
     * 1. Say we choose a different whole note, the note length remains the same,
     * and the note changes to the new note.
     * 2. Say we choose a half note and insert it, the whole note switches over to that half note, the second half
     * of the bar is a half rest, the note length is now 16.
     * 3. If we choose a quarter note, it is followed by quarter rest and a half rest, note length is 8.
     * 4. Eigth note, followed by a eigth rest, quarter rest, and half rest. Note length = 4.
     * 5. Sixteength note, followed by a sixteength rest, eight rest, quarter rest, and half rest. Note length = 2.
     * 6. So on so forth for 32nd.
     */

    // code for navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                redirectActivity(this, HomeScreenActivity.class);
                break;
            case R.id.nav_overview:
                redirectActivity(this, SessionOverviewActivity.class);
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
                if (session.getSmartKeyboardFlag() == false) {
                    redirectActivity(this, KeyboardActivity.class);
                }
                else {
                    redirectActivity(this, SmartKeyboardActivity.class);
                }
                break;
            case R.id.nav_mixer:
                redirectActivity(this, MixerActivity.class);
                break;
            case R.id.nav_audio:
                redirectActivity(this, RecordAudioActivity.class);
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
        } else {
            super.onBackPressed();
        }
    }

    // method to activate intent
    private static void redirectActivity(Activity activity, Class aClass) {
        if(mixer.areTracksPlaying()){
            mixer.stopTracks();
        }
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("flag", "fromMelodyActivity");
        intent.putExtra("session", session);
        intent.putExtra("user", user);
        if (user != null) saveToDB();
        activity.startActivity(intent);
    }

    private static void saveToDB() {
        realm.executeTransactionAsync(r -> {
            Session realmSession = r.where(Session.class).equalTo("_id", session.getId()).findFirst();
            realmSession.setNextMelodyTick(session.mNextMelodyTick);
            Log.d("saveToDB melody", Integer.toString(session.mMelodyElements.size()));
            //Log.d("saveToDB realm melody", Integer.toString(realmSession.getMelodyElements().size()));
            realmSession.setMelodyElements(realmSession.listToRealmList(session.mMelodyElements));
            realmSession.setMidiFile(realmSession.encodeFile(session.getMidiPath()));
            Log.d("saveToDB realm melody", Integer.toString(realmSession.getMelodyElements().size()));
        });
    }

    // pop up menu with session options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_session_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.subitem1:
                redirectActivity(this, SessionOptionsActivity.class);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Hides the system status bar and navigation bar
     */
    private void hideSystemBars(){
        WindowInsetsControllerCompat windowInsetsController = new WindowInsetsControllerCompat(this.getWindow(), this.getCurrentFocus());
        if (windowInsetsController != null) {
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        }
    }


    /**
     * Temporary method to construct melody
     * DO NOT REMOVE
     */
    /*
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
     */
}