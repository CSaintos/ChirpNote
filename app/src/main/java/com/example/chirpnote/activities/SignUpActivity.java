package com.example.chirpnote.activities;

import android.content.Context;
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

import com.example.chirpnote.R;

import java.util.Arrays;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.functions.Functions;

public class SignUpActivity extends AppCompatActivity {
    private EditText mUsername, mPassword;

    App app;
    String appID = "chirpnote-jwrci";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Context context = getApplicationContext();
        hideSystemBars();

        app = new App(new AppConfiguration.Builder(appID).build());
        Credentials credentials = Credentials.anonymous();

        mUsername = (EditText) findViewById(R.id.editTextUsername);
        mPassword = (EditText) findViewById(R.id.editTextPassword);
        Button signUpButton = (Button) findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                app.loginAsync(credentials, it -> {
                    if (it.isSuccess()) {
                        io.realm.mongodb.User user = app.currentUser();
                        assert user != null;
                        Functions functionsManager = app.getFunctions(user);
                        functionsManager.callFunctionAsync("signUp", Arrays.asList(username, password), Boolean.class, result -> {
                            if (result.isSuccess()) {
                                if(result.get()){
                                    Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                } else {
                                    Toast.makeText(context, "Username taken. Try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
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
