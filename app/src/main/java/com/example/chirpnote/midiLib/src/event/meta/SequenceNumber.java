package com.example.chirpnote.midiLib.src.event.meta;

import java.io.IOException;
import java.io.OutputStream;

import com.example.chirpnote.midiLib.src.event.MidiEvent;
import com.example.chirpnote.midiLib.src.util.VariableLengthInt;

public class SequenceNumber extends MetaEvent
{
    private int mNumber;

    public SequenceNumber(long tick, long delta, int number)
    {
        super(tick, delta, MetaEvent.SEQUENCE_NUMBER, new VariableLengthInt(2));

        mNumber = number;
    }

    public int getMostSignificantBits()
    {
        return mNumber >> 8;
    }

    public int getLeastSignificantBits()
    {
        return mNumber & 0xFF;
    }

    public int getSequenceNumber()
    {
        return mNumber;
    }

    @Override
    public void writeToFile(OutputStream out) throws IOException
    {
        super.writeToFile(out);

        out.write(2);
        out.write(getMostSignificantBits());
        out.write(getLeastSignificantBits());
    }

    public static MetaEvent parseSequenceNumber(long tick, long delta, MetaEventData info)
    {
        if(info.length.getValue() != 2)
        {
            return new GenericMetaEvent(tick, delta, info);
        }

        int msb = info.data[0];
        int lsb = info.data[1];
        int number = (msb << 8) + lsb;

        return new SequenceNumber(tick, delta, number);
    }

    @Override
    protected int getEventSize()
    {
        return 5;
    }

    @Override
    public int compareTo(MidiEvent other)
    {
        if(mTick != other.getTick())
        {
            return mTick < other.getTick() ? -1 : 1;
        }
        if(mDelta.getValue() != other.getDelta())
        {
            return mDelta.getValue() < other.getDelta() ? 1 : -1;
        }

        if(!(other instanceof SequenceNumber))
        {
            return 1;
        }

        SequenceNumber o = (SequenceNumber) other;

        if(mNumber != o.mNumber)
        {
            return mNumber < o.mNumber ? -1 : 1;
        }
        return 0;
    }
}
