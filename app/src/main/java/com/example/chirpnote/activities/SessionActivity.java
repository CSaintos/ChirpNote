package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chirpnote.R;
import com.example.chirpnote.Session;

public class SessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Session session = (Session) getIntent().getSerializableExtra("session");
        ((TextView) findViewById(R.id.sessionNameText)).setText("Session Name: " + session.getName());
        ((TextView) findViewById(R.id.tempoText)).setText("Tempo: " + session.getTempo() + " BPM");
        ((TextView) findViewById(R.id.keyText)).setText("Key: " + session.getKey());

        Button nextActivity = (Button) findViewById(R.id.goToNextActivityButton);
        nextActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SessionActivity.this, TestTrackPersistenceActivity.class);
                intent.putExtra("session", session);
                startActivity(intent);
            }
        });
    }
}