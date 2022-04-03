package com.example.chirpnote.activities;

import android.Manifest;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.chirpnote.AudioTrack;
import com.example.chirpnote.Chord;
import com.example.chirpnote.ChordTrack;
import com.example.chirpnote.ConstructedMelody;
import com.example.chirpnote.Key;
import com.example.chirpnote.R;
import com.example.chirpnote.RealTimeMelody;
import com.example.chirpnote.Session;
import com.example.chirpnote.Track;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.util.ArrayList;
import java.util.List;

public class InsertChordsActivity extends AppCompatActivity implements View.OnClickListener
{
    private LinearLayout layoutList;
    private Button buttonAdd;
    private Button buttonSubmitList;
    private Chord[] measures;
    private ArrayList<Chord[]> listOfMeasures = new ArrayList<>();;
    private Button measure1;
    private Button measure2;
    private Button measure3;
    private Button measure4;
//    private Button[] sessionChords;
    private Chord[] sessionChords = new Chord[7];;


    // A chord track that is recorded (constructed) by adding chords one at a time
    private ChordTrack chordTrack;
    // Which track was most recently recorded
    private Track lastTrack;
    // The driver that allows us to play MIDI notes
    private MidiDriver midiDriver;
    // Used to request permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};
    // A melody that is recorded in real time by playing the keyboard
    private RealTimeMelody realTimeMelody;
    // A melody that is recorded (constructed) by adding notes one at a time
    private ConstructedMelody constructedMelody;
    // An audio track that is recorded with the device's microphone
    private AudioTrack audio;



    private ArrayList<Object> chords;
    private List<String> keyTypeList = new ArrayList<>();
    private List<String> keyNameList = new ArrayList<>();
    private Session session;
    private String keyNameChoice;
    private String keyTypeChoice;
    private Key currentKey;
    private int notificationCounter = 0;

//    List<String> keyTypeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_insert_chords);

        buttonAdd = findViewById(R.id.button_add_row);
        buttonAdd.setOnClickListener(this);

        layoutList = findViewById(R.id.layout_list); // the space where the rows will be added

        Button changeKeyButton = (Button) findViewById(R.id.changeKeyButton);
        changeKeyButton.setClickable(true);
        changeKeyButton.setOnClickListener(this);




        // Initialize MIDI driver
        midiDriver = MidiDriver.getInstance();

