package com.example.chirpnote.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.example.chirpnote.AudioTrack;
import com.example.chirpnote.Chord;
import com.example.chirpnote.ConstructedMelody;
import com.example.chirpnote.ConstructedMelody.NoteDuration;
import com.example.chirpnote.Key;
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.Percussion;
import com.example.chirpnote.R;
import com.example.chirpnote.RealTimeMelody;
import com.example.chirpnote.Session;
import com.example.chirpnote.Track;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestOtherActivity extends AppCompatActivity {
    // The driver that allows us to play MIDI notes
    private MidiDriver midiDriver;
    // A list of music notes to be played on the UI keyboard
    private ArrayList<MusicNote> pianoKeys;
    // A map of NoteDurations -> ToggleButtons: toggles are used to select the duration of the note we want to add to the melody
    private HashMap<NoteDuration, ToggleButton> noteDurations;
    // The currently selected note duration to add
    private NoteDuration toggledDuration = null;
    // A melody that is recorded in real time by playing the keyboard
    private RealTimeMelody realTimeMelody;
    // A melody that is recorded (constructed) by adding notes one at a time
    private ConstructedMelody constructedMelody;
    // Flag for whether or not we're constructing a melody
    private boolean constructingMelody = false;
    // An audio track that is recorded with the device's microphone
    private AudioTrack audio;
    // Which track was most recently recorded
    private Track lastTrack;
    // State of playback
    private boolean playing = false;
    // A list of chords
    private ArrayList<Chord> chords;

    // Used to request permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted){
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_other);

        // Initialize MIDI driver
        midiDriver = MidiDriver.getInstance();

        // Request permission to record audio
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        // Touch this button to start adding notes to the constructed melody
        // Touch it again to end the process of adding notes to the melody
        Button addNotesButton = (Button) findViewById(R.id.testAddNotesButton);

        // Touch this button to add a rest (of the currently selected duration) to the constructed melody
        Button restButton = (Button) findViewById(R.id.testRestButton);
        restButton.setEnabled(false);

        // Touch this button to start recording a melody by playing the piano keys (note buttons)
        // Touch it again to end the recording
        Button recordMelodyButton = (Button) findViewById(R.id.testRecordMelodyButton);

        // Touch this button to start recording audio
        // Touch it again to end the recording
        Button recordAudioButton = (Button) findViewById(R.id.testRecordAudioButton);

        // Touch this button to play the most recently recorded track
        // Touch it again to stop the playback of the track
        Button playButton = (Button) findViewById(R.id.testPlayButton);
        playButton.setEnabled(false);

        String basePath = this.getFilesDir().getPath();
        Session session = new Session("Name", new Key(Key.RootNote.C, Key.Type.MAJOR), 120,
                basePath + "/melody.mid", basePath + "/audioTrack.mp3");

        // Real time melody
        String filePath = basePath + "/realTimeMelody.mid";
        realTimeMelody = new RealTimeMelody(120, filePath, playButton);

        // Constructed melody
        constructedMelody = new ConstructedMelody(session);
        constructedMelody.setPlayButton(playButton);

        // Audio track
        filePath = basePath + "/audioTrack.mp3";
        try {
            audio = new AudioTrack(filePath, playButton);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            note.getButton().setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        note.play();
                        if(constructingMelody && toggledDuration != null){
                            constructedMelody.addNote(note, toggledDuration, session.mNextMelodyTick);
                        }
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        note.stop();
                    }
                    return true;
                }
            });
        }

        // Note duration toggles
        noteDurations = new HashMap<>();
        noteDurations.put(NoteDuration.WHOLE_NOTE, (ToggleButton) findViewById(R.id.wholeToggleButton));
        noteDurations.put(NoteDuration.HALF_NOTE, (ToggleButton) findViewById(R.id.halfToggleButton));
        noteDurations.put(NoteDuration.QUARTER_NOTE, (ToggleButton) findViewById(R.id.quarterToggleButton));
        noteDurations.put(NoteDuration.EIGHTH_NOTE, (ToggleButton) findViewById(R.id.eighthToggleButton));
        for(ToggleButton toggle : noteDurations.values()){
            toggle.setEnabled(false);
        }

        // Setup event listener for each note duration toggle
        for(Map.Entry<NoteDuration, ToggleButton> entry : noteDurations.entrySet()){
            NoteDuration duration = entry.getKey();
            ToggleButton toggle = entry.getValue();
            toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        if(toggledDuration != null){
                            noteDurations.get(toggledDuration).setChecked(false);
                        }
                        toggledDuration = duration;
                        restButton.setEnabled(true);
                    } else {
                        toggledDuration = null;
                        restButton.setEnabled(false);
                    }
                }
            });
        }

        // Event listener for add notes button (to construct a melody by adding notes one at a time)
        addNotesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                recordMelodyButton.setEnabled(constructingMelody);
                recordAudioButton.setEnabled(constructingMelody);
                playButton.setEnabled(true);
                if(!constructingMelody){
                    addNotesButton.setText("Stop Adding Notes");
                    constructedMelody.startRecording();
                    lastTrack = constructedMelody;
                } else {
                    addNotesButton.setText("Construct Melody");
                    constructedMelody.stopRecording();
                }
                constructingMelody = !constructingMelody;
                for(ToggleButton toggle : noteDurations.values()){
                    toggle.setEnabled(constructingMelody);
                }
                restButton.setEnabled(constructingMelody);
            }
        });

        // Event listener for rest button (to add rests to the constructed melody)
        restButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggledDuration != null){
                    constructedMelody.addRest(toggledDuration, session.mNextMelodyTick);
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
                    lastTrack = realTimeMelody;
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
                addNotesButton.setEnabled(audio.isRecording());
                recordMelodyButton.setEnabled(audio.isRecording());
                playButton.setEnabled(audio.isRecording());
                if(!audio.isRecording()){
                    recordAudioButton.setText("End Recording");
                    audio.startRecording();
                    lastTrack = audio;
                } else {
                    recordAudioButton.setText("Record Audio");
                    audio.stopRecording();
                }
            }
        });

        // Event listener for play button (to play recorded melody)
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(!playing){
                if(!lastTrack.isPlaying()){
                    playButton.setText("Stop");
                    lastTrack.play();
                    /*realTimeMelody.play();
                    constructedMelody.play();
                    audio.play();*/
                } else {
                    playButton.setText("Play");
                    lastTrack.stop();
                    /*realTimeMelody.stop();
                    constructedMelody.stop();
                    audio.stop();*/
                }
                playing = !playing;
            }
        });

        // Button to go the set key from song activity (for testing)
        Button setKeyTestButton = (Button) findViewById(R.id.testSetKeyButton);
        setKeyTestButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestOtherActivity.this, SetKeyFromSongActivity.class);
                startActivity(intent);
            }
        });

        // Testing playback for chords
        chords = new ArrayList<>();
        chords.add(new Chord(Chord.RootNote.C, Chord.Type.MAJOR, (Button) findViewById(R.id.chordCButton)));
        chords.add(new Chord(Chord.RootNote.D, Chord.Type.MINOR, (Button) findViewById(R.id.chordDmButton)));
        /*Chord cMajor = new Chord(Chord.RootNote.C, Chord.Type.MAJOR, (Button) findViewById(R.id.chordCButton));
        cMajor.octaveUp();
        cMajor.setInversion(Chord.Inversion.FIRST);
        chords.add(cMajor);
        Chord dMinor = new Chord(Chord.RootNote.D, Chord.Type.MINOR, (Button) findViewById(R.id.chordDmButton));
        dMinor.octaveDown();
        dMinor.setInversion(Chord.Inversion.SECOND);
        chords.add(dMinor);*/

        // Setup event listener for each chord button
        for(Chord chord : chords){
            chord.getButton().setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        chord.play();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        chord.stop();
                    }
                    return true;
                }
            });
        }

        // Testing percussion playback
        Button rockMediaButton = (Button) findViewById(R.id.testRockMediaButton);
        rockMediaButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer rockPlayer = MediaPlayer.create(v.getContext(), R.raw.rock_drums);
                rockPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                rockPlayer.start();
            }
        });
        Percussion percussion = new Percussion(this);
        Button rockMidiButton = (Button) findViewById(R.id.testRockMidiButton);
        rockMidiButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                percussion.playRock();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();
        midiDriver.setReverb(ReverbConstants.OFF);
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + 9, (byte) 0x07, (byte) 127});
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE, (byte) 0x07, (byte) 90});
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }
}