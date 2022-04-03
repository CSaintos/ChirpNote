package com.example.chirpnote.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chirpnote.R;

public class UserProfileActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


         /*
        TextView displayCurrentName = (TextView) findViewById(R.id.currentNameText);
        displayCurrentName.setText(name);

        EditText editNewName = (EditText) findViewById(R.id.editNewName);

        Button changeNameButton2 = (Button) findViewById(R.id.changeNameButton2);
        changeNameButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editNewName.getText().toString();

            }
        }

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
          */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.subitem1: //subitem 1 = dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                //setTheme(R.style.Theme_ChirpnoteDark);
                Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
                break;
            case R.id.subitem2: //subitem 2 = light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                //setTheme(R.style.Theme_ChirpNote);
                Toast.makeText(this, "Light Mode Enabled", Toast.LENGTH_SHORT).show();
                break;
            case R.id.subitem3: //subitem 3 = use system preference
                //boolean darkMode = getDarkModeStatus();


                Toast.makeText(this, "Using System Preferences", Toast.LENGTH_SHORT).show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}