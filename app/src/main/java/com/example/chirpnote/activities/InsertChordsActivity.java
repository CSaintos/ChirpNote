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
    private LinearLayout layoutList; // holds all the rows of buttons that are added to it
    private Button buttonAdd;
    private Button buttonSubmitList;

    private Chord[] measures;
//    private ArrayList<Chord> measures;
    private ArrayList<Chord[]> listOfMeasures = new ArrayList<>(); // each element of the arraylist is an array of measures
//    private ArrayList<ArrayList<Chord>> listOfMeasures = new ArrayList<>();

//    private Button[] sessionChords;
    private Chord[] sessionChords = new Chord[7]; // holds the 7 diatonic chords for the session
    private Chord currentChord; // gets updated when user wnats to add a chord to a measure


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
    private ArrayList<Chord> suggestedChords = new ArrayList<>();

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


//        Chord inputChord = keyChords.get(3); // arbitrary chord choice to test chord suggestion
//        suggestedChords = getSuggestedChords(inputChord, keyChords);
//
//
//        Button chordSuggestion = (Button) findViewById(R.id.chordSuggestionButton);
//        chordSuggestion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO: FIND A WAY TO GET THE LAST CHORD OF THE SESSION
//                if (listOfMeasures.size() > 0) {
//                    int lastRowIndex = listOfMeasures.size() - 1;
//                }
//                else
//                {
//                    Toast.makeText(getApplicationContext(), "Please add row of measures first.", Toast.LENGTH_SHORT).show();
//                }
//
//
//                String listOfSuggestedChords = "";
//                ((TextView) findViewById(R.id.inputChord)).setText("Input Chord: " + inputChord);
//                for (int i = 0; i < suggestedChords.size(); i++)
//                {
//                    listOfSuggestedChords = listOfSuggestedChords + suggestedChords.get(i) + " ";
//                }
//                ((TextView) findViewById(R.id.chordSuggestion_list)).setText("Suggested Chords: " + listOfSuggestedChords);
//
//
//            }
//        });
//        // end of Sam's section


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

    private void modifyMeasure(Chord sessionChord)
    {
//        System.out.println("layoutList size = " + layoutList.getChildCount());
        currentChord = new Chord();
        currentChord = new Chord(sessionChord);
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
            System.out.println("outside for-loop sessionChord = " + currentChord);
            for (int row = 0; row < layoutList.getChildCount(); row++)
            {
//                System.out.println("before second for loop row = " + row);
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


//                            currentMeasure.getButton().setText(sessionChord.getButton().getText());


                            // start
                            System.out.println("before setting text sessionChord = " + currentChord);
                            currentMeasure.getButton().setText(currentChord.getButton().getText());
                            System.out.println("sessionChord = " + currentChord);
                            System.out.println("currentMeasure = " + currentChord);

                            Chord[] currentRowMeasure = listOfMeasures.get(finalRow);
                            currentRowMeasure[finalMeasure] = new Chord(currentChord); // need to make a copy constructor in chord

                            listOfMeasures.set(finalRow, currentRowMeasure);

//                            System.out.println("sessionChord = " + sessionChord);
//                            System.out.println("currentRowMeasure[finalMeasure] = " + currentRowMeasure[finalMeasure]);
                            // end same as bellow but using currentChord




//                            // start
//                            System.out.println("before setting text sessionChord = " + sessionChord);
//                            currentMeasure.getButton().setText(sessionChord.getButton().getText());
//                            System.out.println("sessionChord = " + sessionChord);
//                            System.out.println("currentMeasure = " + currentMeasure);
//
//                            Chord[] currentRowMeasure = listOfMeasures.get(finalRow);
//                            currentRowMeasure[finalMeasure] = new Chord(sessionChord); // need to make a copy constructor in chord
//
//                            listOfMeasures.set(finalRow, currentRowMeasure);
//
////                            System.out.println("sessionChord = " + sessionChord);
////                            System.out.println("currentRowMeasure[finalMeasure] = " + currentRowMeasure[finalMeasure]);
//                            // end

                        }
                    });
                    System.out.println("row = " + row + ", measure " + measure + " = " + listOfMeasures.get(row)[measure]);

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

