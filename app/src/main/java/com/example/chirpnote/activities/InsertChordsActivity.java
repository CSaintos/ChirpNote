package com.example.chirpnote.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chirpnote.ChirpNoteSession;
import com.example.chirpnote.ChirpNoteUser;
import com.example.chirpnote.Chord;
import com.example.chirpnote.ChordTrack;
import com.example.chirpnote.ConstructedMelody;
import com.example.chirpnote.Key;
import com.example.chirpnote.Mixer;
import com.example.chirpnote.R;
import com.example.chirpnote.Session;
import com.google.android.material.navigation.NavigationView;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.realm.Realm;

public class InsertChordsActivity extends AppCompatActivity
        implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener
{
    private static Realm realm;
    private static ChirpNoteUser user;

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
    private static ChirpNoteSession session;
    private static Mixer mixer;
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
    private String selectedSuggestedChord;
    // The top three variables should be defined at the top of the file

    private DrawerLayout drawer;

//    List<String> keyTypeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_insert_chords);
        hideSystemBars();

        user = (ChirpNoteUser) getIntent().getSerializableExtra("user");
        realm = Realm.getDefaultInstance();

        // nav drawer
        Toolbar toolbar = findViewById(R.id.nav_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState(); // end nav drawer

        // actionbar play and stop buttons
        ImageView navPlayButton = findViewById(R.id.nav_play_button);
        navPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(InsertChordsActivity.this, "Play", Toast.LENGTH_SHORT).show();
                if(mixer.areTracksPlaying()){
                    mixer.stopTracks();
                }
                mixer.playTracks();
            }
        });
        ImageView navStopButton = findViewById(R.id.nav_stop_button);
        navStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(InsertChordsActivity.this, "Stop", Toast.LENGTH_SHORT).show();
                if(mixer.areTracksPlaying()){
                    mixer.stopTracks();
                }
            }
        }); // play and stop end

        buttonAdd = findViewById(R.id.button_add_row);
        buttonAdd.setOnClickListener(this);

        layoutList = findViewById(R.id.layout_list); // the space where the rows will be added

