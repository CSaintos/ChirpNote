package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.example.chirpnote.ConstructedMelody;
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.R;
import com.example.chirpnote.RealTimeMelody;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;

public class TestOtherActivity extends AppCompatActivity {
    // The driver that allows us to play MIDI notes
    private MidiDriver midiDriver;
    // A list of music notes to be played on the UI keyboard
    private ArrayList<MusicNote> pianoKeys;
    // A list of toggle buttons, used to select the duration of the note we want to add to the melody
    private ArrayList<ToggleButton> noteTypes;
    // The currently selected note type to add
    private int toggledNoteType = -1;
    // A melody that is recorded in real time by playing the keyboard
    RealTimeMelody realTimeMelody;
    // A melody that is recorded (constructed) by adding notes one at a time
    ConstructedMelody constructedMelody;
    // State of recording audio
    boolean recordingAudio = false;
    // State of playback
    boolean playing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_other);

        Button setKeyTestButton = (Button) findViewById(R.id.setKeyTestButton);
        // Touch this button to start adding notes to the constructed melody
        // Touch it again to end the process of adding notes to the melody
        Button addNotesButton = (Button) findViewById(R.id.addNotesButton);

        // Touch this button to start recording a melody by playing the piano keys (note buttons)
        // Touch it again to end the recording
        Button recordMelodyButton = (Button) findViewById(R.id.recordMelodyButton);

        // Touch this button to start recording audio
        // Touch it again to end the recording
        Button recordAudioButton = (Button) findViewById(R.id.recordAudioButton);

        // Touch this button to play all recorded tracks (realTimeMelody, constructedMelody, and audio)
        // Touch it again to stop the playback of all tracks
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setEnabled(false);

        Context context = this;

        // Real time melody
        String melodyFilePath = context.getFilesDir().getPath() + "/realTimeMelody.mid";
        realTimeMelody = new RealTimeMelody(120, melodyFilePath, playButton);

        // Constructed melody
        melodyFilePath = context.getFilesDir().getPath() + "/constructedMelody.mid";
        constructedMelody = new ConstructedMelody(120, melodyFilePath, playButton);

        // MIDI driver
        midiDriver = MidiDriver.getInstance();

        // Music notes
        pianoKeys = new ArrayList<>();
        // You can create a new MusicNote without a Melody, if you just want to test the keyboard playback stuff
        // Just omit the melody parameter: pianoKeys.add(new MusicNote(60, (Button) findViewById(R.id.noteCButton))
        pianoKeys.add(new MusicNote(60, (Button) findViewById(R.id.noteCButton), realTimeMelody));
        pianoKeys.add(new MusicNote(61, (Button) findViewById(R.id.noteCSharpButton), realTimeMelody));
        pianoKeys.add(new MusicNote(62, (Button) findViewById(R.id.noteDButton), realTimeMelody));
        pianoKeys.add(new MusicNote(63, (Button) findViewById(R.id.noteDSharpButton), realTimeMelody));
        pianoKeys.add(new MusicNote(64, (Button) findViewById(R.id.noteEButton), realTimeMelody));

        // Setup event listener for each piano key
        for(MusicNote note : pianoKeys){
            note.getButton().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        note.playNote(midiDriver);
                        if(constructedMelody.isRecording() && toggledNoteType > -1){
                            constructedMelody.addNote(note, (int) Math.pow(2, toggledNoteType));
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        note.stopNote(midiDriver);
                    }
                    return false;
                }
            });
        }

        // Button to go the free play keyboard activity (for testing)
        setKeyTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestOtherActivity.this, SetKeyActivity.class);
                startActivity(intent);
            }
        });
        // Event listener for record melody button (to record a melody with MIDI notes (the piano keys))
        // Note type/duration toggles
        noteTypes = new ArrayList<>();
        noteTypes.add((ToggleButton) findViewById(R.id.wholeToggleButton));
        noteTypes.add((ToggleButton) findViewById(R.id.halfToggleButton));
        noteTypes.add((ToggleButton) findViewById(R.id.quarterToggleButton));
        noteTypes.add((ToggleButton) findViewById(R.id.eighthToggleButton));
        for(ToggleButton tb : noteTypes){
            tb.setEnabled(false);
        }

        // Setup event listener for each note type toggle
        for(int i = 0; i < noteTypes.size(); i++){
            int current = i;
            noteTypes.get(i).setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        if(toggledNoteType > -1){
                            noteTypes.get(toggledNoteType).setChecked(false);
                        }
                        toggledNoteType = current;
                    } else {
                        toggledNoteType = -1;
                    }
                }
            });
        }

        // Event listener for add notes button (to construct a melody by adding notes one at a time)
        addNotesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                recordMelodyButton.setEnabled(constructedMelody.isRecording());
                recordAudioButton.setEnabled(constructedMelody.isRecording());
                playButton.setEnabled(constructedMelody.isRecording());
                if(!constructedMelody.isRecording()){
                    addNotesButton.setText("Stop Adding Notes");
                    constructedMelody.startRecording();
                } else {
                    addNotesButton.setText("Add Notes to Melody");
                    constructedMelody.stopRecording();
                }
                for(ToggleButton tb : noteTypes){
                    tb.setEnabled(constructedMelody.isRecording());
                }
            }
        });

        // Event listener for record melody button (to record a melody in real time with the piano keys)
        recordMelodyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addNotesButton.setEnabled(realTimeMelody.isRecording());
                recordAudioButton.setEnabled(realTimeMelody.isRecording());
                playButton.setEnabled(realTimeMelody.isRecording());
                if(!realTimeMelody.isRecording()){
                    recordMelodyButton.setText("End Recording");
                    realTimeMelody.startRecording();
                } else {
                    recordMelodyButton.setText("Record Melody");
                    realTimeMelody.stopRecording();
                }
            }
        });

        // Event listener for record audio button (to record audio from the device's microphone)
        recordAudioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addNotesButton.setEnabled(recordingAudio);
                recordMelodyButton.setEnabled(recordingAudio);
                playButton.setEnabled(recordingAudio);
                if(!recordingAudio){
                    recordAudioButton.setText("Does not work yet");
                } else {
                    recordAudioButton.setText("Record Audio");
                }
                recordingAudio = !recordingAudio;
            }
        });

        // Event listener for play button (to play recorded melody)
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!playing){
                    playButton.setText("Stop");
                    realTimeMelody.play();
                    constructedMelody.play();
                    // audio.play();
                } else {
                    playButton.setText("Play");
                    realTimeMelody.stop();
                    constructedMelody.stop();
                    // audio.stop();
                }
                playing = !playing;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }
}