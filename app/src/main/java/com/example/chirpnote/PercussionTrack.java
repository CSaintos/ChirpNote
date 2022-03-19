package com.example.chirpnote;

import android.content.Context;
import android.widget.Button;

import com.example.midiFileLib.src.MidiFile;
import com.example.midiFileLib.src.event.NoteOff;
import com.example.midiFileLib.src.event.NoteOn;
import com.example.midiFileLib.src.event.meta.Tempo;
import com.example.midiFileLib.src.util.MidiProcessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PercussionTrack {
    private String mFilePath;

    // For playback
    private MidiProcessor mMidiProcessor;
    private MidiEventHandler mMidiEventHandler;

    /**
     * A Percussion track
     * @param label A label for this Percussion track
     * @param session The session to play this Percussion track on
     * @param context The context from the activity (pass "this")
     * @param rawResource The MIDI file containing this Percussion track (eg. R.raw.rock_drums)
     */
    public PercussionTrack(String label, Session session, Context context, int rawResource, Button playButton){
        mMidiEventHandler = new MidiEventHandler(label, playButton);
        try{
            InputStream inputStream = context.getResources().openRawResource(rawResource);
            File tempFile = File.createTempFile("temp", label);
            mFilePath = tempFile.getPath();
            copyFile(inputStream, new FileOutputStream(tempFile));

            // Set tempo to session tempo
            MidiFile midiFile = null;
            try {
                midiFile = new MidiFile(tempFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Tempo tempo = new Tempo();
            tempo.setBpm(session.getTempo());
            midiFile.getTracks().get(0).insertEvent(tempo);
            try {
                midiFile.writeToFile(tempFile);
            } catch(IOException e) {
                System.err.println(e);
            }
            inputStream.close();
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

    /**
     * Sets the tempo for all percussion tracks
     * @param bpm The new tempo
     */
    public void setTempo(int bpm){
        MidiFile midiFile = null;
        try {
            midiFile = new MidiFile(new File(mFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        midiFile.getTracks().get(0).removeFirstEvent();
        Tempo tempo = new Tempo();
        tempo.setBpm(bpm);
        midiFile.getTracks().get(0).insertEvent(tempo);
        try {
            midiFile.writeToFile(new File(mFilePath));
        } catch(IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Gets whether or not this percussion track is currently being played back
     * @return True if this percussion track is being played
     */
    public boolean isPlaying(){
        return mMidiProcessor != null && mMidiProcessor.isRunning();
    }

    /**
     * Plays this percussion track
     * @exception IllegalStateException if the percussion track cannot be played
     */
    public void play() throws IllegalStateException {
        if(isPlaying()){
            throw new IllegalStateException("Cannot play the percussion track if it is already being played (stop playback first)");
        }
        MidiFile midiFile = null;
        try {
            midiFile = new MidiFile(new File(mFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMidiProcessor = new MidiProcessor(midiFile);
        mMidiProcessor.registerEventListener(mMidiEventHandler, NoteOn.class);
        mMidiProcessor.registerEventListener(mMidiEventHandler, NoteOff.class);
        mMidiProcessor.start();
    }

    /**
     * Stops this percussion track
     * @exception IllegalStateException if the percussion track cannot be stopped
     */
    public void stop() throws IllegalStateException {
        if(!isPlaying()){
            throw new IllegalStateException("Cannot stop the percussion track if it is not being played (start playback first)");
        }
        mMidiProcessor.reset();
    }
}
