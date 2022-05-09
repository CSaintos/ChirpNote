package com.example.chirpnote.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.chirpnote.ChirpNoteUser;
import com.example.chirpnote.R;

import org.bson.Document;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class LoginActivity extends AppCompatActivity {
    App app;
    String appID = "chirpnote-jwrci";

//    private InterstitialAd mInterstitialAd;

    private EditText mUsername, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //hideSystemBars();

        app = new App(new AppConfiguration.Builder(appID).build());

        mUsername = (EditText) findViewById(R.id.editTextUsername);
        mPassword = (EditText) findViewById(R.id.editTextPassword);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button signUpButton = (Button) findViewById(R.id.signUpButton);
//        Button bypassLoginButton = (Button) findViewById(R.id.bypassLoginButton);
//        bypassLoginButton.setVisibility(View.INVISIBLE);

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle("Log in");
                progressDialog.setMessage("Logging you in...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                Credentials customFunctionCredentials =
                        Credentials.customFunction(new org.bson.Document("username", username).append("password", password));
                app.loginAsync(customFunctionCredentials, it -> {
                    if (it.isSuccess()) {
                        // Setup a synced MongoDB Realm configuration for the current user
                        SyncConfiguration config = new SyncConfiguration.Builder(
                                app.currentUser(),
                                username)
                                .allowWritesOnUiThread(true)
                                .build();
                        Realm.setDefaultConfiguration(config);

                        // Get user data on separate thread
                        ChirpNoteUser user = new ChirpNoteUser();
                        Thread getUserThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // Query database
                                Document userDoc = app.currentUser()
                                        .getMongoClient("mongodb-atlas")
                                        .getDatabase("ChirpNote")
                                        .getCollection("Users")
                                        .findOne(new Document("username", username)).get();

                                // Update user object
                                user.setUsername(userDoc.getString("username"));
                                user.setName(userDoc.getString("name"));
                                user.setEmail(userDoc.getString("email"));
                                user.setPassword(userDoc.getString("password"));
                            }
                        });
                        getUserThread.start();
                        try {
                            getUserThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // Go to home screen
                        Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
                        intent.putExtra("user", user);
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Login succeeded", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    } else {
                        progressDialog.dismiss();
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

//        bypassLoginButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(LoginActivity.this, HomeScreenActivity.class));
//            }
//        });
    }

    @Override
    public void onBackPressed() {}

    @Override
    protected void onResume(){
        super.onResume();
        mUsername.setText("");
        mPassword.setText("");
        //hideSystemBars();
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