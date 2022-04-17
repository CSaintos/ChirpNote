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

import com.example.chirpnote.Chord;
import com.example.chirpnote.ChordTrack;
import com.example.chirpnote.Key;
import com.example.chirpnote.R;
import com.example.chirpnote.Session;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InsertChordsActivity extends AppCompatActivity implements View.OnClickListener
{
    private LinearLayout layoutList; // holds all the rows of buttons that are added to it
    private Button buttonAdd;
    private Button buttonSubmitList;
    private Button chordSuggestionButton;

    private Chord[] measures;
    //    private ArrayList<Chord> measures;
    private ArrayList<Chord[]> listOfMeasures = new ArrayList<>(); // each element of the arraylist is an array of measures
//    private ArrayList<ArrayList<Chord>> listOfMeasures = new ArrayList<>();

    //    private Button[] sessionChords;
    private Chord[] sessionChords = new Chord[7]; // holds the 7 diatonic chords for the session
    private Chord currentChord; // gets updated when user wants to add a chord to a measure


    // A chord track that is recorded (constructed) by adding chords one at a time
    private ChordTrack chordTrack;
    // The driver that allows us to play MIDI notes
    private MidiDriver midiDriver;
    // Used to request permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};



    private ArrayList<Object> chords;
    private List<String> keyTypeList = new ArrayList<>();
    private List<String> keyNameList = new ArrayList<>();
    private Session session;
    private String keyNameChoice;
    private String keyTypeChoice;
    private Key currentKey;
    private int notificationCounter = 0;
    private ArrayList<Chord> suggestedChords = new ArrayList<>();
    private boolean chordSuggestionStatus = false;
    boolean nullFlag = false;
    private int randChoice;

    // The bottom three variables should be defined at the top of the file
    private ArrayList<Chord[]> listOfChords = new ArrayList<>();
    private ArrayList<Button[]> listOfButtons = new ArrayList<>();
    private Chord selectedSessionChord = null;
    // The top three variables should be defined at the top of the file

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

        chordSuggestionButton = (Button) findViewById(R.id.chordSuggestionButton);
        chordSuggestionButton.setClickable(true);




        // Initialize MIDI driver
        midiDriver = MidiDriver.getInstance();

        // Request permission to record audio
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        String basePath = this.getFilesDir().getPath();
//        session = new Session("Name", new Key(Key.RootNote.C, Key.Type.MAJOR), 120,
//                basePath + "/chords.mid", basePath + "/cMelody.mid", basePath + "/rMelody.mid", basePath + "/audioTrack.mp3");
        session = new Session("Name", new Key(Key.RootNote.C, Key.Type.MAJOR), 120,
                basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3");
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


//        suggestedChords = getSuggestedChords(inputChord, keyChords);

        Button chordSuggestion = (Button) findViewById(R.id.chordSuggestionButton);
        chordSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chordSuggestion.isSelected())
                {
                    chordSuggestionStatus = false;
                    chordSuggestion.setSelected(false);

//                    for (int row = 0; row < layoutList.getChildCount(); row++)
//                    {
//                        for (int measure = 0; measure < measures.length; measure++)
//                        {
//                            Chord currentMeasure = listOfMeasures.get(row)[measure];
//                            int finalMeasure = measure;
//                            int finalRow = row;
//                            currentMeasure.getButton().setOnClickListener(new View.OnClickListener()
//                            {
//                                @Override
//                                public void onClick(View v) {
//                                    if (currentMeasure.getButton().isSelected())
//                                    {
//                                        currentMeasure.getButton().setSelected(false);
//                                        System.out.println("SELECTED TRUE");
//                                    }
//                                    else
//                                    {
//                                        currentMeasure.getButton().setSelected(true);
//                                        System.out.println("SELECTED FALSE");
//                                    }
//                                }
//                            });
//                        }
//                    }
                }
                else
                {
                    chordSuggestionStatus = true;
                    chordSuggestion.setSelected(true);
                }


            }
        });
        // end of Sam's section


    }

    @Override
    public void onClick(View v) {
//        addRowOfMeasures();
        switch (v.getId()) {
            case R.id.button_add_row:
                if (areMeasuresFilled() == true || layoutList.getChildCount() == 0)
                {
//                    addRowOfMeasures();

                    addRowOfMeasures2();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please insert chords to all available measures.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.changeKeyButton:
                changeKey(session);
                //chordTrack.play();
                break;
//            case R.id.chordSuggestionButton:
//                chordSuggestion(session);
//                break;
        }
    }

//    private void chordSuggestion(Session session) {
//        for (int row = 0; row < layoutList.getChildCount(); row++)
//        {
//            for (int measure = 0; measure < measures.length; measure++)
//            {
//                Chord currentMeasure = listOfMeasures.get(row)[measure];
//                int finalMeasure = measure;
//                int finalRow = row;
//                currentMeasure.getButton().setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        for (int i = 0; i < sessionChords.length; i++)
//                        {
//                            if (currentMeasure.toString().equals(sessionChords[i].toString()))
//                            {
//                                sessionChords[i].getButton().setSelected(true);
//                                System.out.println("sessionChord = " + sessionChords[i]);
//                            }
//                        }
//
////                        System.out.println(" ");
////                        System.out.println("row = " + finalRow);
////                        System.out.println("measure = " + finalMeasure);
////                        System.out.println("layoutList size = " + layoutList.getChildCount());
////
////                        // start
////                        System.out.println("before setting text sessionChord = " + currentChord);
////                        currentMeasure.getButton().setText(currentChord.getButton().getText());
////                        System.out.println("sessionChord = " + currentChord);
////                        System.out.println("currentMeasure = " + currentChord);
////
////                        Chord[] currentRowMeasure = listOfMeasures.get(finalRow);
////                        currentRowMeasure[finalMeasure] = new Chord(currentChord); // need to make a copy constructor in chord
////
////                        listOfMeasures.set(finalRow, currentRowMeasure);
////
////                            System.out.println("sessionChord = " + sessionChord);
////                            System.out.println("currentRowMeasure[finalMeasure] = " + currentRowMeasure[finalMeasure]);
//                        // end same as bellow but using currentChord
//
//
//
//                    }
//                });
//                System.out.println("row = " + row + ", measure " + measure + " = " + listOfMeasures.get(row)[measure]);
//
//            }
//        }
//    }


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
//            System.out.println("outside for-loop sessionChord = " + currentChord);
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
//                            System.out.println(" ");
//                            System.out.println("row = " + finalRow);
//                            System.out.println("measure = " + finalMeasure);
//                            System.out.println("layoutList size = " + layoutList.getChildCount());

                            // start
//                            System.out.println("before setting text sessionChord = " + currentChord);

                            currentMeasure.getButton().setText(currentChord.getButton().getText());
//                            currentMeasure.getButton().setText(sessionChord.getButton().getText());

//                            System.out.println("sessionChord = " + currentChord);
//                            System.out.println("currentMeasure = " + currentChord);

                            Chord[] currentRowMeasure = listOfMeasures.get(finalRow);
                            currentRowMeasure[finalMeasure] = new Chord(currentChord); // need to make a copy constructor in chord

                            listOfMeasures.set(finalRow, currentRowMeasure);

                            if (chordSuggestionStatus == true)
                            {
                                String inputChord = (String) currentMeasure.getButton().getText();
                                suggestedChords = getSuggestedChords(inputChord, sessionChords);

                                for (int i = 0; i < suggestedChords.size(); i++)
                                {
                                    suggestedChords.get(i).getButton().setSelected(true);
                                }
                            }
                            else if (chordSuggestionStatus == false)
                            {
                                for (int i = 0; i < suggestedChords.size(); i++)
                                {
                                    suggestedChords.get(i).getButton().setSelected(false);
                                }
                                suggestedChords.clear();
                            }

//                            System.out.println("sessionChord = " + sessionChord);
//                            System.out.println("currentRowMeasure[finalMeasure] = " + currentRowMeasure[finalMeasure]);
                            // end same as bellow but using currentChord



                        }
                    });
                    System.out.println("row = " + row + ", measure " + measure + " = " + listOfMeasures.get(row)[measure]);
