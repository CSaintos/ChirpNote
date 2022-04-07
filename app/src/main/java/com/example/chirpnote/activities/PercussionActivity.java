package com.example.chirpnote.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.chirpnote.R;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.util.ArrayList;

public class PercussionActivity extends AppCompatActivity {

    private ArrayMap<RadioButton, ArrayList<RadioButton>> chordButtons;
    private ArrayList<ArrayList<RadioButton>> patternList;
    private ArrayList<RadioButton> styleList;
    //private ArrayList<TableRow> tableRows;
    private String[] styles;

    //private TableLayout tableLayout;
    private LinearLayout chordLayout;
    private RadioGroup chordGroup;
    private RadioGroup styleGroup;
    private RadioGroup patternGroup;
    //private Button backButton;
    private Button leftButton;
    private Button rightButton;

    // The driver that allows us to play MIDI notes
    private MidiDriver midiDriver;

    private int chordButtonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percussion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize MIDI driver
        midiDriver = MidiDriver.getInstance();

        //tableRows = new ArrayList<>();
        patternList = new ArrayList<>();
        styleList = new ArrayList<>();
        chordButtons = new ArrayMap<>();
        styles = new String[]{"Pop", "Rock"};

        chordGroup = findViewById(R.id.percussionChordGroup);
        styleGroup = findViewById(R.id.percussionStyleGroup);
        patternGroup = findViewById(R.id.percussionPatternGroup);
        //backButton = (Button) findViewById(R.id.percussionbackbutton);
        leftButton = (Button) findViewById(R.id.percussionLeftButton);
        rightButton = (Button) findViewById(R.id.percussionRightButton);
        chordLayout = (findViewById(R.id.percussionChordLayout));

        chordButtonId = 69;
        /*
        tableLayout = findViewById(R.id.percussionTableLayout);
        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            tableRows.add((TableRow) tableLayout.getChildAt(i));
        }*/

        chordGroup.removeAllViews();
        styleGroup.removeAllViews();
        patternGroup.removeAllViews();
        chordLayout.removeAllViews();

        initStyles();
        displayChords();

        /*
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(PercussionActivity.this, SessionActivity.class));
                startActivity(new Intent(PercussionActivity.this, HomeScreenActivity.class));
            }
        });*/

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    void displayChords() {

        for (int i = 0; i < 4; i++) {
            addRow();
        }
    }

    void addRow() {
        LinearLayout row = new LinearLayout(this);

        row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setDividerPadding(10);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(150, 80);

        // Children of row Linear Layout
        RadioButton rb = new RadioButton(this);
        layoutParams.setMargins(70, 10, 70, 10);
        rb.setLayoutParams(layoutParams);
        rb.setId(chordButtonId);
        chordButtonId++;

        rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
                if (isChecked) {
                    ArrayList<RadioButton> tempButtons = chordButtons.get(cb);
                    for (int i = 0; i < tempButtons.size(); i++) {
                        RadioButton temp = tempButtons.get(i);
                        temp.setChecked(true);
                        temp.setBackground(getDrawable(R.drawable.radio_selected));
                    }
                } else {

                }
            }
        });

        chordButtons.put(rb, new ArrayList<>());
        row.addView(rb);

        layoutParams.setMargins(20, 10, 20, 10);

        for (int i = 0; i < 4; i++) {
            RadioButton rb0 = new RadioButton(this);
            rb0.setLayoutParams(layoutParams);
            rb0.setBackground(getDrawable(R.drawable.radio_selector));
            rb0.setText("I");
            rb0.setId(chordButtonId);
            chordButtonId++;

            rb0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CompoundButton cb = (CompoundButton) v;
                    cb.setChecked(true);
                    cb.setBackground(getDrawable(R.drawable.radio_selected));

                    for (int i = 0; i < chordButtons.size(); i++) {
                        for (int j = 0; j < chordButtons.valueAt(i).size(); j++) {
                            RadioButton temp = chordButtons.valueAt(i).get(j);
                            if (cb.getId() != temp.getId()) {
                                temp.setChecked(false);
                                temp.setBackground(getDrawable(R.drawable.radio_normal));
                                //Log.d("RadioButton", (String) (temp.getId() + "turned off"));
                            }
                        }
                        chordButtons.keyAt(i).setChecked(false);
                    }
                }
            });

            /*
            rb0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
                    if (isChecked) {
                        cb.setBackground(getDrawable(R.drawable.radio_selected));
                        for (int i = 0; i < chordButtons.size(); i++) {
                            for (int j = 0; j < chordButtons.valueAt(i).size(); j++) {
                                RadioButton temp = chordButtons.valueAt(i).get(j);
                                if (cb.getId() != temp.getId()) {
                                    temp.setChecked(false);
                                    temp.setBackground(getDrawable(R.drawable.radio_normal));
                                }
                            }
                            chordButtons.keyAt(i).setChecked(false);
                        }
                    } else {
                        cb.setBackground(getDrawable(R.drawable.radio_normal));
                    }
                }
            });*/

            chordButtons.get(rb).add(rb0);
            row.addView(rb0);
        }

        chordLayout.addView(row);
    }

    /*
    void displayChords() {
        int chordCount = 0;
        for (TableRow row : tableRows) {
            row.removeAllViews();
            for (int i = 0; i < 3; i++) {
                RadioButton rb = new RadioButton(this);
                rb.setLayoutParams(row.getLayoutParams());
                rb.setText(("Chord " + chordCount));
                rb.setButtonTintMode(PorterDuff.Mode.CLEAR);
                rb.setBackground(getDrawable(R.drawable.radio_normal));

                // Set chord radio button listener
                rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
                        if (isChecked) {
                            cb.setBackground(getDrawable(R.drawable.radio_selected));
                        } else {
                            cb.setBackground(getDrawable(R.drawable.radio_normal));
                        }
                    }
                });
                chordCount++;
                //chordGroup.addView(rb);
                row.addView(rb);
            }
        }
    }*/

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