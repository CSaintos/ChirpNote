package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.chirpnote.R;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.util.ArrayList;

public class PercussionActivity extends AppCompatActivity {

    private ArrayList<ArrayList<RadioButton>> patternList;
    private ArrayList<RadioButton> styleList;
    private String[] styles;

    private RadioGroup styleGroup;
    private RadioGroup patternGroup;
    private Button backButton;

    // The driver that allows us to play MIDI notes
    private MidiDriver midiDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percussion);

        // Initialize MIDI driver
        midiDriver = MidiDriver.getInstance();

        patternList = new ArrayList<>();
        styleList = new ArrayList<>();
        styles = new String[]{"Pop", "Rock"};

        styleGroup = findViewById(R.id.percussionStyleGroup);
        patternGroup = findViewById(R.id.percussionPatternGroup);
        backButton = (Button) findViewById(R.id.percussionbackbutton);

        styleGroup.removeAllViews();
        patternGroup.removeAllViews();

        initStyles();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(PercussionActivity.this, SessionActivity.class));
                startActivity(new Intent(PercussionActivity.this, HomeScreenActivity.class));
            }
        });
    }

    void initStyles() {

        for (String str : styles) {
            RadioButton rb = new RadioButton(this);
            rb.setLayoutParams(styleGroup.getLayoutParams());
            rb.setText(str);
            rb.setButtonTintMode(PorterDuff.Mode.CLEAR);
            rb.setBackground(getDrawable(R.drawable.radio_normal));

            rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
                   if (isChecked) {
                       cb.setBackground(getDrawable(R.drawable.radio_selected));
                       initPatterns(str);
                   } else {
                       cb.setBackground(getDrawable(R.drawable.radio_normal));
                   }
               }
            });

            styleGroup.addView(rb);
        }
    }

    void initPatterns(String style) {
        patternGroup.removeAllViews();
        int patternNum = 1;

        // Percussion code here... FIXME
        MediaPlayer rockPlayer = MediaPlayer.create(this, R.raw.rock_drums);
        rockPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // FIXME: Include session in params
        //Percussion percussion = new Percussion(this);
        // End of percussion code

        // Create Radio button(s)
        RadioButton rb = new RadioButton(this);
        rb.setText(("Beat_" + patternNum));
        rb.setButtonTintMode(PorterDuff.Mode.CLEAR);
        rb.setBackground(getDrawable(R.drawable.radio_normal));

        // Set Radio Button listener
        rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
                if (isChecked) {
                    cb.setBackground(getDrawable(R.drawable.radio_selected));
                    // Test rock playback
                    rockPlayer.start();
                    //percussion.playRock();
                } else {
                    cb.setBackground(getDrawable(R.drawable.radio_normal));
                }
            }
        });

        // Add radio button to radio group
        patternGroup.addView(rb);
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();
        midiDriver.setReverb(ReverbConstants.OFF);
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + 9, (byte) 0x07, (byte) 127});
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE, (byte) 0x07, (byte) 90});
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }
}