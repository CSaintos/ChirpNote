package com.example.chirpnote.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.chirpnote.AudioTrack;
import com.example.chirpnote.Chord;
import com.example.chirpnote.ChordTrack;
import com.example.chirpnote.ConstructedMelody;
import com.example.chirpnote.ConstructedMelody.NoteDuration;
import com.example.chirpnote.Key;
import com.example.chirpnote.Mixer;
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.PercussionPattern;
import com.example.chirpnote.PercussionTrack;
import com.example.chirpnote.R;
import com.example.chirpnote.RealTimeMelody;
import com.example.chirpnote.ChirpNoteSession;
import com.example.chirpnote.Track;

import org.billthefarmer.mididriver.GeneralMidiConstants;
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
    // A chord track that is recorded (constructed) by adding chords one at a time
    private ChordTrack chordTrack;
    // Which track was most recently recorded
    private Track lastTrack;
    // A container for all percussion patterns
    private static ArrayMap<String, ArrayList<PercussionPattern>> stylePatternMap;
    // A percussion track to store many patterns
    private PercussionTrack percussionTrack;
    // A list of chords
    private ArrayList<Chord> chords;
    // A mixer object; used to manage all tracks (volume, playback, etc...)
    private Mixer mixer;
    // A session that stores all data and settings
    private ChirpNoteSession session;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        session = new ChirpNoteSession("Name", new Key(Key.RootNote.C, Key.Type.MAJOR), 120,
                basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3", "username");
        mixer = new Mixer(session);

        // Real time melody
        /*realTimeMelody = new RealTimeMelody(session);
        realTimeMelody.setPlayButton(playButton);*/
        realTimeMelody = mixer.realTimeMelody;

        // Constructed melody
        /*constructedMelody = new ConstructedMelody(session);
        constructedMelody.setPlayButton(playButton);*/
        constructedMelody = mixer.constructedMelody;

        // Audio track
        try {
            audio = new AudioTrack(basePath + "/audioTrack.mp3", playButton);
        } catch (IOException e) {
            e.printStackTrace();
        }

        percussionTrack = mixer.percussionTrack;

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
                if(!mixer.areTracksPlaying()){
                    mixer.playTracks();
                    /*playButton.setText("Stop");
                    lastTrack.play();*/
                } else {
                    mixer.stopTracks();
                    /*playButton.setText("Play");
                    lastTrack.stop();*/
                }
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
        // Touch this button to quantize the melody by preset options
        Button quantizeButton = (Button) findViewById(R.id.testQuantizeButton);
        quantizeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(TestOtherActivity.this,v);
                popupMenu.getMenuInflater().inflate(R.menu.quantize_popup_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.quantize_quarter:
                                realTimeMelody.quantize((long) 960);
                                Toast.makeText(TestOtherActivity.this,"Quantized Melody 1/4ths", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.quantize_eigth:
                                realTimeMelody.quantize((long) 480);
                                Toast.makeText(TestOtherActivity.this,"Quantized Melody 1/8ths", Toast.LENGTH_SHORT).show();

                                return true;
                            case R.id.quantize_sixteenth:
                                realTimeMelody.quantize((long) 240);
                                Toast.makeText(TestOtherActivity.this,"Quantized Melody 1/16ths", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });

        // Testing playback for chords
        chords = new ArrayList<>();
        chords.add(new Chord(Chord.RootNote.C, Chord.Type.MAJOR, session.getTempo(), (Button) findViewById(R.id.chordCButton)));
        chords.add(new Chord(Chord.RootNote.D, Chord.Type.MINOR, session.getTempo(), (Button) findViewById(R.id.chordDmButton)));
        /*Chord cMajor = new Chord(Chord.RootNote.C, Chord.Type.MAJOR, session.getTempo(), (Button) findViewById(R.id.chordCButton));
        cMajor.octaveUp();
        cMajor.setInversion(Chord.Inversion.FIRST);
        cMajor.setAlteration(2);
        chords.add(cMajor);
        Chord dMinor = new Chord(Chord.RootNote.D, Chord.Type.MINOR, session.getTempo(), (Button) findViewById(R.id.chordDmButton));
        dMinor.octaveDown();
        dMinor.setInversion(Chord.Inversion.SECOND);
        dMinor.setAlteration(1);
        chords.add(dMinor);*/

        /*chordTrack = new ChordTrack(session);
        chordTrack.startRecording();*/
        chordTrack = mixer.chordTrack;

        // Setup event listener for each chord button
        for(Chord chord : chords){
            chord.getButton().setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        chord.play();
                        chordTrack.addChord(chord, session.mChords.size());
                        lastTrack = chordTrack;
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        chord.stop();
                    }
                    return true;
                }
            });
        }

        // Initialize stylePatternMap with tracks in assets folder
        stylePatternMap = new ArrayMap<>();
        try {
            String[] styles = this.getAssets().list("percussion");
            for (int i = 0; i < styles.length; i++) {
                stylePatternMap.put(styles[i], new ArrayList<>());
                String[] patterns = this.getAssets().list("percussion/" + styles[i]);
                for (int j = 0; j < patterns.length; j++) {
                    PercussionPattern.PatternAsset patternAsset = new PercussionPattern.PatternAsset(patterns[j], styles[i], j, i);
                    stylePatternMap.get(styles[i]).add(new PercussionPattern(patternAsset, session, this));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }

        // Testing percussion playback
        Button percussionButton1 = (Button) findViewById(R.id.testRockMediaButton);
        PercussionPattern percussion1 = stylePatternMap.get("pop").get(0);
        percussionButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!percussion1.isPlaying()) {
                    percussion1.play();
                    percussionButton1.setText("Stop");
                    percussionTrack.addPattern(percussion1, session.mPercussionPatterns.size());
                } else {
                    percussion1.stop();
                    percussionButton1.setText("Play");
                }
                //percussionTrack.addPattern(null, session.mPercussionPatterns.size());
            }
        });
        Button percussionButton2 = (Button) findViewById(R.id.testRockMidiButton);
        PercussionPattern percussion2 = stylePatternMap.get("rock").get(0);
        percussionTrack.startRecording();
        percussionButton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!percussion2.isPlaying()) {
                    percussion2.play();
                    percussionButton2.setText("Stop");
                    percussionTrack.addPattern(percussion2, session.mPercussionPatterns.size());
                } else {
                    percussion2.stop();
                    percussionButton2.setText("Play");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();
        mixer.syncWithSession();
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }
}