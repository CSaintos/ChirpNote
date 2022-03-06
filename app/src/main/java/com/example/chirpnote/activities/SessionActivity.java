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
        if(session.isMelodyRecorded() && session.isAudioRecorded()) {
            nextActivityButton.setEnabled(true);
        } else {
            nextActivityButton.setEnabled(false);
        }

        Button generateMelodyButton = (Button) findViewById(R.id.generateMelodyButton);
        ConstructedMelody melody = new ConstructedMelody(session);

        Button recAudioButton = (Button) findViewById(R.id.recAudioButton3);
        AudioTrack audio = new AudioTrack(session);

//
//        // Sam's section
//        Button chordSuggestion = (Button) findViewById(R.id.chordSuggestionButton);
//
//        keyChords = new ArrayList<>();
//        Key currentKey = session.getKey(); // gets the key set when session was initialized
//        for (int i = 0; i < session.getKey().getScaleNotes().length; i++)
//        {
//            /** arraylist of all chords that belong to the current key based on the type of chord
//             * it takes in the type of chord and the MIDI int value of the root of the chord
//             */
//            keyChords.add(new Chord(currentKey.getKeyChordTypes()[i], currentKey.getScaleNotes()[i]));
//        }
//
//        suggestedChords = new ArrayList<>();
//        Chord inputChord = keyChords.get(0); // arbitrary chord choice to test chord suggestion
//        suggestedChords = getSuggestedChords(inputChord, keyChords);
//        // end of Sam's section


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
                melody.stopRecording();
                generateMelodyButton.setText("Melody generated!");
                session.setMelodyRecorded();
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

//    // sam
//
//    /**
//     * this function takes in the lastest chord in chosen by the user in the session and the set of
//     * all the chords found in the current key of session and returns a list of suggested chords
//     * back to the user to help them make a choice
//     *
//     * @param inputChord lastest chord in session
//     * @param keyChords list of all the chords found in the key
//     * @return list of suggested chords
//     */
//    public ArrayList<Chord> getSuggestedChords(Chord inputChord, ArrayList<Chord> keyChords)
//    {
//        ArrayList<Chord> listOfChords = null;
//        int indexOfInputChord = keyChords.indexOf(inputChord); // grabs the index of chord to determine what roman numeral it is in
//
//        // it doesn't matter if its major or minor since they share most of the same chord suggestions with the exception of VII which we're not using
//        // if statements used to collect all the chord suggestions that pertain to the chord found at index of input chord
//        if (indexOfInputChord == 0 || indexOfInputChord == 7) // I chord
//        {
//            for (int i = 1; i < 8; i++)
//            {
//                listOfChords.add(keyChords.get(i));
//            }
//        }
//        else if (indexOfInputChord == 1) // ii Chord
//        {
//            listOfChords.add(keyChords.get(6));
//            listOfChords.add(keyChords.get(4));
//        }
//        else if (indexOfInputChord == 2) // iii Chord
//        {
//            listOfChords.add(keyChords.get(3));
//            listOfChords.add(keyChords.get(5));
//        }
//        else if (indexOfInputChord == 3) // IV Chord
//        {
//            listOfChords.add(keyChords.get(1));
//            listOfChords.add(keyChords.get(6));
//            listOfChords.add(keyChords.get(4));
//        }
//        else if (indexOfInputChord == 4) // V chord
//        {
//            listOfChords.add(keyChords.get(1));
//            listOfChords.add(keyChords.get(5));
//            listOfChords.add(keyChords.get(7));
//        }
//        else if (indexOfInputChord == 5) // vi chord
//        {
//            listOfChords.add(keyChords.get(3));
//            listOfChords.add(keyChords.get(1));
//        }
//        else // vii* chord
//        {
//            listOfChords.add(keyChords.get(1));
//            listOfChords.add(keyChords.get(5));
//        }
//
//        return listOfChords;
//    }
//    // end sam
}