//        Button changeKeyButton = (Button) findViewById(R.id.changeKeyButton);
//        changeKeyButton.setClickable(true);
//        changeKeyButton.setOnClickListener(this);

        chordSuggestionButton = (Button) findViewById(R.id.chordSuggestionButton);
        chordSuggestionButton.setClickable(true);

        // Initialize MIDI driver
        midiDriver = MidiDriver.getInstance();

        // Request permission to record audio
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        String basePath = this.getFilesDir().getPath();
        session = (ChirpNoteSession) getIntent().getSerializableExtra("session");
        if(session == null){
            session = new ChirpNoteSession("Name", new Key(Key.RootNote.C, Key.Type.MAJOR), 120,
                    basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3");
        }
        mixer = new Mixer(session);

        System.out.println("================================================== InsertChordsActivity session.getSmartKeyboardFlag = " + session.getSmartKeyboardFlag());

        initializeKeyNameList(session);
        initializeKeyTypeList(session);
        initializeSessionChords();
        initializeChordListeners(session);

        /*session.mChords.add("0000300");
        session.mChords.add("0500303");
        session.mChords.add("0000300");
        session.mChords.add("0700304");
        session.mChords.add("0000300");
        session.mChords.add("0700304");
        session.mChords.add("0910305");
        session.mChords.add("0500303");*/
        initializeSongMeasures(session);

        // Touch this button to play the most recently recorded track
        // Touch it again to stop the playback of the track
//        Button playButton = (Button) findViewById(R.id.testPlayButton);
//        playButton.setEnabled(false);

//        // set user input key name and type to new key in session
//        Spinner keyNameSpinner = findViewById(R.id.spinner_key_name);
//        ArrayAdapter keyNameAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, keyNameList);
//        keyNameSpinner.setAdapter(keyNameAdapter);
//        keyNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                keyNameChoice = parent.getItemAtPosition(position).toString();
////                Toast.makeText(getApplicationContext(), keyNameChoice, Toast.LENGTH_LONG).show();
////                Toast.makeText(SmartKeyboardActivity.this, keyNameChoice, Toast.LENGTH_LONG).show();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        Spinner keyTypeSpinner = findViewById(R.id.spinner_key_type);
//        ArrayAdapter keyTypeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, keyTypeList);
//        keyTypeSpinner.setAdapter(keyTypeAdapter);
//        keyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                keyTypeChoice = parent.getItemAtPosition(position).toString();
////                Toast.makeText(getApplicationContext(), keyTypeChoice, Toast.LENGTH_LONG).show();
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        suggestedChords = getSuggestedChords(inputChord, keyChords);

        Button chordSuggestion = (Button) findViewById(R.id.chordSuggestionButton);
        chordSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chordSuggestion.isSelected())
                {
                    chordSuggestionStatus = false;
                    chordSuggestion.setSelected(false);

                    for (int i = 0; i < sessionChords.length; i++)
                    {
                        sessionChords[i].getButton().setSelected(false);
                    }

                }
                else
                {
                    chordSuggestionStatus = true;
                    chordSuggestion.setSelected(true);
                }
            }
        });
    }

    private void initializeSongMeasures(ChirpNoteSession session)
    {
        Chord[] importChords = new Chord[4];
        for(int i = 0; i < session.mChords.size(); i++){
            importChords[i % 4] = chordTrack.decodeChord(session.mChords.get(i));
            if ((i + 1) % 4 == 0) {
                addRowOfMeasures(importChords, true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_row:
                Chord[] randChords = randomChordProgression(sessionChords);
                addRowOfMeasures(randChords, false);
                break;
//            case R.id.changeKeyButton:
//                changeKey(session);
//                break;
        }
    }

    private void addRowOfMeasures(Chord[] prefilledMeasures, boolean loadingSession) {
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

        listOfChords.add(prefilledMeasures);

        listOfButtons.add(new Button[4]);
        int[] buttonIds = new int[]{R.id.measure1, R.id.measure2, R.id.measure3, R.id.measure4}; // these are the tags that are going to be needed to look up the specific buttons from the particular view

        for (int col = 0; col < prefilledMeasures.length; col++)
        {
            int romanChordIndex = prefilledMeasures[col].getRoman();
            String romanChordString = session.getKey().getRomanTypes()[romanChordIndex];
            Button tempMeasure = layoutList.getChildAt(rowIdx).findViewById(buttonIds[col]);
            tempMeasure.setText(romanChordString);

            if(!loadingSession) chordTrack.addChord(prefilledMeasures[col], (rowIdx * 4) + col);
        }

        for(int colIdx = 0; colIdx < 4; colIdx++){
            int col = colIdx;
            listOfButtons.get(rowIdx)[col] = layoutList.getChildAt(rowIdx).findViewById(buttonIds[col]);
            listOfButtons.get(rowIdx)[col].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int newRowIdx = layoutList.indexOfChild(rowOfMeasures);
                    if(selectedSessionChord != null){
                        // Use copy constructor once it has been fixed
                        // Chord newChord = new Chord(selectedSessionChord);

                        System.out.println("============= selectedSessionChord.getRoman() = " + selectedSessionChord.getRoman()); // selectedSessionChord.getRoman()
                        System.out.println("============= selectedSessionChord.getRootNote() = " + selectedSessionChord.getRootNote()); // selectedSessionChord.getRoman()
                        System.out.println("============= selectedSessionChord.getButton().getText() = " + selectedSessionChord.getButton().getText());


                        int selectedRomanIndex = 0;
                        for (int i = 0; i < currentKey.getRomanTypes().length; i++)
                        {
                            if (currentKey.getRomanTypes()[i].equals(selectedSessionChord.getButton().getText()))
                            {
                                selectedRomanIndex = i;
                                break;
                            }
                        }
                        System.out.println("selectedRomanIndex = " + selectedRomanIndex);

//                        Chord newChord = new Chord(selectedSessionChord.getRootNote(), selectedSessionChord.getType(), session.getTempo(), selectedSessionChord.getRoman());
                        Chord newChord = new Chord(selectedSessionChord.getRootNote(), selectedSessionChord.getType(), session.getTempo(), selectedRomanIndex);


//                        System.out.println("================= HERE =======================");
//                        System.out.println("rowIdx = " + rowIdx);
//                        int newRowIdx = layoutList.indexOfChild(rowOfMeasures);
//                        System.out.println("newRowIdx = " + newRowIdx);
//                        System.out.println("col = " + col);
//                        System.out.println("listOfChords.size() = " + listOfChords.size());

                        listOfChords.get(newRowIdx)[col] = newChord; // ERROR HERE IF I REMOVE FROM MIDDLE


                        for (int i = 0; i < listOfChords.size(); i++)
                        {
                            for (int j = 0; j < 4; j++)
                            {
                                int romanInt = listOfChords.get(i)[j].getRoman();
                                String romanStr = session.getKey().getRomanTypes()[romanInt];

                                System.out.println("listOfChords.get("+i+")["+j+"]" + listOfChords.get(i)[j] + "; roman = " + romanStr);
                            }
                        }

                        if(!loadingSession) chordTrack.addChord(newChord, (newRowIdx * 4) + col);
                        ((Button) v).setText(selectedSessionChord.getButton().getText());

                    }

                    selectedSessionChord = null; // clears selectedChord so that it doesn't keep adding to other measures

                    if (isAnotherChordSelected())
                    {
                        for (int i = 0; i < sessionChords.length; i++) {
                            sessionChords[i].getButton().setSelected(false);
                        }
                    }

                    String inputChord = listOfChords.get(newRowIdx)[col].toString();
//                    System.out.println("inputChord = " + inputChord);
                    suggestedChords = getSuggestedChords(inputChord, sessionChords);


                    if (listOfButtons.get(newRowIdx)[col].isSelected())
                    {
                        listOfButtons.get(newRowIdx)[col].setSelected(false);
                        // resets everything to off
                        for (int i = 0; i < suggestedChords.size(); i++) {
                            suggestedChords.get(i).getButton().setSelected(false);
                        }

                    }
                    else // if listOfButtons.get(rowIdx)[col].isSelected() == false
                    {
                        listOfButtons.get(newRowIdx)[col].setSelected(true);


                        // resets everything to off
                        for (int i = 0; i < suggestedChords.size(); i++) {
                            suggestedChords.get(i).getButton().setSelected(false);
                        }

                        // turn on relevant chords
                        if (chordSuggestionStatus == true) {
                            for (int i = 0; i < suggestedChords.size(); i++) {
                                suggestedChords.get(i).getButton().setSelected(true);
                            }
                        }
                        else if (chordSuggestionStatus == false) {
                            for (int i = 0; i < suggestedChords.size(); i++) {
                                suggestedChords.get(i).getButton().setSelected(false);
                            }
                        }
                    }
                }
            });

            // Add empty measure to melody and percussion track
            mixer.constructedMelody.addRest(ConstructedMelody.NoteDuration.WHOLE_NOTE, (rowIdx * 4) + col);
            mixer.percussionTrack.addPattern(null, (rowIdx * 4) + col);
        }

    }

    private int getRomanIndex(String chordRootName) {
        ArrayList<String> notesArray = new ArrayList<>();
        for (int i = 0; i < currentKey.getScaleNotes().length - 1; i++) {
            int rootIdx = (currentKey.getScaleNotes()[i] - 60) % 12;
            notesArray.add(Key.RootNote.values()[rootIdx].toString());
        }

        return notesArray.indexOf(chordRootName);
    }


    private void changeKey(ChirpNoteSession session)
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

    private boolean isAnotherChordSelected()
    {
        boolean isSelected = false;

        for (int i = 0; i < listOfButtons.size(); i++)
        {
            for (int j = 0; j < 4; j++)
            {
                if (listOfButtons.get(i)[j].isSelected())
                    return true;
            }

        }

        return isSelected;
    }

    private void removeRowOfMeasures(View view, int rowIndex)
    {
        layoutList.removeView(view);
        listOfChords.remove(rowIndex);
        listOfButtons.remove(rowIndex);
        chordTrack.removeFourMeasures(rowIndex * 4);
    }

    public void initializeSessionChords()
    {
        currentKey = session.getKey(); // gets the key set when session was initialized
        Button [] romanButtons = getRomanButtons(session);
        Chord.Type[] chordTypes = currentKey.getChordTypes();
        for (int i = 0; i < currentKey.getScaleNotes().length - 1; i++)
        {
            int rootIdx = (currentKey.getScaleNotes()[i] - 60) % 12;
            /** arraylist of all chords that belong to the current key based on the type of chord
             * it takes in the root note of the chord and type of chord
             */
            sessionChords[i] = new Chord(Chord.RootNote.values()[rootIdx], chordTypes[i], session.getTempo(), romanButtons[i]);
        }
        for (int i = 0; i < sessionChords.length; i++)
        {
            sessionChords[i].getButton().setText(session.getKey().getRomanTypes()[i]);
        }
//        System.out.println("====================================== sessionChords =================================== = " + Arrays.toString(sessionChords));
    }

    private void initializeChordListeners(ChirpNoteSession session)
    {
        chordTrack = mixer.chordTrack;

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
//        System.out.println("inputChord = " + inputChord);

        ArrayList<Chord> listOfChords = new ArrayList<Chord>();
//        int indexOfInputChord = session.getKey().returnRomanInt(inputChord); // grabs the index of chord to determine what roman numeral it is in
//        System.out.println("indexOfInputChord = " + indexOfInputChord);

        int indexOfInputChord = 0;
        for (int i = 0; i < keyChords.length; i++)
        {
            if (inputChord.equals(keyChords[i].toString()))
            {
                indexOfInputChord = i;
            }
        }

        //        System.out.println("indexOfInputChord = " + indexOfInputChord);

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
    }

    private void initializeKeyNameList(ChirpNoteSession session)
    {
        keyNameList.add("Key Name");
        for (int i = 0; i < Key.RootNote.values().length; i++)
        {
            keyNameList.add(Key.RootNote.values()[i].toString());
        }
    }

    private void initializeKeyTypeList(ChirpNoteSession session)
    {
        keyTypeList.add("Key Type");
        keyTypeList.add("Major");
        keyTypeList.add("Minor");
    }

    private Button[] getRomanButtons(ChirpNoteSession session)
    {
        Button[] list = new Button[7];
        list[0] = findViewById(R.id.roman1);
        list[1] = findViewById(R.id.roman2);
        list[2] = findViewById(R.id.roman3);
        list[3] = findViewById(R.id.roman4);
        list[4] = findViewById(R.id.roman5);
        list[5] = findViewById(R.id.roman6);
        list[6] = findViewById(R.id.roman7);

//        if (session.getKey().toString().equals("Minor"))
//        {
//            for (int i = 0; i < list.length; i++)
//            {
//                list[i].setText(session.getKey().getRomanTypes()[i]);
//            }
//        }

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

        return progression;
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

    // nav drawer code
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                redirectActivity(this, HomeScreenActivity.class);
                break;
            case R.id.nav_overview:
                redirectActivity(this, SessionOverviewActivity.class);
                break;
            case R.id.nav_melody:
                redirectActivity(this, MelodyActivity.class);
                break;
            case R.id.nav_chords:
                // Just close the drawer since we're already on this activity
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_percussion:
                redirectActivity(this, PercussionActivity.class);
                break;
            case R.id.nav_keyboard:
                if (session.getSmartKeyboardFlag() == false) {
                    redirectActivity(this, KeyboardActivity.class);
                }
                else {
                    redirectActivity(this, SmartKeyboardActivity.class);
                }
                break;
            case R.id.nav_mixer:
                redirectActivity(this, MixerActivity.class);
                break;
            case R.id.nav_audio:
                redirectActivity(this, RecordAudioActivity.class);
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private static void redirectActivity(Activity activity, Class aClass) {
        if(mixer.areTracksPlaying()){
            mixer.stopTracks();
        }
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("flag", "fromInsertChordsActivity");
        intent.putExtra("session", session);
        intent.putExtra("user", user);
        if(user != null) saveToDB();
        activity.startActivity(intent);
    }

    private static void saveToDB(){
        realm.executeTransactionAsync(r -> {
            Session realmSession = r.where(Session.class).equalTo("_id", session.getId()).findFirst();
            realmSession.setNextMelodyTick(session.mNextMelodyTick);
            realmSession.setChords(realmSession.listToRealmList(session.mChords));
            realmSession.setMelodyElements(realmSession.listToRealmList(session.mMelodyElements));
            realmSession.setPercussionPatterns(realmSession.listToRealmList(session.mPercussionPatterns));
            realmSession.setMidiFile(realmSession.encodeFile(session.getMidiPath()));
        });
    }

    // pop up menu with session options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_session_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.subitem1:
                redirectActivity(this, SessionOptionsActivity.class);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Hides the system status bar and navigation bar
     */
    private void hideSystemBars(){
        WindowInsetsControllerCompat windowInsetsController = new WindowInsetsControllerCompat(this.getWindow(), this.getCurrentFocus());
        if (windowInsetsController != null) {
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        }
    }
}
