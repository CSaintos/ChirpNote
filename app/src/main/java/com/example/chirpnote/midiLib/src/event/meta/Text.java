package com.example.chirpnote.midiLib.src.event.meta;

public class Text extends TextualMetaEvent
{
    public Text(long tick, long delta, String text)
    {
        super(tick, delta, MetaEvent.TEXT_EVENT, text);
    }

    public void setText(String t)
    {
        super.setText(t);
    }

    public String getText()
    {
        return super.getText();
    }
}
