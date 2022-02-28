package com.example.chirpnote.midiLib.src.event.meta;

public class Marker extends TextualMetaEvent
{
    public Marker(long tick, long delta, String marker)
    {
        super(tick, delta, MetaEvent.MARKER, marker);
    }

    public void setMarkerName(String name)
    {
        setText(name);
    }

    public String getMarkerName()
    {
        return getText();
    }
}
