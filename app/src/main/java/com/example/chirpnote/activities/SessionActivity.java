package com.example.chirpnote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.AudioTrack;
import com.example.chirpnote.Chord;
import com.example.chirpnote.ConstructedMelody;
import com.example.chirpnote.Key;
import com.example.chirpnote.MusicNote;
import com.example.chirpnote.R;
import com.example.chirpnote.Session;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class SessionActivity extends AppCompatActivity {
    // Sam
    // A list of chords
    private ArrayList<Chord> keyChords;
    private ArrayList<Chord> suggestedChords;
    private Key currentKey;
    // end Sam

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Session session = (Session) getIntent().getSerializableExtra("session");
        ((TextView) findViewById(R.id.sessionNameText)).setText("Session Name: " + session.getName());
        ((TextView) findViewById(R.id.tempoText)).setText("Tempo: " + session.getTempo() + " BPM");
        ((TextView) findViewById(R.id.keyText)).setText("Key: " + session.getKey());

        Button nextActivityButton = (Button) findViewById(R.id.goToNextActivityButton);
        if(session.isConstructedMelodyRecorded() && session.isAudioRecorded()) {
            nextActivityButton.setEnabled(true);
        } else {
            nextActivityButton.setEnabled(false);
        }

        Button generateMelodyButton = (Button) findViewById(R.id.generateMelodyButton);
        ConstructedMelody melody = new ConstructedMelody(session);

        Button recAudioButton = (Button) findViewById(R.id.recAudioButton3);
        AudioTrack audio = new AudioTrack(session);


        // Sam's section
        Button chordSuggestion = (Button) findViewById(R.id.chordSuggestionButton);

        keyChords = new ArrayList<>();
        currentKey = session.getKey(); // gets the key set when session was initialized
        for (int i = 0; i < currentKey.getScaleNotes().length; i++)
        {
            // TODO: Think of a better way to do this
            int rootIdx = (currentKey.getScaleNotes()[i] - 60) % 12;
            /** arraylist of all chords that belong to the current key based on the type of chord
             * it takes in the root note of the chord and type of chord
             */
            keyChords.add(new Chord(Chord.RootNote.values()[rootIdx], currentKey.getChordTypes()[i], session.getTempo()));
        }


        suggestedChords = new ArrayList<>();
        Chord inputChord = keyChords.get(3); // arbitrary chord choice to test chord suggestion
        suggestedChords = getSuggestedChords(inputChord, keyChords);
        // end of Sam's section


        generateMelodyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                melody.startRecording();
                melody.addNote(new MusicNote(ThreadLocalRandom.current().nextInt(50, 70)),
                        ConstructedMelody.NoteDuration.QUARTER_NOTE, session.mNextMelodyTick);
                melody.addNote(new MusicNote(ThreadLocalRandom.current().nextInt(50, 70)),
                        ConstructedMelody.NoteDuration.QUARTER_NOTE, session.mNextMelodyTick);
                melody.addNote(new MusicNote(ThreadLocalRandom.current().nextInt(50, 70)),
                        ConstructedMelody.NoteDuration.QUARTER_NOTE, session.mNextMelodyTick);
                melody.addNote(new MusicNote(ThreadLocalRandom.current().nextInt(50, 70)),
                        ConstructedMelody.NoteDuration.QUARTER_NOTE, session.mNextMelodyTick);
                generateMelodyButton.setText("Melody generated!");
                session.setConstructedMelodyRecorded();
                if(audio.isRecorded()){
                    nextActivityButton.setEnabled(true);
                }
            }
        });

        recAudioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!audio.isRecording()){
                    recAudioButton.setText("End Recording");
                    audio.startRecording();
                } else {
                    recAudioButton.setText("Record Audio");
                    audio.stopRecording();
                    session.setAudioRecorded();
                    if(melody.isRecorded()){
                        nextActivityButton.setEnabled(true);
                    }
                }
            }
        });

        nextActivityButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SessionActivity.this, TestTrackPersistenceActivity.class);
                intent.putExtra("session", session);
                startActivity(intent);
            }
        });


    }

    // sam

    /**
     * This function takes in the latest chord chosen by the user in the session and the set of
     * all the chords found in the current key of session and returns a list of suggested chords
     * back to the user to help them make a choice
     *
     * @param inputChord Latest chord in session
     * @param keyChords List of all the chords found in the key
     * @return List of suggested chords
     */
    private ArrayList<Chord> getSuggestedChords(Chord inputChord, ArrayList<Chord> keyChords)
    {
        ArrayList<Chord> listOfChords = new ArrayList<Chord>();
        int indexOfInputChord = keyChords.indexOf(inputChord); // grabs the index of chord to determine what roman numeral it is in

        /** // used to test, delete later
        System.out.println("======================LOOK HERE===========================");
        int length = keyChords.size();
        System.out.println("length of keychords = " + length);
        System.out.println("root of keyChords.get(0) = " + keyChords.get(0).getRoot());
        System.out.println("root of keyChords.get(1) = " + keyChords.get(1).getRoot());
        Chord tempChord = keyChords.get(0);
        System.out.println("root of tempChord.get(0) = " + tempChord);
         */

        // it doesn't matter if its major or minor since they share most of the same chord suggestions with the exception of VII which we're not using
        // if statements used to collect all the chord suggestions that pertain to the chord found at index of input chord
        if (indexOfInputChord == 0 || indexOfInputChord == 7) // I chord
        {
            for (int i = 1; i < 7; i++)
            {
                listOfChords.add(keyChords.get(i)); // a I chord can go virtually anywhere
            }
        }
        else if (indexOfInputChord == 1) // ii Chord
        {
            listOfChords.add(keyChords.get(6));
            listOfChords.add(keyChords.get(4));
        }
        else if (indexOfInputChord == 2) // iii Chord
        {
            listOfChords.add(keyChords.get(3));
            listOfChords.add(keyChords.get(5));
        }
        else if (indexOfInputChord == 3) // IV Chord
        {
            listOfChords.add(keyChords.get(1));
            listOfChords.add(keyChords.get(4));
            listOfChords.add(keyChords.get(6));
        }
        else if (indexOfInputChord == 4) // V chord
        {
            listOfChords.add(keyChords.get(1));
            listOfChords.add(keyChords.get(5));
            listOfChords.add(keyChords.get(7));
        }
        else if (indexOfInputChord == 5) // vi chord
        {
            listOfChords.add(keyChords.get(3));
            listOfChords.add(keyChords.get(1));
        }
        else // vii* chord
        {
            listOfChords.add(keyChords.get(1));
            listOfChords.add(keyChords.get(5));
        }

        /** testing the output of getChordSuggestion() in the terminal */
        for (int i = 0; i < currentKey.getScaleNotes().length; i++)
        {
            int chord = i + 1;
            System.out.println("chord " + chord + ": " + currentKey.getScaleNotes()[i] );
        }
        System.out.println("Key name = " + currentKey);
        System.out.println("Input Chord  = " + inputChord);
        System.out.println("Index of input chord = " + indexOfInputChord);
        System.out.println("Size of suggested chords list = " + listOfChords.size());
        System.out.println("Expected suggested chords = B Minor, E Major, G# Diminished");
        System.out.println("Actual suggested chords = " + listOfChords.get(0) + ", " + listOfChords.get(1) + ", " + listOfChords.get(2));

        return listOfChords;
    }
    // end sam
}