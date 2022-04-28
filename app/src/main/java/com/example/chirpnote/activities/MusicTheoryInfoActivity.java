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

public class MusicTheoryInfoActivity extends AppCompatActivity implements View.OnClickListener {

    ViewFlipper viewFlipper;
    Button next;
    Button back;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_theory_info);

        //actionbar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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