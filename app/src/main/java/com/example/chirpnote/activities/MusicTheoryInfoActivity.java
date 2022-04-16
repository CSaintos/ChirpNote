package com.example.chirpnote.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chirpnote.R;
import com.google.android.material.navigation.NavigationView;

public class MusicTheoryInfoActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    ViewFlipper viewFlipper;
    Button next;
    Button back;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_theory_info);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        next = (Button) findViewById(R.id.next);
        back = (Button) findViewById(R.id.back);

        next.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v == next)
        {
            viewFlipper.showNext();
        }
        else if (v == back)
        {
            viewFlipper.showPrevious();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                redirectActivity(this, HomeScreenActivity.class);
                break;
            case R.id.nav_profile:
                redirectActivity(this, UserProfileActivity.class);
                break;
            case R.id.nav_music_theory:
                // Just close the drawer since we're already on this activity
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_overview:
                Toast.makeText(this, "Overview", Toast.LENGTH_SHORT).show();
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
                redirectActivity(this, KeyboardActivity.class);
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
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
}