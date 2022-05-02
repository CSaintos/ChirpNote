package com.example.chirpnote.activities;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.R;
import com.google.android.material.slider.Slider;

public class MixerActivity extends AppCompatActivity {
    private Slider chordsVolumeSlider;
    private Slider composedMelodyVolumeSlider;
    private Slider recordedMelodyVolumeSlider;
    private Slider audioVolumeSlider;
    private Slider percussionVolumeSlider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixer);

        chordsVolumeSlider = findViewById(R.id.chordsVolumeSlider);
        chordsVolumeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                System.out.println(value);

            }
        });

        composedMelodyVolumeSlider = findViewById(R.id.composedMelodyVolumeSlider);
        composedMelodyVolumeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                System.out.println(value);
            }
        });

        recordedMelodyVolumeSlider = findViewById(R.id.recordedMelodyVolumeSlider);
        recordedMelodyVolumeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                System.out.println(value);
            }
        });

        audioVolumeSlider = findViewById(R.id.audioVolumeSlider);
        audioVolumeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                System.out.println(value);
            }
        });

        percussionVolumeSlider = findViewById(R.id.percussionVolumeSlider);
        percussionVolumeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                System.out.println(value);
            }
        });
    }
}
