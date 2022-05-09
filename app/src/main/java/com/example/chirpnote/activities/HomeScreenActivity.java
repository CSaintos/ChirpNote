package com.example.chirpnote.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.chirpnote.ChirpNoteUser;
import com.example.chirpnote.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;

public class HomeScreenActivity extends AppCompatActivity {

    App app;
    String appID = "chirpnote-jwrci";
    private AdView mAdViewTop;
    private AdView mAdViewBottom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        getSupportActionBar().hide();
        hideSystemBars();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdViewTop = findViewById(R.id.adViewTop);
        mAdViewBottom = findViewById(R.id.adViewBottom);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdViewTop.loadAd(adRequest);
        mAdViewBottom.loadAd(adRequest);

        ChirpNoteUser user = (ChirpNoteUser) getIntent().getSerializableExtra("user");
        app = new App(new AppConfiguration.Builder(appID).build());

        Button newSessionButton = (Button) findViewById(R.id.newSessionButton);
        Button loadSessionButton = (Button) findViewById(R.id.loadSessionButton);
        Button profileButton = (Button) findViewById(R.id.profileButton);
        Button musicTheoryButton = (Button) findViewById(R.id.musicTheoryButton);

        // For testing only
        Button testKeyboardButton = (Button) findViewById(R.id.testKeyboardButton);
        Button testOtherButton = (Button) findViewById(R.id.testOtherButton);
        Button testMelodyButton = (Button) findViewById(R.id.testMelodyButton);
        Button testPercussionButton = (Button) findViewById(R.id.testPercussionButton);
        Button testSmartKeyboardButton = (Button) findViewById(R.id.testSmartKeyboardButton);
        Button testInsertChordsButton = (Button) findViewById(R.id.testInsertChords);

        // Hide the testing buttons for the real app
        testKeyboardButton.setVisibility(View.INVISIBLE);
        testOtherButton.setVisibility(View.INVISIBLE);
        testMelodyButton.setVisibility(View.INVISIBLE);
        testPercussionButton.setVisibility(View.INVISIBLE);
        testSmartKeyboardButton.setVisibility(View.INVISIBLE);
        testInsertChordsButton.setVisibility(View.INVISIBLE);

        //new session button function
        newSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, NewSessionActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        //load session button function
        loadSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, LoadSessionActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        //profile button function
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, UserProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        //music theory info button function
        musicTheoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, MusicTheoryInfoActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        // Button to go the free play keyboard activity (for testing)
        testKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, KeyboardActivity.class);
                startActivity(intent);
            }
        });

        // Button to go an activity to test miscellaneous functionality
        testOtherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreenActivity.this, TestOtherActivity.class);
                startActivity(intent);
            }
        });

        // Button to Melody Activity (for testing)
        testMelodyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, MelodyActivity.class));
            }
        });

        // Button to Percussion Activity (for testing)
        testPercussionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, PercussionActivity.class));
            }
        });

        // Button to Smart Keyboard (for testing)
        testSmartKeyboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, SmartKeyboardActivity.class));
            }
        });

        // Button to Insert Chords (for testing)
        testInsertChordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenActivity.this, InsertChordsActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
        // Log out in the background
        new LogOut(this).execute(app);
        startActivity(new Intent(HomeScreenActivity.this, LoginActivity.class));
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

    private class LogOut extends AsyncTask<App, Void, String> {
        private ProgressDialog mDialog;
        private Context mContext;

        public LogOut(Context context){
            mContext = context;
        }

        @Override
        protected void onPreExecute(){
            mDialog = ProgressDialog.show(mContext, "Log out", "Logging you out...", true);
            mDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(App... app){
            app[0].currentUser().logOut();
            return "Done";
        }

        @Override
        protected void onPostExecute(String result){
            mDialog.dismiss();
        }
    }
}