        // Request permission to record audio
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        String basePath = this.getFilesDir().getPath();
        session = new Session("Name", new Key(Key.RootNote.C, Key.Type.MAJOR), 120,
                basePath + "/chords.mid", basePath + "/cMelody.mid", basePath + "/rMelody.mid", basePath + "/audioTrack.mp3");
        initializeKeyNameList(session);
        initializeKeyTypeList(session);
        initializeSessionChords();
        initializeChordListeners(session);




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


    }

    @Override
    public void onClick(View v) {
//        addRowOfMeasures();
        switch (v.getId()) {
            case R.id.button_add_row:
                addRowOfMeasures();
                break;
            case R.id.changeKeyButton:
                changeKey(session);
                break;
        }
    }



    private void changeKey(Session session)
    {
        /** Allows the user to switch between keys whenever they want */
        if (keyNameChoice.equals("Key Name") || keyTypeChoice.equals("Key Type"))
        {
            Toast.makeText(getApplicationContext(), "Please make proper selection.", Toast.LENGTH_LONG).show();
        }
        else
        {
            Key.RootNote newRootNote = Key.RootNote.returnRootNote(keyNameChoice);
            Key.Type newKeyType = Key.Type.valueOf(keyTypeChoice.toUpperCase());

            session.setKey(new Key(newRootNote, newKeyType));
            initializeSessionChords();
            initializeChordListeners(session);

            for (int i = 0; i < sessionChords.length; i++)
            {
                sessionChords[i].getButton().setText(session.getKey().getRomanTypes()[i]);
            }

            Toast.makeText(getApplicationContext(), "New Key: " + keyNameChoice + " " + keyTypeChoice, Toast.LENGTH_SHORT).show();
        }
    }

    private void modifyMeasure(Button sessionChord)
    {
//        System.out.println("layoutList size = " + layoutList.getChildCount());

        if (layoutList.getChildCount() == 0)
        {
            if (notificationCounter < 1) {
                Toast.makeText(getApplicationContext(), "Please add row of measures first.", Toast.LENGTH_SHORT).show();
                notificationCounter += 1;
            }
        }
        else {
            if (notificationCounter < 2) {
                Toast.makeText(getApplicationContext(), "Select a measure.", Toast.LENGTH_SHORT).show();
                notificationCounter += 1;
            }
//            updateOnClickListenForMeasures(sessionChord);


//            System.out.println("current selected chord = " + sessionChord.getText());

            for (int row = 0; row < layoutList.getChildCount(); row++)
            {
                System.out.println("before second for loop row = " + row);
                for (int measure = 0; measure < measures.length; measure++)
                {

                    Chord currentMeasure = listOfMeasures.get(row)[measure];
                    int finalMeasure = measure;
                    int finalRow = row;
//                    System.out.println("before onClick row = " + finalRow);
                    currentMeasure.getButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println(" ");
                            System.out.println("row = " + finalRow);
                            System.out.println("measure = " + finalMeasure);
                            System.out.println("layoutList size = " + layoutList.getChildCount());
                            currentMeasure.getButton().setText(sessionChord.getText());
                        }
                    });
                }
            }
        }
    }

    private void addRowOfMeasures()
    {
        View rowOfMeasures = getLayoutInflater().inflate(R.layout.add_row, null, false);

        ImageView imageClose = (ImageView)rowOfMeasures.findViewById(R.id.row_remove);

        layoutList.addView(rowOfMeasures);

        int currentRowIndex = layoutList.getChildCount() - 1; // grabs the index of the recently added row, i.e. row 1 - 1 = row 0 => first row of measures

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRowOfMeasures(rowOfMeasures, currentRowIndex);
//                removeRowOfMeasures(rowOfMeasures);//, currentRowIndex);
            }
        });


        // adds 4 measures to a measures array which is then set to listOfMeasures so that each row of the listOfMeasures will correspond to a specific row which ideally would make it easier to add and remove later
        measures = new Chord[4];
        measures[0] = new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure1));
        measures[1] = new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure2));
        measures[2] = new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure3));
        measures[3] = new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure4));

        listOfMeasures.add(measures);

    }

    private void removeRowOfMeasures(View view, int rowIndex)
    {
        listOfMeasures.remove(rowIndex);
        layoutList.removeView(view);
    }

    public void initializeSessionChords()
    {
        currentKey = session.getKey(); // gets the key set when session was initialized
        Button [] romanButtons = getRomanButtons(session);
        Chord.Type[] chordTypes = currentKey.getChordTypes();
        for (int i = 0; i < currentKey.getScaleNotes().length - 1; i++)
        {
            // TODO: Think of a better way to do this
            int rootIdx = (currentKey.getScaleNotes()[i] - 60) % 12;
            /** arraylist of all chords that belong to the current key based on the type of chord
             * it takes in the root note of the chord and type of chord
             */
            sessionChords[i] = new Chord(Chord.RootNote.values()[rootIdx], chordTypes[i], session.getTempo(), romanButtons[i]);
        }
    }

    private ArrayList<Chord> returnValidMeasures() {
        ArrayList<Chord> list = new ArrayList<>();
        for (int row = 0; row < layoutList.getChildCount(); row++)
        {
            for (int measure = 0; measure < measures.length; measure++)
            {
                list.add(listOfMeasures.get(row)[measure]);
            }
        }

        return list;
    }

    private void updateOnClickListenForMeasures(Button sessionChord)
    {
        for (int row = 0; row < layoutList.getChildCount(); row++) {
            for (int measure = 0; measure < measures.length; measure++) {
                Chord currentMeasure = listOfMeasures.get(row)[measure];
                int finalMeasure = measure;
                int finalRow = row;
                currentMeasure.getButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println(" ");
                        System.out.println("row = " + finalRow);
                        System.out.println("measure = " + finalMeasure);
                        currentMeasure.getButton().setText(sessionChord.getText());
                    }
                });
            }
        }
    }


    private void initializeChordListeners(Session session)
    {
        chordTrack = new ChordTrack(session);
        chordTrack.startRecording();

        // Setup event listener for each chord button
        for(Chord chord : sessionChords){
            chord.getButton().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    modifyMeasure(chord.getButton());
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
    }

    private void initializeKeyNameList(Session session)
    {
        keyNameList.add("Key Name");
        for (int i = 0; i < Key.RootNote.values().length; i++)
        {
            keyNameList.add(Key.RootNote.values()[i].toString());
        }
    }

    private void initializeKeyTypeList(Session session)
    {
        keyTypeList.add("Key Type");
        keyTypeList.add("Major");
        keyTypeList.add("Minor");
    }

    private Button[] getRomanButtons(Session session)
    {
        Button[] list = new Button[7];
        list[0] = findViewById(R.id.roman1);
        list[1] = findViewById(R.id.roman2);
        list[2] = findViewById(R.id.roman3);
        list[3] = findViewById(R.id.roman4);
        list[4] = findViewById(R.id.roman5);
        list[5] = findViewById(R.id.roman6);
        list[6] = findViewById(R.id.roman7);

        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();
        midiDriver.setReverb(ReverbConstants.OFF);
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE, (byte) 0x07, (byte) 80});
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + 1, (byte) 0x07, (byte) 80});
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + 2, (byte) 0x07, (byte) 80});
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + 3, (byte) 0x07, (byte) 80});
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + 9, (byte) 0x07, (byte) 127});
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }


}
