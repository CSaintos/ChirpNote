package com.example.chirpnote.midiLib.src.event.meta;

public class CopyrightNotice extends TextualMetaEvent
{
    public CopyrightNotice(long tick, long delta, String text)
    {
        super(tick, delta, MetaEvent.COPYRIGHT_NOTICE, text);
    }

    public void setNotice(String t)
    {
        setText(t);
    }

    public String getNotice()
    {
        return getText();
    }
}
