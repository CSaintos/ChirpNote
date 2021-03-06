package com.example.midiFileLib.src.event;

public class NoteOff extends ChannelEvent
{
    public NoteOff(long tick, int channel, int note, int velocity)
    {
        super(tick, ChannelEvent.NOTE_OFF, channel, note, velocity);
    }

    public NoteOff(long tick, long delta, int channel, int note, int velocity)
    {
        super(tick, delta, ChannelEvent.NOTE_OFF, channel, note, velocity);
    }

    public int getNoteValue()
    {
        return mValue1;
    }

    public int getVelocity()
    {
        return mValue2;
    }

    public void setNoteValue(int p)
    {
        mValue1 = p;
    }

    public void setVelocity(int v)
    {
        mValue2 = v;
    }
}
