package com.example.chirpnote.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.chirpnote.ChirpNoteSession;
import com.example.chirpnote.ChirpNoteUser;
import com.example.chirpnote.Key;
import com.example.chirpnote.R;

import java.util.ArrayList;
import java.util.List;

public class SetKeyActivity extends AppCompatActivity {

    private ChirpNoteSession session;
    private ChirpNoteUser user;
    private String keyNameChoice;
    private String keyTypeChoice;
    private List<String> keyTypeList = new ArrayList<>();
    private List<String> keyNameList = new ArrayList<>();
    private String flag;

    private Button confirmChangesButton;
    private Key newKey;
//    private TextWatcher checkTextValid = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
//
//        @Override
//        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}
//
//        @Override
//        public void afterTextChanged(Editable editable) {
//            String name = keyNameChoice;
//            String type = keyTypeChoice;
//
//            if(name.equals("Key Name") || type.equals("Key Type")){
//                confirmChangesButton.setEnabled(false);
//            } else {
//                confirmChangesButton.setEnabled(true);
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_key);
        hideSystemBars();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Set Key");

        user = (ChirpNoteUser) getIntent().getSerializableExtra("user");
        flag = getIntent().getStringExtra("flag");

        Button confirmChangesButton = (Button) findViewById(R.id.confirmChangesButton);
        confirmChangesButton.setEnabled(false);

        initializeKeyNameList();
        initializeKeyTypeList();

        Spinner keyNameSpinner = findViewById(R.id.spinner_key_name);
        ArrayAdapter keyNameAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, keyNameList);
        keyNameSpinner.setAdapter(keyNameAdapter);

        Spinner keyTypeSpinner = findViewById(R.id.spinner_key_type);
        ArrayAdapter keyTypeAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, keyTypeList);
        keyTypeSpinner.setAdapter(keyTypeAdapter);

        Intent intent = getIntent();
        if (intent != null && flag != null)
        {
            if (flag.equals("fromSetKeyFromSongActivity"))
            {
                System.out.println("intent = " + "fromSetKeyFromSongActivity");

                String[] keyArray = (String[]) intent.getSerializableExtra("keyArray"); // str[0] = keyname, str[1] = keytype
                session = (ChirpNoteSession) getIntent().getSerializableExtra("session");
                keyNameChoice = keyArray[0]; // e.g. C
                keyTypeChoice = keyArray[1]; // e.g. Major

//                newKey = new Key(Key.RootNote.returnRootNote(keyNameChoice), Key.Type.valueOf(keyTypeChoice.toUpperCase()));

                System.out.println("keyName from SetKeyFromSongActivity = " + keyNameChoice);
                System.out.println("keyType from SetKeyFromSongActivity = " + keyTypeChoice);

                int keyNamePosition = keyNameAdapter.getPosition(keyNameChoice);
                keyNameSpinner.setSelection(keyNamePosition);
                int keyTypePosition = keyTypeAdapter.getPosition(keyTypeChoice);
                keyTypeSpinner.setSelection(keyTypePosition);
            }
            else if (flag.equals("fromSmartKeyboardActivity"))
            {
                System.out.println("intent = " + "fromSmartKeyboardActivity");

                session = (ChirpNoteSession) getIntent().getSerializableExtra("session"); // coming from keyboard activity
                System.out.println("key = " + session.getKey().toString());
            }
            else if (flag.equals("fromNewSessionActivity"))
            {
                session = (ChirpNoteSession) getIntent().getSerializableExtra("session"); // coming from keyboard activity
            }

        }

        // Button to go the set key from song activity
        Button setKeyTestButton = (Button) findViewById(R.id.changeKeyFromSongButton);
        setKeyTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetKeyActivity.this, SetKeyFromSongActivity.class);
//                intent.putExtra("session", session);
                intent.putExtra("flag", "fromSetKeyActivity");
                intent.putExtra("session", session);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        confirmChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (keyNameChoice.equals("Key Name") || keyTypeChoice.equals("Key Type"))
//                {
//                    Toast.makeText(getApplicationContext(), "Please make proper selection.", Toast.LENGTH_LONG).show();
//                }
//                else {
                    Key.RootNote newRootNote = Key.RootNote.returnRootNote(keyNameChoice);
                    Key.Type newKeyType = Key.Type.valueOf(keyTypeChoice.toUpperCase());


                    newKey = new Key(newRootNote, newKeyType);
                    session.setKey(newKey);
//                }

                Intent intent = getIntent();
                if (flag.equals("fromSmartKeyboardActivity"))
                {
                    intent = new Intent(SetKeyActivity.this, SmartKeyboardActivity.class);
                }
                else if (flag.equals("fromNewSessionActivity"))
                {
                    intent = new Intent(SetKeyActivity.this, NewSessionActivity.class);
                }
                else if (flag.equals("fromSetKeyFromSongActivity"))
                {
                    intent = new Intent(SetKeyActivity.this, NewSessionActivity.class);
                }
                intent.putExtra("flag", "fromSetKeyActivity");
                intent.putExtra("session", session);
                intent.putExtra("user", user);
                intent.putExtra("newKey", newKey);
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
//                System.out.println("keyName from SetKeyActivity = " + keyNameChoice);

                if (keyNameChoice == "Key Name" || keyTypeChoice == "Key Type")
                {
                    confirmChangesButton.setEnabled(false);
                }
                else
                {
                    confirmChangesButton.setEnabled(true);
                }
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
//                System.out.println("keyType from SetKeyActivity = " + keyTypeChoice);

                if (keyNameChoice == "Key Name" || keyTypeChoice == "Key Type")
                {
                    confirmChangesButton.setEnabled(false);
                }
                else
                {
                    confirmChangesButton.setEnabled(true);
                }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = getIntent();
        switch(item.getItemId()) {
            case android.R.id.home:
                if (flag.equals("fromSmartKeyboardActivity"))
                {
                    intent = new Intent(SetKeyActivity.this, SmartKeyboardActivity.class);
                }
                else if (flag.equals("fromNewSessionActivity"))
                {
                    intent = new Intent(SetKeyActivity.this, NewSessionActivity.class);
                }
                else if (flag.equals("fromSetKeyFromSongActivity"))
                {
                    intent = new Intent(SetKeyActivity.this, NewSessionActivity.class);
                }
                intent.putExtra("user", user);
                intent.putExtra("session", session);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Hides the system status bar and navigation bar
     */
    private void hideSystemBars(){
        WindowInsetsControllerCompat windowInsetsController = new WindowInsetsControllerCompat(this.getWindow(), this.getCurrentFocus());
        if (windowInsetsController != null) {
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        }
    }
}
