package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.chirpnote.AudioTrack;
import com.example.chirpnote.ConstructedMelody;
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.R;
import com.example.chirpnote.Session;

import java.util.concurrent.ThreadLocalRandom;

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

        Button generateMelodyButton = (Button) findViewById(R.id.generateMelodyButton);
        ConstructedMelody melody = new ConstructedMelody(session);

        Button recAudioButton = (Button) findViewById(R.id.recAudioButton3);
        AudioTrack audio = new AudioTrack(session);

        generateMelodyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                melody.startRecording();
                melody.addNote(new MusicNote(ThreadLocalRandom.current().nextInt(50, 70)), ConstructedMelody.NoteDuration.QUARTER_NOTE);
                melody.addNote(new MusicNote(ThreadLocalRandom.current().nextInt(50, 70)), ConstructedMelody.NoteDuration.QUARTER_NOTE);
                melody.addNote(new MusicNote(ThreadLocalRandom.current().nextInt(50, 70)), ConstructedMelody.NoteDuration.QUARTER_NOTE);
                melody.addNote(new MusicNote(ThreadLocalRandom.current().nextInt(50, 70)), ConstructedMelody.NoteDuration.QUARTER_NOTE);
                melody.stopRecording();
                generateMelodyButton.setText("Melody generated!");
                session.setMelodyRecorded();
                if(audio.isRecorded()){
                    nextActivity.setEnabled(true);
                }
            }
        });

        recAudioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                nextActivity.setEnabled(audio.isRecording());
                if(!audio.isRecording()){
                    recAudioButton.setText("End Recording");
                    audio.startRecording();
                } else {
                    recAudioButton.setText("Record Audio");
                    audio.stopRecording();
                    session.setAudioRecorded();
                    if(melody.isRecorded()){
                        nextActivity.setEnabled(true);
                    }
                }
            }
        });

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