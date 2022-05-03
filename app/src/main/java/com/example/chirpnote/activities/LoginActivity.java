package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chirpnote.R;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class LoginActivity extends AppCompatActivity {
    App app;
    String appID = "chirpnote-jwrci";

    private EditText mUsername, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        hideSystemBars();

        app = new App(new AppConfiguration.Builder(appID).build());

        mUsername = (EditText) findViewById(R.id.editTextUsername);
        mPassword = (EditText) findViewById(R.id.editTextPassword);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button signUpButton = (Button) findViewById(R.id.signUpButton);
        Button bypassLoginButton = (Button) findViewById(R.id.bypassLoginButton);
        bypassLoginButton.setVisibility(View.INVISIBLE);

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                Credentials customFunctionCredentials =
                        Credentials.customFunction(new org.bson.Document("username", username).append("password", password));
                app.loginAsync(customFunctionCredentials, it -> {
                    if (it.isSuccess()) {
                        Toast.makeText(LoginActivity.this,"Login succeeded", Toast.LENGTH_SHORT).show();

                        // Setup a synced MongoDB Realm configuration for the current user
                        SyncConfiguration config = new SyncConfiguration.Builder(
                                app.currentUser(),
                                username)
                                .build();
                        Realm.setDefaultConfiguration(config);

                        Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this,"Login failed", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        bypassLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, HomeScreenActivity.class));
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        hideSystemBars();
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