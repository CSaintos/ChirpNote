package com.example.chirpnote.activities;

import static com.example.chirpnote.Notation.Syntax;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

//import com.example.chirpnote.Notation.Syntax;
import com.example.chirpnote.ConstructedMelody;
import com.example.chirpnote.Key;
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.Notation;
import com.example.chirpnote.Notation.NoteFont;
import com.example.chirpnote.R;
import com.example.chirpnote.Session;

import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class MelodyActivity extends AppCompatActivity {

    private Notation notation = new Notation();

    private TextView[] staffLines;
    private RadioButton[] noteLengthButtons;
    private MusicNote[] pianoKeys;
    private Button backButton;
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

    private LinkedList<NoteFont> noteList;
    private ListIterator<NoteFont> itr;
    private NoteFont currentNote;
    private NoteFont currentDuration;
    private MidiDriver midiDriver;
    private Session session;
    private Key key;
    private int octNum;
    private boolean wasNext;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melody);

        noteList = new LinkedList<>();
        itr = noteList.listIterator();
        midiDriver = MidiDriver.getInstance();

        // TODO: get key from Session Activity
        key = new Key(Key.RootNote.C, Key.Type.MAJOR);
        octNum = 4;

        // TODO: get session from Session Activity
        session = new Session("Default", key, 140);

        // Initialize buttons
        backButton = (Button) findViewById(R.id.melodybackbutton);
        leftButton = (Button) findViewById(R.id.melodyleftbutton);
        rightButton = (Button) findViewById(R.id.melodyrightbutton);
        restButton = (Button) findViewById(R.id.melodyrestbutton);
        playButton = (Button) findViewById(R.id.melodyPlayButton);
        navLeftButton = (Button) findViewById(R.id.melodynavleft);
        navRightButton = (Button) findViewById(R.id.melodynavright);
        octUpButton = (Button) findViewById(R.id.melodyoctupbutton);
        octDownButton = (Button) findViewById(R.id.melodyoctdownbutton);

        // Initialize the keyboard buttons
        pianoKeys = new MusicNote[] {
                new MusicNote(60, (Button) findViewById(R.id.melodynoteCbutton)),
                new MusicNote(61, (Button) findViewById(R.id.melodynoteCsharpbutton)),
                new MusicNote(62, (Button) findViewById(R.id.melodynoteDbutton)),
                new MusicNote(63, (Button) findViewById(R.id.melodynoteDsharpbutton)),
                new MusicNote(64, (Button) findViewById(R.id.melodynoteEbutton)),
                new MusicNote(65, (Button) findViewById(R.id.melodynoteFbutton)),
                new MusicNote(66, (Button) findViewById(R.id.melodynoteFsharpbutton)),
                new MusicNote(67, (Button) findViewById(R.id.melodynoteGbutton)),
                new MusicNote(68, (Button) findViewById(R.id.melodynoteGsharpbutton)),
                new MusicNote(69, (Button) findViewById(R.id.melodynoteAbutton)),
                new MusicNote(70, (Button) findViewById(R.id.melodynoteAsharpbutton)),
                new MusicNote(71, (Button) findViewById(R.id.melodynoteBbutton))
        };

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

        /**
         * Go back to the previous activity
         */
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MelodyActivity.this, ChordsActivity.class));
                // Temp activity to go back to
                startActivity(new Intent(MelodyActivity.this, HomeScreenActivity.class));
            }
        });

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

                switch (currentDuration.symbol) {
                    case NOTE_WHOLE:
                        currentNote.symbol = Syntax.REST_WHOLE;
                        currentNote.lineNum = 9;
                        break;
                    case NOTE_HALF_UP: case NOTE_HALF_DOWN:
                        currentNote.symbol = Syntax.REST_HALF;
                        currentNote.lineNum = 7;
                        break;
                    case NOTE_QUARTER_UP: case NOTE_QUARTER_DOWN:
                        currentNote.symbol = Syntax.REST_QUARTER;
                        currentNote.lineNum = 7;
                        break;
                    case NOTE_8TH_UP: case NOTE_8TH_DOWN:
                        currentNote.symbol = Syntax.REST_8TH;
                        currentNote.lineNum = 7;
                        break;
                    case NOTE_16TH_UP: case NOTE_16TH_DOWN:
                        currentNote.symbol = Syntax.REST_16TH;
                        currentNote.lineNum = 7;
                        break;
                    case NOTE_32ND_UP: case NOTE_32ND_DOWN:
                        currentNote.symbol = Syntax.REST_32ND;
                        currentNote.lineNum = 7;
                        break;
                }

                currentNote.prefix = Syntax.EMPTY;
                currentNote.suffix = Syntax.EMPTY;

                itr.set(notation.new NoteFont(currentNote));

                //if (itr.hasPrevious()) currentNote = notation.new NoteFont(itr.previous());
                //wasNext = false; // must set after every call to itr position
                // Double check the rest
                Log.d("NoteList", currentNote.symbol.toString());

                displayText();
            }
        });

        /**
         *
         */
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toConstructedMelody();
            }
        });

        /**
         * Navigate to the left note
         */
        navLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itr.hasPrevious()) {
                    if (wasNext) currentNote = notation.new NoteFont(itr.previous());
                    currentNote = notation.new NoteFont(itr.previous()); // Returns previous node, not current

                    if (Syntax.CLEF.contains(currentNote.symbol)) {
                        currentNote = notation.new NoteFont(itr.next());
                        wasNext = true;
                    } else {
                        currentNote = notation.new NoteFont(itr.next()); // Due to calling previous in if statement. Returns current node
                        currentNote = notation.new NoteFont(itr.next()); // Returns next node
                        currentNote.color = Color.DKGRAY;
                        itr.set(notation.new NoteFont(currentNote));
                        currentNote = notation.new NoteFont(itr.previous()); // Returns current node...
                        currentNote = notation.new NoteFont(itr.previous());
                        wasNext = false;
                        currentNote.color = Color.BLUE;
                        itr.set(notation.new NoteFont(currentNote));
                        Log.d("NoteList pre", currentNote.symbol.toString());
                    }
                }
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
                    // returns the last edited node. the Next next will be null if this next is the last node
                    //if (!wasNext) currentNote = notation.new NoteFont(itr.next()); // previous was called b4
                    currentNote = notation.new NoteFont(itr.next());
                    // Set currentNote.color to DKGRAY
                    currentNote.color = Color.DKGRAY;
                    // set the last returned node to this node
                    itr.set(notation.new NoteFont(currentNote));
                    if (!itr.hasNext()) {
                        Log.d("Iterator", "the next node is null");
                        currentNote = notation.new NoteFont(currentNote.symbol, Syntax.EMPTY, Syntax.EMPTY, -1, Color.BLUE);
                        itr.add(notation.new NoteFont(currentNote));
                        currentNote = notation.new NoteFont(itr.previous());
                        wasNext = false;
                        if (itr.hasNext()) Log.d("Iterator", "the next node is empty");
                    } else {
                        currentNote = notation.new NoteFont(itr.next());
                        currentNote.color = Color.BLUE;
                        itr.set(notation.new NoteFont(currentNote));
                        currentNote = notation.new NoteFont(itr.previous());
                        wasNext = false;
                    }
                }
                displayText();
            }
        });

        octDownButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (octNum > 3) {
                    octNum--;
                }
                displayOctaveText();
            }
        });

        octUpButton.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View v) {
               if (octNum < 5) {
                   octNum++;
               }
               displayOctaveText();
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
                            currentDuration.symbol = Syntax.NOTE_WHOLE;
                            break;
                        case 1:
                            currentDuration.symbol = Syntax.NOTE_HALF_UP;
                            break;
                        case 2:
                            currentDuration.symbol = Syntax.NOTE_QUARTER_UP;
                            break;
                        case 3:
                            currentDuration.symbol = Syntax.NOTE_8TH_UP;
                            break;
                        case 4:
                            currentDuration.symbol = Syntax.NOTE_16TH_UP;
                            break;
                        case 5:
                            currentDuration.symbol = Syntax.NOTE_32ND_UP;
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
        for (int i = 0; i < pianoKeys.length; i++) {
            int finalI = i;

            pianoKeys[finalI].getButton().setOnTouchListener(new OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        pianoKeys[finalI].play();

                        // FIXME this is still ugly

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

                        currentNote.symbol = currentDuration.symbol;
                        // FIXME end of fixme

                        // Was .add
                        itr.set(notation.new NoteFont(currentNote)); // set the last returned note to the currentNote

                        //if (itr.hasPrevious()) currentNote = notation.new NoteFont(itr.previous());
                        // Double check the note
                        Log.d("NoteList", currentNote.symbol.toString() + " " + Integer.toString(currentNote.lineNum));

                        displayText();

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        pianoKeys[finalI].stop();
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

    private void initClefText() {
        // FIXME ? this might need to be implemented differently later
        SpannableString clef = new SpannableString(Syntax.G_CLEF.unicode);
        clef.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, clef.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        gclefText.setText(clef);

        itr.add(notation.new NoteFont(Syntax.SPACE_CLEF, 5));
        if (itr.hasPrevious()) {
            Log.d("NoteList", itr.previous().symbol.toString());
            itr.next();
            wasNext = true;
        }
        // FIXME ? end of fixme
    }

    private void initNoteText() {
        // Default noteLength and noteLengthButton
        noteLengthButtons[0].toggle(); // set Whole Note not length button
        currentDuration = notation.new NoteFont(Syntax.NOTE_WHOLE, -1);
        currentNote = notation.new NoteFont(currentDuration.symbol, 5);
        currentNote.color = Color.BLUE;
        // Add default note to staff
        itr.add(notation.new NoteFont(currentNote));
        // Check that the note added is the currentNote
        if (itr.hasPrevious()) currentNote = notation.new NoteFont(itr.previous());
        //if (itr.hasPrevious()) currentNote = notation.new NoteFont(itr.previous());
        wasNext = false;
        Log.d("NoteList", currentNote.symbol.toString() + " " + Integer.toString(currentNote.lineNum));
        Log.d("BLUE", Integer.toString(Color.BLUE));
        Log.d("DKGRAY", Integer.toString(Color.DKGRAY));
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
        for (ListIterator<NoteFont> itr2 = noteList.listIterator(); itr2.hasNext();) {
            NoteFont nf = itr2.next();
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

    private void toConstructedMelody() {
        ConstructedMelody constructedMelody = new ConstructedMelody(session);
        int position = 0;

        for (ListIterator<NoteFont> itr2 = noteList.listIterator(); itr2.hasNext();) {
            NoteFont nf = itr2.next();

            Notation.MusicFontAdapter mfAdapter = notation.new MusicFontAdapter(nf);
            if (Notation.Syntax.REST.contains(nf.symbol)) {
                constructedMelody.addRest(mfAdapter.getNoteDuration(), position);
            } else if (Notation.Syntax.NOTE.contains(nf.symbol)) {
                constructedMelody.addNote(mfAdapter.getMusicNote(), mfAdapter.getNoteDuration(), position);
            }

            position++;
        }
        constructedMelody.startRecording();
        constructedMelody.stopRecording();

        constructedMelody.play();
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
}