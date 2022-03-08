package com.example.midiFileLib.src.event.meta;

public class TrackName extends TextualMetaEvent
{
    public TrackName(long tick, long delta, String name)
    {
        super(tick, delta, MetaEvent.TRACK_NAME, name);
    }

    public void setName(String name)
    {
        setText(name);
    }

    public String getTrackName()
    {
        return getText();
    }
}
