package com.example.chirpnote.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.ChirpNoteSession;
import com.example.chirpnote.R;

public class SessionOptionsActivity extends AppCompatActivity {

    private AlertDialog.Builder setKeyDialogBuilder;
    private AlertDialog.Builder setTempoDialogBuilder;
    private AlertDialog.Builder setChordInstDialogBuilder;
    private AlertDialog.Builder setMelodyInstDialogBuilder;
    private AlertDialog dialog;
    private ChirpNoteSession session;
    private Button confirmChangesButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_options);
        confirmChangesButton = findViewById(R.id.confirmChangesButton);


        session = (ChirpNoteSession) getIntent().getSerializableExtra("session");

        // note suggestion switch ifelse
        Switch noteSuggestSwitch = findViewById(R.id.switch1);
        noteSuggestSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) { //if the switch is on
                    session.setNoteSuggestionFlag(true);
                }
                else { //if the switch is on
                    session.setNoteSuggestionFlag(false);
                }
            }
        });

        Switch smartKeyboardSwitch = findViewById(R.id.switch2);
        smartKeyboardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) { //if the switch is on
                    session.setSmartKeyboardFlag(true);
                }
                else { //if the switch is on
                    session.setSmartKeyboardFlag(false);
                }
            }
        });


        if (session.getNoteSuggestionFlag() == true)
        {
            noteSuggestSwitch.setChecked(true);
        }
        else {noteSuggestSwitch.setChecked(false);}

        if (session.getSmartKeyboardFlag() == true)
        {
            smartKeyboardSwitch.setChecked(true);
        }
        else {smartKeyboardSwitch.setChecked(false);}


        LinearLayout setKeyLayout = findViewById(R.id.LayoutSetKey);
        setKeyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setKeyDialogBox();
            }
        });

        LinearLayout setTempoLayout = findViewById(R.id.LayoutSetTempo);
        setTempoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTempoDialogBox();
            }
        });

        LinearLayout setChordInstLayout = findViewById(R.id.LayoutSetChordsInst);
        setChordInstLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChordInstDialogBox();
            }
        });

        LinearLayout setMelodyInstLayout = findViewById(R.id.LayoutSetMelodyInst);
        setMelodyInstLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMelodyInstDialogBox();
            }
        });

        confirmChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
