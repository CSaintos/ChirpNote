package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.functions.Functions;

import com.example.chirpnote.ChirpNoteUser;
import com.example.chirpnote.R;

import java.util.Arrays;

public class UserProfileActivity extends AppCompatActivity {
    App app;
    String appID = "chirpnote-jwrci";
    ChirpNoteUser user;

    EditText usernameText;
    EditText nameText;
    EditText emailText;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        hideSystemBars();
        findViewById(android.R.id.content).setFocusableInTouchMode(true);
        //actionbar back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Profile");

        Context context = getApplicationContext();
        user = (ChirpNoteUser) getIntent().getSerializableExtra("user");
        app = new App(new AppConfiguration.Builder(appID).build());

        usernameText = (EditText) findViewById(R.id.editProfileUsername);
        nameText = (EditText) findViewById(R.id.editProfileName);
        emailText = (EditText) findViewById(R.id.editProfileEmail);
        passwordText = (EditText) findViewById(R.id.editProfilePassword);
        // Set the current user profile data
        usernameText.setText(user.getUsername());
        nameText.setText(user.getName());
        emailText.setText(user.getEmail());
        passwordText.setText(user.getPassword());

        Button saveChangesButton = (Button) findViewById(R.id.saveSettingsButton);
        saveChangesButton.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(UserProfileActivity.this);
                progressDialog.setTitle("Update profile");
                progressDialog.setMessage("Saving changes...");
                progressDialog.show();
                progressDialog.setCancelable(false);

                String oldUsername = user.getUsername();
                String newUsername = usernameText.getText().toString();
                String newName = nameText.getText().toString();
                String newEmail = emailText.getText().toString();
                String newPassword = passwordText.getText().toString();

                Functions functionsManager = app.getFunctions(app.currentUser());
                functionsManager.callFunctionAsync("updateProfile",
                        Arrays.asList(oldUsername, newUsername, newName, newEmail, newPassword),
                        Boolean.class, result -> {
                    if (result.isSuccess()) {
                        if(result.get()){
                            user.setUsername(newUsername);
                            user.setName(newName);
                            user.setEmail(newEmail);
                            user.setPassword(newPassword);

                            Intent intent = new Intent(UserProfileActivity.this, HomeScreenActivity.class);
                            intent.putExtra("user", user);
                            progressDialog.dismiss();
                            Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Username taken. Try again", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Request failed. Try again", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int[] sourceCoordinates = new int[2];
            v.getLocationOnScreen(sourceCoordinates);
            float x = ev.getRawX() + v.getLeft() - sourceCoordinates[0];
            float y = ev.getRawY() + v.getTop() - sourceCoordinates[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
                hideKeyboard(this);
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    private void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            activity.getWindow().getDecorView();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
                findViewById(android.R.id.content).clearFocus();
            }
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(UserProfileActivity.this, HomeScreenActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
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
            case R.id.subitem4:
                new LogOut(this).execute(app);
                intent = new Intent(UserProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            case android.R.id.home:
                intent = new Intent(UserProfileActivity.this, HomeScreenActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                return true;
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