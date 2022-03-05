package com.example.chirpnote.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.chirpnote.AudioTrack;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.R;
import com.example.chirpnote.WaveformView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class represents recording audio from the user's microphone. It will provide certain monitoring elements such as the current chord and waveform representataion of intensity of amplitude.
 */
public class RecordAudioActivity extends AppCompatActivity {

    //layout items
    private ImageButton recordButton;
    private ImageButton playRecordedAudioButton;
    private ImageButton stopRecordedAudioButton;
    // An audio track that is recorded with the device's microphone
    private AudioTrack audio;
    Context context = this;
    private boolean playing = false;
    //This is the minutes/second timer of the recording
    private Chronometer timer;
    //This is the custom view for the waveform generation
    private WaveformView waveformView;
    //This timer is a timer initializer for the recording functionality of a waveform
    Timer ticker = new Timer();




    /***
     * onCreate function sets values
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_record_audio);
        super.onCreate(savedInstanceState);
        timer = findViewById(R.id.recordTimer);
        recordButton = findViewById(R.id.recordAudioActivityButton);
        playRecordedAudioButton = findViewById(R.id.playRecordedAudioButton);
        playRecordedAudioButton.setEnabled(false);
        stopRecordedAudioButton = findViewById(R.id.stopRecordedAudioButton);
        stopRecordedAudioButton.setEnabled(false);
        waveformView = findViewById(R.id.waveformView);
        // Audio track
        String filePath = context.getFilesDir().getPath() + "/audioTrack.mp3";
        audio = new AudioTrack(filePath, playRecordedAudioButton);
        recordButton.setColorFilter(Color.parseColor("#777777"));




        // Event listener for record audio button (to record audio from the device's microphone)
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //purge the timer ticker at every onclick instance to avoid all multiplicative timer errors.
                ticker.cancel();
                ticker.purge();
                //refresh the chronometer timer to 0:00
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();

                playRecordedAudioButton.setEnabled(audio.isRecording());

                //Create the task at every onclick as it gets discarded after every recording and has to be recreated
                TimerTask scheduleDraws = new TimerTask() {
                    @Override
                    public void run() {
                        waveformView.insertAmplitude((float) (audio.getmMediaRecorder().getMaxAmplitude()));
                    }
                };

                if(!audio.isRecording()){
                    audio.startRecording();
                    recordButton.setColorFilter(Color.parseColor("#994444"));
                    //timer logic
                    ticker = new Timer();
                    ticker.schedule(scheduleDraws,0,50);


                } else {
                    stopRecordedAudioButton.setEnabled(true);
                    audio.stopRecording();
                    timer.stop();
                    recordButton.setColorFilter(Color.parseColor("#777777"));
                    //shut down the task so you can create onClick (Avoid taskAlreadyScheduled)
                    scheduleDraws.cancel();
                }
            }
        });

        /*
        OnClick Listener for the play button
        TODO chronometer needs to be stopped after audio is played.
         */
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
                    audio.getmMediaRecorder().pause();
                    timer.stop();
                    /*realTimeMelody.stop();
                    constructedMelody.stop();
                    audio.stop();*/
                }
                playing = !playing;
            }
        });

        stopRecordedAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //write to file
                try {
                    InputStream inputStream = new FileInputStream(filePath);
                    byte arr[] = readByte(inputStream);


                    //file output stream
                    File audioFile = new File(context.getFilesDir(), "SessionAudio.mp3");
                    FileOutputStream fileOutput = new FileOutputStream(audioFile);
                    fileOutput.write(arr);
                    fileOutput.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(RecordAudioActivity.this,"Audio saved to " + context.getFilesDir(),Toast.LENGTH_LONG).show();

            }
        });


    }

    public static byte[] readByte(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            os.write(buffer, 0, len);
        }

        return os.toByteArray();
    }
}
