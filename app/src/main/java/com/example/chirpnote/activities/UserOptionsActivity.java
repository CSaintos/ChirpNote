package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.chirpnote.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_options);

        Button changeNameButton = (Button) findViewById(R.id.changeNameButton);
        Button changePasswordButton = (Button) findViewById(R.id.changePasswordButton);
        Button changeUsernameButton = (Button) findViewById(R.id.changeUsernameButton);
        Button changeEmailButton = (Button) findViewById(R.id.changeEmailButton);
        FloatingActionButton backButton1 = findViewById(R.id.backButton1);

        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserOptionsActivity.this, ChangeNameActivity.class);
                        startActivity(intent);
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserOptionsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        changeUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserOptionsActivity.this, ChangeUsernameActivity.class);
                        startActivity(intent);
            }
        });

        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserOptionsActivity.this, ChangeEmailActivity.class);
                        startActivity(intent);
            }
        });

        backButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserOptionsActivity.this, HomeScreenActivity.class);
                        startActivity(intent);
            }
        });

    }
}