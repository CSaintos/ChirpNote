package com.example.chirpnote;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
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

public class PercussionPattern {
    // Style of percussion
    public enum Style {
        POP("Pop", R.raw.pop_drums),
        ROCK("Rock", R.raw.rock_drums);

        private String string;
        private int rawResource;

        Style(String s, int r){
            string = s;
            rawResource = r;
        }

        @Override
        public String toString() {
            return string;
        }
    }
    private String mFilePath;

    // For playback
    private MidiProcessor mMidiProcessor;
    private MidiEventHandler mMidiEventHandler;

    /**
     * A Percussion track
     * @param style The style of this percussion track
     * @param session The session to play this Percussion track on
     * @param context The context from the activity (pass "this")
     * @param playButton The button used to play this track
     */
    public PercussionPattern(Style style, Session session, Context context, Button playButton){
        mMidiEventHandler = new MidiEventHandler(style.toString(), playButton);
        try{
            InputStream inputStream = context.getApplicationContext().getResources().openRawResource(style.rawResource);
            File tempFile = File.createTempFile("temp", style.toString());
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

    /**
     * A Percussion track
     * @param style The style of this percussion track
     * @param session The session to play this Percussion track on
     * @param context The context from the activity (pass "this")
     */
    public PercussionPattern(Style style, Session session, Context context){
        mMidiEventHandler = new MidiEventHandler(style.toString());
        try{
            InputStream inputStream = context.getApplicationContext().getResources().openRawResource(style.rawResource);
            File tempFile = File.createTempFile("temp", style.toString());
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

    /**
     * A Percussion track
     * @param label the label of this percussion track
     * @param path The path of this percussion track
     * @param session The session to play this Percussion track on
     * @param context The context from the activity (pass "this")
     */
    public PercussionPattern(String label, String path, Session session, Context context) {
        mMidiEventHandler = new MidiEventHandler(label);

        try {
            // get file
            AssetFileDescriptor afd = context.getAssets().openFd(path);
            InputStream inputStream = afd.createInputStream();
            File tempFile = File.createTempFile("temp", "file");
            mFilePath = tempFile.getPath();
            copyFile(inputStream, new FileOutputStream(tempFile));

            MidiFile midiFile = new MidiFile(tempFile);

            // set tempo
            Tempo tempo = new Tempo();
            tempo.setBpm(session.getTempo());
            midiFile.getTracks().get(0).insertEvent(tempo);
            midiFile.writeToFile(tempFile);
        } catch (IOException e) {
            e.printStackTrace();
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
     * @exception IllegalStateException if the percussion track cannot be played at this time
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
     * @exception IllegalStateException if the percussion track cannot be stopped at this time
     */
    public void stop() throws IllegalStateException {
        if(!isPlaying()){
            throw new IllegalStateException("Cannot stop the percussion track if it is not being played (start playback first)");
        }
        mMidiProcessor.reset();
    }

    public String getLabel() {
        return mMidiEventHandler.getLabel();
    }
}
