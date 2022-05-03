package com.example.chirpnote.activities;

import android.app.Activity;
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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

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
import com.example.chirpnote.Key;
import com.example.chirpnote.Mixer;
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.R;
import com.example.chirpnote.RealTimeMelody;
import com.example.chirpnote.Session;
import com.google.android.material.navigation.NavigationView;

import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class SmartKeyboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static Realm realm;
    private static String username;

    private MidiDriver midiDriver;
    private ArrayList<MusicNote> pianoKeys;
    RealTimeMelody melody;
    private AlertDialog dialog;

    private String keyNameChoice;
    private String keyTypeChoice;
    private static ChirpNoteSession session;
    private Mixer mixer;
    private Button minimizeBtn;


    private List<String> keyTypeList = new ArrayList<>();
    private List<String> keyNameList = new ArrayList<>();

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_keyboard);
        hideSystemBars();

        username = getIntent().getStringExtra("username");
        realm = Realm.getDefaultInstance();

        //navigation drawer
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

        // keyboard code
        minimizeBtn = findViewById(R.id.buttonMinimize);

//        ChirpNoteSession session = new ChirpNoteSession("Session1", new Key(Key.RootNote.D, Key.Type.HARMONIC_MINOR), 120);
        /*Intent intent = getIntent();
        if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromSetKeyActivity"))
        {

            session = (ChirpNoteSession) getIntent().getSerializableExtra("session"); // coming from keyboard activity
        }
        else
        {
            session = new ChirpNoteSession("SessionFreePlay", new Key(Key.RootNote.C, Key.Type.MAJOR), 120);
            System.out.println("session key name = " + session.getKey().toString());
        }*/
        session = (ChirpNoteSession) getIntent().getSerializableExtra("session");
        String basePath = this.getFilesDir().getPath();
        if(session == null) {
            session = new ChirpNoteSession("SessionFreePlay", new Key(Key.RootNote.C, Key.Type.MAJOR), 120,
                    basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3", "username");
        }
        mixer = new Mixer(session);
        melody = mixer.realTimeMelody;

        initializeKeys(session);

        eventListener(pianoKeys);

        initializeKeyNameList(session);
        initializeKeyTypeList(session);

//        System.out.println(keyNameList.get(0));

        Button recordButton = (Button) findViewById(R.id.recordButton);
        Button playButton = (Button) findViewById(R.id.playButton);

        midiDriver = MidiDriver.getInstance(); // MIDI driver to send MIDI events to


//        pianoKeys = new ArrayList<>(); // List of MusicNotes
//        int[] notes = session.getKey().getScaleNotes(); // Array of MIDI note numbers
//
//        View[] keys = new View[]{findViewById(R.id.key1), findViewById(R.id.key2), findViewById(R.id.key3), findViewById(R.id.key4),
//                findViewById(R.id.key5), findViewById(R.id.key6), findViewById(R.id.key7), findViewById(R.id.key8)};
//
//        for(int i = 0; i < keys.length; i++){
//            pianoKeys.add(new MusicNote(notes[i], (Button) keys[i], melody));
//        }

        // Event listener for record button (to record melody)
        recordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!melody.isRecording()){
                    recordButton.setText("Stop");
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
        playButton.setVisibility(View.INVISIBLE);

//        // Setup event listener for each piano key
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



//        /** Allows the user to switch between keys whenever they want */
//        Button changeKeyButton = (Button) findViewById(R.id.changeKeyButton);
//        changeKeyButton.setClickable(true);
//
//        changeKeyButton.setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                if (keyNameChoice.equals("Key Name") || keyTypeChoice.equals("Key Type"))
//                {
//                    Toast.makeText(getApplicationContext(), "Please make proper selection.", Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    Key.RootNote newRootNote = Key.RootNote.returnRootNote(keyNameChoice);
//                    Key.Type newKeyType = Key.Type.valueOf(keyTypeChoice.toUpperCase());
//
//                    session.setKey(new Key(newRootNote, newKeyType));
//                    initializeKeys(session);
//
//                    Toast.makeText(getApplicationContext(), "New Key: " + keyNameChoice + " " + keyTypeChoice, Toast.LENGTH_LONG).show();
//                }
//                eventListener(pianoKeys);
//            }
//        });
        /** Allows the user to switch between keys whenever they want */
        Button changeKeyButton = (Button) findViewById(R.id.changeKeyButton);
        changeKeyButton.setClickable(true);

        changeKeyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SmartKeyboardActivity.this, SetKeyActivity.class);
                intent.putExtra("flag", "fromSmartKeyboardActivity");
                intent.putExtra("session", session);
                startActivity(intent);
            }
//            eventListener(pianoKeys);
        });

        // The Main Button that helps to minimize the app
        minimizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // First it confirms whether the
                // 'Display over other apps' permission in given
                if (checkOverlayDisplayPermission()) {
                    // FloatingWindowService service is started
//                    Intent intent = new Intent(KeyboardActivity.this, FloatingWindowService.class);
//                    intent.putExtra("session", session);
//                    startService(intent);

                    Intent intent = new Intent(SmartKeyboardActivity.this, FloatingWindowService.class);
                    intent.putExtra("session", session);
                    startService(intent);


                    // The MainActivity closes here
                    finish();
                } else {
                    // If permission is not given,
                    // it shows the AlertDialog box and
                    // redirects to the Settings
                    requestOverlayDisplayPermission();
                }
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
        mixer.syncWithSession();
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
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
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("session", session);
        intent.putExtra("username", username);
        if(username != null && !session.getName().equals("SessionFreePlay")) saveToDB();
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
