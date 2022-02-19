package com.example.chirpnote.activities;

//import static com.example.chirpnote.Notation.unicode;
import static com.example.chirpnote.Notation.Syntax;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

//import com.example.chirpnote.Notation.Syntax;
import com.example.chirpnote.Notation;
import com.example.chirpnote.Notation.NoteFont;
import com.example.chirpnote.R;

import java.util.HashMap;
import java.util.LinkedList;

public class MelodyActivity extends AppCompatActivity {

    private Notation notation = new Notation();

    private Button[] keyButtons;
    private TextView[] staffLines;
    private Button backButton;
    private Button leftButton;
    private Button rightButton;
    private TextView melodyText;
    private TextView gclefText;

    private LinkedList<NoteFont> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melody);

        noteList = new LinkedList<>();

        backButton = (Button) findViewById(R.id.melodybackbutton);
        leftButton = (Button) findViewById(R.id.melodyleftbutton);
        rightButton = (Button) findViewById(R.id.melodyrightbutton);

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

        melodyText = (TextView) findViewById(R.id.stafftextview);
        gclefText = (TextView) findViewById(R.id.gcleftextview);

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MelodyActivity.this, ChordsActivity.class));
                // Temp activity to go back to
                startActivity(new Intent(MelodyActivity.this, HomeScreenActivity.class));
            }
        });

        initText();
        // FIXME remove comments once done
        //noteList.add(notation.new NoteFont(Syntax.SPACE_NOTE, 5));
        //noteList.add(notation.new NoteFont(Syntax.ACCIDENTAL_SHARP, 5));
        noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, 5));

        // Setup listeners for each piano key
        // Attaches each key to a specifc line on the staff
        for (int i = 0; i < keyButtons.length; i++) {
            int finalI = i;
            keyButtons[i].setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    // FIXME this is ugly
                    noteList.removeLast();
                    switch (finalI) {
                        case 0:
                            noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, 1));
                            break;
                        case 1:
                            noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 1));
                            break;
                        case 2:
                            noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, 2));
                            break;
                        case 3:
                            noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 2));
                            break;
                        case 4:
                            noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, 3));
                            break;
                        case 5:
                            noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, 4));
                            break;
                        case 6:
                            noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 4));
                            break;
                        case 7:
                            noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, 5));
                            break;
                        case 8:
                            noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 5));
                            break;
                        case 9:
                            noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, 6));
                            break;
                        case 10:
                            noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, Syntax.ACCIDENTAL_SHARP, Syntax.EMPTY, 6));
                            break;
                        case 11:
                            noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, 7));
                            break;
                        case 12:
                            noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, 8));
                    }
                    //noteList.add(notation.new NoteFont(Syntax.NOTE_8TH_UP, finalI + 1));
                    displayText();

                    return false;
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

        noteList.add(notation.new NoteFont(Syntax.SPACE_CLEF, 5));
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
        for (NoteFont nf : noteList) {
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