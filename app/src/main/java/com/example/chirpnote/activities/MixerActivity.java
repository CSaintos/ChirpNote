package com.example.chirpnote.activities;
import com.example.chirpnote.Mixer;
import com.example.chirpnote.R;
import com.google.android.material.slider.RangeSlider;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MixerActivity extends AppCompatActivity {
    private Mixer mixer;
    private RangeSlider trackSlider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixer);
        trackSlider = findViewById(R.id.Slider);
    }
}
