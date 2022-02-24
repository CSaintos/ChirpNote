package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chirpnote.R;

import org.w3c.dom.Document;

import java.util.concurrent.atomic.AtomicReference;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsername, mPassword;

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> mongoCollection; // not sure we need this yet
    App app;
    String appID = "chirpnote-jwrci";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        hideSystemBars();

        app = new App(new AppConfiguration.Builder(appID).build());
        //startActivity(new Intent(LoginActivity.this,SignUpActivity.class)); // not ready to implement this yet.

        AtomicReference<io.realm.mongodb.User> user = new AtomicReference<>();

        mUsername = (EditText) findViewById(R.id.editTextUsername);
        mPassword = (EditText) findViewById(R.id.editTextPassword);
        TextView auth = (TextView) findViewById(R.id.authTextView);
        TextView test = (TextView) findViewById(R.id.testingTextView);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        Button signUpButton = (Button) findViewById(R.id.signUpButton);
        Button bypassLoginButton = (Button) findViewById(R.id.bypassLoginButton);

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                Credentials customFunctionCredentials =
                        Credentials.customFunction(new org.bson.Document("username", username).append("password", password));
                app.loginAsync(customFunctionCredentials, it -> {
                    if (it.isSuccess()) {
                        auth.setText("Login succeeded!");
                        auth.setTextColor(Color.GREEN);
                        /*user.set(app.currentUser());
                        String partitionValue = "username";
                        SyncConfiguration config = new SyncConfiguration.Builder(
                                user.get(),
                                partitionValue)
                                .build();
                        Realm backgroundThreadRealm = Realm.getInstance(config);
                        String userId = it.get().getId();
                        com.example.chirpnote.User newUser =
                                backgroundThreadRealm.where(com.example.chirpnote.User.class).equalTo("_id", user.get().getId()).findFirst();
                        test.setText("Chord suggestions: " + newUser.getChordSuggestions());*/
                    } else {
                        auth.setText("Login failed...");
                        auth.setTextColor(Color.RED);
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
     * TODO: Add this to all other activities once UI navigation is complete
     */
    private void hideSystemBars(){
        WindowInsetsControllerCompat windowInsetsController = new WindowInsetsControllerCompat(this.getWindow(), this.getCurrentFocus());
        if (windowInsetsController != null) {
            windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
        }
    }
}