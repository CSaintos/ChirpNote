package com.example.chirpnote.activities;

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

//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//        setAds();

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

//                if (mInterstitialAd != null) {
//                    mInterstitialAd.show(LoginActivity.this);
//                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                        @Override
//                        public void onAdDismissedFullScreenContent() {
//                            super.onAdDismissedFullScreenContent();
//                            String username = mUsername.getText().toString();
//                            String password = mPassword.getText().toString();
//                            Credentials customFunctionCredentials =
//                                    Credentials.customFunction(new org.bson.Document("username", username).append("password", password));
//                            app.loginAsync(customFunctionCredentials, it -> {
//                                if (it.isSuccess()) {
//                                    Toast.makeText(LoginActivity.this, "Login succeeded", Toast.LENGTH_SHORT).show();
//
//                                    // Setup a synced MongoDB Realm configuration for the current user
//                                    SyncConfiguration config = new SyncConfiguration.Builder(
//                                            app.currentUser(),
//                                            username)
//                                            .build();
//                                    Realm.setDefaultConfiguration(config);
//
//                                    Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
//                                    intent.putExtra("username", username);
//                                    startActivity(intent);
//                                } else {
//                                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
//                                }
//                            });
//                            mInterstitialAd = null;
//                            setAds();
//                        }
//                    });
//                }
//                else
//                {
//                    String username = mUsername.getText().toString();
//                    String password = mPassword.getText().toString();
//                    Credentials customFunctionCredentials =
//                            Credentials.customFunction(new org.bson.Document("username", username).append("password", password));
//                    app.loginAsync(customFunctionCredentials, it -> {
//                        if (it.isSuccess()) {
//                            Toast.makeText(LoginActivity.this,"Login succeeded", Toast.LENGTH_SHORT).show();
//
//                            // Setup a synced MongoDB Realm configuration for the current user
//                            SyncConfiguration config = new SyncConfiguration.Builder(
//                                    app.currentUser(),
//                                    username)
//                                    .build();
//                            Realm.setDefaultConfiguration(config);
//
//                            Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
//                            intent.putExtra("username", username);
//                            startActivity(intent);
//                        } else {
//                            Toast.makeText(LoginActivity.this,"Login failed", Toast.LENGTH_LONG).show();
//                        }
//                    });
//                }

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

//        bypassLoginButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(LoginActivity.this, HomeScreenActivity.class));
//            }
//        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        //hideSystemBars();
    }

//    public void setAds()
//    {
//        AdRequest adRequest = new AdRequest.Builder().build();
//
////        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
//        InterstitialAd.load(this,getString(R.string.adsUnit), adRequest, new InterstitialAdLoadCallback() {
//            @Override
//            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                // The mInterstitialAd reference will be null until
//                // an ad is loaded.
//                mInterstitialAd = interstitialAd;
////                        Log.i(TAG, "onAdLoaded");
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                // Handle the error
////                        Log.i(TAG, loadAdError.getMessage());
//                mInterstitialAd = null;
//            }
//        });
//    }

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