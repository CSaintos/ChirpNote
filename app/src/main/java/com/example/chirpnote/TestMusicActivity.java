package com.example.chirpnote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;

public class TestMusicActivity extends AppCompatActivity {

    private MidiDriver midiDriver;
    private ArrayList<MusicNote> pianoKeys;
    Melody melody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_music);

        Button recordButton = (Button) findViewById(R.id.recordButton);
        Button playButton = (Button) findViewById(R.id.playButton);
        Context context = this;
        String melodyFilePath = context.getFilesDir().getPath() + "/melody.mid";
        melody = new Melody(120, melodyFilePath, playButton);

        midiDriver = MidiDriver.getInstance(); // MIDI driver to send MIDI events to
        pianoKeys = new ArrayList<>(); // List of notes
        // You can also create a new MusicNote without a Melody if you just want to test the keyboard playback stuff
        // For example: pianoKeys.add(new MusicNote(59, (Button) findViewById(R.id.noteBButton))
//        pianoKeys.add(new MusicNote(60, (Button) findViewById(R.id.noteCButton), melody));
//        pianoKeys.add(new MusicNote(62, (Button) findViewById(R.id.noteDButton), melody));
//        pianoKeys.add(new MusicNote(64, (Button) findViewById(R.id.noteEButton), melody));

        pianoKeys.add(new MusicNote(60, (Button) findViewById(R.id.noteC4Button), melody));
        pianoKeys.add(new MusicNote(61, (Button) findViewById(R.id.noteCSharp4Button), melody));
        pianoKeys.add(new MusicNote(62, (Button) findViewById(R.id.noteD4Button), melody));
        pianoKeys.add(new MusicNote(63, (Button) findViewById(R.id.noteDSharp4Button), melody));
        pianoKeys.add(new MusicNote(64, (Button) findViewById(R.id.noteE4Button), melody));
        pianoKeys.add(new MusicNote(65, (Button) findViewById(R.id.noteF4Button), melody));
        pianoKeys.add(new MusicNote(66, (Button) findViewById(R.id.noteFSharp4Button), melody));
        pianoKeys.add(new MusicNote(67, (Button) findViewById(R.id.noteG4Button), melody));
        pianoKeys.add(new MusicNote(68, (Button) findViewById(R.id.noteGSharp4Button), melody));
        pianoKeys.add(new MusicNote(69, (Button) findViewById(R.id.noteA4Button), melody));
        pianoKeys.add(new MusicNote(70, (Button) findViewById(R.id.noteASharp4Button), melody));
        pianoKeys.add(new MusicNote(71, (Button) findViewById(R.id.noteB4Button), melody));
        pianoKeys.add(new MusicNote(72, (Button) findViewById(R.id.noteC5Button), melody));

        // Event listener for record button (to record melody)
        recordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!melody.isRecording()){
                    recordButton.setText("End recording");
                    melody.startRecording();
                } else {
                    recordButton.setText("Record");
                    melody.stopRecording();
                }
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

        // Setup event listener for each piano key
        for(MusicNote note : pianoKeys){
            note.getButton().setOnTouchListener(new OnTouchListener() {
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
