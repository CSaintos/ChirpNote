package com.example.chirpnote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {

    private Button newSessionButton = (Button) findViewById(R.id.newSessionButton);
    private Button loadSessionButton = (Button) findViewById(R.id.loadSessionButton);
    private Button profileButton = (Button) findViewById(R.id.profileButton);
    private Button userOpButton = (Button) findViewById(R.id.userOpButton);
    private Button musicTheoryButton = (Button) findViewById(R.id.musicTheoryButton);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //new session button function
        newSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, NewSession.class);
                startActivity(intent);
            }
        });

        //load session button function
        loadSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, LoadSession.class);
                startActivity(intent);
            }
        });

        //profile button function
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, UserProfile.class);
                startActivity(intent);
            }
        });
        //user options button function
        userOpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, UserOptions.class);
                startActivity(intent);
            }
        });

        //music theory info button function
        musicTheoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, MusicTheoryInfo.class);
                startActivity(intent);
            }
        });
    }
}
