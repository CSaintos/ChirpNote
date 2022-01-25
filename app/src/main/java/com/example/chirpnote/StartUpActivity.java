package com.example.chirpnote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class StartUpActivity extends AppCompatActivity {

    App app;
    String appID = "chirpnote-jwrci";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        Realm.init(this); // initializes realm, only need to do this once

        app = new App(new AppConfiguration.Builder(appID).build());
        Log.d("Success","Start Screen Created");
        startActivity(new Intent(StartUpActivity.this, LoginActivity.class));
        Animatoo.animateSpin(StartUpActivity.this);
    }
}