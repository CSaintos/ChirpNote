package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.os.Bundle;
import com.example.chirpnote.R;

public class ChangeNameActivity extends AppCompatActivity {

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        TextView displayCurrentName = (TextView) findViewById(R.id.currentNameText);
        displayCurrentName.setText(name);

        EditText editNewName = (EditText) findViewById(R.id.editNewName);

        Button changeNameButton2 = (Button) findViewById(R.id.changeNameButton2);
        changeNameButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editNewName.getText().toString();

            }
        });
    }
}