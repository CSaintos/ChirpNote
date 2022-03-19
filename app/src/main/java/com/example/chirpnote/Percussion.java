package com.example.chirpnote;

import android.content.Context;

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

public class Percussion {
    private String mRockPath;

    /**
     * A Percussion object to play percussion tracks/beats
     * @param session The session to play percussion on
     * @param context The context from the activity (pass "this")
     */
    public Percussion(Session session, Context context){
        try{
            InputStream inputStream = context.getResources().openRawResource(R.raw.rock_drums);
            File rockTempFile = File.createTempFile("temp", "rock_drums.mid");
            mRockPath = rockTempFile.getPath();
            copyFile(inputStream, new FileOutputStream(rockTempFile));

            // Set tempo to session tempo
            MidiFile rockMidiFile = null;
            try {
                rockMidiFile = new MidiFile(rockTempFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Tempo tempo = new Tempo();
            tempo.setBpm(session.getTempo());
            rockMidiFile.getTracks().get(0).insertEvent(tempo);
            try {
                rockMidiFile.writeToFile(rockTempFile);
            } catch(IOException e) {
                System.err.println(e);
            }
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
     * @param bpm The new tempo (bpm)
     */
    public void setTempo(int bpm){
        MidiFile rockMidiFile = null;
        try {
            rockMidiFile = new MidiFile(new File(mRockPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        rockMidiFile.getTracks().get(0).removeFirstEvent();
        Tempo tempo = new Tempo();
        tempo.setBpm(bpm);
        rockMidiFile.getTracks().get(0).insertEvent(tempo);
        try {
            rockMidiFile.writeToFile(new File(mRockPath));
        } catch(IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Plays the rock percussion track
     */
    public void playRock(){
        MidiFile midiFile = null;
        try {
            midiFile = new MidiFile(new File(mRockPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MidiProcessor processor = new MidiProcessor(midiFile);
        MidiEventHandler m = new MidiEventHandler("RockPlayback");
        processor.registerEventListener(m, NoteOn.class);
        processor.registerEventListener(m, NoteOff.class);
        processor.start();
    }
}
