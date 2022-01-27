package com.example.chirpnote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;

import org.billthefarmer.mididriver.MidiDriver;

public class TestMusicActivity extends AppCompatActivity {

    private MidiDriver midiDriver;
    private byte[] event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_music);

        midiDriver = MidiDriver.getInstance();
        midiDriver.start();

        Button noteC = (Button) findViewById(R.id.noteCButton);
        Button noteD = (Button) findViewById(R.id.noteDButton);
        Button noteE = (Button) findViewById(R.id.noteEButton);

        noteC.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    playNote(60);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopNote(60);
                }
                return false;
            }
        });

        noteD.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    playNote(62);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopNote(62);
                }
                return false;
            }
        });

        noteE.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    playNote(64);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopNote(64);
                }
                return false;
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

    private void playNote(int noteNumber) {
        event = new byte[3];
        event[0] = (byte) (0x90 | 0x00);
        event[1] = (byte) noteNumber;
        event[2] = (byte) 0x7F;
        midiDriver.write(event);
    }

    private void stopNote(int noteNumber) {
        event = new byte[3];
        event[0] = (byte) (0x80 | 0x00);
        event[1] = (byte) noteNumber;
        event[2] = (byte) 0x00;
        midiDriver.write(event);
    }
}