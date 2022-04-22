package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.chirpnote.Key;
import com.example.chirpnote.PercussionPattern;
import com.example.chirpnote.R;
import com.example.chirpnote.ChirpNoteSession;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.io.IOException;
import java.util.ArrayList;

public class PercussionActivity extends AppCompatActivity {

    private ArrayMap<RadioButton, ArrayList<RadioButton>> chordButtons;
    private ArrayList<String[]> storedList; // to supposedly store chord's patterns
    private ArrayMap<String, ArrayList<PercussionPattern>> stylePatternMap;

    private LinearLayout chordLayout;
    private RadioGroup chordGroup;
    private RadioGroup styleGroup;
    private RadioGroup patternGroup;
    private RadioButton currentPatternButton;
    private Button leftButton;
    private Button rightButton;
    private Button insertButton;
    private Button removeButton;
    private TextView indicator;

    // The driver that allows us to play MIDI notes
    private MidiDriver midiDriver;

    private ChirpNoteSession session;
    private Key key;

    private String selectedStyle;
    private String selectedPattern;

    private int chordButtonId;
    private int chordIndex;

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
        session = new ChirpNoteSession("Name", key, 120,
                basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3", "username");

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
                    stylePatternMap.get(style).add(new PercussionPattern(item, path, session, this));
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

        // identify radio buttons
        chordButtonId = 0;
        // identify chords
        chordIndex = 0;

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
                if (currentPatternButton != null && currentPatternButton.isChecked()) {
                    for (int i = 0; i < chordButtons.size(); i++) {
                        ArrayList<RadioButton> rbs = chordButtons.valueAt(i);
                        for (RadioButton rb : rbs) {
                            if (rb.isChecked()) {
                                storedList.set((int)rb.getTag(R.id.chord_index), new String[] {selectedStyle, selectedPattern});
                                //Log.d("Insert", "index: " + (int)rb.getTag(R.id.chord_index));
                            }
                        }
                    }

                    displayIndicator(selectedStyle, selectedPattern);
                }
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean removed = false;
                for (int i = 0; i < chordButtons.size(); i++) {
                    ArrayList<RadioButton> rbs = chordButtons.valueAt(i);
                    for (RadioButton rb : rbs) {
                        if (rb.isChecked()) {
                            if (!removed) removed = true;
                            storedList.set((int)rb.getTag(R.id.chord_index), new String[] {"null", "null"});
                            //Log.d("Remove", "index: " + (int)rb.getTag(R.id.chord_index));
                        }
                    }
                }
                if (removed) {
                    displayIndicator("null", "null");
                }
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

                        if (currentPatternButton != null) {
                            currentPatternButton.setChecked(false);
                            currentPatternButton = null;
                        }
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
        ArrayList<PercussionPattern> patterns = stylePatternMap.get(style);

        for (PercussionPattern pattern : patterns) {
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
                        currentPatternButton = rb;
                        selectedStyle = style;
                        selectedPattern = pattern.getLabel();
                    } else {
                        cb.setBackground(getDrawable(R.drawable.radio_normal));
                        if (pattern.isPlaying()) pattern.stop();
                    }
                }
            });

            // add radio button to radio group
            patternGroup.addView(rb);
        }
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

        /*
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
         */

        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompoundButton cb = (CompoundButton) v;

                // set all buttons to false
                for (int i = 0; i < chordButtons.size(); i++) {
                    for (int j = 0; j < chordButtons.valueAt(i).size(); j++) {
                        RadioButton temp = chordButtons.valueAt(i).get(j);
                        temp.setChecked(false);
                        temp.setBackground(getDrawable(R.drawable.radio_normal));
                        //Log.d("RadioButton", (String) (temp.getId() + "turned off"));
                    }
                    chordButtons.keyAt(i).setChecked(false);
                }

                String[] indication = null;
                boolean sameIndication = true;
                // select row
                cb.setChecked(true);
                ArrayList<RadioButton> tempButtons = chordButtons.get(cb);
                for (int i = 0; i < tempButtons.size(); i++) {
                    RadioButton temp = tempButtons.get(i);
                    temp.setChecked(true);
                    temp.setBackground(getDrawable(R.drawable.radio_selected));

                    String[] chordInd = storedList.get((int)temp.getTag(R.id.chord_index));
                    if (indication == null) {
                        indication = chordInd;
                    } else {
                        if (!indication[1].equals(chordInd[1])) {
                            sameIndication = false;
                        }
                    }
                }

                // Display indicator for row
                if (sameIndication) {
                    displayIndicator(indication[0], indication[1]);
                } else {
                    displayIndicator("null", "null");
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
            rb0.setTag(R.id.chord_index, chordIndex);
            chordIndex++;

            rb0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CompoundButton cb = (CompoundButton) v;
                    cb.setChecked(true);
                    cb.setBackground(getDrawable(R.drawable.radio_selected));

                    // set all other buttons to false
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


                    // update displayIndicator
                    int index = (int) rb0.getTag(R.id.chord_index);
                    Log.d("Retrieve", "index: " + index);
                    String[] indication = storedList.get(index);
                    displayIndicator(indication[0], indication[1]);
                }
            });

            chordButtons.get(rb).add(rb0);
            row.addView(rb0);

            storedList.add(new String[] {"null", "null"});
        }

        chordLayout.addView(row);
    }

    void displayIndicator(String style, String pattern) {
        indicator.setText("style\n" + style + "\npattern\n" + pattern);
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