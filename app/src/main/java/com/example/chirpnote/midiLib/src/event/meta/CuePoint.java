package com.example.chirpnote.midiLib.src.event.meta;

public class CuePoint extends TextualMetaEvent
{
    public CuePoint(long tick, long delta, String marker)
    {
        super(tick, delta, MetaEvent.CUE_POINT, marker);
    }

    public void setCue(String name)
    {
        setText(name);
    }

    public String getCue()
    {
        return getText();
    }
}
