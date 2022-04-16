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
    private int mChannel;
    private Button mPlayButton;
    MidiDriver midiDriver = MidiDriver.getInstance();

    /**
     * Handles MIDI Events dispatched by the MIDI Processor
     * @param label The identifying label for this handler
     */
    public MidiEventHandler(String label){
        mLabel = label;
        mChannel = -1;
        mPlayButton = null;
    }

    /**
     * Handles MIDI Events dispatched by the MIDI Processor
     * @param label The identifying label for this handler
     * @param channel The MIDI channel whose events should be handled
     */
    public MidiEventHandler(String label, int channel){
        mLabel = label;
        mChannel = channel;
        mPlayButton = null;
    }

    public MidiEventHandler(String label, Button playButton){
        mLabel = label;
        mChannel = -1;
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
        if(mChannel == -1){
            if(event instanceof NoteOn){
                sendNoteOnEvent((NoteOn) event);
            } else if(event instanceof NoteOff){
                sendNoteOffEvent((NoteOff) event);
            }
        } else {
            if (event instanceof NoteOn) {
                NoteOn noteEvent = (NoteOn) event;
                if(noteEvent.getChannel() == mChannel){
                    sendNoteOnEvent(noteEvent);
                }
            } else if (event instanceof NoteOff) {
                NoteOff noteEvent = (NoteOff) event;
                if(noteEvent.getChannel() == mChannel){
                    sendNoteOffEvent(noteEvent);
                }
            }
        }
    }

    /**
     * Sends a NoteOn event to the MIDI driver
     * @param event The event to send
     */
    private void sendNoteOnEvent(NoteOn event){
        midiDriver.write(new byte[]{(byte) (MidiConstants.NOTE_ON + event.getChannel()),
                (byte) event.getNoteValue(), (byte) event.getVelocity()});
    }

    /**
     * Sends a NoteOff event to the MIDI driver
     * @param event The event to send
     */
    private void sendNoteOffEvent(NoteOff event){
        // NoteOn event with velocity of 0 is equivalent to a NoteOff event
        midiDriver.write(new byte[]{(byte) (MidiConstants.NOTE_ON + event.getChannel()),
                (byte) event.getNoteValue(), (byte) 0});
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

    public String getLabel() {
        return mLabel;
    }
}
