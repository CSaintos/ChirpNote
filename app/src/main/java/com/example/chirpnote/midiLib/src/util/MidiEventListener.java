package com.example.chirpnote.midiLib.src.util;

import com.example.chirpnote.midiLib.src.event.MidiEvent;

public interface MidiEventListener
{
    public void onStart(boolean fromBeginning);

    public void onEvent(MidiEvent event, long ms);

    public void onStop(boolean finished);
}
