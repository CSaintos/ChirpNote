package com.example.chirpnote.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.Key;
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.R;
import com.example.chirpnote.RealTimeMelody;
import com.example.chirpnote.ChirpNoteSession;

import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.util.ArrayList;
import java.util.List;

public class SmartKeyboardActivity extends AppCompatActivity {

    private MidiDriver midiDriver;
    private ArrayList<MusicNote> pianoKeys;
    RealTimeMelody melody;
    private Spinner keyTypeSpinner;
    private String keyNameChoice;
    private String keyTypeChoice;

    private Button changeKeyNameSpinner;

    private List<String> keyTypeList = new ArrayList<>();
    private List<String> keyNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_keyboard);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();

        ChirpNoteSession session = new ChirpNoteSession("Session1", new Key(Key.RootNote.D, Key.Type.HARMONIC_MINOR), 120);



        initializeKeyNameList(session);
        initializeKeyTypeList(session);

//        System.out.println(keyNameList.get(0));

        Button recordButton = (Button) findViewById(R.id.recordButton);
        Button playButton = (Button) findViewById(R.id.playButton);

        Context context = this;
        String melodyFilePath = context.getFilesDir().getPath() + "/melody.mid";


        // set user input key name and type to new key in session
        Spinner keyNameSpinner = findViewById(R.id.spinner_key_name);
        ArrayAdapter keyNameAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, keyNameList);
        keyNameSpinner.setAdapter(keyNameAdapter);
        keyNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                keyNameChoice = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getApplicationContext(), keyNameChoice, Toast.LENGTH_LONG).show();
//                Toast.makeText(SmartKeyboardActivity.this, keyNameChoice, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner keyTypeSpinner = findViewById(R.id.spinner_key_type);
        ArrayAdapter keyTypeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, keyTypeList);
        keyTypeSpinner.setAdapter(keyTypeAdapter);
        keyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                keyTypeChoice = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getApplicationContext(), keyTypeChoice, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


//        keyNameSpinner.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
////                View currentView = getLayoutInflater().inflate(R.layout.activity_smart_keyboard, null, false);
////                AppCompatSpinner keyTypeSpinner = (AppCompatSpinner)currentView.findViewById(R.id.spinner_key_type);
//            }
//        });



        /** Allows the user to switch between keys whenever they want */
        Button changeKeyButton = (Button) findViewById(R.id.changeKeyButton);
        changeKeyButton.setClickable(true);

        changeKeyButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (keyNameChoice.equals("Key Name") || keyTypeChoice.equals("Key Type"))
                {
                    Toast.makeText(getApplicationContext(), "Please make proper selection.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Key.RootNote newRootNote = Key.RootNote.returnRootNote(keyNameChoice);
                    Key.Type newKeyType = Key.Type.valueOf(keyTypeChoice.toUpperCase());

                    session.setKey(new Key(newRootNote, newKeyType));
                    initializeKeys(session);

                    Toast.makeText(getApplicationContext(), "New Key: " + keyNameChoice + " " + keyTypeChoice, Toast.LENGTH_LONG).show();
                }
                eventListener(pianoKeys);
            }
        });


        melody = new RealTimeMelody(120, melodyFilePath, playButton);

        midiDriver = MidiDriver.getInstance(); // MIDI driver to send MIDI events to
        pianoKeys = new ArrayList<>(); // List of MusicNotes
        int[] notes = session.getKey().getScaleNotes(); // Array of MIDI note numbers

        View[] keys = new View[]{findViewById(R.id.key1), findViewById(R.id.key2), findViewById(R.id.key3), findViewById(R.id.key4),
                findViewById(R.id.key5), findViewById(R.id.key6), findViewById(R.id.key7), findViewById(R.id.key8)};

        for(int i = 0; i < keys.length; i++){
            pianoKeys.add(new MusicNote(notes[i], (Button) keys[i], melody));
        }

        // Event listener for record button (to record melody)
        recordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!melody.isRecording()){
                    recordButton.setText("End recording");
                    melody.startRecording();
                } else {
                    recordButton.setText("Record");
                    melody.stopRecording();
                }
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

        // Setup event listener for each piano key
        for(MusicNote note : pianoKeys){
            note.getButton().setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        note.play();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        note.stop();
                    }
                    return true;
                }
            });
        }
    }

    private void eventListener(ArrayList<MusicNote> pianoKeys)
    {
        // Setup event listener for each piano key
        for(MusicNote note : pianoKeys){
            note.getButton().setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        note.play();
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        note.stop();
                    }
                    return true;
                }
            });
        }
    }

    private void initializeKeys(ChirpNoteSession session)
    {
        pianoKeys = new ArrayList<>(); // List of MusicNotes
        int[] notes = session.getKey().getScaleNotes(); // Array of MIDI note numbers

        View[] keys = new View[]{findViewById(R.id.key1), findViewById(R.id.key2), findViewById(R.id.key3), findViewById(R.id.key4),
                findViewById(R.id.key5), findViewById(R.id.key6), findViewById(R.id.key7), findViewById(R.id.key8)};

        for(int i = 0; i < keys.length; i++){
            pianoKeys.add(new MusicNote(notes[i], (Button) keys[i], melody));
        }
    }

    private void initializeKeyNameList(ChirpNoteSession session)
    {
        keyNameList.add("Key Name");
        for (int i = 0; i < Key.RootNote.values().length; i++)
        {
            //System.out.println("===============PASS===================");
            keyNameList.add(Key.RootNote.values()[i].toString());
            //System.out.println(temp.get(i));
        }
    }

    private void initializeKeyTypeList(ChirpNoteSession session)
    {
        keyTypeList.add("Key Type");
        keyTypeList.add("Major");
        keyTypeList.add("Minor");
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();
        midiDriver.setReverb(ReverbConstants.OFF);
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }
}