//        // adds 4 measures to a measures array which is then set to listOfMeasures so that each row of the listOfMeasures will correspond to a specific row which ideally would make it easier to add and remove later
//        measures = new ArrayList<>();
//        measures.set(0, new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure1)));
//        measures.set(1, new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure2)));
//        measures.set(2, new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure3)));
//        measures.set(3, new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure4)));

        listOfMeasures.add(measures); // HERE IS WHERE I FINISHED

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
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        modifyMeasure(chord);
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

    /**
     * This function takes in the latest chord chosen by the user in the session and the set of
     * all the chords found in the current key of session and returns a list of suggested chords
     * back to the user to help them make a choice
     *
     * @param inputChord Latest chord in session
     * @param keyChords List of all the chords found in the key
     * @return List of suggested chords
     */
    private ArrayList<Chord> getSuggestedChords(Chord inputChord, ArrayList<Chord> keyChords)
    {
        ArrayList<Chord> listOfChords = new ArrayList<Chord>();
        int indexOfInputChord = keyChords.indexOf(inputChord); // grabs the index of chord to determine what roman numeral it is in

        /** // used to test, delete later
         System.out.println("======================LOOK HERE===========================");
         int length = keyChords.size();
         System.out.println("length of keychords = " + length);
         System.out.println("root of keyChords.get(0) = " + keyChords.get(0).getRoot());
         System.out.println("root of keyChords.get(1) = " + keyChords.get(1).getRoot());
         Chord tempChord = keyChords.get(0);
         System.out.println("root of tempChord.get(0) = " + tempChord);
         */

        // it doesn't matter if its major or minor since they share most of the same chord suggestions with the exception of VII which we're not using
        // if statements used to collect all the chord suggestions that pertain to the chord found at index of input chord
        if (indexOfInputChord == 0 || indexOfInputChord == 7) // I chord
        {
            for (int i = 1; i < 7; i++)
            {
                listOfChords.add(keyChords.get(i)); // a I chord can go virtually anywhere
            }
        }
        else if (indexOfInputChord == 1) // ii Chord
        {
            listOfChords.add(keyChords.get(6));
            listOfChords.add(keyChords.get(4));
        }
        else if (indexOfInputChord == 2) // iii Chord
        {
            listOfChords.add(keyChords.get(3));
            listOfChords.add(keyChords.get(5));
        }
        else if (indexOfInputChord == 3) // IV Chord
        {
            listOfChords.add(keyChords.get(1));
            listOfChords.add(keyChords.get(4));
            listOfChords.add(keyChords.get(6));
        }
        else if (indexOfInputChord == 4) // V chord
        {
            listOfChords.add(keyChords.get(1));
            listOfChords.add(keyChords.get(5));
            listOfChords.add(keyChords.get(7));
        }
        else if (indexOfInputChord == 5) // vi chord
        {
            listOfChords.add(keyChords.get(3));
            listOfChords.add(keyChords.get(1));
        }
        else // vii* chord
        {
            listOfChords.add(keyChords.get(1));
            listOfChords.add(keyChords.get(5));
        }

        /** testing the output of getChordSuggestion() in the terminal */
        for (int i = 0; i < currentKey.getScaleNotes().length; i++)
        {
            int chord = i + 1;
            System.out.println("chord " + chord + ": " + currentKey.getScaleNotes()[i] );
        }
        System.out.println("Key name = " + currentKey);
        System.out.println("Input Chord  = " + inputChord);
        System.out.println("Index of input chord = " + indexOfInputChord);
        System.out.println("Size of suggested chords list = " + listOfChords.size());
        System.out.println("Expected suggested chords = B Minor, E Major, G# Diminished");
        System.out.println("Actual suggested chords = " + listOfChords.get(0) + ", " + listOfChords.get(1) + ", " + listOfChords.get(2));

        return listOfChords;
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
