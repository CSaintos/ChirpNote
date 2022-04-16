package com.example.chirpnote.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
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
import android.widget.TextView;

import com.example.chirpnote.Key;
import com.example.chirpnote.PercussionTrack;
import com.example.chirpnote.R;
import com.example.chirpnote.Session;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PercussionActivity extends AppCompatActivity {

    private ArrayMap<RadioButton, ArrayList<RadioButton>> chordButtons;
    private ArrayList<String> storedList; // to supposedly store chord's patterns
    private ArrayMap<String, ArrayList<PercussionTrack>> stylePatternMap;

    private LinearLayout chordLayout;
    private RadioGroup chordGroup;
    private RadioGroup styleGroup;
    private RadioGroup patternGroup;
    private Button leftButton;
    private Button rightButton;
    private Button insertButton;
    private Button removeButton;
    private TextView indicator;

    // The driver that allows us to play MIDI notes
    private MidiDriver midiDriver;

    private Session session;
    private Key key;

    private int chordButtonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percussion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize MIDI driver
        midiDriver = MidiDriver.getInstance();

        // TODO: get session from session activity
        key = new Key(Key.RootNote.C, Key.Type.MAJOR);
        // Initialize session
        String basePath = this.getFilesDir().getPath();
        session = new Session("Name", key, 120,
                basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3");

        storedList = new ArrayList<>();
        chordButtons = new ArrayMap<>();
        stylePatternMap = new ArrayMap<>();

        // Initialize stylePatternMap with tracks in assets folder
        try {
            String[] styles = this.getAssets().list("percussion");
            for (String style : styles) {
                stylePatternMap.put(style, new ArrayList<>());
                String[] items = this.getAssets().list("percussion/" + style);
                for (String item : items) {
                    String path = "percussion/" + style + "/" + item;
                    stylePatternMap.get(style).add(new PercussionTrack(item, path, session, this));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }

        // Initialize views
        chordGroup = findViewById(R.id.percussionChordGroup);
        styleGroup = findViewById(R.id.percussionStyleGroup);
        patternGroup = findViewById(R.id.percussionPatternGroup);
        leftButton = (Button) findViewById(R.id.percussionLeftButton);
        rightButton = (Button) findViewById(R.id.percussionRightButton);
        chordLayout = (findViewById(R.id.percussionChordLayout));
        insertButton = findViewById(R.id.percussionInsert);
        removeButton = findViewById(R.id.percussionRemove);
        indicator = findViewById(R.id.percussionIndicator);

        // yes...idk
        chordButtonId = 69;

        // reset views
        chordGroup.removeAllViews();
        styleGroup.removeAllViews();
        patternGroup.removeAllViews();
        chordLayout.removeAllViews();

        // Initialize chords and style scrollbar
        initStyles();
        displayChords();

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

        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    void initStyles() {

        for (int i = 0; i < stylePatternMap.size(); i++) {
            String style = stylePatternMap.keyAt(i);
            RadioButton rb = new RadioButton(this); // in the future, set all views, and then simply enable/disable them.
            // Increases performance by trading processing for memory
            rb.setLayoutParams(styleGroup.getLayoutParams());
            rb.setText(style);
            rb.setButtonTintMode(PorterDuff.Mode.CLEAR);
            rb.setBackground(getDrawable(R.drawable.radio_normal));

            rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
                    if (isChecked) {
                        cb.setBackground(getDrawable(R.drawable.radio_selected));
                        initPatterns(style);
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
        ArrayList<PercussionTrack> patterns = stylePatternMap.get(style);

        for (PercussionTrack pattern : patterns) {
            // Create pattern radio button
            String name = pattern.getLabel().replace(".mid", ""); // returns copy of label
            RadioButton rb = new RadioButton(this);
            rb.setText(name);
            rb.setButtonTintMode(PorterDuff.Mode.CLEAR);
            rb.setBackground(getDrawable(R.drawable.radio_normal));

            // Set button's listener
            rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
                    if (isChecked) {
                        cb.setBackground(getDrawable(R.drawable.radio_selected));
                        pattern.play();
                    } else {
                        cb.setBackground(getDrawable(R.drawable.radio_normal));
                        pattern.stop();
                    }
                }
            });

            // add radio button to radio group
            patternGroup.addView(rb);
        }

        /*
        AssetFileDescriptor afd;
        MediaPlayer mediaPlayer;

        try {
            String[] assetItems = this.getAssets().list("percussion/" + style);

            for (String item : assetItems) { // for each pattern
                String path = "percussion/" + style + "/" + item;

                // testing percussion track initialization
                PercussionTrack percussionTrack = new PercussionTrack(item, path, session, this);

                // get audio asset
                mediaPlayer = new MediaPlayer();
                Log.d("path", path);
                afd = this.getAssets().openFd(path);
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mediaPlayer.prepare();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                // Create Radio button
                String name = item.replace(".mid", "");
                RadioButton rb = new RadioButton(this);
                rb.setText(name);
                Log.d("asset", item);
                rb.setButtonTintMode(PorterDuff.Mode.CLEAR);
                rb.setBackground(getDrawable(R.drawable.radio_normal));

                // Set Radio Button Listener
                MediaPlayer finalMediaPlayer = mediaPlayer;
                rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
                       if (isChecked) {
                           cb.setBackground(getDrawable(R.drawable.radio_selected));
                           finalMediaPlayer.start();
                       } else {
                           cb.setBackground(getDrawable(R.drawable.radio_normal));
                       }
                   }
                });

                // add radio button to radio group
                patternGroup.addView(rb);

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.d("AssetFileDescriptor", "Unable to open file");
        }
         */
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

            chordButtons.get(rb).add(rb0);
            row.addView(rb0);
        }

        chordLayout.addView(row);
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();
        midiDriver.setReverb(ReverbConstants.OFF);
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE, (byte) 0x07, (byte) 80});
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + 1, (byte) 0x07, (byte) 80});
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + 2, (byte) 0x07, (byte) 80});
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + 3, (byte) 0x07, (byte) 80});
        midiDriver.write(new byte[]{MidiConstants.CONTROL_CHANGE + 9, (byte) 0x07, (byte) 127});
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }
}