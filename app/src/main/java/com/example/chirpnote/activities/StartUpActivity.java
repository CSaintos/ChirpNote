package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.chirpnote.R;

import java.io.File;

import io.realm.Realm;

public class StartUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        Realm.init(this); // initializes realm, only need to do this once

        Log.d("Success","Start Screen Created");

        //folder structure creation temporary before integrating to new session activity
        File nestedSessionsFolder = new File(this.getFilesDir(), "Session");
        if (!nestedSessionsFolder.exists())
            nestedSessionsFolder.mkdir();
        File audioFolder = new File(nestedSessionsFolder, "Audio");
        if (!audioFolder.exists())
            audioFolder.mkdir();
        startActivity(new Intent(StartUpActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.fade_out, R.anim.static_animation);
    }

}