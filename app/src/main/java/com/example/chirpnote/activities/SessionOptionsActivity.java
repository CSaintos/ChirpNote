package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chirpnote.R;

public class SessionOptionsActivity extends AppCompatActivity {

    private AlertDialog.Builder setKeyDialogBuilder;
    private AlertDialog.Builder setTempoDialogBuilder;
    private AlertDialog.Builder setChordInstDialogBuilder;
    private AlertDialog.Builder setMelodyInstDialogBuilder;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_options);

        // note suggestion switch ifelse
        Switch noteSuggestSwitch = findViewById(R.id.switch1);
        noteSuggestSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) { //if the switch is on

                }
                else { //if the switch is on

                }
            }
        });

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