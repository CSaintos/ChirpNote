package com.example.chirpnote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;

import java.util.HashMap;
import java.util.Map;

public class TestMusicActivity extends AppCompatActivity {

    private MidiDriver midiDriver;
    private HashMap<Integer, Button> pianoKeys = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_music);

        midiDriver = MidiDriver.getInstance();
        midiDriver.start();
        midiDriver.config()[2] = 44100; // Setting sample rate

        pianoKeys.put(60, (Button) findViewById(R.id.noteCButton));
        pianoKeys.put(62, (Button) findViewById(R.id.noteDButton));
        pianoKeys.put(64, (Button) findViewById(R.id.noteEButton));

        for(Map.Entry<Integer, Button> entry : pianoKeys.entrySet()){
            int noteNumber = entry.getKey();
            Button b = entry.getValue();
            b.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        midiDriver.write(new byte[]{MidiConstants.NOTE_ON, (byte) noteNumber, (byte) 0x7F});
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        midiDriver.write(new byte[]{MidiConstants.NOTE_OFF, (byte) noteNumber, (byte) 0x00});
                    }
                    return false;
                }
            });
        }
    }
}