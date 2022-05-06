package com.example.chirpnote.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chirpnote.ChirpNoteSession;
import com.example.chirpnote.R;
import com.google.android.material.navigation.NavigationView;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class SessionOverviewActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    App app;
    String appID = "chirpnote-jwrci";
    static ChirpNoteSession session;
    static String username;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_overview);
        hideSystemBars();

        app = new App(new AppConfiguration.Builder(appID).build());
        username = getIntent().getStringExtra("username");

        Toolbar toolbar = findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        session = (ChirpNoteSession) getIntent().getSerializableExtra("session");
        ((TextView) findViewById(R.id.sessionName)).setText("Name: " + session.getName());
        ((TextView) findViewById(R.id.sessionTempo)).setText("Tempo: " + session.getTempo());
        ((TextView) findViewById(R.id.sessionKey)).setText("Key: " + session.getKey());
        if(session.mChords.size() == 0) {
            ((TextView) findViewById(R.id.suggestedAction)).setText("Try adding chords to your session from the menu on the left");
        } else if(session.mMelodyElements.size() == 0) {
            ((TextView) findViewById(R.id.suggestedAction)).setText("Try adding a melody to your session from the menu on the left");
        } else if(session.mPercussionPatterns.size() == 0) {
            ((TextView) findViewById(R.id.suggestedAction)).setText("Try adding percussion to your session from the menu on the left");
        } else if(!session.isAudioRecorded()) {
            ((TextView) findViewById(R.id.suggestedAction)).setText("Try adding some audio to your session from the menu on the left");
        } else {
            ((TextView) findViewById(R.id.suggestedAction)).setText("");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                redirectActivity(this, HomeScreenActivity.class);
                break;
            case R.id.nav_overview:
                // Just close the drawer since we're already on this activity
                drawer.closeDrawer(GravityCompat.START);
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
//                System.out.println("================================================== SessionOverviewActivity session.getSmartKeyboardFlag = " + session.getSmartKeyboardFlag());
                if (session.getSmartKeyboardFlag() == false) {
//                    System.out.println("========================================= here 1");
                    redirectActivity(this, KeyboardActivity.class);
                }
                else {
//                    System.out.println("========================================= here 2");
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
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("flag", "fromSessionOverviewActivity");
        intent.putExtra("session", session);
        intent.putExtra("username", username);
        activity.startActivity(intent);
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