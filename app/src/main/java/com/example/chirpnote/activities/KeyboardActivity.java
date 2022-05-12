package com.example.chirpnote.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chirpnote.ChirpNoteSession;
import com.example.chirpnote.ChirpNoteUser;
import com.example.chirpnote.Key;
import com.example.chirpnote.Mixer;
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.R;
import com.example.chirpnote.RealTimeMelody;
import com.example.chirpnote.Session;
import com.google.android.material.navigation.NavigationView;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class KeyboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private static Realm realm;
    private static ChirpNoteUser user;

    private MidiDriver midiDriver;
    private ArrayList<MusicNote> pianoKeys;
    private ArrayList<MusicNote> sharps;
    private ArrayList<MusicNote> flats;
    private ArrayList<MusicNote> sharpKeys;
    private ArrayList<MusicNote> flatKeys;
    RealTimeMelody melody;

    private Button minimizeBtn;
    private AlertDialog dialog;

    private List<String> keyTypeList = new ArrayList<>();
    private List<String> keyNameList = new ArrayList<>();
    private String keyNameChoice;
    private String keyTypeChoice;
    private Key currentKey;
    private ArrayList<MusicNote> keyButtons;
    private static ChirpNoteSession session;
    private static Mixer mixer;

    private DrawerLayout drawer;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        hideSystemBars();
//      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().hide();

        user = (ChirpNoteUser) getIntent().getSerializableExtra("user");
        realm = Realm.getDefaultInstance();

        // navigation drawer
        Toolbar toolbar = findViewById(R.id.nav_toolbar);
        toolbar.setTitle("Keyboard");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // play and stop button
        ImageView navPlayButton = findViewById(R.id.nav_play_button);
        navPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MelodyActivity.this, "Play", Toast.LENGTH_SHORT).show();
                if (mixer.areTracksPlaying()) {
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
                if (mixer.areTracksPlaying()) {
                    mixer.stopTracks();
                }
            }
        }); //end play and stop