//                    for (int i = 0; i < listOfMeasures.size(); i++) {
//                        if (listOfMeasures.get(row)[measure].getRootNote() != null) {
//                            System.out.println("row = " + row + ", measure " + measure + " = " + listOfMeasures.get(row)[measure]);
//                        }
//                        else
//                        {
//                            System.out.println("row = " + row + ", measure " + measure + " = " + "NO CHORD");
//                        }
//                    }

                }
            }
        }
    }

    private boolean areMeasuresFilled() {
        if (measures != null) {
            for (int row = 0; row < layoutList.getChildCount(); row++) {
                for (int measure = 0; measure < measures.length; measure++) {
                    if (listOfMeasures.get(row)[measure].getRootNote() == null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void addRowOfMeasures()
    {

        View rowOfMeasures = getLayoutInflater().inflate(R.layout.add_row, null, false);
        ImageView imageClose = (ImageView) rowOfMeasures.findViewById(R.id.row_remove);
        layoutList.addView(rowOfMeasures);


        int currentRowIndex = layoutList.indexOfChild(rowOfMeasures);
        //        int currentRowIndex = layoutList.getChildCount() - 1; // grabs the index of the recently added row, i.e. row 1 - 1 = row 0 => first row of measures


        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = layoutList.indexOfChild(rowOfMeasures);
                removeRowOfMeasures(rowOfMeasures, index);

                //                removeRowOfMeasures(rowOfMeasures, currentRowIndex);
                //                removeRowOfMeasures(rowOfMeasures);//, currentRowIndex); // old
            }
        });


        // adds 4 measures to a measures array which is then set to listOfMeasures so that each row of the listOfMeasures will correspond to a specific row which ideally would make it easier to add and remove later
        measures = new Chord[4];
        measures[0] = new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure1));
        measures[1] = new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure2));
        measures[2] = new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure3));
        measures[3] = new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure4));

        //        // 4.7 changes
        //        for (int i = 0; i < measures.length; i++)
        //        {
        //            measures[i].getButton().setOnClickListener(new View.OnClickListener() {
        //                @Override
        //                public void onClick(View v) {
        //                    modifyMeasure(measures);
        //
        //                }
        //            });
        //        }
        //        // end of 4.7 changes

        //        // adds 4 measures to a measures array which is then set to listOfMeasures so that each row of the listOfMeasures will correspond to a specific row which ideally would make it easier to add and remove later
        //        measures = new ArrayList<>();
        //        measures.set(0, new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure1)));
        //        measures.set(1, new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure2)));
        //        measures.set(2, new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure3)));
        //        measures.set(3, new Chord(layoutList.getChildAt(currentRowIndex).findViewById(R.id.measure4)));

        listOfMeasures.add(measures); // HERE IS WHERE I FINISHED

    }

