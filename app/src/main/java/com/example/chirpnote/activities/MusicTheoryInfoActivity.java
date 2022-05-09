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
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chirpnote.ChirpNoteUser;
import com.example.chirpnote.R;
import com.google.android.material.navigation.NavigationView;

public class MusicTheoryInfoActivity extends AppCompatActivity implements View.OnClickListener {
    ChirpNoteUser user;

    ViewFlipper viewFlipper;
    Button next;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_theory_info);
        hideSystemBars();

        user = (ChirpNoteUser) getIntent().getSerializableExtra("user");

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
    public void onBackPressed(){
        Intent intent = new Intent(MusicTheoryInfoActivity.this, HomeScreenActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(MusicTheoryInfoActivity.this, HomeScreenActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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