package com.example.chirpnote;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.IOException;

public class AudioTrack implements Track {
    // States
    private boolean mRecording;
    private boolean mRecorded;

    // For recording the audio
    private MediaRecorder mMediaRecorder;
    private String mFilePath;

    // For playback
    private MediaPlayer mMediaPlayer;
    private Button mPlayButton;
    private ImageButton mPlayImageButton;

    private Session mSession;

    /**
     * An audio track
     * @param filePath The path to store the file (of the audio recording) at
     * @param playButton The button used to start playback of the audio track
     */
    public AudioTrack(String filePath, Button playButton){
        mRecording = false;
        mRecorded = false;

        mMediaRecorder = new MediaRecorder();
        mFilePath = filePath;

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
        mPlayButton = playButton;
    }

    public AudioTrack(String filePath, ImageButton playImageButton) {
        mRecording = false;
        mRecorded = false;

        mMediaRecorder = new MediaRecorder();
        mFilePath = filePath;

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
            }
        });
        mPlayImageButton = playImageButton;
    }

    /**
     * An audio track
     * @param session The session this audio track is a part of
     */
    public AudioTrack(Session session){
        mRecording = false;

        mMediaRecorder = new MediaRecorder();

        mSession = session;
        mFilePath = session.getAudioPath();

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
    }

    /**
     * Gets whether or not this audio track is currently being played back
     * @return True if the audio track is being played
     */
    @Override
    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }

    /**
     * Gets whether or not this audio track is currently being recorded
     * @return True if the audio track is being recorded
     */
    @Override
    public boolean isRecording(){
        return mRecording;
    }

    /**
     * Gets whether or not this audio track has been recorded
     * @return True if the audio track has been recorded
     */
    public boolean isRecorded(){
        return mSession == null ? mRecorded : mSession.isAudioRecorded();
    }

    /**
     * Starts the recording process for this audio track
     * @exception IllegalStateException if the recording process cannot be started
     */
    @Override
    public void startRecording() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot start the recording process when the audio track is already being recorded (stop recording first)");
        }
        if(isPlaying()){
            throw new IllegalStateException("Cannot start the recording process when the audio track is being played back (stop playback first)");
        }
        mRecording = true;
        mMediaRecorder.setAudioSource(AudioSource.MIC);
        mMediaRecorder.setOutputFormat(OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mFilePath);
        mMediaRecorder.setAudioEncoder(AudioEncoder.DEFAULT);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaRecorder.start();
    }

    /**
     * Stops the recording process for this audio track
     * @exception IllegalStateException if the recording process cannot be stopped
     */
    @Override
    public void stopRecording() throws IllegalStateException {
        if(!isRecording()){
            throw new IllegalStateException("Cannot stop the recording process if there is no active recording process (start recording first)");
        }
        mRecorded = true;
        mMediaRecorder.stop();
        mRecording = false;
        if(mSession != null) {
            mSession.setAudioRecorded();
        }
    }

    /**
     * Plays back this audio track
     * @exception IllegalStateException if the audio track cannot be played
     */
    @Override
    public void play() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot play the audio track when there is an active recording process (stop recording first)");
        }
        if(!isRecorded()){
            throw new IllegalStateException("Cannot play the audio track if it has not been recorded yet (record it first)");
        }
        if(isPlaying()){
            throw new IllegalStateException("Cannot play the audio track if it is already being played (stop playback first)");
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mFilePath);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter for mediaRecorder
     * @return mediaRecorder
     */
    public MediaRecorder getmMediaRecorder() {
        return mMediaRecorder;
    }

    /**
     * Stops playback of this audio track
     * @exception IllegalStateException if the audio track cannot be stopped
     */
    @Override
    public void stop() throws IllegalStateException {
        if(isRecording()){
            throw new IllegalStateException("Cannot stop the audio track when there is an active recording process (stop recording first)");
        }
        if(!isPlaying() ){
            throw new IllegalStateException("Cannot stop the audio track if it is not being played (start playback first)");
        }
        mMediaPlayer.stop();
    }

    /**
     * For testing
     */
    public void setPlayButton(Button button){
        mPlayButton = button;
    }
}
