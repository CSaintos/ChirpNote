package com.example.chirpnote.midiLib.src.event.meta;

import java.io.IOException;
import java.io.OutputStream;

import com.example.chirpnote.midiLib.src.event.MidiEvent;
import com.example.chirpnote.midiLib.src.util.MidiUtil;
import com.example.chirpnote.midiLib.src.util.VariableLengthInt;

public class SequencerSpecificEvent extends MetaEvent
{
    private byte[] mData;

    public SequencerSpecificEvent(long tick, long delta, byte[] data)
    {
        super(tick, delta, MetaEvent.SEQUENCER_SPECIFIC, new VariableLengthInt(data.length));

        mData = data;
    }

    public void setData(byte[] data)
    {
        mData = data;
        mLength.setValue(mData.length);
    }

    public byte[] getData()
    {
        return mData;
    }

    protected int getEventSize()
    {
        return 1 + 1 + mLength.getByteCount() + mData.length;
    }

    @Override
    public void writeToFile(OutputStream out) throws IOException
    {
        super.writeToFile(out);

        out.write(mLength.getBytes());
        out.write(mData);
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

        if(!(other instanceof SequencerSpecificEvent))
        {
            return 1;
        }

        SequencerSpecificEvent o = (SequencerSpecificEvent) other;

        if(MidiUtil.bytesEqual(mData, o.mData, 0, mData.length))
        {
            return 0;
        }
        return 1;
    }
}
