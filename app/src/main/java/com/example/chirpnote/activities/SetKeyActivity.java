package com.example.chirpnote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.ChirpNoteSession;
import com.example.chirpnote.Key;
import com.example.chirpnote.R;

import java.util.ArrayList;
import java.util.List;

public class SetKeyActivity extends AppCompatActivity {

    private ChirpNoteSession session;
    private String keyNameChoice;
    private String keyTypeChoice;
    private List<String> keyTypeList = new ArrayList<>();
    private List<String> keyNameList = new ArrayList<>();
    boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_key);

        initializeKeyNameList();
        initializeKeyTypeList();

        Spinner keyNameSpinner = findViewById(R.id.spinner_key_name);
        ArrayAdapter keyNameAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, keyNameList);
        keyNameSpinner.setAdapter(keyNameAdapter);

        Spinner keyTypeSpinner = findViewById(R.id.spinner_key_type);
        ArrayAdapter keyTypeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, keyTypeList);
        keyTypeSpinner.setAdapter(keyTypeAdapter);



        Intent intent = getIntent();
        if (intent != null)
        {
//            String intentName = intent.getComponent().getClassName(); // intentName = com.example.chirpnote.activities.SetKeyActivity <- WRONG NAME
//            System.out.println("================================================================================== intentName = " + intentName);
            String intentName = getIntent().getType(); // doesn't work
            System.out.println("================================================================================== intentName = " + intentName);

            //TODO: Need to find a way to get the name of setkeyfromsong so that i can use the passed in string array to set the key

            if (intentName != null && intentName.equals("fromSetKeyFromSong"))
            {
                String[] checkFlag = (String[]) intent.getSerializableExtra("fromSetKeyFromSong"); // str[0] = keyname, str[1] = keytype
                keyNameChoice = checkFlag[0]; // e.g. C
                keyTypeChoice = checkFlag[1]; // e.g. Major

                int keyNamePosition = keyNameAdapter.getPosition(keyNameChoice);
                keyNameSpinner.setSelection(keyNamePosition);
                int keyTypePosition = keyNameAdapter.getPosition(keyTypeChoice);
                keyTypeSpinner.setSelection(keyTypePosition);
            }
            else// if (intentName != null && intentName.equals("SessionFreePlay"))
            {
                session = (ChirpNoteSession) getIntent().getSerializableExtra("SessionFreePlay"); // coming from keyboard activity
                System.out.println("key = " + session.getKey().toString());
            }

        }

//        String checkFlag = intent.getStringExtra("flag");
//        if (checkFlag.equals("A")){
//            flag = true;
//        }
//        else
//            flag = false;




        // Button to go the set key from song activity (for testing)
        Button setKeyTestButton = (Button) findViewById(R.id.changeKeyFromSongButton);
        setKeyTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetKeyActivity.this, SetKeyFromSongActivity.class);
//                intent.putExtra("session", session);
                intent.putExtra("flag", "fromSetKeyActivity");
                startActivity(intent);
            }
        });

        Button setKeyManuallyButton = (Button) findViewById(R.id.setKeyManuallyButton);
        setKeyManuallyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keyNameChoice.equals("Key Name") || keyTypeChoice.equals("Key Type"))
                {
                    Toast.makeText(getApplicationContext(), "Please make proper selection.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Key.RootNote newRootNote = Key.RootNote.returnRootNote(keyNameChoice);
                    Key.Type newKeyType = Key.Type.valueOf(keyTypeChoice.toUpperCase());

                    session.setKey(new Key(newRootNote, newKeyType));

                    Toast.makeText(getApplicationContext(), "New Key: " + keyNameChoice + " " + keyTypeChoice, Toast.LENGTH_LONG).show();
                }
            }
        });

        Button confirmChangesButton = (Button) findViewById(R.id.confirmChangesButton);
        confirmChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(SetKeyActivity.this, KeyboardActivity.class);
                intent.putExtra("session", session);
                startActivity(intent);
            }
        });


        // set user input key name and type to new key in session


        keyNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                keyNameChoice = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getApplicationContext(), keyNameChoice, Toast.LENGTH_LONG).show();
//                Toast.makeText(SmartKeyboardActivity.this, keyNameChoice, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        keyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                keyTypeChoice = parent.getItemAtPosition(position).toString();
//                Toast.makeText(getApplicationContext(), keyTypeChoice, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void initializeKeyNameList()
    {
        keyNameList.add("Key Name");
        for (int i = 0; i < Key.RootNote.values().length; i++)
        {
            keyNameList.add(Key.RootNote.values()[i].toString());
        }
    }

    private void initializeKeyTypeList()
    {
        keyTypeList.add("Key Type");
        keyTypeList.add("Major");
        keyTypeList.add("Minor");
    }

}