//        // keyboard code
//        minimizeBtn = findViewById(R.id.buttonMinimize);



        /*Intent intent = getIntent();
        if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromSetKeyActivity"))
        {

            session = (ChirpNoteSession) getIntent().getSerializableExtra("session"); // coming from keyboard activity
        }
        else
        {
            session = new ChirpNoteSession("SessionFreePlay", new Key(Key.RootNote.A, Key.Type.MAJOR), 120);
            System.out.println("session key name = " + session.getKey().toString());
        }*/
        session = (ChirpNoteSession) getIntent().getSerializableExtra("session");
        String basePath = this.getFilesDir().getPath();
        if(session == null) {
            session = new ChirpNoteSession("SessionFreePlay", new Key(Key.RootNote.C, Key.Type.MAJOR), 120,
                    basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3");
        }
        mixer = new Mixer(session);
        melody = mixer.realTimeMelody;

//        initializeKeyNameList(session);
//        initializeKeyTypeList(session);

        Button recordButton = (Button) findViewById(R.id.recordButton);
        //Button playButton = (Button) findViewById(R.id.playButton);

        midiDriver = MidiDriver.getInstance(); // MIDI driver to send MIDI events to
        pianoKeys = new ArrayList<>(); // List of notes

        pianoKeys = getPianoKeys();

        keyButtons = new ArrayList<>();
        currentKey = session.getKey(); // gets the key set when session was initialized
        for (int i = 0; i < currentKey.getScaleNotes().length; i++)
        {
            int rootIdx = (currentKey.getScaleNotes()[i] - 60) % 12;
            if (keyButtons.contains(pianoKeys.get(0))) // because i designed the keyboard to include 2 C's I need to check if the keyboard already contains a c note to highlight the one an octave above
            {
//
                keyButtons.add(pianoKeys.get(12));
            }
            /** arraylist of all chords that belong to the current key based on the type of chord
             * it takes in the root note of the chord and type of chord
             */
//            keyButtons.add(new Chord(Chord.RootNote.values()[rootIdx], currentKey.getChordTypes()[i]));
            keyButtons.add(pianoKeys.get(rootIdx));
        }

//        Toast.makeText(getApplicationContext(), "New Key: " + keyNameChoice + " " + keyTypeChoice, Toast.LENGTH_LONG).show();

        eventListener(pianoKeys);



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


//        /** Allows the user to switch between keys whenever they want */
//        Button changeKeyButton = (Button) findViewById(R.id.changeKeyButton);
//        changeKeyButton.setClickable(true);
//
//        changeKeyButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                Intent intent = new Intent(KeyboardActivity.this, SetKeyActivity.class);
//                intent.putExtra("flag", "fromKeyboardActivity");
//                intent.putExtra("session", session);
//                startActivity(intent);
//
////                if (keyNameChoice.equals("Key Name") || keyTypeChoice.equals("Key Type"))
////                {
////                    Toast.makeText(getApplicationContext(), "Please make proper selection.", Toast.LENGTH_LONG).show();
////                }
////                else
////                {
////                    Key.RootNote newRootNote = Key.RootNote.returnRootNote(keyNameChoice);
////                    Key.Type newKeyType = Key.Type.valueOf(keyTypeChoice.toUpperCase());
////
////                    session.setKey(new Key(newRootNote, newKeyType));
//////                    initializeKeys(session);
////                    keyButtons = new ArrayList<>();
////                    currentKey = session.getKey(); // gets the key set when session was initialized
////                    for (int i = 0; i < currentKey.getScaleNotes().length; i++)
////                    {
////                        // TODO: Think of a better way to do this
////                        int rootIdx = (currentKey.getScaleNotes()[i] - 60) % 12;
////                        if (keyButtons.contains(pianoKeys.get(0))) // because i designed the keyboard to include 2 C's I need to check if the keyboard already contains a c note to highlight the one an octave above
////                        {
//////
////                            keyButtons.add(pianoKeys.get(12));
////                        }
////                        /** arraylist of all chords that belong to the current key based on the type of chord
////                         * it takes in the root note of the chord and type of chord
////                         */
//////            keyButtons.add(new Chord(Chord.RootNote.values()[rootIdx], currentKey.getChordTypes()[i]));
////                        keyButtons.add(pianoKeys.get(rootIdx));
////                    }
////
////                    Toast.makeText(getApplicationContext(), "New Key: " + keyNameChoice + " " + keyTypeChoice, Toast.LENGTH_LONG).show();
////                }
//////                eventListener(pianoKeys);
//            }
//        });


//        Button noteSuggestButton = findViewById(R.id.noteSuggestion);
//        noteSuggestButton.setClickable(true);
//
//        noteSuggestButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (noteSuggestButton.isSelected())
//                {
//                    noteSuggestButton.setSelected(false);
//                    for (int i = 0; i < keyButtons.size(); i++)
//                    {
////                        pianoKeys.get(i).getButton().setSelected(false);
//                        keyButtons.get(i).getButton().setSelected(false);
//                    }
//
//                }
//                else
//                {
//                    noteSuggestButton.setSelected(true);
//                    for (int i = 0; i < keyButtons.size(); i++)
//                    {
////                        pianoKeys.get(i).getButton().setSelected(true);
//                        keyButtons.get(i).getButton().setSelected(true);
//                    }
//
//                }
//            }
//        });
        if (session.getNoteSuggestionFlag() == true)
        {
            for (int i = 0; i < keyButtons.size(); i++)
            {
                keyButtons.get(i).getButton().setSelected(true);
            }
        }
        else
        {
            for (int i = 0; i < keyButtons.size(); i++)
            {
                keyButtons.get(i).getButton().setSelected(false);
            }
        }




        // If the app is started again while the floating window service is running
        // then the floating window service will stop
        if (isMyServiceRunning()) {
            // onDestroy() method in FloatingWindowService, class will be called here
            stopService(new Intent(KeyboardActivity.this, FloatingWindowService.class));
        }

//        // The Main Button that helps to minimize the app
//        minimizeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // First it confirms whether the
//                // 'Display over other apps' permission in given
//                if (checkOverlayDisplayPermission()) {
//                    // FloatingWindowService service is started
////                    Intent intent = new Intent(KeyboardActivity.this, FloatingWindowService.class);
////                    intent.putExtra("session", session);
////                    startService(intent);
//
//                    Intent intent = new Intent(KeyboardActivity.this, FloatingWindowService.class);
//                    intent.putExtra("session", session);
//                    startService(intent);
//
//
//                    // The MainActivity closes here
//                    finish();
//                } else {
//                    // If permission is not given,
//                    // it shows the AlertDialog box and
//                    // redirects to the Settings
//                    requestOverlayDisplayPermission();
//                }
//            }
//        });

        // Event listener for record button (to record melody)
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!melody.isRecording()){
                    recordButton.setText("Stop");
                    melody.startRecording();
                    if (mixer.areTracksPlaying()) {
                        mixer.stopTracks();
                    }
                    mixer.playTracks();
                } else {
                    recordButton.setText("Record");
                    melody.stopRecording();
                    mixer.stopTracks();
                }
            }
        });

        // Event listener for play button (to play recorded melody)
