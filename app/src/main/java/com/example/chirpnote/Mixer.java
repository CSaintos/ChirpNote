package com.example.chirpnote;

import android.widget.Button;

import com.example.midiFileLib.src.MidiFile;
import com.example.midiFileLib.src.MidiTrack;
import com.example.midiFileLib.src.event.NoteOn;
import com.example.midiFileLib.src.event.meta.Tempo;
import com.example.midiFileLib.src.event.meta.TimeSignature;
import com.example.midiFileLib.src.util.MidiProcessor;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Mixer {
    private static HashMap<ChirpNoteSession, Mixer> mixerInstances = new HashMap<>();
    private ChirpNoteSession mSession;

    public ChordTrack chordTrack;
    public ConstructedMelody constructedMelody;
    public RealTimeMelody realTimeMelody;
    public AudioTrack audioTrack;
    public PercussionTrack percussionTrack;
    private int mPercussionTrack;

    // For writing the MIDI file
    private MidiTrack mTempoTrack;
    private MidiTrack mNoteTrack;

    // For playback
    private MidiDriver midiDriver = MidiDriver.getInstance();
    private MidiEventHandler mMidiEventHandler;
    private MidiProcessor mMidiProcessor;
    private Button mPlayButton;

    private Mixer(ChirpNoteSession session){
        mSession = session;
        mMidiEventHandler = new MidiEventHandler("MidiPlayback", mPlayButton);

        // Initialize tracks
        chordTrack = new ChordTrack(session);
        constructedMelody = new ConstructedMelody(session);
        realTimeMelody = new RealTimeMelody(session);
        audioTrack = new AudioTrack(session);
        percussionTrack = new PercussionTrack(session);

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
        } catch(IOException e) {
            System.err.println(e);
        }
        mSession.setMidiPrepared();
        syncSessionVolume();
    }

    /**
     * Gets Mixer used to manage all track volumes and playback in the given Session
     * @param session The session whose tracks this mixer will control
     * @param playButton The button in the activity used to start/stop playback
     * @return The mixer
     */
    public static Mixer getInstance(ChirpNoteSession session, Button playButton){
        if(mixerInstances.get(session) == null){
            mixerInstances.put(session, new Mixer(session));
        }
        Mixer mixer = mixerInstances.get(session);
        mixer.setPlayButton(playButton);
        return mixer;
    }

    /**
     * Gets if a mixer exists yet for this session
     * @param session The session to check
     * @return True if a mixer exists
     */
    public static boolean mixerExists(ChirpNoteSession session){
        return mixerInstances.get(session) != null;
    }

    /**
     * Sets the play button for this mixer
     * Use to set a new play button in the getInstance() method,
     * as we may be calling from a new activity with a new play button
     * @param playButton
     */
    private void setPlayButton(Button playButton){
        mPlayButton = playButton;
    }

    private void syncSessionVolume(){
        setChordVolume(mSession.mTrackVolumes.get(0));
        setConstructedMelodyVolume(mSession.mTrackVolumes.get(1));
        setRealTimeMelodyVolume(mSession.mTrackVolumes.get(2));
        setAudioVolume(mSession.mTrackVolumes.get(3));
        setPercussionVolume(mSession.mTrackVolumes.get(4));
    }

    public void setChordVolume(int volume){
        if(volume < 0 || volume > 127){
            return;
        }
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + ChordTrack.CHANNEL, (byte) 0x07, (byte) volume});
        mSession.mTrackVolumes.set(0, volume);
    }

    public void setConstructedMelodyVolume(int volume){
        if(volume < 0 || volume > 127){
            return;
        }
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + ConstructedMelody.CHANNEL, (byte) 0x07, (byte) volume});
        mSession.mTrackVolumes.set(1, volume);
    }

    public void setRealTimeMelodyVolume(int volume){
        if(volume < 0 || volume > 127){
            return;
        }
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + RealTimeMelody.CHANNEL, (byte) 0x07, (byte) volume});
        mSession.mTrackVolumes.set(2, volume);
    }

    public void setAudioVolume(int volume){
        // TODO: Implement a way to set the audio volume
        mSession.mTrackVolumes.set(3, volume);
    }

    public void setPercussionVolume(int volume){
        if(volume < 0 || volume > 127){
            return;
        }
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + PercussionTrack.CHANNEL, (byte) 0x07, (byte) volume});
        mSession.mTrackVolumes.set(4, volume);
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
