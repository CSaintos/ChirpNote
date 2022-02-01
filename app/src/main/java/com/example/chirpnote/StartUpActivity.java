package com.example.chirpnote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import io.realm.Realm;

public class StartUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        Realm.init(this); // initializes realm, only need to do this once

        Log.d("Success","Start Screen Created");
        startActivity(new Intent(StartUpActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.fade_out, R.anim.static_animation);
    }

}