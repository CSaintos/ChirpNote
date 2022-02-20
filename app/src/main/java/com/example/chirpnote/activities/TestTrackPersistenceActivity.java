package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.chirpnote.R;
import com.example.chirpnote.ConstructedMelody;
import com.example.chirpnote.Session;

public class TestTrackPersistenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_track_persistence);

        Button playMelodyButton = (Button) findViewById(R.id.playMelodyButton1);
        Button playAudioButton = (Button) findViewById(R.id.playAudioButton1);
        Button previousActivityButton = (Button) findViewById(R.id.goToPreviousActivityButton);

        Session session = (Session) getIntent().getSerializableExtra("session");
        ConstructedMelody melody = (ConstructedMelody) session.getMelody();
        melody.setPlayButton(playMelodyButton);
        session.getAudio().setPlayButton(playAudioButton);

        playAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!session.getAudio().isPlaying()){
                    playAudioButton.setText("Stop");
                    session.getAudio().play();
                } else {
                    playAudioButton.setText("Play");
                    session.getAudio().stop();
                }
            }
        });

        playMelodyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!session.getAudio().isPlaying()){
                    playMelodyButton.setText("Stop");
                    melody.play();
                } else {
                    playMelodyButton.setText("Play");
                    melody.stop();
                }
            }
        });

        previousActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestTrackPersistenceActivity.this, SessionActivity.class);
                intent.putExtra("session", session);
                startActivity(intent);
            }
        });
    }
}