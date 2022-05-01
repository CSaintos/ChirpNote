package com.example.chirpnote.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chirpnote.Key;
import com.example.chirpnote.PercussionPattern;
import com.example.chirpnote.PercussionTrack;
import com.example.chirpnote.R;
import com.example.chirpnote.ChirpNoteSession;
import com.google.android.material.navigation.NavigationView;

import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;
import org.billthefarmer.mididriver.ReverbConstants;

import java.io.IOException;
import java.util.ArrayList;

public class PercussionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static ArrayMap<String, ArrayList<PercussionPattern>> stylePatternMap;

    private ArrayMap<RadioButton, ArrayList<RadioButton>> chordButtons;
    private ArrayList<String[]> storedList; // to supposedly store chord's patterns

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
    private DrawerLayout drawer;

    // The driver that allows us to play MIDI notes
    private MidiDriver midiDriver;
    private PercussionTrack track;
    private ChirpNoteSession session;
    private Key key;

    private String selectedStyle;
    private String selectedPattern;

    private int chordButtonId;
    private int chordIndex;
    private int percussionIndex;
    private int measureIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percussion);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //navigation drawer
        Toolbar toolbar = findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // Initialize MIDI driver
        midiDriver = MidiDriver.getInstance();

        // TODO: get session from session activity
        key = new Key(Key.RootNote.C, Key.Type.MAJOR);
        // Initialize session
        String basePath = this.getFilesDir().getPath();
        session = new ChirpNoteSession("Name", key, 120,
                basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3", "username");
        track = new PercussionTrack(session);
        track.startRecording();
        percussionIndex = 0;

        storedList = new ArrayList<>();
        chordButtons = new ArrayMap<>();
        stylePatternMap = new ArrayMap<>();

        // Initialize stylePatternMap with tracks in assets folder
        try {
            String[] styles = this.getAssets().list("percussion");
            for (int i = 0; i < styles.length; i++) {
                stylePatternMap.put(styles[i], new ArrayList<>());
                String[] patterns = this.getAssets().list("percussion/" + styles[i]);
                for (int j = 0; j < patterns.length; j++) {
                    PercussionPattern.PatternAsset patternAsset = new PercussionPattern.PatternAsset(patterns[j], styles[i], j, i);
                    //Log.d("PatternAssets", patterns[j]);
                    stylePatternMap.get(styles[i]).add(new PercussionPattern(patternAsset, session, this));
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
        // measure index
        measureIndex = 0;

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
                previous16Measures();
                String[] measures = get16Measures();
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next16Measures();
                String[] measures = get16Measures();
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
                                int position = (int)rb.getTag(R.id.percussion_index);
                                PercussionPattern pp = null;
                                for (PercussionPattern p : stylePatternMap.get(selectedStyle)) {
                                    PercussionPattern.PatternAsset patternAsset = p.getPatternAsset();
                                    if (patternAsset.patternStr.equals(selectedPattern)) pp = p;
                                }
                                track.addPattern(pp, position);
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
                            int position = (int)rb.getTag(R.id.percussion_index);
                            //track.addPattern(constructPattern("null", "null"), position);
                            track.addPattern(null, position);
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
            PercussionPattern.PatternAsset patternAsset = pattern.getPatternAsset();
            String name = patternAsset.patternStr.replace(".mid", "");
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
                        selectedPattern = patternAsset.patternStr;
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
            rb0.setTag(R.id.percussion_index, percussionIndex);
            percussionIndex++;

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


    public String[] get16Measures() {
        ArrayList<String> measures = new ArrayList<>();
        int index = measureIndex;
        int size = 0;
        boolean underSized = true;

        if (session.mPercussionPatterns.size() == 0) {
            return new String[0];
        }

        while (index < session.mPercussionPatterns.size() && underSized) {
            measures.add(session.mPercussionPatterns.get(index));
            index++;
            size++;
            if (size > 15) {
                underSized = false;
            }
        }

        return measures.toArray(new String[0]);
    }

    public void next16Measures() {
        String[] measures = get16Measures();
        if (measures.length == 16) {
            measureIndex+=16;
        }
    }

    public void previous16Measures() {
        if (measureIndex != 0) {
            measureIndex-=16;
        }
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

    // navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                redirectActivity(this, HomeScreenActivity.class);
                break;
            case R.id.nav_overview:
                Toast.makeText(this, "Overview", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_melody:
                redirectActivity(this, MelodyActivity.class);
                break;
            case R.id.nav_chords:
                redirectActivity(this, InsertChordsActivity.class);
                break;
            case R.id.nav_percussion:
                // Just close the drawer since we're already on this activity
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_keyboard:
                redirectActivity(this, KeyboardActivity.class);
                break;
            case R.id.nav_mixer:
                Toast.makeText(this, "Mixer", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_audio:
                redirectActivity(this, RecordAudioActivity.class);
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
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