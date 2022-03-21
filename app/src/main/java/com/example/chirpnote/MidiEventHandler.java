package com.example.chirpnote;

import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

import com.example.midiFileLib.src.event.MidiEvent;
import com.example.midiFileLib.src.event.NoteOff;
import com.example.midiFileLib.src.event.NoteOn;
import com.example.midiFileLib.src.util.MidiEventListener;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;

public class MidiEventHandler implements MidiEventListener {
    private String mLabel;
    private Button mPlayButton;
    MidiDriver midiDriver = MidiDriver.getInstance();

    public MidiEventHandler(String label){
        mLabel = label;
        mPlayButton = null;
    }

    public MidiEventHandler(String label, Button playButton){
        mLabel = label;
        mPlayButton = playButton;
    }

    @Override
    public void onStart(boolean fromBeginning){
        if(fromBeginning){
            System.out.println(mLabel + " Started!");
        } else {
            System.out.println(mLabel + " resumed");
        }
    }

    @Override
    public void onEvent(MidiEvent event){
        if(event instanceof NoteOn){
            NoteOn noteEvent = (NoteOn) event;
            midiDriver.write(new byte[]{(byte) (MidiConstants.NOTE_ON + noteEvent.getChannel()),
                    (byte) noteEvent.getNoteValue(), (byte) noteEvent.getVelocity()});
        } else if(event instanceof NoteOff){
            NoteOff noteEvent = (NoteOff) event;
            midiDriver.write(new byte[]{(byte) (MidiConstants.NOTE_ON + noteEvent.getChannel()),
                    (byte) noteEvent.getNoteValue(), (byte) 0});
        }
    }

    @Override
    public void onStop(boolean finished){
        System.out.println(mLabel + " Finished!");
        if(mPlayButton != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable(){
                @Override
                public void run() {
                    mPlayButton.setText("Play");
                }
            });
        }
        /*if(finished){
            System.out.println(mLabel + " Finished!");
            if(mPlayButton != null) {
                new Handler(Looper.getMainLooper()).post(new Runnable(){
                    @Override
                    public void run() {
                        mPlayButton.setText("Play");
                    }
                });
            }
        } else {
            System.out.println(mLabel + " paused");
        }*/
    }
}
