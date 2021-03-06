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
    public static class PatternAsset {
        public String patternStr;
        public String styleStr;
        public int patIndex;
        public int styIndex;

        public PatternAsset(String patternStr, String styleStr) {
            this.patternStr = patternStr;
            this.styleStr = styleStr;
        }

        public PatternAsset(String patternStr, String styleStr, int patIndex, int styIndex) {
            this(patternStr, styleStr);
            this.patIndex = patIndex;
            this.styIndex = styIndex;
        }

        public String getPath() {
            return "percussion/" + styleStr + "/" + patternStr;
        }
    }

    private PatternAsset patternAsset;
    private String mFilePath;

    // For playback
    private MidiProcessor mMidiProcessor;
    private MidiEventHandler mMidiEventHandler;

    /**
     * A Percussion pattern
     * @param patternAsset pattern asset information
     * @param session The session to play this Percussion pattern on
     * @param context The context from the activity (pass "this")
     */
    public PercussionPattern(PatternAsset patternAsset, ChirpNoteSession session, Context context) {
        mMidiEventHandler = new MidiEventHandler(patternAsset.patternStr);
        this.patternAsset = patternAsset;

        try {
            // get file
            AssetFileDescriptor afd = context.getAssets().openFd(patternAsset.getPath());
            InputStream inputStream = afd.createInputStream();
            File tempFile = File.createTempFile("temp", "file");
            mFilePath = tempFile.getPath();
            copyFile(inputStream, new FileOutputStream(tempFile));

            inputStream.close();
            setTempo(session.getTempo());
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
     * Sets the tempo for this percussion pattern
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
     * Gets the MIDI file container for this percussion pattern
     * @return The MIDI file of this pattern
     */
    public MidiFile getMidiFile(){
        MidiFile midiFile = null;
        File output = new File(mFilePath);
        try {
            midiFile = new MidiFile(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return midiFile;
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

    public PatternAsset getPatternAsset() {
        return patternAsset;
    }
}
