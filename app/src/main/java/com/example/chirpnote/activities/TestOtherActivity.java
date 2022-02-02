package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.chirpnote.Melody;
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.R;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;

public class TestOtherActivity extends AppCompatActivity {

    private MidiDriver midiDriver;
    private ArrayList<MusicNote> pianoKeys;
    Melody melody;
    boolean recordingAudio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_other);

        Button recordMelodyButton = (Button) findViewById(R.id.recordMelodyButton);
        Button recordAudioButton = (Button) findViewById(R.id.recordAudioButton);
        Button playButton = (Button) findViewById(R.id.playButton);
        Context context = this;
        String melodyFilePath = context.getFilesDir().getPath() + "/melodyTest.mid";
        melody = new Melody(120, melodyFilePath, playButton);

        midiDriver = MidiDriver.getInstance(); // MIDI driver to send MIDI events to
        pianoKeys = new ArrayList<>(); // List of notes
        // You can also create a new MusicNote without a Melody if you just want to test the keyboard playback stuff
        // For example: pianoKeys.add(new MusicNote(59, (Button) findViewById(R.id.noteBButton))
        pianoKeys.add(new MusicNote(60, (Button) findViewById(R.id.noteCButton), melody));
        pianoKeys.add(new MusicNote(62, (Button) findViewById(R.id.noteDButton), melody));
        pianoKeys.add(new MusicNote(64, (Button) findViewById(R.id.noteEButton), melody));

        // Setup event listener for each piano key
        for(MusicNote note : pianoKeys){
            note.getButton().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        note.playNote(midiDriver);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        note.stopNote(midiDriver);
                    }
                    return false;
                }
            });
        }

        // Event listener for record melody button (to record a melody with MIDI notes (the piano keys))
        recordMelodyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!melody.isRecording()){
                    recordMelodyButton.setText("End recording");
                    melody.startRecording();
                } else {
                    recordMelodyButton.setText("Record Melody");
                    melody.stopRecording();
                }
            }
        });

        // Event listener for record audio button (to record audio from the device's microphone)
        recordAudioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!recordingAudio){
                    recordAudioButton.setText("End recording");
                } else {
                    recordAudioButton.setText("Record Audio");
                }
                recordingAudio = !recordingAudio;
            }
        });

        // Event listener for play button (to play recorded melody)
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!melody.isPlaying()){
                    playButton.setText("Stop");
                    melody.play();
                } else {
                    playButton.setText("Play");
                    melody.stop();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }
}