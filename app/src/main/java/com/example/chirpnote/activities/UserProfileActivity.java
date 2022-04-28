package com.example.chirpnote.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import io.realm.mongodb.App;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;

import com.example.chirpnote.R;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Document;

public class UserProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    Activity context;

    MongoClient mongoClient;
    MongoDatabase mongoDatabase;
    MongoCollection<Document> mongoCollection;
    App app;
    String appID = "chirpnote-jwrci";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.nav_toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //navigationView.setCheckedItem(R.id.nav_profile);

        /*
        String username, password;
        AtomicReference<User> user = new AtomicReference<>();
        Context context = getApplicationContext();
        app = new App(new AppConfiguration.Builder(appID).build());
        Credentials customFunctionCredentials =
                Credentials.customFunction(new org.bson.Document("username", username).append("password", password));
        TextView displayCurrentName = (TextView) findViewById(R.id.currentNameText);
        displayCurrentName.setText(username);

        EditText editNewName = (EditText) findViewById(R.id.editNewName);

        Button changeNameButton2 = (Button) findViewById(R.id.changeNameButton2);
        changeNameButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editNewName.getText().toString();

            }
        });

        TextView displayCurrentEmail = (TextView) findViewById(R.id.currentEmailText);
        displayCurrentEmail.setText(username);

        EditText editNewEmail = (EditText) findViewById(R.id.editNewEmail);

        Button changeEmailButton2 = (Button) findViewById(R.id.changeEmailButton2);
        changeEmailButton2.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editNewEmail.getText().toString();

            }
        });*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                redirectActivity(this, HomeScreenActivity.class);
                break;
            case R.id.nav_overview:
                Toast.makeText(this, "Overview", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_melody:
                redirectActivity(this, MelodyActivity.class);
                break;
            case R.id.nav_chords:
                redirectActivity(this, InsertChordsActivity.class);
                break;
            case R.id.nav_percussion:
                redirectActivity(this, PercussionActivity.class);
                break;
            case R.id.nav_keyboard:
                redirectActivity(this, KeyboardActivity.class);
                break;
            case R.id.nav_mixer:
                Toast.makeText(this, "Mixer", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_audio:
                redirectActivity(this, RecordAudioActivity.class);
                break;
            default:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private static void redirectActivity(Activity activity, Class aClass) {
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.subitem1: //subitem 1 = dark mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                //setTheme(R.style.Theme_ChirpnoteDark);
                Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
                break;
            case R.id.subitem2: //subitem 2 = light mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                //setTheme(R.style.Theme_ChirpNote);
                Toast.makeText(this, "Light Mode Enabled", Toast.LENGTH_SHORT).show();
                break;
            case R.id.subitem3: //subitem 3 = use system preference
                //boolean darkMode = getDarkModeStatus();
                if(getDarkModeStatus()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Toast.makeText(this, "Light Mode Enabled", Toast.LENGTH_SHORT).show();
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean getDarkModeStatus() {
        int nightModeFlags=this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                return false;
        }
        return false;
    }


}