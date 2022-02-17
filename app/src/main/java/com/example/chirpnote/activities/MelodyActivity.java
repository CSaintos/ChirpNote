package com.example.chirpnote.activities;

import static com.example.chirpnote.Notation.unicode;

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

import com.example.chirpnote.Notation.Syntax;
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

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MelodyActivity.this, ChordsActivity.class));
                // Temp activity to go back to
                startActivity(new Intent(MelodyActivity.this, HomeScreenActivity.class));
            }
        });

        initText();
        // FIXME only for testing
        noteList.add(notation.new NoteFont(Syntax.SPACE, 5));
        noteList.add(notation.new NoteFont(Syntax.NOTE8THUP, 5));

        // Setup listeners for each piano key
        for (int i = 0; i < keyButtons.length; i++) {
            int finalI = i;
            keyButtons[i].setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    // FIXME this is incorrect
                    noteList.removeLast();
                    noteList.add(notation.new NoteFont(Syntax.NOTE8THUP, finalI + 1));
                    displayText();

                    return false;
                }
            });
        }

        displayText();
    }

    /*
    Initializes the display text with the staff and adds a temporary
    element into noteList
     */
    void initText() {
        StringBuffer sb = new StringBuffer();

        sb.append(unicode.get(Syntax.BARLINESINGLE));
        for (int i = 0; i < 16; i++) {
            sb.append(unicode.get(Syntax.STAFF5LINES));
        }
        sb.append(unicode.get(Syntax.BARLINESINGLE));

        melodyText.setText(sb.toString());

        // TODO do this better
        noteList.add(notation.new NoteFont(Syntax.GCLEF, 5));
    }

    /*
    Displays text from the noteList in the order given.
     */
    void displayText() {
        StringBuffer[] sb = new StringBuffer[staffLines.length];
        for (int i = 0; i < staffLines.length; i++) {
            sb[i] = new StringBuffer();
        }

        for (NoteFont nf : noteList) {
            for (int i = 0; i < staffLines.length; i++) {
                if (nf.lineNum == i) {
                    sb[i].append(unicode.get(nf.symbol));
                } else {
                    sb[i].append(unicode.get(Syntax.SPACE));
                }
            }
        }

        for (int i = 0; i < staffLines.length; i++) {
            staffLines[i].setText(sb[i]);
        }
    }
}