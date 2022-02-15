package com.example.chirpnote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.R;

public class HomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Button newSessionButton = (Button) findViewById(R.id.newSessionButton);
        Button loadSessionButton = (Button) findViewById(R.id.loadSessionButton);
        Button profileButton = (Button) findViewById(R.id.profileButton);
        Button userOpButton = (Button) findViewById(R.id.userOpButton);
        Button musicTheoryButton = (Button) findViewById(R.id.musicTheoryButton);

        // For testing only; these will be removed eventually
        Button testKeyboardButton = (Button) findViewById(R.id.testKeyboardButton);
        Button testOtherButton = (Button) findViewById(R.id.testOtherButton);
        Button testMelodyActButton = (Button) findViewById(R.id.testMelodyActButton);

        // create my button here, link it button in xml
        Button testSmartKeyboardButton = (Button) findViewById(R.id.testSmartKeyboardButton);


        //new session button function
        newSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, NewSessionActivity.class);
                startActivity(intent);
            }
        });

        //load session button function
        loadSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, LoadSessionActivity.class);
                startActivity(intent);
            }
        });

        //profile button function
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, UserProfileActivity.class);
                startActivity(intent);
            }
        });
        //user options button function
        userOpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, UserOptionsActivity.class);
                startActivity(intent);
            }
        });

        //music theory info button function
        musicTheoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, MusicTheoryInfoActivity.class);
                startActivity(intent);
            }
        });

        // Button to go the free play keyboard activity (for testing)
        testKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, TestMusicActivity.class);
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
        testMelodyActButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, MelodyActivity.class));
            }
        });

        // Button to Melody Activity (for testing)
        testSmartKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, SmartKeyboardActivity.class));
            }
        });

    }
}