//                if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromKeyboardActivity")) {
//                    if (session.getSmartKeyboardFlag() == false) {
//                        intent = new Intent(SessionOptionsActivity.this, KeyboardActivity.class);
//                    }
//                    else
//                    {
//                        intent = new Intent(SessionOptionsActivity.this, SmartKeyboardActivity.class);
//                    }
//                }
//                else if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromSmartKeyboardActivity")) {
//                    if (session.getSmartKeyboardFlag() == false) {
//                        intent = new Intent(SessionOptionsActivity.this, KeyboardActivity.class);
//                    }
//                    else
//                    {
//                        intent = new Intent(SessionOptionsActivity.this, SmartKeyboardActivity.class);
//                    }
//                }
                if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromKeyboardActivity")) {
                    if (session.getSmartKeyboardFlag() == false) {
                        intent = new Intent(SessionOptionsActivity.this, KeyboardActivity.class);
                    }
                    else
                    {
                        intent = new Intent(SessionOptionsActivity.this, SmartKeyboardActivity.class);
                    }
                }
                else if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromSmartKeyboardActivity")) {
                    if (session.getSmartKeyboardFlag() == false) {
                        intent = new Intent(SessionOptionsActivity.this, KeyboardActivity.class);
                    }
                    else
                    {
                        intent = new Intent(SessionOptionsActivity.this, SmartKeyboardActivity.class);
                    }
                }
                else if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromInsertChordsActivity")) {
                    intent = new Intent(SessionOptionsActivity.this, InsertChordsActivity.class);
                }
                else if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromMixerActivity")) {
                    intent = new Intent(SessionOptionsActivity.this, MixerActivity.class);
                }
                else if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromMelodyActivity")) {
                    intent = new Intent(SessionOptionsActivity.this, MelodyActivity.class);
                }
                else if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromPercussionActivity")) {
                    intent = new Intent(SessionOptionsActivity.this, PercussionActivity.class);
                }
                else if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromRecordAudioActivity")) {
                    intent = new Intent(SessionOptionsActivity.this, RecordAudioActivity.class);
                }
                else if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromSessionOverviewActivity")) {
                    intent = new Intent(SessionOptionsActivity.this, SessionOverviewActivity.class);
                }
                intent.putExtra("flag", "fromSessionOptionsActivity");
                intent.putExtra("session", session);
                startActivity(intent);
            }
        });

    }

    public void setKeyDialogBox() {
        setKeyDialogBuilder = new AlertDialog.Builder(this);
        final View setKeyPopupView = getLayoutInflater().inflate(R.layout.popup_set_key, null);

        setKeyDialogBuilder.setView(setKeyPopupView);
        dialog = setKeyDialogBuilder.create();
        dialog.show();

        Button setKeyConfirmButton = (Button) setKeyPopupView.findViewById(R.id.setKeyConfirmButton);
        Button setKeyCancelButton = (Button) setKeyPopupView.findViewById(R.id.setKeyCancelButton);

        setKeyConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save button definition

            }
        });
        setKeyCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); //close dialog box
            }
        });
    }

    public void setTempoDialogBox() {
        setTempoDialogBuilder = new AlertDialog.Builder(this);
        final View setTempoPopupView = getLayoutInflater().inflate(R.layout.popup_set_tempo, null);

        setTempoDialogBuilder.setView(setTempoPopupView);
        dialog = setTempoDialogBuilder.create();
        dialog.show();

        Button setTempoConfirmButton = (Button) setTempoPopupView.findViewById(R.id.setTempoConfirmButton);
        Button setTempoCancelButton = (Button) setTempoPopupView.findViewById(R.id.setTempoCancelButton);

        setTempoConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save button definition

            }
        });
        setTempoCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); //close dialog box
            }
        });
    }

    public void setChordInstDialogBox() {
        setChordInstDialogBuilder = new AlertDialog.Builder(this);
        final View setChordPopupView = getLayoutInflater().inflate(R.layout.popup_set_chords_instrument, null);

        setChordInstDialogBuilder.setView(setChordPopupView);
        dialog = setChordInstDialogBuilder.create();
        dialog.show();

        /*
        RadioGroup setChordRadioGroup = findViewById(R.id.setChordRadioGroup);
        int radioId = setChordRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedOption = findViewById(radioId);

        Button setChordConfirmButton = (Button) setChordPopupView.findViewById(R.id.setChordConfirmButton);
        Button setChordCancelButton = (Button) setChordPopupView.findViewById(R.id.setChordCancelButton);

        setChordConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(this,"Chord Instrument changed to " + selectedOption.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        setChordCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); //close dialog box
            }
        });
        */
    }

    public void setMelodyInstDialogBox() {
        setMelodyInstDialogBuilder = new AlertDialog.Builder(this);
        final View setMelodyPopupView = getLayoutInflater().inflate(R.layout.popup_set_melody_instrument, null);

        setMelodyInstDialogBuilder.setView(setMelodyPopupView);
        dialog = setMelodyInstDialogBuilder.create();
        dialog.show();

        /*
        RadioGroup setChordRadioGroup = findViewById(R.id.setChordRadioGroup);
        int radioId = setChordRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedOption = findViewById(radioId);

        Button setChordConfirmButton = (Button) setChordPopupView.findViewById(R.id.setChordConfirmButton);
        Button setChordCancelButton = (Button) setChordPopupView.findViewById(R.id.setChordCancelButton);

        setChordConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(this,"Chord Instrument changed to " + selectedOption.getText(), Toast.LENGTH_SHORT).show();
            }
        });
        setChordCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); //close dialog box
            }
        });
        */
    }
}