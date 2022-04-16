package com.example.chirpnote.activities;
import com.example.chirpnote.Mixer;
import com.example.chirpnote.R;
import com.google.android.material.slider.Slider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MixerActivity extends AppCompatActivity {
    private Slider trackSlider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixer);
        trackSlider = findViewById(R.id.Slider);
        trackSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                System.out.println(value);
            }
        });
    }

}
