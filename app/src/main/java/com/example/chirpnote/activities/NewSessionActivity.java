package com.example.chirpnote.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.Key;
import com.example.chirpnote.R;
import com.example.chirpnote.Session;

public class NewSessionActivity extends AppCompatActivity {
    private EditText setName, setTempo;
    private Button createSessionButton;
    private String tempoInvalidMessage = "Tempo must be " + Session.MIN_TEMPO + "-" + Session.MAX_TEMPO + " BPM";
    private TextView tempoInvalid;
    private TextWatcher checkTextValid = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void afterTextChanged(Editable editable) {
            String name = setName.getText().toString();
            String tempo = setTempo.getText().toString();

            if(name.equals("") || tempo.equals("")){
                createSessionButton.setEnabled(false);
            } else {
                int t = Integer.parseInt(tempo);
                if(t < Session.MIN_TEMPO || t > Session.MAX_TEMPO) {
                    tempoInvalid.setText(tempoInvalidMessage);
                    createSessionButton.setEnabled(false);
                } else {
                    tempoInvalid.setText("");
                    createSessionButton.setEnabled(true);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);

        tempoInvalid = (TextView) findViewById(R.id.tempoInvalidText);

        setName = (EditText) findViewById(R.id.setSessionNameText);
        setName.addTextChangedListener(checkTextValid);

        setTempo = (EditText) findViewById(R.id.setTempoText);
        setTempo.addTextChangedListener(checkTextValid);

        Button setKeyButton = (Button) findViewById(R.id.setKeyButton);
        setKeyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Go to the set key dialog or activity or whatever
            }
        });

        Context context = this;
        String basePath = context.getFilesDir().getPath();

        createSessionButton = (Button) findViewById(R.id.createSessionButton);
        createSessionButton.setEnabled(false);
        createSessionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewSessionActivity.this, SessionActivity.class);
                Session session = new Session(setName.getText().toString(), new Key(Key.RootNote.C, Key.Type.MAJOR),
                                                Integer.parseInt(setTempo.getText().toString()), basePath + "/melody.mid", basePath + "audioTrack.mp4");
                intent.putExtra("session", session);
                startActivity(intent);
            }
        });
    }
}