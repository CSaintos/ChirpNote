package com.example.chirpnote;

import android.content.Context;

import com.example.midiFileLib.src.MidiFile;
import com.example.midiFileLib.src.event.NoteOff;
import com.example.midiFileLib.src.event.NoteOn;
import com.example.midiFileLib.src.util.MidiProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Percussion {
    private String mRockPath;

    public Percussion(Context context){
        try{
            InputStream inputStream = context.getResources().openRawResource(R.raw.rock_drums);
            File tempFile = File.createTempFile("temp", "rock_drums.mid");
            mRockPath = tempFile.getPath();
            copyFile(inputStream, new FileOutputStream(tempFile));
        } catch (IOException e) {
            throw new RuntimeException("Can't create temp file ", e);
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public void playRock(){
        MidiFile midiFile = null;
        try {
            midiFile = new MidiFile(new File(mRockPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MidiProcessor processor = new MidiProcessor(midiFile);
        MidiEventHandler m = new MidiEventHandler("TestingPercussionPlayback");
        processor.registerEventListener(m, NoteOn.class);
        processor.registerEventListener(m, NoteOff.class);
        processor.start();
    }
}
