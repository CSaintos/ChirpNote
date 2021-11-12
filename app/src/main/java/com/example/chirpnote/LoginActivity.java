package com.example.chirpnote;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.sync.SyncConfiguration;

public class LoginActivity extends AppCompatActivity {

    private EditText mUsername, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Realm.init(this);

        String appID = "chirpnote-jwrci";
        App app = new App(new AppConfiguration.Builder(appID).build());
        AtomicReference<io.realm.mongodb.User> user = new AtomicReference<>();

        mUsername = (EditText) findViewById(R.id.editTextUsername);
        mPassword = (EditText) findViewById(R.id.editTextPassword);
        TextView auth = (TextView) findViewById(R.id.authTextView);
        TextView test = (TextView) findViewById(R.id.testingTextView);
        Button loginButton = (Button) findViewById(R.id.loginButton);

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
    }
}