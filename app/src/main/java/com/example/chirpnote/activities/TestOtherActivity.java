package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.example.chirpnote.Melody;
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.R;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;

public class TestOtherActivity extends AppCompatActivity {

    private MidiDriver midiDriver;
    private ArrayList<MusicNote> pianoKeys;
    private ToggleButton[] noteTypes = new ToggleButton[4];
    private int toggledNoteType = 0;
    Melody melody;
    boolean recordingAudio = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_other);

        Button addNotesButton = (Button) findViewById(R.id.addNotesButton);
        Button recordMelodyButton = (Button) findViewById(R.id.recordMelodyButton);
        Button recordAudioButton = (Button) findViewById(R.id.recordAudioButton);
        Button playButton = (Button) findViewById(R.id.playButton);
        Context context = this;
        String melodyFilePath = context.getFilesDir().getPath() + "/melodyTest.mid";
        melody = new Melody(120, melodyFilePath, playButton);

        midiDriver = MidiDriver.getInstance(); // MIDI driver to send MIDI events to
        pianoKeys = new ArrayList<>(); // List of notes
        // You can also create a new MusicNote without a Melody if you just want to test the keyboard playback stuff
        // For example: pianoKeys.add(new MusicNote(59, (Button) findViewById(R.id.noteBButton))
        pianoKeys.add(new MusicNote(60, (Button) findViewById(R.id.noteCButton), melody));
        pianoKeys.add(new MusicNote(61, (Button) findViewById(R.id.noteCSharpButton), melody));
        pianoKeys.add(new MusicNote(62, (Button) findViewById(R.id.noteDButton), melody));
        pianoKeys.add(new MusicNote(63, (Button) findViewById(R.id.noteDSharpButton), melody));
        pianoKeys.add(new MusicNote(64, (Button) findViewById(R.id.noteEButton), melody));

        // Setup event listener for each piano key
        for(MusicNote note : pianoKeys){
            note.getButton().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        note.playNote(midiDriver);
                        melody.addNote(note, (int) Math.pow(2, toggledNoteType));
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        note.stopNote(midiDriver);
                    }
                    return false;
                }
            });
        }

        // Toggle buttons to select the type/duration of note you want to add to the melody
        noteTypes[0] = (ToggleButton) findViewById(R.id.wholeToggleButton);
        noteTypes[1] = (ToggleButton) findViewById(R.id.halfToggleButton);
        noteTypes[2] = (ToggleButton) findViewById(R.id.quarterToggleButton);
        noteTypes[3] = (ToggleButton) findViewById(R.id.eighthToggleButton);
        for(ToggleButton tb : noteTypes){
            tb.setEnabled(false);
        }

        // Setup event listener for each note type toggle
        for(int i = 0; i < noteTypes.length; i++){
            int current = i;
            noteTypes[i].setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        noteTypes[toggledNoteType].setChecked(false);
                        toggledNoteType = current;
                    }
                }
            });
        }

        // Event listener for add notes button (to build a melody by adding notes one at a time)
        addNotesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                recordMelodyButton.setEnabled(melody.isBuilding());
                recordAudioButton.setEnabled(melody.isBuilding());
                playButton.setEnabled(melody.isBuilding());
                if(!melody.isBuilding()){
                    addNotesButton.setText("Stop Adding Notes");
                    melody.startBuilding();
                } else {
                    addNotesButton.setText("Add Notes to Melody");
                    melody.stopBuilding();
                }
                for(ToggleButton tb : noteTypes){
                    tb.setEnabled(melody.isBuilding());
                }
            }
        });

        // Event listener for record melody button (to record a melody in real time with the piano keys)
        recordMelodyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addNotesButton.setEnabled(melody.isRecording());
                recordAudioButton.setEnabled(melody.isRecording());
                playButton.setEnabled(melody.isRecording());
                if(!melody.isRecording()){
                    recordMelodyButton.setText("End Recording");
                    melody.startRecording();
                } else {
                    recordMelodyButton.setText("Record Melody");
                    melody.stopRecording();
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
                    recordAudioButton.setText("End Recording");
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
                if(!melody.isPlaying()){
                    playButton.setText("Stop");
                    melody.play();
                } else {
                    playButton.setText("Play");
                    melody.stop();
                }
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