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

public class Melody {
    // States
    private boolean mRecording;
    private boolean mRecorded;
    private boolean mMelodyRewritten;

    // For recording
    public final int RESOLUTION = 960;
    private int mBPM;
    private MidiTrack mTempoTrack;
    private MidiTrack mNoteTrack;
    private File mOutput;
    private String mFilePath;
    private long mRecordingStartTime;

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
        mPlayButton.setEnabled(false);
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
     * @return True if the melody is being played
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
     * Starts the recording process for this MIDI melody
     * @return False if melody is already being recorded (cannot start a new recording process until the current one is stopped)
     */
    public boolean startRecording(){
        if(mRecording){
            return false;
        }
        mRecording = true;
        mPlayButton.setEnabled(false);
        mRecordingStartTime = System.currentTimeMillis();

        // Setup MIDI tracks
        mTempoTrack = new MidiTrack();
        mNoteTrack = new MidiTrack();

        // Time signature setup (Hard coding 4/4...should be obtained from session settings)
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
     * Adds a note to this melody
     * (Will write this method later, but will be used for when we "record" a melody by just letting the user select notes to add)
     * @param note The note to add
     * @return False because this method does nothing yet
     */
    public boolean addNote(MusicNote note){
        return false;
    }

    /**
     * Writes a note on event for the given note to this melody. Should be called when a note is stopped.
     * (Note: Do not call, unless you manually want to add a noteOn event.
     * This happens automatically during an active recording process, if the note has been constructed with a melody)
     * @param note The note to write a note on event for
     * @return False if melody is not being recorded yet (cannot add a note if there is no active recording process)
     */
    public boolean writeNoteOn(MusicNote note){
        if(!mRecording || note == null){
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
        if(!mRecording || note == null){
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
        try
        {
            midi.writeToFile(mOutput);
        }
        catch(IOException e)
        {
            System.err.println(e);
        }
        mRecording = false;
        mRecorded = true;
        mMelodyRewritten = true;
        mPlayButton.setEnabled(true);
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
