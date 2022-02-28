package com.example.chirpnote.midiLib.src.event.meta;

import java.io.IOException;
import java.io.OutputStream;

import com.example.chirpnote.midiLib.src.event.MidiEvent;
import com.example.chirpnote.midiLib.src.util.VariableLengthInt;

public abstract class TextualMetaEvent extends MetaEvent
{
    protected String mText;

    protected TextualMetaEvent(long tick, long delta, int type, String text)
    {
        super(tick, delta, type, new VariableLengthInt(text.length()));

        setText(text);
    }

    protected void setText(String t)
    {
        mText = t;
        mLength.setValue(t.getBytes().length);
    }

    protected String getText()
    {
        return mText;
    }

    @Override
    protected int getEventSize()
    {
        return 1 + 1 + mLength.getByteCount() + mLength.getValue();
    }

    @Override
    public void writeToFile(OutputStream out) throws IOException
    {
        super.writeToFile(out);

        out.write(mLength.getBytes());
        out.write(mText.getBytes());
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

        if(!(other instanceof TextualMetaEvent))
        {
            return 1;
        }

        TextualMetaEvent o = (TextualMetaEvent) other;

        return mText.compareTo(o.mText);
    }

    @Override
    public String toString()
    {
        return super.toString() + ": " + mText;
    }
}
