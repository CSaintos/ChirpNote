package com.example.chirpnote.activities;

import static com.example.chirpnote.Notation.Syntax;

import androidx.appcompat.app.AppCompatActivity;

//import android.annotation.SuppressLint;
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
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.Notation;
import com.example.chirpnote.Notation.NoteFont;
import com.example.chirpnote.R;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class MelodyActivity extends AppCompatActivity {

    private Notation notation = new Notation();

    //private Button[] keyButtons;
    private TextView[] staffLines;
    private RadioButton[] noteLengthButtons;
    private MusicNote[] pianoKeys;
    private Button backButton;
    private Button leftButton;
    private Button rightButton;
    private Button restButton;
    private Button navLeftButton;
    private Button navRightButton;
    private TextView melodyText;
    private TextView gclefText;

    private LinkedList<NoteFont> noteList;
    private ListIterator<NoteFont> itr;
    private NoteFont currentNote;
    private MidiDriver midiDriver;

    //@SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melody);

        noteList = new LinkedList<>();
        itr = noteList.listIterator();
        midiDriver = MidiDriver.getInstance();

        // Initialize buttons
        backButton = (Button) findViewById(R.id.melodybackbutton);
        leftButton = (Button) findViewById(R.id.melodyleftbutton);
        rightButton = (Button) findViewById(R.id.melodyrightbutton);
        restButton = (Button) findViewById(R.id.melodyrestbutton);
        navLeftButton = (Button) findViewById(R.id.melodynavleft);
        navRightButton = (Button) findViewById(R.id.melodynavright);

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
                new MusicNote(71, (Button) findViewById(R.id.melodynoteBbutton)),
                new MusicNote(72, (Button) findViewById(R.id.melodynoteCbutton2))
        };

        /*keyNotes = new ArrayList<>();
        for (int i = 60; i < 73; i++) {
            keyNotes.add(new MusicNote(i));
        }*/

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

        // Initialize staff text and clef text views
        melodyText = (TextView) findViewById(R.id.stafftextview);
        gclefText = (TextView) findViewById(R.id.gcleftextview);

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
                if (itr.hasNext()) {
                    itr.next();
                    itr.previous();
                    itr.remove();
                    //itr.previous();
                }

                switch (currentNote.symbol) {
                    case NOTE_WHOLE:
                        itr.add(notation.new NoteFont(Syntax.REST_WHOLE, 9));
                        break;
                    case NOTE_HALF_UP:
                    case NOTE_HALF_DOWN:
                        itr.add(notation.new NoteFont(Syntax.REST_HALF, 7));
                        break;
                    case NOTE_QUARTER_UP:
                    case NOTE_QUARTER_DOWN:
                        itr.add(notation.new NoteFont(Syntax.REST_QUARTER, 7));
                        break;
                    case NOTE_8TH_UP:
                    case NOTE_8TH_DOWN:
                        itr.add(notation.new NoteFont(Syntax.REST_8TH, 7));
                        break;
                    case NOTE_16TH_UP:
                    case NOTE_16TH_DOWN:
                        itr.add(notation.new NoteFont(Syntax.REST_16TH, 7));
                        break;
                    case NOTE_32ND_UP:
                    case NOTE_32ND_DOWN:
                        itr.add(notation.new NoteFont(Syntax.REST_32ND, 7));
                        break;
                }
                if (itr.hasPrevious()) itr.previous();

                displayText();
            }
        });

        /**
         * Navigate to the left note
         */
        navLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itr.hasPrevious()) {
                    if (Syntax.CLEF.contains(itr.previous().symbol)) {
                        itr.next();
                    } else {
                        //currentNote = itr.next();
                        currentNote.color = Color.DKGRAY;
                        currentNote = itr.previous();
                        currentNote.color = Color.BLUE;
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
                    currentNote.color = Color.DKGRAY;
                    currentNote = itr.next();
                    currentNote.color = Color.BLUE;
                    Log.d("NoteList", currentNote.symbol.toString());
                }
                displayText();
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
                            currentNote = notation.new NoteFont(Syntax.NOTE_WHOLE, -1);
                            break;
                        case 1:
                            currentNote = notation.new NoteFont(Syntax.NOTE_HALF_UP, -1);
                            break;
                        case 2:
                            currentNote = notation.new NoteFont(Syntax.NOTE_QUARTER_UP, -1);
                            break;
                        case 3:
                            currentNote = notation.new NoteFont(Syntax.NOTE_8TH_UP, -1);
                            break;
                        case 4:
                            currentNote = notation.new NoteFont(Syntax.NOTE_16TH_UP, -1);
                            break;
                        case 5:
                            currentNote = notation.new NoteFont(Syntax.NOTE_32ND_UP, -1);
                            break;
                    }
                }
            });
        }

        // Initialize staff
        initText();

        // Setup listeners for each piano key
        // Attaches each key to a specifc line on the staff
        /*
        for (int i = 0; i < pianoKeys.length; i++) {
            int finalI = i;
            /*
            pianoKeys[i].getButton().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // FIXME this is ugly
                    if (itr.hasNext()) {
                        itr.next();
                        itr.previous();
                        itr.remove();
                        //itr.previous();
                    }
                    switch (finalI) {
                        case 0:
                            itr.add(notation.new NoteFont(currentNote.symbol, 1));
                            break;
                        case 1:
                            itr.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 1));
                            break;
                        case 2:
                            itr.add(notation.new NoteFont(currentNote.symbol, 2));
                            break;
                        case 3:
                            itr.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 2));
                            break;
                        case 4:
                            itr.add(notation.new NoteFont(currentNote.symbol, 3));
                            break;
                        case 5:
                            itr.add(notation.new NoteFont(currentNote.symbol, 4));
                            break;
                        case 6:
                            itr.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 4));
                            break;
                        case 7:
                            itr.add(notation.new NoteFont(currentNote.symbol, 5));
                            break;
                        case 8:
                            itr.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 5));
                            break;
                        case 9:
                            itr.add(notation.new NoteFont(currentNote.symbol, 6));
                            break;
                        case 10:
                            itr.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 6));
                            break;
                        case 11:
                            itr.add(notation.new NoteFont(currentNote.symbol, 7));
                            break;
                        case 12:
                            itr.add(notation.new NoteFont(currentNote.symbol, 8));
                    }
                    if (itr.hasPrevious()) itr.previous();

                    displayText();
                }
            });*/
            /*
            pianoKeys[finalI].getButton().setOnTouchListener(new OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        pianoKeys[finalI].play(midiDriver);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        pianoKeys[finalI].stop(midiDriver);
                    }
                    return true;
                }
            });
        }*/

        for(MusicNote note : pianoKeys){
            note.getButton().setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        note.play(midiDriver);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        note.stop(midiDriver);
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
    void initText() {
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        ssb.append(Syntax.BARLINE_SINGLE.unicode);
        for (int i = 0; i < 16; i++) {
            ssb.append(Syntax.STAFF_5_LINES.unicode);
        }
        ssb.append(Syntax.BARLINE_SINGLE.unicode);

        melodyText.setText(ssb);
        // FIXME ? this might need to be implemented differently later
        SpannableString clef = new SpannableString(Syntax.G_CLEF.unicode);
        clef.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, clef.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        gclefText.setText(clef);

        itr.add(notation.new NoteFont(Syntax.SPACE_CLEF, 5));
        if (itr.hasPrevious()) {
            Log.d("NoteList", itr.previous().symbol.toString());
            itr.next();
        }
        // FIXME ? end of fixme

        // Default noteLength and noteLengthButton
        noteLengthButtons[0].toggle();
        currentNote = notation.new NoteFont(Syntax.NOTE_WHOLE, -1);
        // Add default note to staff
        itr.add(notation.new NoteFont(currentNote.symbol, 5));
        if (itr.hasPrevious()) Log.d("NoteList", itr.previous().symbol.toString());
    }

    /**
    Displays text from the noteList in the order given.
     */
    void displayText() {
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
}