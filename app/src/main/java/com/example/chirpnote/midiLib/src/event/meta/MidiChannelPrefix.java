package com.example.chirpnote.midiLib.src.event.meta;

import java.io.IOException;
import java.io.OutputStream;

import com.example.chirpnote.midiLib.src.event.MidiEvent;
import com.example.chirpnote.midiLib.src.util.VariableLengthInt;

public class MidiChannelPrefix extends MetaEvent
{
    private int mChannel;

    public MidiChannelPrefix(long tick, long delta, int channel)
    {
        super(tick, delta, MetaEvent.MIDI_CHANNEL_PREFIX, new VariableLengthInt(4));

        mChannel = channel;
    }

    public void setChannel(int c)
    {
        mChannel = c;
    }

    public int getChannel()
    {
        return mChannel;
    }

    @Override
    protected int getEventSize()
    {
        return 4;
    }

    @Override
    public void writeToFile(OutputStream out) throws IOException
    {
        super.writeToFile(out);

        out.write(1);
        out.write(mChannel);
    }

    public static MetaEvent parseMidiChannelPrefix(long tick, long delta, MetaEventData info)
    {
        if(info.length.getValue() != 1)
        {
            return new GenericMetaEvent(tick, delta, info);
        }

        int channel = info.data[0];

        return new MidiChannelPrefix(tick, delta, channel);
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

        if(!(other instanceof MidiChannelPrefix))
        {
            return 1;
        }

        MidiChannelPrefix o = (MidiChannelPrefix) other;

        if(mChannel != o.mChannel)
        {
            return mChannel < o.mChannel ? -1 : 1;
        }
        return 0;
    }
}
