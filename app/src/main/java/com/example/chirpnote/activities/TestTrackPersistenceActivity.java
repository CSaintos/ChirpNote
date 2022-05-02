package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.chirpnote.AudioTrack;
import com.example.chirpnote.Mixer;
import com.example.chirpnote.R;
import com.example.chirpnote.ConstructedMelody;
import com.example.chirpnote.ChirpNoteSession;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

public class TestTrackPersistenceActivity extends AppCompatActivity {
    MidiDriver midiDriver = MidiDriver.getInstance();
    Mixer mixer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_track_persistence);

        Button playMelodyButton = (Button) findViewById(R.id.playMelodyButton1);
        Button playAudioButton = (Button) findViewById(R.id.playAudioButton1);
        Button previousActivityButton = (Button) findViewById(R.id.goToPreviousActivityButton);

        ChirpNoteSession session = (ChirpNoteSession) getIntent().getSerializableExtra("session");
        mixer = new Mixer(session);
        ConstructedMelody melody = mixer.constructedMelody;
        AudioTrack audio = mixer.audioTrack;

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

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();
        mixer.syncWithSession();
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }
}