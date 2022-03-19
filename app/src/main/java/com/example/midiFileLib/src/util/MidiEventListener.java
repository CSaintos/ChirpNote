package com.example.midiFileLib.src.util;

import com.example.midiFileLib.src.event.MidiEvent;

public interface MidiEventListener
{
    public void onStart(boolean fromBeginning);

    public void onEvent(MidiEvent event);

    public void onStop(boolean finished);
}
