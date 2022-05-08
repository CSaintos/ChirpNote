package com.example.chirpnote.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.RealmResultTask;
import io.realm.mongodb.functions.Functions;
import io.realm.Realm;

import com.example.chirpnote.R;
import com.example.chirpnote.User;
import com.google.android.material.navigation.NavigationView;

import org.bson.Document;

import java.util.Arrays;

public class UserProfileActivity extends AppCompatActivity {
    App app;
    String appID = "chirpnote-jwrci";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        hideSystemBars();
        //actionbar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Profile");

        Context context = getApplicationContext();
        String username = getIntent().getStringExtra("username");
        app = new App(new AppConfiguration.Builder(appID).build());
		Realm realm = Realm.getDefaultInstance();
		User user = realm.where(User.class).findFirst();

        EditText usernameText = (EditText) findViewById(R.id.editProfileUsername);
        EditText nameText = (EditText) findViewById(R.id.editProfileName);
        EditText emailText = (EditText) findViewById(R.id.editProfileEmail);
        EditText passwordText = (EditText) findViewById(R.id.editProfilePassword);
        usernameText.setText(user.getUsername());
        nameText.setText(user.getName());
        emailText.setText(user.getEmail());
        passwordText.setText(user.getPassword());

        Button saveChangesButton = (Button) findViewById(R.id.saveSettingsButton);
        saveChangesButton.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldUsername = username;
                String newUsername = usernameText.getText().toString();
                String newName = nameText.getText().toString();
                String newEmail = emailText.getText().toString();
                String newPassword = passwordText.getText().toString();

                Functions functionsManager = app.getFunctions(app.currentUser());
                functionsManager.callFunctionAsync("updateProfile", Arrays.asList(oldUsername, newUsername, newName, newEmail, newPassword),
                        Boolean.class, result -> {
                    if (result.isSuccess()) {
                        if(result.get()){
                            Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UserProfileActivity.this, HomeScreenActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "Username taken. Try again", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

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