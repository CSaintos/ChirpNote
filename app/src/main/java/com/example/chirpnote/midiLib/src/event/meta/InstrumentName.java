package com.example.chirpnote.midiLib.src.event.meta;

public class InstrumentName extends TextualMetaEvent
{
    public InstrumentName(long tick, long delta, String name)
    {
        super(tick, delta, MetaEvent.INSTRUMENT_NAME, name);
    }

    public void setName(String name)
    {
        setText(name);
    }

    public String getName()
    {
        return getText();
    }
}
