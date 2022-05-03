package com.example.chirpnote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.R;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class HomeScreenActivity extends AppCompatActivity {

    App app;
    String appID = "chirpnote-jwrci";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        getSupportActionBar().hide();

        String username = getIntent().getStringExtra("username");
        app = new App(new AppConfiguration.Builder(appID).build());

        Button newSessionButton = (Button) findViewById(R.id.newSessionButton);
        Button loadSessionButton = (Button) findViewById(R.id.loadSessionButton);
        Button profileButton = (Button) findViewById(R.id.profileButton);
        Button musicTheoryButton = (Button) findViewById(R.id.musicTheoryButton);

        // For testing only
        Button testKeyboardButton = (Button) findViewById(R.id.testKeyboardButton);
        Button testOtherButton = (Button) findViewById(R.id.testOtherButton);
        Button testMelodyButton = (Button) findViewById(R.id.testMelodyButton);
        Button testPercussionButton = (Button) findViewById(R.id.testPercussionButton);
        Button testSmartKeyboardButton = (Button) findViewById(R.id.testSmartKeyboardButton);
        Button testInsertChordsButton = (Button) findViewById(R.id.testInsertChords);

        // Hide the testing buttons for the real app
        testKeyboardButton.setVisibility(View.INVISIBLE);
        testOtherButton.setVisibility(View.INVISIBLE);
        testMelodyButton.setVisibility(View.INVISIBLE);
        testPercussionButton.setVisibility(View.INVISIBLE);
        testSmartKeyboardButton.setVisibility(View.INVISIBLE);
        testInsertChordsButton.setVisibility(View.INVISIBLE);

        //new session button function
        newSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, NewSessionActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        //load session button function
        loadSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, LoadSessionActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        //profile button function
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, UserProfileActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        //music theory info button function
        musicTheoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, MusicTheoryInfoActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        // Button to go the free play keyboard activity (for testing)
        testKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, KeyboardActivity.class);
                startActivity(intent);
            }
        });

        // Button to go an activity to test miscellaneous functionality
        testOtherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, TestOtherActivity.class);
                startActivity(intent);
            }
        });

        // Button to Melody Activity (for testing)
        testMelodyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, MelodyActivity.class));
            }
        });

        // Button to Percussion Activity (for testing)
        testPercussionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, PercussionActivity.class));
            }
        });

        // Button to Smart Keyboard (for testing)
        testSmartKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, SmartKeyboardActivity.class));
            }
        });

        // Button to Insert Chords (for testing)
        testInsertChordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, InsertChordsActivity.class));
            }
        });

    }
}
