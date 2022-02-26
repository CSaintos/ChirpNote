package com.example.chirpnote.activities;

//import static com.example.chirpnote.Notation.unicode;
import static com.example.chirpnote.Notation.Syntax;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

//import com.example.chirpnote.Notation.Syntax;
import com.example.chirpnote.Notation;
import com.example.chirpnote.Notation.NoteFont;
import com.example.chirpnote.R;

import java.util.LinkedList;
import java.util.ListIterator;

public class MelodyActivity extends AppCompatActivity {

    private Notation notation = new Notation();

    private Button[] keyButtons;
    private TextView[] staffLines;
    private RadioButton[] noteLengthButtons;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melody);

        noteList = new LinkedList<>();
        itr = noteList.listIterator();

        // Initialize buttons
        backButton = (Button) findViewById(R.id.melodybackbutton);
        leftButton = (Button) findViewById(R.id.melodyleftbutton);
        rightButton = (Button) findViewById(R.id.melodyrightbutton);
        restButton = (Button) findViewById(R.id.melodyrestbutton);
        navLeftButton = (Button) findViewById(R.id.melodynavleft);
        navRightButton = (Button) findViewById(R.id.melodynavright);

        // Initialize the keyboard buttons
        keyButtons = new Button[] {
                (Button) findViewById(R.id.melodynoteCbutton),
                (Button) findViewById(R.id.melodynoteCsharpbutton),
                (Button) findViewById(R.id.melodynoteDbutton),
                (Button) findViewById(R.id.melodynoteDsharpbutton),
                (Button) findViewById(R.id.melodynoteEbutton),
                (Button) findViewById(R.id.melodynoteFbutton),
                (Button) findViewById(R.id.melodynoteFsharpbutton),
                (Button) findViewById(R.id.melodynoteGbutton),
                (Button) findViewById(R.id.melodynoteGsharpbutton),
                (Button) findViewById(R.id.melodynoteAbutton),
                (Button) findViewById(R.id.melodynoteAsharpbutton),
                (Button) findViewById(R.id.melodynoteBbutton),
                (Button) findViewById(R.id.melodynoteCbutton2)
        };

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

        leftButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        rightButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        /**
         * Same as setting the keyboard listeners,
         *  just with a rest instead
         */
        restButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                itr.remove();
                //noteList.removeLast();
                switch (currentNote.symbol) {
                    case NOTE_WHOLE:
                        itr.add(notation.new NoteFont(Syntax.REST_WHOLE, 9));
                        //noteList.add(notation.new NoteFont(Syntax.REST_WHOLE, 9));
                        break;
                    case NOTE_HALF_UP:
                    case NOTE_HALF_DOWN:
                        itr.add(notation.new NoteFont(Syntax.REST_HALF, 7));
                        //noteList.add(notation.new NoteFont(Syntax.REST_HALF, 7));
                        break;
                    case NOTE_QUARTER_UP:
                    case NOTE_QUARTER_DOWN:
                        itr.add(notation.new NoteFont(Syntax.REST_QUARTER, 7));
                        //noteList.add(notation.new NoteFont(Syntax.REST_QUARTER, 7));
                        break;
                    case NOTE_8TH_UP:
                    case NOTE_8TH_DOWN:
                        itr.add(notation.new NoteFont(Syntax.REST_8TH, 7));
                        //noteList.add(notation.new NoteFont(Syntax.REST_8TH, 7));
                        break;
                    case NOTE_16TH_UP:
                    case NOTE_16TH_DOWN:
                        itr.add(notation.new NoteFont(Syntax.REST_16TH, 7));
                        //noteList.add(notation.new NoteFont(Syntax.REST_16TH, 7));
                        break;
                    case NOTE_32ND_UP:
                    case NOTE_32ND_DOWN:
                        itr.add(notation.new NoteFont(Syntax.REST_32ND, 7));
                        //noteList.add(notation.new NoteFont(Syntax.REST_32ND, 7));
                        break;
                }
                if (!itr.hasNext()) itr.previous();

                displayText();
            }
        });

        /**
         * Navigate to the left note
         */
        navLeftButton.setOnClickListener(new OnClickListener() {
            //currentNote = itr.previous();
            @Override
            public void onClick(View v) {
                if (itr.hasPrevious()) {
                    if (Syntax.CLEF.contains(itr.previous().symbol)) {
                        itr.next();
                    } else {
                        itr.next();
                        Log.d("NoteList pre", itr.previous().symbol.toString());
                    }
                }
                //currentNote = itr.previous();
                //Log.d("Linked list position", currentNote.symbol.toString());
            }
        });

        /**
         * Navigate to the right note
         */
        navRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itr.hasNext()) {
                    Log.d("NoteList", itr.next().symbol.toString());
                    //Log.d("NoteList", itr.next().symbol.toString());
                }
                //currentNote = itr.next();
                //Log.d("Linked list position", currentNote.symbol.toString());
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

        /**
         * Default noteLength and noteLengthButton
         */
        noteLengthButtons[0].toggle();
        currentNote = notation.new NoteFont(Syntax.NOTE_WHOLE, -1);

        // Initialize staff
        initText();
        // Add default note to staff
        itr.add(notation.new NoteFont(currentNote.symbol, 5));
        if (itr.hasPrevious()) Log.d("NoteList", itr.previous().symbol.toString());
        //noteList.add(notation.new NoteFont(currentNote.symbol, 5));

        // Setup listeners for each piano key
        // Attaches each key to a specifc line on the staff
        for (int i = 0; i < keyButtons.length; i++) {
            int finalI = i;
            keyButtons[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // FIXME this is ugly
                    //Log.d("NoteList size", Integer.toString(noteList.size()));
                    if (itr.hasNext()) {
                        itr.next();
                        itr.previous();
                        itr.remove();
                        //itr.previous();
                    }
                    //noteList.removeLast();
                    switch (finalI) {
                        case 0:
                            itr.add(notation.new NoteFont(currentNote.symbol, 1));
                            //noteList.add(notation.new NoteFont(currentNote.symbol, 1));
                            break;
                        case 1:
                            itr.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 1));
                            //noteList.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 1));
                            break;
                        case 2:
                            itr.add(notation.new NoteFont(currentNote.symbol, 2));
                            //noteList.add(notation.new NoteFont(currentNote.symbol, 2));
                            break;
                        case 3:
                            itr.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 2));
                            //noteList.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 2));
                            break;
                        case 4:
                            itr.add(notation.new NoteFont(currentNote.symbol, 3));
                            //noteList.add(notation.new NoteFont(currentNote.symbol, 3));
                            break;
                        case 5:
                            itr.add(notation.new NoteFont(currentNote.symbol, 4));
                            //noteList.add(notation.new NoteFont(currentNote.symbol, 4));
                            break;
                        case 6:
                            itr.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 4));
                            //noteList.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 4));
                            break;
                        case 7:
                            itr.add(notation.new NoteFont(currentNote.symbol, 5));
                            //noteList.add(notation.new NoteFont(currentNote.symbol, 5));
                            break;
                        case 8:
                            itr.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 5));
                            //noteList.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 5));
                            break;
                        case 9:
                            itr.add(notation.new NoteFont(currentNote.symbol, 6));
                            //noteList.add(notation.new NoteFont(currentNote.symbol, 6));
                            break;
                        case 10:
                            itr.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 6));
                            //noteList.add(notation.new NoteFont(currentNote.symbol, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 6));
                            break;
                        case 11:
                            itr.add(notation.new NoteFont(currentNote.symbol, 7));
                            //noteList.add(notation.new NoteFont(currentNote.symbol, 7));
                            break;
                        case 12:
                            itr.add(notation.new NoteFont(currentNote.symbol, 8));
                            //noteList.add(notation.new NoteFont(currentNote.symbol, 8));
                    }
                    if (itr.hasPrevious()) itr.previous();

                    displayText();
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
        StringBuffer sb = new StringBuffer();

        sb.append(Syntax.BARLINE_SINGLE.unicode);
        for (int i = 0; i < 16; i++) {
            sb.append(Syntax.STAFF_5_LINES.unicode);
        }
        sb.append(Syntax.BARLINE_SINGLE.unicode);

        melodyText.setText(sb.toString());
        // FIXME ? this might need to be implemented differently later
        gclefText.setText(Syntax.G_CLEF.unicode);

        itr.add(notation.new NoteFont(Syntax.SPACE_CLEF, 5));
        if (itr.hasPrevious()) {
            Log.d("NoteList", itr.previous().symbol.toString());
            itr.next();
        }
        //noteList.add(notation.new NoteFont(Syntax.SPACE_CLEF, 5));
        // FIXME ? end of fixme
    }

    /**
    Displays text from the noteList in the order given.
     */
    void displayText() {
        StringBuffer[] sb = new StringBuffer[staffLines.length];
        // Initialize string buffers
        for (int i = 0; i < staffLines.length; i++) {
            sb[i] = new StringBuffer();
        }

        // Add notes to noteList
        for (ListIterator<NoteFont> itr2 = noteList.listIterator(); itr2.hasNext();/*NoteFont nf : noteList*/) {
            NoteFont nf = itr2.next();
            for (int i = 0; i < staffLines.length; i++) {
                if (nf.lineNum == i) { // If this is the line where the note belongs
                    if (nf.prefix != Syntax.EMPTY) {
                        sb[i].append(nf.prefix.unicode);
                    }
                    sb[i].append(nf.symbol.unicode);
                } else {
                    if (nf.prefix != Syntax.EMPTY) {
                        sb[i].append(Syntax.SPACE_NOTE.unicode);
                    }
                    if (Syntax.CLEF.contains(nf.symbol)) {
                        sb[i].append(Syntax.SPACE_CLEF.unicode);
                    } else {
                        sb[i].append(Syntax.SPACE_NOTE.unicode);
                    }
                }
            }
        }

        // reset test in staffLines text views
        for (int i = 0; i < staffLines.length; i++) {
            staffLines[i].setText(sb[i]);
        }
    }
}