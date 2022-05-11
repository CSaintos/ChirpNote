package com.example.chirpnote;

import com.example.midiFileLib.src.MidiFile;
import com.example.midiFileLib.src.MidiTrack;
import com.example.midiFileLib.src.event.NoteOn;
import com.example.midiFileLib.src.event.meta.Tempo;
import com.example.midiFileLib.src.event.meta.TimeSignature;
import com.example.midiFileLib.src.util.MidiProcessor;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Mixer {
    private ChirpNoteSession mSession;

    public ChordTrack chordTrack;
    public ConstructedMelody constructedMelody;
    public RealTimeMelody realTimeMelody;
    public AudioTrack audioTrack;
    public PercussionTrack percussionTrack;

    // For writing the MIDI file
    private MidiTrack mTempoTrack;
    private MidiTrack mNoteTrack;

    // For playback
    private MidiDriver midiDriver = MidiDriver.getInstance();
    private MidiEventHandler mMidiEventHandler;
    private MidiProcessor mMidiProcessor;

    /**
     * A Mixer used to manage volume and playback of tracks in a session
     * @param session The session whose tracks this mixer will manage
     */
    public Mixer(ChirpNoteSession session){
        mSession = session;
        mMidiEventHandler = new MidiEventHandler("MidiPlayback");

        // Initialize tracks
        chordTrack = new ChordTrack(session);
        constructedMelody = new ConstructedMelody(session);
        realTimeMelody = new RealTimeMelody(session);
        audioTrack = new AudioTrack(session);
        percussionTrack = new PercussionTrack(session);

        if(!mSession.isMidiPrepared()) {
            // Prepare MIDI file for MIDI tracks
            mTempoTrack = new MidiTrack();
            mNoteTrack = new MidiTrack();

            // Time signature setup
            TimeSignature ts = new TimeSignature();
            ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
            mTempoTrack.insertEvent(ts);

            // Tempo setup
            Tempo tempo = new Tempo();
            tempo.setBpm(session.getTempo());
            mTempoTrack.insertEvent(tempo);

            ArrayList<MidiTrack> tracks = new ArrayList<>();
            tracks.add(mTempoTrack);
            tracks.add(mNoteTrack);

            // Write tracks to MIDI file
            MidiFile midiFile = new MidiFile(session.RESOLUTION, tracks);
            try {
                midiFile.writeToFile(new File(session.getMidiPath()));
            } catch (IOException e) {
                System.err.println(e);
            }
            mSession.setMidiPrepared();
        }
    }

    /**
     * Syncs the Mixer's volumes with the saved volumes in the session
     */
    public void syncWithSession(){
        midiDriver.setReverb(ReverbConstants.OFF);
        syncSessionVolume();
        syncSessionInstruments();
    }

    private void syncSessionVolume(){
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + ChordTrack.CHANNEL, (byte) 0x07, (byte) (int) mSession.mTrackVolumes.get(0)});
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + ConstructedMelody.CHANNEL, (byte) 0x07, (byte) (int) mSession.mTrackVolumes.get(1)});
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + RealTimeMelody.CHANNEL, (byte) 0x07, (byte) (int) mSession.mTrackVolumes.get(2)});
        audioTrack.getMediaPlayer().setVolume(mSession.mTrackVolumes.get(3), mSession.mTrackVolumes.get(3));
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + PercussionTrack.CHANNEL, (byte) 0x07, (byte) (int) mSession.mTrackVolumes.get(4)});
    }

    private void syncSessionInstruments(){
        setChordInstrument(mSession.mInstruments.get(0));
        setConstructedMelodyInstrument(mSession.mInstruments.get(1));
        setRealTimeMelodyInstrument(mSession.mInstruments.get(2));
    }

    public void setChordInstrument(byte instrument){
        midiDriver.write(new byte[]{MidiConstants.PROGRAM_CHANGE + ChordTrack.CHANNEL, instrument});
        mSession.mInstruments.set(0, instrument);
    }

    public void setConstructedMelodyInstrument(byte instrument){
        midiDriver.write(new byte[]{MidiConstants.PROGRAM_CHANGE + ConstructedMelody.CHANNEL, instrument});
        mSession.mInstruments.set(1, instrument);
    }

    public void setRealTimeMelodyInstrument(byte instrument){
        midiDriver.write(new byte[]{MidiConstants.PROGRAM_CHANGE + RealTimeMelody.CHANNEL, instrument});
        mSession.mInstruments.set(2, instrument);
    }

    /**
     * Gets the volume of the ChordTrack
     * @return The chord volume
     */
    public float getChordVolume(){
        float volume = mSession.mTrackVolumes.get(0);
        return (float) Math.ceil((volume * 100) / 80);
    }

    /**
     * Gets the volume of the ConstructedMelody
     * @return The constructed melody volume
     */
    public float getConstructedMelodyVolume(){
        float volume = mSession.mTrackVolumes.get(1);
        return (float) Math.ceil((volume * 100) / 80);
    }

    /**
     * Gets the volume of the RealTimeMelody
     * @return The real time melody volume
     */
    public float getRealTimeMelodyVolume(){
        float volume = mSession.mTrackVolumes.get(2);
        return (float) Math.ceil((volume * 100) / 80);
    }

    /**
     * Gets the volume of the AudioTrack
     * @return The audio volume
     */
    public float getAudioVolume(){
        return mSession.mTrackVolumes.get(3);
    }

    /**
     * Gets the volume of the PercussionTrack
     * @return The percussion volume
     */
    public float getPercussionVolume(){
        float volume = mSession.mTrackVolumes.get(4);
        return (float) Math.ceil((volume * 100) / 127);
    }

    /**
     * Sets the volume of the ChordTrack
     * @param volume A volume value between 0 and 100
     */
    public void setChordVolume(float volume){
        if(volume < 0 || volume > 100){
            return;
        }
        int adjustedVolume = (int) ((volume * 80) / 100);
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + ChordTrack.CHANNEL, (byte) 0x07, (byte) adjustedVolume});
        mSession.mTrackVolumes.set(0, adjustedVolume);
    }

    /**
     * Sets the volume of the ConstructedMelody
     * @param volume A volume value between 0 and 100
     */
    public void setConstructedMelodyVolume(float volume){
        if(volume < 0 || volume > 100){
            return;
        }
        int adjustedVolume = (int) ((volume * 80) / 100);
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + ConstructedMelody.CHANNEL, (byte) 0x07, (byte) adjustedVolume});
        mSession.mTrackVolumes.set(1, adjustedVolume);
    }

    /**
     * Sets the volume of the RealTimeMelody
     * @param volume A volume value between 0 and 100
     */
    public void setRealTimeMelodyVolume(float volume){
        if(volume < 0 || volume > 100){
            return;
        }
        int adjustedVolume = (int) ((volume * 80) / 100);
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + RealTimeMelody.CHANNEL, (byte) 0x07, (byte) adjustedVolume});
        mSession.mTrackVolumes.set(2, adjustedVolume);
    }

    /**
     * Sets the volume of the AudioTrack
     * @param volume A volume value between 0 and 100
     */
    public void setAudioVolume(float volume){
        if(volume < 0 || volume > 100){
            return;
        }
        audioTrack.getMediaPlayer().setVolume(volume/100, volume/100);
        mSession.mTrackVolumes.set(3, (int) volume);
    }

    /**
     * Sets the volume of the PercussionTrack
     * @param volume A volume value between 0 and 100
     */
    public void setPercussionVolume(float volume){
        if(volume < 0 || volume > 100){
            return;
        }
        int adjustedVolume = (int) ((volume * 127) / 100);
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + PercussionTrack.CHANNEL, (byte) 0x07, (byte) adjustedVolume});
        mSession.mTrackVolumes.set(4, adjustedVolume);
    }

    /**
     * Gets whether or not the tracks attached to this mixer are currently being played back
     * @return True if the tracks are playing
     */
    public boolean areTracksPlaying(){
        return (mMidiProcessor != null && mMidiProcessor.isRunning()) || audioTrack.isPlaying();
    }

    /**
     * Plays back the tracks attached to this mixer
     * @exception IllegalStateException if the tracks cannot be played
     */
    public void playTracks(){
        if(areTracksPlaying()){
            throw new IllegalStateException("Cannot play tracks if they are already being played (stop playback first)");
        }
        MidiFile midiFile = null;
        try {
            midiFile = new MidiFile(new File(mSession.getMidiPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMidiProcessor = new MidiProcessor(midiFile);
        mMidiProcessor.registerEventListener(mMidiEventHandler, NoteOn.class);
        mMidiProcessor.start();
        if(audioTrack.isRecorded()) {
            audioTrack.play();
        }
    }

    /**
     * Stops playback of this mixer's tracks
     * @exception IllegalStateException if the tracks cannot be stopped
     */
    public void stopTracks(){
        if(!areTracksPlaying()){
            throw new IllegalStateException("Cannot stop tracks if they are not being played (start playback first)");
        }
        mMidiProcessor.reset();
        if(audioTrack.isPlaying()) {
            audioTrack.stop();
        }
    }
}
