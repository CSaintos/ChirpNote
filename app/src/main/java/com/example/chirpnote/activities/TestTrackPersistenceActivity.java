package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.chirpnote.AudioTrack;
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
        ConstructedMelody melody = new ConstructedMelody(session);
        melody.setPlayButton(playMelodyButton);
        AudioTrack audio = new AudioTrack(session);
        audio.setPlayButton(playAudioButton);

        playAudioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!audio.isPlaying()){
                    playAudioButton.setText("Stop");
                    audio.play();
                } else {
                    playAudioButton.setText("Play");
                    audio.stop();
                }
            }
        });

        playMelodyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!melody.isPlaying()){
                    playMelodyButton.setText("Stop");
                    melody.play();
                } else {
                    playMelodyButton.setText("Play");
                    melody.stop();
                }
            }
        });

        previousActivityButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestTrackPersistenceActivity.this, SessionActivity.class);
                intent.putExtra("session", session);
                startActivity(intent);
            }
        });
    }
}