package com.example.chirpnote;

import com.example.midiFileLib.src.event.MidiEvent;
import com.example.midiFileLib.src.event.NoteOff;
import com.example.midiFileLib.src.event.NoteOn;
import com.example.midiFileLib.src.util.MidiEventListener;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;

public class MidiEventHandler implements MidiEventListener {
    private String mLabel;
    MidiDriver midiDriver = MidiDriver.getInstance();

    public MidiEventHandler(String label){
        mLabel = label;
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
    public void onEvent(MidiEvent event, long ms){
        if(event instanceof NoteOn){
            NoteOn noteEvent = (NoteOn) event;
            midiDriver.write(new byte[]{(byte) (MidiConstants.NOTE_ON + noteEvent.getChannel()),
                    (byte) noteEvent.getNoteValue(), (byte) noteEvent.getVelocity()});
        } else if(event instanceof NoteOff){
            NoteOff noteEvent = (NoteOff) event;
            midiDriver.write(new byte[]{(byte) (MidiConstants.NOTE_OFF + noteEvent.getChannel()),
                    (byte) noteEvent.getNoteValue(), (byte) noteEvent.getVelocity()});
        }
    }

    @Override
    public void onStop(boolean finished){
        if(finished){
            System.out.println(mLabel + " Finished!");
        } else {
            System.out.println(mLabel + " paused");
        }
    }
}
