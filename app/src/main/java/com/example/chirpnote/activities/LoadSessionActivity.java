package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.FragmentTransitionSupport;

import android.os.Bundle;

import com.example.chirpnote.R;

public class LoadSessionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_session);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}