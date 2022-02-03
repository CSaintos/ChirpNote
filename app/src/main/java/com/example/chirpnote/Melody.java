package com.example.chirpnote;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.widget.Button;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class Melody {
    // States
    private boolean mRecording;
    private boolean mRecorded;
    private boolean mBuilding;
    private boolean mMelodyRewritten;

    // For recording the melody (in real time)
    public final int RESOLUTION = 960;
    private int mBPM;
    private MidiTrack mTempoTrack;
    private MidiTrack mNoteTrack;
    private File mOutput;
    private String mFilePath;
    private long mRecordingStartTime;

    // For building the melody (one note at a time)
    private HashSet<Integer> mNoteDurations;
    private int mPrevTick;

    // For playback
    private MediaPlayer mMediaPlayer;
    private Button mPlayButton;

    /**
     * A MIDI melody which the user can record on the UI keyboard
     * @param filePath The path to store the file (of the melody recording) at
     * @param playButton The button used to start playback of the melody
     */
    public Melody(int tempo, String filePath, Button playButton){
        mRecording = false;
        mRecorded = false;
        mBuilding = false;
        mMelodyRewritten = false;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                mPlayButton.setText("Play");
            }
        });
        mBPM = tempo;
        mFilePath = filePath;
        mPlayButton = playButton;

        // Possible note durations when building a melody
        mNoteDurations = new HashSet<>();
        mNoteDurations.add(1); // Whole note
        mNoteDurations.add(2); // Half note
        mNoteDurations.add(4); // Quarter note
        mNoteDurations.add(8); // Eighth note
        mNoteDurations.add(16); // Sixteenth note

        mPrevTick = 0;
    }

    /**
     * Gets whether or not the melody is currently being played back
     * @return True if the melody is being played
     */
    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

    /**
     * Gets whether or not the melody is currently being recorded
     * @return True if the melody is being recorded in real time
     */
    public boolean isRecording(){
        return mRecording;
    }

    /**
     * Gets whether or not this melody has been recorded
     * @return True if the melody was recorded already
     */
    public boolean isRecorded(){
        return mRecorded;
    }

    /**
     * Gets whether or not the melody is currently being built
     * @return True if the melody is being built by adding notes
     */
    public boolean isBuilding(){
        return mBuilding;
    }

    /**
     * Starts the recording process for this MIDI melody
     * @return False if melody is already being recorded (cannot start a new recording process until the current one is stopped)
     */
    public boolean startRecording(){
        if(mRecording || mBuilding || mMediaPlayer.isPlaying()){
            return false;
        }
        mRecording = true;
        mRecordingStartTime = System.currentTimeMillis();

        // Setup MIDI tracks
        mTempoTrack = new MidiTrack();
        mNoteTrack = new MidiTrack();

        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
        mTempoTrack.insertEvent(ts);

        // Tempo setup
        Tempo tempo = new Tempo();
        tempo.setBpm(mBPM);
        mTempoTrack.insertEvent(tempo);

        return true;
    }

    /**
     * Writes a note on event for the given note to this melody. Should be called when a note is stopped.
     * (Note: Do not call, unless you manually want to add a noteOn event.
     * This happens automatically during an active recording process, if the note has been constructed with a melody)
     * @param note The note to write a note on event for
     * @return False if melody is not being recorded yet (cannot add a note if there is no active recording process)
     */
    public boolean writeNoteOn(MusicNote note){
        if(!mRecording || mBuilding || note == null){
            return false;
        }
        mNoteTrack.insertEvent(new NoteOn(getCurrentTick(), 0, note.getNoteNumber(), note.VELOCITY));
        return true;
    }

    /**
     * Writes a note off event for the given note to this melody. Should be called when a note is stopped.
     * (Note: Do not call, unless you manually want to add a noteOff event.
     * This happens automatically during an active recording process, if the note has been constructed with a melody)
     * @param note The note to write a note off event for
     * @return False if melody is not being recorded, or if the given note is null
     */
    public boolean writeNoteOff(MusicNote note){
        if(!mRecording || mBuilding || note == null){
            return false;
        }
        mNoteTrack.insertEvent(new NoteOff(getCurrentTick(), 0, note.getNoteNumber(), note.VELOCITY));
        return true;
    }

    private long getCurrentTick(){
        return (System.currentTimeMillis() - mRecordingStartTime) * mBPM  * RESOLUTION / 60000;
    }

    /**
     * Stops the recording process for this MIDI melody
     * @return False if melody is not being recorded yet (cannot stop a recording process if it has not been started yet)
     */
    public boolean stopRecording(){
        if(!mRecording){
            return false;
        }
        ArrayList<MidiTrack> tracks = new ArrayList<>();
        tracks.add(mTempoTrack);
        tracks.add(mNoteTrack);

        // Write tracks to MIDI file
        MidiFile midi = new MidiFile(RESOLUTION, tracks);
        mOutput = new File(mFilePath);
        try {
            midi.writeToFile(mOutput);
        } catch(IOException e) {
            System.err.println(e);
        }
        mRecording = false;
        mRecorded = true;
        mMelodyRewritten = true;
        return true;
    }

    /**
     * Starts the building process for this MIDI melody
     * @return False if melody is already being built (cannot start a new building process until the current one is stopped)
     */
    public boolean startBuilding(){
        if(mBuilding || mRecording || mMediaPlayer.isPlaying()){
            return false;
        }
        mBuilding = true;

        // Setup MIDI tracks
        mTempoTrack = new MidiTrack();
        mNoteTrack = new MidiTrack();

        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
        mTempoTrack.insertEvent(ts);

        // Tempo setup
        Tempo tempo = new Tempo();
        tempo.setBpm(mBPM);
        mTempoTrack.insertEvent(tempo);

        return true;
    }

    /**
     * Adds a note to this melody with the given duration. Used to add notes
     * when "building" a melody, instead of playing it in real time
     * @param note The note to add
     * @param duration The note duration (1 = whole note, 2 = half, 4 = quarter, etc...)
     * @return False if not currently building a melody, or the note duration is invalid
     */
    public boolean addNote(MusicNote note, int duration){
        if(!mBuilding || mRecording || note == null || !mNoteDurations.contains(duration)){
            return false;
        }
        int noteDuration = RESOLUTION * 4 / duration;
        mNoteTrack.insertNote(0, note.getNoteNumber(), note.VELOCITY, mPrevTick, noteDuration);
        mPrevTick += noteDuration;
        return true;
    }

    /**
     * Stops the building process for this MIDI melody
     * @return False if melody is not being built yet (cannot stop a building process if it has not been started yet)
     */
    public boolean stopBuilding(){
        if(!mBuilding){
            return false;
        }
        ArrayList<MidiTrack> tracks = new ArrayList<>();
        tracks.add(mTempoTrack);
        tracks.add(mNoteTrack);

        // Write tracks to MIDI file
        MidiFile midi = new MidiFile(RESOLUTION, tracks);
        mOutput = new File(mFilePath);
        try {
            midi.writeToFile(mOutput);
        } catch(IOException e) {
            System.err.println(e);
        }
        mBuilding = false;
        mRecorded = true;
        mMelodyRewritten = true;
        mPrevTick = 0;
        return true;
    }

    /**
     * Plays back this melody
     * @return False if recording process active or currently playing the melody
     */
    public boolean play(){
        if(mRecording || !mRecorded || mMediaPlayer.isPlaying()){
            return false;
        }
        try {
            if(mMelodyRewritten){
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mFilePath);
                mMelodyRewritten = false;
            }
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Stops playback of this melody
     */
    public void stop(){
        mMediaPlayer.stop();
    }
}
