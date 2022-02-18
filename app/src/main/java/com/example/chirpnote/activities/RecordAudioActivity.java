package com.example.chirpnote.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.example.chirpnote.AudioTrack;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.R;

public class RecordAudioActivity extends AppCompatActivity {

    //layout items
    private ImageButton recordButton;
    private ImageButton playRecordedAudioButton;
    // An audio track that is recorded with the device's microphone
    private AudioTrack audio;
    Context context = this;
    private boolean playing = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_record_audio);
        super.onCreate(savedInstanceState);
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
                playRecordedAudioButton.setEnabled(audio.isRecording());
                if(!audio.isRecording()){
                    audio.startRecording();
                } else {
                    audio.stopRecording();
                }
            }

        });

        playRecordedAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(!playing){
                if(!audio.isPlaying()){
                    audio.play();
                    /*realTimeMelody.play();
                    constructedMelody.play();
                    audio.play();*/
                } else {
                    audio.stop();
                    /*realTimeMelody.stop();
                    constructedMelody.stop();
                    audio.stop();*/
                }
                playing = !playing;
            }
        });

    }
}
