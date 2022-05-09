package com.example.chirpnote.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
import com.example.chirpnote.R;
import com.example.chirpnote.Session;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;
import com.google.android.material.slider.Slider.OnChangeListener;

import org.billthefarmer.mididriver.MidiDriver;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class MixerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Slider chordsVolumeSlider;
    private Slider composedMelodyVolumeSlider;
    private Slider recordedMelodyVolumeSlider;
    private Slider audioVolumeSlider;
    private Slider percussionVolumeSlider;
    private DrawerLayout drawer;

    private static ChirpNoteSession session;
    private static ChirpNoteUser user;
    private MidiDriver midiDriver;
    private static Mixer mixer;

    App app;
    String appID = "chirpnote-jwrci";
    static Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixer);
        hideSystemBars();

        // Initialize session
        session = (ChirpNoteSession) getIntent().getSerializableExtra("session");
        String basePath = this.getFilesDir().getPath();
        if(session == null) {
            session = new ChirpNoteSession("Name", new Key(Key.RootNote.C, Key.Type.MAJOR), 120,
                    basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3");
        }
        midiDriver = MidiDriver.getInstance();
        mixer = new Mixer(session);
        user = (ChirpNoteUser) getIntent().getSerializableExtra("user");

        app = new App(new AppConfiguration.Builder(appID).build());
        realm = Realm.getDefaultInstance();

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
        toggle.syncState();

        //play and stop buttons
        ImageView navPlayButton = findViewById(R.id.nav_play_button);
        navPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if(mixer.areTracksPlaying()){
                    mixer.stopTracks();
                }
            }
        });

        OnChangeListener chordListener = (slider, value, fromUser) -> mixer.setChordVolume(value);
        chordsVolumeSlider = findViewById(R.id.chordsVolumeSlider);
        chordsVolumeSlider.removeOnChangeListener(chordListener);
        chordsVolumeSlider.setValue(mixer.getChordVolume());
        chordsVolumeSlider.addOnChangeListener(chordListener);

        OnChangeListener composedMelodyListener = (slider, value, fromUser) -> mixer.setConstructedMelodyVolume(value);
        composedMelodyVolumeSlider = findViewById(R.id.composedMelodyVolumeSlider);
        composedMelodyVolumeSlider.removeOnChangeListener(composedMelodyListener);
        composedMelodyVolumeSlider.setValue(mixer.getConstructedMelodyVolume());
        composedMelodyVolumeSlider.addOnChangeListener(composedMelodyListener);

        OnChangeListener recordedMelodyListener = (slider, value, fromUser) -> mixer.setRealTimeMelodyVolume(value);
        recordedMelodyVolumeSlider = findViewById(R.id.recordedMelodyVolumeSlider);
        recordedMelodyVolumeSlider.removeOnChangeListener(recordedMelodyListener);
        recordedMelodyVolumeSlider.setValue(mixer.getRealTimeMelodyVolume());
        recordedMelodyVolumeSlider.addOnChangeListener(recordedMelodyListener);

        OnChangeListener audioListener = (slider, value, fromUser) -> mixer.setAudioVolume(value);
        audioVolumeSlider = findViewById(R.id.audioVolumeSlider);
        audioVolumeSlider.removeOnChangeListener(audioListener);
        audioVolumeSlider.setValue(mixer.getAudioVolume());
        audioVolumeSlider.addOnChangeListener(audioListener);

        OnChangeListener percussionListener = (slider, value, fromUser) -> mixer.setPercussionVolume(value);
        percussionVolumeSlider = findViewById(R.id.percussionVolumeSlider);
        percussionVolumeSlider.removeOnChangeListener(percussionListener);
        percussionVolumeSlider.setValue(mixer.getPercussionVolume());
        percussionVolumeSlider.addOnChangeListener(percussionListener);
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
                if (session.getSmartKeyboardFlag() == false) {
                    redirectActivity(this, KeyboardActivity.class);
                }
                else {
                    redirectActivity(this, SmartKeyboardActivity.class);
                }
                break;
            case R.id.nav_mixer:
                // Just close the drawer since we're already on this activity
                drawer.closeDrawer(GravityCompat.START);
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
        }
        else {
            super.onBackPressed();
        }
    }

    private static void redirectActivity(Activity activity, Class aClass) {
        if(mixer.areTracksPlaying()){
            mixer.stopTracks();
        }
        Intent intent = new Intent(activity, aClass);
        intent.putExtra("flag", "fromMixerActivity");
        intent.putExtra("session", session);
        intent.putExtra("user", user);
        if(user != null) saveToDB();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    private static void saveToDB(){
        realm.executeTransactionAsync(r -> {
            Session realmSession = r.where(Session.class).equalTo("_id", session.getId()).findFirst();
            realmSession.setTrackVolumes(realmSession.listToRealmList(session.mTrackVolumes));
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