//        playButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!melody.isPlaying()){
//                    playButton.setText("Stop");
//                    melody.play();
//                } else {
//                    playButton.setText("Play");
//                    melody.stop();
//                }
//            }
//        });

        // Setup event listener for each piano key
//        eventListener(pianoKeys);
//        for(MusicNote note : pianoKeys){
//            note.getButton().setOnTouchListener(new OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
//                        note.play();
//                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                        note.stop();
//                    }
//                    return true;
//                }
//            });
//        }
        // Touch this button to quantize the melody by preset options
        Button quantizeButton = (Button) findViewById(R.id.quantizeButtonKey);
        quantizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(KeyboardActivity.this,v);
                popupMenu.getMenuInflater().inflate(R.menu.quantize_popup_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.quantize_quarter:
                                melody.quantize((long) 960);
                                Toast.makeText(KeyboardActivity.this,"Quantized Melody 1/4ths", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.quantize_eigth:
                                melody.quantize((long) 480);
                                Toast.makeText(KeyboardActivity.this,"Quantized Melody 1/8ths", Toast.LENGTH_SHORT).show();

                                return true;
                            case R.id.quantize_sixteenth:
                                melody.quantize((long) 240);
                                Toast.makeText(KeyboardActivity.this,"Quantized Melody 1/16ths", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();
            }
        });
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

    private ArrayList<MusicNote> getPianoKeys()
    {
        ArrayList<MusicNote> keys = new ArrayList<>();

        keys.add(new MusicNote(60, (Button) findViewById(R.id.noteC4Button), melody));
        keys.add(new MusicNote(61, (Button) findViewById(R.id.noteCSharp4Button), melody));
        keys.add(new MusicNote(62, (Button) findViewById(R.id.noteD4Button), melody));
        keys.add(new MusicNote(63, (Button) findViewById(R.id.noteDSharp4Button), melody));
        keys.add(new MusicNote(64, (Button) findViewById(R.id.noteE4Button), melody));
        keys.add(new MusicNote(65, (Button) findViewById(R.id.noteF4Button), melody));
        keys.add(new MusicNote(66, (Button) findViewById(R.id.noteFSharp4Button), melody));
        keys.add(new MusicNote(67, (Button) findViewById(R.id.noteG4Button), melody));
        keys.add(new MusicNote(68, (Button) findViewById(R.id.noteGSharp4Button), melody));
        keys.add(new MusicNote(69, (Button) findViewById(R.id.noteA4Button), melody));
        keys.add(new MusicNote(70, (Button) findViewById(R.id.noteASharp4Button), melody));
        keys.add(new MusicNote(71, (Button) findViewById(R.id.noteB4Button), melody));
        keys.add(new MusicNote(72, (Button) findViewById(R.id.noteC5Button), melody));

        return keys;
    }



    private void sharpsList()
    {
        sharps.add(new MusicNote(66, (Button) findViewById(R.id.noteF4Button), melody)); // these keys get deactivated
        sharps.add(new MusicNote(61, (Button) findViewById(R.id.noteC4Button), melody));
        sharps.add(new MusicNote(61, (Button) findViewById(R.id.noteC5Button), melody));
        sharps.add(new MusicNote(68, (Button) findViewById(R.id.noteG4Button), melody));
        sharps.add(new MusicNote(63, (Button) findViewById(R.id.noteD4Button), melody));
        sharps.add(new MusicNote(70, (Button) findViewById(R.id.noteA4Button), melody));
        sharps.add(new MusicNote(64, (Button) findViewById(R.id.noteE4Button), melody));
        sharps.add(new MusicNote(71, (Button) findViewById(R.id.noteB4Button), melody));

    }

    private void flatsList()
    {
        flats.add(new MusicNote(70, (Button) findViewById(R.id.noteB4Button), melody)); // these keys get deactivated
        flats.add(new MusicNote(63, (Button) findViewById(R.id.noteE4Button), melody));
        flats.add(new MusicNote(68, (Button) findViewById(R.id.noteA4Button), melody));
        flats.add(new MusicNote(61, (Button) findViewById(R.id.noteD4Button), melody));
        flats.add(new MusicNote(66, (Button) findViewById(R.id.noteG4Button), melody));
        flats.add(new MusicNote(71, (Button) findViewById(R.id.noteC4Button), melody));
        flats.add(new MusicNote(71, (Button) findViewById(R.id.noteC5Button), melody));
        flats.add(new MusicNote(71, (Button) findViewById(R.id.noteF4Button), melody));

    }

    private boolean isMyServiceRunning() {
        // The ACTIVITY_SERVICE is needed to retrieve a
        // ActivityManager for interacting with the global system
        // It has a constant String value "activity".
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);


        // A loop is needed to get Service information that
        // are currently running in the System.
        // So ActivityManager.RunningServiceInfo is used.
        // It helps to retrieve a
        // particular service information, here its this service.
        // getRunningServices() method returns a list of the
        // services that are currently running
        // and MAX_VALUE is 2147483647. So at most this many services
        // can be returned by this method.
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            // If this service is found as a running,
            // it will return true or else false.
            if (FloatingWindowService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void requestOverlayDisplayPermission() {
        // An AlertDialog is created
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        // This dialog can be closed, just by taping
        // anywhere outside the dialog-box
        builder.setCancelable(true);


        // The title of the Dialog-box is set
        builder.setTitle("Screen Overlay Permission Needed");


        // The message of the Dialog-box is set
        builder.setMessage("Enable 'Display over other apps' from System Settings.");



        // The event of the Positive-Button is set
        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The app will redirect to the 'Display over other apps' in Settings.
                // This is an Implicit Intent. This is needed when any Action is needed
                // to perform, here it is
                // redirecting to an other app(Settings).
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));


                // This method will start the intent. It takes two parameter, one is the Intent and the other is
                // an requestCode Integer. Here it is -1.
                startActivityForResult(intent, RESULT_OK);
            }
        });

        dialog = builder.create();
        // The Dialog will
        // show in the screen
        dialog.show();
    }

    private boolean checkOverlayDisplayPermission() {
        // Android Version is lesser than Marshmallow or
        // the API is lesser than 23
        // doesn't need 'Display over other apps' permission enabling.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // If 'Display over other apps' is not enabled
            // it will return false or else true
            if (!Settings.canDrawOverlays(this)) {
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
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

    //navigation drawer
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
                redirectActivity(this, InsertChordsActivity.class);
                break;
            case R.id.nav_percussion:
                redirectActivity(this, PercussionActivity.class);
                break;
            case R.id.nav_keyboard:
                // Just close the drawer since we're already on this activity
                drawer.closeDrawer(GravityCompat.START);
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

    private static void redirectActivity(Activity activity, Class aClass) {
        if(mixer.areTracksPlaying()){
            mixer.stopTracks();
        }
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("flag", "fromKeyboardActivity");
        intent.putExtra("session", session);
        intent.putExtra("user", user);
        if(user != null && !session.getName().equals("SessionFreePlay")) saveToDB();
        activity.startActivity(intent);
    }

    private static void saveToDB(){
        realm.executeTransactionAsync(r -> {
            Session realmSession = r.where(Session.class).equalTo("_id", session.getId()).findFirst();
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
