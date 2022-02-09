package com.example.chirpnote;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.widget.Button;

import java.io.IOException;

public class AudioTrack implements Track {
    // States
    private boolean mRecording;
    private boolean mRecorded;
    private boolean mAudioRewritten;

    // For recording the audio
    private MediaRecorder mMediaRecorder;
    private String mFilePath;

    // For playback
    private MediaPlayer mMediaPlayer;
    private Button mPlayButton;

    /**
     * An audio track
     * @param filePath The path to store the file (of the audio recording) at
     * @param playButton The button used to start playback of the audio track
     */
    public AudioTrack(String filePath, Button playButton){
        mRecording = false;
        mRecorded = false;
        mAudioRewritten = false;

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
     * Starts the recording process for this audio track
     * @return False if the audio track is already being recorded
     */
    @Override
    public boolean startRecording(){
        if(mRecording || mMediaPlayer.isPlaying()){
            return false;
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
        return true;
    }

    /**
     * Stops the recording process for this audio track
     * @return False if the audio track is not being recorded yet
     */
    @Override
    public boolean stopRecording(){
        if(!mRecording){
            return false;
        }
        mMediaRecorder.stop();
        mRecording = false;
        mRecorded = true;
        mAudioRewritten = true;
        return true;
    }

    /**
     * Plays back this audio track
     * @return False if recording process active or currently playing the audio track
     */
    @Override
    public boolean play() {
        if(mRecording || !mRecorded || mMediaPlayer.isPlaying()){
            return false;
        }
        try {
            if(mAudioRewritten){
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(mFilePath);
                mAudioRewritten = false;
            }
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Stops playback of this audio track
     * @return False if not currently playing the audio track
     */
    @Override
    public boolean stop() {
        if(!mMediaPlayer.isPlaying() || mRecording){
            return false;
        }
        mMediaPlayer.stop();
        return false;
    }
}
