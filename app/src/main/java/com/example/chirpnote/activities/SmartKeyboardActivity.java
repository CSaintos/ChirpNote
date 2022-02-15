package com.example.chirpnote.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.MusicNote;
import com.example.chirpnote.R;
import com.example.chirpnote.RealTimeMelody;
import com.example.chirpnote.Session;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;

public class SmartKeyboardActivity extends AppCompatActivity {

    private MidiDriver midiDriver;
    private ArrayList<MusicNote> pianoKeys;
    RealTimeMelody melody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_keyboard);

        Button recordButton = (Button) findViewById(R.id.recordButton);
        Button playButton = (Button) findViewById(R.id.playButton);

        Context context = this;
        String melodyFilePath = context.getFilesDir().getPath() + "/melody.mid";

        Session session = new Session("Session1", Session.Note.D, Session.KeyType.MINOR, 120);

        melody = new RealTimeMelody(120, melodyFilePath, playButton);

        midiDriver = MidiDriver.getInstance(); // MIDI driver to send MIDI events to
        //midiDriver.setVolume(25);  attempted to change the global volume of the midiDriver just to see if the volume of the keys played would change. They don't.
        pianoKeys = new ArrayList<>(); // List of notes
        // You can also create a new MusicNote without a Melody if you just want to test the keyboard playback stuff
        // For example: pianoKeys.add(new MusicNote(59, (Button) findViewById(R.id.noteBButton))
//        pianoKeys.add(new MusicNote(60, (Button) findViewById(R.id.noteCButton), melody));
//        pianoKeys.add(new MusicNote(62, (Button) findViewById(R.id.noteDButton), melody));
//        pianoKeys.add(new MusicNote(64, (Button) findViewById(R.id.noteEButton), melody));


        //maybe create a session.getKeyValue to initialize the piano keys in a for loop

        //int keyStart = session.getKeyValue; // need to modify session class to get this to work
        int noteStart = session.getNoteValue();
        int currentNote = noteStart;
        Session.KeyType keyType = session.getKeyType(); // major or minor
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(noteStart);

        if (keyType == Session.KeyType.MAJOR) {
            for (int i = 1; i < 8; i++)
                if (i == 3 || i == 7) // for half steps
                {
                    currentNote = currentNote + 1;
                    list.add(currentNote);
                }
                else
                {
                    currentNote = currentNote + 2;
                    list.add(currentNote);
                }
        }
        else // if not major then harmonic minor
        {
            for (int i = 1; i < 8; i++) {
                if (i == 6) // for the raised 7th
                {
                    currentNote = currentNote + 3;
                    list.add(currentNote);
                } else if (i == 2 || i == 5 || i == 7) // half steps
                {
                    currentNote = currentNote + 1;
                    list.add(currentNote);
                } else {
                    currentNote = currentNote + 2;
                    list.add(currentNote);
                }
            }
        }


        pianoKeys.add(new MusicNote(list.get(0), (Button) findViewById(R.id.key1), melody));
        pianoKeys.add(new MusicNote(list.get(1), (Button) findViewById(R.id.key2), melody));
        pianoKeys.add(new MusicNote(list.get(2), (Button) findViewById(R.id.key3), melody));
        pianoKeys.add(new MusicNote(list.get(3), (Button) findViewById(R.id.key4), melody));
        pianoKeys.add(new MusicNote(list.get(4), (Button) findViewById(R.id.key5), melody));
        pianoKeys.add(new MusicNote(list.get(5), (Button) findViewById(R.id.key6), melody));
        pianoKeys.add(new MusicNote(list.get(6), (Button) findViewById(R.id.key7), melody));
        pianoKeys.add(new MusicNote(list.get(7), (Button) findViewById(R.id.key8), melody));



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
                        note.play(midiDriver);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        note.stop(midiDriver);
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
