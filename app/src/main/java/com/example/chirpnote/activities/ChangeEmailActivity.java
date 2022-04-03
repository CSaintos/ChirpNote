package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chirpnote.activities.LoginActivity;
import com.example.chirpnote.R;

public class ChangeEmailActivity extends AppCompatActivity {

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        TextView displayCurrentEmail = (TextView) findViewById(R.id.currentEmailText);
        displayCurrentEmail.setText(username);

        EditText editNewEmail = (EditText) findViewById(R.id.editNewEmail);

        Button changeEmailButton2 = (Button) findViewById(R.id.changeEmailButton2);
        changeEmailButton2.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editNewEmail.getText().toString();

            }
        });

    }
}