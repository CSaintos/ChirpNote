package com.example.chirpnote.activities;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import com.example.chirpnote.AudioTrack;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.R;
import com.example.chirpnote.WaveformView;

public class RecordAudioActivity extends AppCompatActivity {

    //layout items
    private ImageButton recordButton;
    private ImageButton playRecordedAudioButton;
    // An audio track that is recorded with the device's microphone
    private AudioTrack audio;
    Context context = this;
    private boolean playing = false;
    private Chronometer timer;
    private WaveformView waveformView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_record_audio);
        super.onCreate(savedInstanceState);
        timer = findViewById(R.id.recordTimer);
        recordButton = findViewById(R.id.recordAudioActivityButton);
        playRecordedAudioButton = findViewById(R.id.playRecordedAudioButton);
        playRecordedAudioButton.setEnabled(false);
        // Audio track
        String filePath = context.getFilesDir().getPath() + "/audioTrack.mp4";
        audio = new AudioTrack(filePath, playRecordedAudioButton);

        // Event listener for record audio button (to record audio from the device's microphone)
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
                playRecordedAudioButton.setEnabled(audio.isRecording());
                if(!audio.isRecording()){
                    audio.startRecording();

                    timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                        @Override
                        public void onChronometerTick(Chronometer chronometer) {
                            //TODO Visualizer Fix
                            //waveformView.insertAmplitude((byte) (audio.getmMediaRecorder().getMaxAmplitude()));
                        }
                    });


                } else {
                    audio.stopRecording();
                    timer.stop();
                }
            }
        });

        playRecordedAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //if(!playing){
                if(!audio.isPlaying()){
                    timer.setBase(SystemClock.elapsedRealtime());
                    timer.stop();
                    timer.start();
                    audio.play();
                    /*realTimeMelody.play();
                    constructedMelody.play();
                    audio.play();*/
                } else {
                    audio.stop();
                    timer.stop();
                    /*realTimeMelody.stop();
                    constructedMelody.stop();
                    audio.stop();*/
                }
                playing = !playing;
            }
        });

    }
}
