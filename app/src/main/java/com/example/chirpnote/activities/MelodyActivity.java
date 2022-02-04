package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.chirpnote.Notation;
import com.example.chirpnote.R;

public class MelodyActivity extends AppCompatActivity {

    Notation notation = new Notation();

    Button backButton;
    Button leftButton;
    Button rightButton;
    TextView melodyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_melody);

        backButton = (Button) findViewById(R.id.melodybackbutton);
        leftButton = (Button) findViewById(R.id.melodyleftbutton);
        rightButton = (Button) findViewById(R.id.melodyrightbutton);
        melodyText = (TextView) findViewById(R.id.melodytextview);

        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MelodyActivity.this, ChordsActivity.class));
                // Temp activity to go back to
                startActivity(new Intent(MelodyActivity.this, HomeScreenActivity.class));
            }
        });

        initText();
    }

    void initText() {

        String text = notation.unicode.get(Notation.syntax.STAFF5LINES)
                + notation.unicode.get(Notation.syntax.STAFF5LINES)
                + notation.unicode.get(Notation.syntax.STAFF5LINES);

        melodyText.setText(text);
    }
}