//    // The bottom three variables should be defined at the top of the file
//    private ArrayList<Chord[]> listOfChords = new ArrayList<>();
//    private ArrayList<Button[]> listOfButtons = new ArrayList<>();
//    private Chord selectedSessionChord = null;
//    // The top three variables should be defined at the top of the file

    private void addRowOfMeasures2(){

        View rowOfMeasures = getLayoutInflater().inflate(R.layout.add_row, null, false);
        layoutList.addView(rowOfMeasures);
        int rowIdx = layoutList.indexOfChild(rowOfMeasures);

        ImageView imageClose = (ImageView) rowOfMeasures.findViewById(R.id.row_remove);
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = layoutList.indexOfChild(rowOfMeasures);
                removeRowOfMeasures(rowOfMeasures, index);
            }
        });

//        listOfChords.add(new Chord[4]);
        Chord[] prefilledMeasures = randomChordProgression(sessionChords);
        listOfChords.add(prefilledMeasures);

        listOfButtons.add(new Button[4]);
        int[] buttonIds = new int[]{R.id.measure1, R.id.measure2, R.id.measure3, R.id.measure4}; // these are the tags that are going to be needed to look up the specific buttons from the particular view

        for (int col = 0; col < prefilledMeasures.length; col++)
        {
            int romanChordIndex = prefilledMeasures[col].returnRoman();
            String romanChordString = session.getKey().getRomanTypes()[romanChordIndex];
            Button tempMeasure = layoutList.getChildAt(rowIdx).findViewById(buttonIds[col]);
            tempMeasure.setText(romanChordString);

            chordTrack.addChord(prefilledMeasures[col], (rowIdx * 4) + col);
        }

        for(int colIdx = 0; colIdx < 4; colIdx++){
            int col = colIdx;
            listOfButtons.get(rowIdx)[col] = layoutList.getChildAt(rowIdx).findViewById(buttonIds[col]);
            listOfButtons.get(rowIdx)[col].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(selectedSessionChord != null){
                        // Use copy constructor once it has been fixed
                        // Chord newChord = new Chord(selectedSessionChord);
                        Chord newChord = new Chord(selectedSessionChord.getRootNote(), selectedSessionChord.getType(), session.getTempo());
                        listOfChords.get(rowIdx)[col] = newChord;
                        chordTrack.addChord(newChord, (rowIdx * 4) + col);
                        ((Button) v).setText(selectedSessionChord.getButton().getText());
                    }

                    if (chordSuggestionStatus == true) {
                        String inputChord = (String) listOfButtons.get(rowIdx)[col].getText();
                        suggestedChords = getSuggestedChords(inputChord, sessionChords);

                        for (int i = 0; i < suggestedChords.size(); i++) {
                            suggestedChords.get(i).getButton().setSelected(true);
                        }
                    }
                    else if (chordSuggestionStatus == false) {
                        String inputChord = (String) listOfButtons.get(rowIdx)[col].getText();
                        suggestedChords = getSuggestedChords(inputChord, sessionChords);

                        for (int i = 0; i < suggestedChords.size(); i++) {
                            suggestedChords.get(i).getButton().setSelected(false);
                        }
                        suggestedChords.clear();
                    }
                }
            });
        }

        for (int row = 0; row < layoutList.getChildCount(); row++)
        {
            for (int measure = 0; measure < 4; measure++)
            {
                System.out.println("row = " + row + ", measure " + measure + " = " + listOfChords.get(row)[measure]);
            }
        }

