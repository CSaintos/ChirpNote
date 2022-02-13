package com.example.chirpnote.activities;

import static com.example.chirpnote.Notation.Syntax.*;
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

import com.example.chirpnote.R;
import com.example.chirpnote.Notation.Syntax;

import java.util.HashMap;

public class MelodyActivity extends AppCompatActivity {

    //private Notation notation = new Notation();
    private Button[] keyButtons;
    private TextView[] staffLines;
    private Button backButton;
    private Button leftButton;
    private Button rightButton;
    private TextView melodyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melody);

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

        // Setup listeners for each piano key
        for (Button note : keyButtons) {
            note.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent e) {
                    // TODO add functionality
                    return false;
                }
            });
        }
    }

    void initText() {

        //HashMap<syntax, String> unicode = Notation.unicode;

        // FIXME Testing text view
        // Log.d("melody text view",  (melodyText.getText()).toString());

        String text =
                unicode.get(BARLINESINGLE)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(STAFF5LINES)
                + unicode.get(BARLINESINGLE);

        //Log.d("setting text to: ", String.valueOf(Html.fromHtml(text, 0)));

        melodyText.setText(text);
        //melodyText.setText((new UnicodeSet("[\\u20B9]")).toString());
        //melodyText.setText(getString(R.string.STAFF5LINES));
        //melodyText.setText(new char[]{'♩'}, 0, 1);
        //Log.d("new melody text view", melodyText.getText().toString());
    }

    void addNoteToStaff(Syntax symbol, int lineNum) {

    }
}