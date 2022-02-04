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

abstract class Melody {
    // States
    private boolean mRecording;
    private boolean mRecorded;
    private boolean mMelodyRewritten;

    // For recording the melody
    public final int RESOLUTION = 960;
    private int mBPM;
    private MidiTrack mTempoTrack;
    protected MidiTrack mNoteTrack;
    private File mOutput;
    private String mFilePath;
    private long mRecordingStartTime;

    // For playback
    private MediaPlayer mMediaPlayer;
    private Button mPlayButton;

    /**
     * A MIDI melody
     * @param tempo The tempo of the melody
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
    }

    /**
     * Gets whether or not this melody is currently being played back
     * @return True if the melody is being played
     */
    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

    /**
     * Gets whether or not this melody is currently being recorded
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
     * Gets the time at which the recording was started
     * @return The recording start time in milliseconds
     */
    protected long getRecordingStart(){
        return mRecordingStartTime;
    }

    /**
     * Gets the tempo of this melody
     * @return The BPM of this melody
     */
    public int getTempo(){
        return mBPM;
    }

    /**
     * Starts the recording process for this MIDI melody
     * @return False if melody is already being recorded (cannot start a new recording process until the current one is stopped)
     */
    public boolean startRecording(){
        if(mRecording || mMediaPlayer.isPlaying()){
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
     * @return False if not currently playing the melody
     */
    public boolean stop(){
        if(!mMediaPlayer.isPlaying() || mRecording){
            return false;
        }
        mMediaPlayer.stop();
        return true;
    }
}