//        Chord[] tempList = randomChordProgression(sessionChords);

    }

    private void removeRowOfMeasures(View view, int rowIndex)
    {
//        listOfMeasures.remove(rowIndex);

        layoutList.removeView(view);
        listOfChords.remove(rowIndex);
//        chordTrack.removeChord(rowIndex); // still need to implement
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

//        System.out.println("====================================== sessionChords =================================== = " + Arrays.toString(sessionChords));
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
//                        modifyMeasure(chord);
                        selectedSessionChord = chord;
                        chord.play();
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
    private ArrayList<Chord> getSuggestedChords(String inputChord, Chord[] keyChords)
    {
        ArrayList<Chord> listOfChords = new ArrayList<Chord>();
//        int indexOfInputChord = keyChords.indexOf(inputChord); // grabs the index of chord to determine what roman numeral it is in
        int indexOfInputChord = 0;

        for (int i = 0; i < keyChords.length; i++)
        {
            if (inputChord.equals(keyChords[i].toString()))
            {
                indexOfInputChord = i;
            }
        }

        // it doesn't matter if its major or minor since they share most of the same chord suggestions with the exception of VII which we're not using
        // if statements used to collect all the chord suggestions that pertain to the chord found at index of input chord
        if (indexOfInputChord == 0 || indexOfInputChord == 7) // I chord
        {
            for (int i = 1; i < 7; i++)
            {
                listOfChords.add(keyChords[i]); // a I chord can go virtually anywhere
            }
        }
        else if (indexOfInputChord == 1) // ii Chord
        {
            listOfChords.add(keyChords[6]);
            listOfChords.add(keyChords[4]);
        }
        else if (indexOfInputChord == 2) // iii Chord
        {
            listOfChords.add(keyChords[3]);
            listOfChords.add(keyChords[5]);
        }
        else if (indexOfInputChord == 3) // IV Chord
        {
            listOfChords.add(keyChords[1]);
            listOfChords.add(keyChords[4]);
            listOfChords.add(keyChords[6]);
        }
        else if (indexOfInputChord == 4) // V chord
        {
            listOfChords.add(keyChords[0]); // 1
            listOfChords.add(keyChords[5]); // 6
            listOfChords.add(keyChords[6]); // 7
        }
        else if (indexOfInputChord == 5) // vi chord
        {
            listOfChords.add(keyChords[3]);
            listOfChords.add(keyChords[1]);
        }
        else // vii* chord
        {
            listOfChords.add(keyChords[0]);
            listOfChords.add(keyChords[5]);
        }
        return listOfChords;
//        ArrayList<Chord> listOfChords = new ArrayList<Chord>();
////        int indexOfInputChord = keyChords.indexOf(inputChord); // grabs the index of chord to determine what roman numeral it is in
//        int indexOfInputChord = 0;
//
//        for (int i = 0; i < keyChords.size(); i++)
//        {
//            if (inputChord.equals(keyChords.get(i).toString()))
//            {
//                indexOfInputChord = i;
//            }
//        }
//
//        // it doesn't matter if its major or minor since they share most of the same chord suggestions with the exception of VII which we're not using
//        // if statements used to collect all the chord suggestions that pertain to the chord found at index of input chord
//        if (indexOfInputChord == 0 || indexOfInputChord == 7) // I chord
//        {
//            for (int i = 1; i < 7; i++)
//            {
//                listOfChords.add(keyChords.get(i)); // a I chord can go virtually anywhere
//            }
//        }
//        else if (indexOfInputChord == 1) // ii Chord
//        {
//            listOfChords.add(keyChords.get(6));
//            listOfChords.add(keyChords.get(4));
//        }
//        else if (indexOfInputChord == 2) // iii Chord
//        {
//            listOfChords.add(keyChords.get(3));
//            listOfChords.add(keyChords.get(5));
//        }
//        else if (indexOfInputChord == 3) // IV Chord
//        {
//            listOfChords.add(keyChords.get(1));
//            listOfChords.add(keyChords.get(4));
//            listOfChords.add(keyChords.get(6));
//        }
//        else if (indexOfInputChord == 4) // V chord
//        {
//            listOfChords.add(keyChords.get(1));
//            listOfChords.add(keyChords.get(5));
//            listOfChords.add(keyChords.get(7));
//        }
//        else if (indexOfInputChord == 5) // vi chord
//        {
//            listOfChords.add(keyChords.get(3));
//            listOfChords.add(keyChords.get(1));
//        }
//        else // vii* chord
//        {
//            listOfChords.add(keyChords.get(1));
//            listOfChords.add(keyChords.get(5));
//        }
//        return listOfChords;
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
    
    private Chord[] randomChordProgression(Chord[] keyChords)//, int randChoice)
    {
        Chord[] progression = new Chord[4];
        Random rand = new Random();
        int randChoice = rand.nextInt(3);

        String keyType = session.getKey().getType().toString();
        int[][] majorProgressions = {{0,4,5,3},{0,5,1,4},{0,3,0,4}}; // 1 5 6 4
        int[][] minorProgressions = {{0,5,2,6},{0,3,5,4},{0,4,5,6}};

        if (keyType.equals("Major")) {
            for (int i = 0; i < progression.length; i++) {
                int[] chosenProgression = majorProgressions[randChoice];
                progression[i] = new Chord(keyChords[chosenProgression[i]].getRootNote(), keyChords[chosenProgression[i]].getType(), keyChords[chosenProgression[i]].getTempo(), chosenProgression[i]);
            }
        }
        else {
            for (int i = 0; i < progression.length; i++) {
                int[] chosenProgression = minorProgressions[randChoice];
                progression[i] = new Chord(keyChords[chosenProgression[i]].getRootNote(), keyChords[chosenProgression[i]].getType(), keyChords[chosenProgression[i]].getTempo(), chosenProgression[i]);
            }
        }

        System.out.println("=============================== Chord Progression ========================================");
        for (int i = 0; i < progression.length; i++)
        {
            System.out.println(progression[i]);
        }

        return progression;
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
