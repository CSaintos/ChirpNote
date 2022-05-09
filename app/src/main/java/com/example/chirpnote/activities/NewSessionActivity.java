package com.example.chirpnote.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.chirpnote.ChirpNoteSession;
import com.example.chirpnote.ChirpNoteUser;
import com.example.chirpnote.Key;
import com.example.chirpnote.R;
import com.example.chirpnote.Session;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import io.realm.Realm;

public class NewSessionActivity extends AppCompatActivity {
    ChirpNoteUser user;

    private InterstitialAd mInterstitialAd;

    ChirpNoteSession session;
    Key newKey;
    Button setKeyButton;
    ChirpNoteSession dummySession;

    private EditText setName, setTempo;
    private Button createSessionButton;
    private String tempoInvalidMessage = "Tempo must be " + ChirpNoteSession.MIN_TEMPO + "-" + ChirpNoteSession.MAX_TEMPO + " BPM";
    private TextView tempoInvalid;
    private TextWatcher checkTextValid = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void afterTextChanged(Editable editable) {
            String name = setName.getText().toString();
            String tempo = setTempo.getText().toString();

            if(name.equals("") || tempo.equals("")){
                createSessionButton.setEnabled(false);
                setKeyButton.setEnabled(false);
            } else {
                int t = Integer.parseInt(tempo);
                if(t < ChirpNoteSession.MIN_TEMPO || t > ChirpNoteSession.MAX_TEMPO) {
                    tempoInvalid.setText(tempoInvalidMessage);
                    createSessionButton.setEnabled(false);
                    setKeyButton.setEnabled(false);
                } else {
                    tempoInvalid.setText("");
                    createSessionButton.setEnabled(newKey != null);
                    setKeyButton.setEnabled(true);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_session);
        hideSystemBars();
        findViewById(android.R.id.content).setFocusableInTouchMode(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("New Session");

        setKeyButton = (Button) findViewById(R.id.setKeyButton);
        setKeyButton.setEnabled(false);
        createSessionButton = (Button) findViewById(R.id.createSessionButton);
        createSessionButton.setEnabled(false);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        setAds();

        user = (ChirpNoteUser) getIntent().getSerializableExtra("user");
        Realm realm = Realm.getDefaultInstance();

        tempoInvalid = (TextView) findViewById(R.id.tempoInvalidText);

        setName = (EditText) findViewById(R.id.setSessionNameText);
        setName.addTextChangedListener(checkTextValid);

        setTempo = (EditText) findViewById(R.id.setTempoText);
        setTempo.addTextChangedListener(checkTextValid);

        String basePath = this.getFilesDir().getPath();

//        // replaced the initialization of session in createSession onClickListener
//        Intent intent = getIntent();
//        if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromSetKeyActivity"))
//        {
//            System.out.println("intent = " + "fromSetKeyActivity");
////            session = (ChirpNoteSession) getIntent().getSerializableExtra("session"); // coming from keyboard activity
//            Key newKey = (Key) getIntent().getSerializableExtra("newKey");
//        }
//        else
//        {
////            session = new ChirpNoteSession("SessionFreePlay", new Key(Key.RootNote.C, Key.Type.MAJOR), 120);
////            System.out.println("session key name = " + session.getKey().toString());
//            System.out.println("intent = " + "fromNewSessionActivity");
//            session = new ChirpNoteSession(setName.getText().toString(), new Key(Key.RootNote.C, Key.Type.MAJOR),
//                    Integer.parseInt(setTempo.getText().toString()), basePath + "midiTrack.mid", basePath + "audioTrack.mp3");
//        }

        Intent intent = getIntent();
        if (intent.getStringExtra("flag") != null && intent.getStringExtra("flag").equals("fromSetKeyActivity")) {
            newKey = (Key) intent.getSerializableExtra("newKey");
            dummySession = (ChirpNoteSession) intent.getSerializableExtra("session");

            setName.setText(dummySession.getName());
            setName.addTextChangedListener(checkTextValid);

            setTempo.setText(Integer.toString(dummySession.getTempo()));
            setTempo.addTextChangedListener(checkTextValid);

            createSessionButton.setEnabled(true);
        } else {
            createSessionButton.setEnabled(false);
        }

        setKeyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewSessionActivity.this, SetKeyActivity.class);
                intent.putExtra("flag", "fromNewSessionActivity");
                dummySession = new ChirpNoteSession(setName.getText().toString(), new Key(Key.RootNote.C, Key.Type.MAJOR), Integer.parseInt(setTempo.getText().toString()));
                intent.putExtra("session", dummySession);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        createSessionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (mInterstitialAd != null)
                {
                    mInterstitialAd.show(NewSessionActivity.this);

                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            Intent intent = new Intent(NewSessionActivity.this, SessionOverviewActivity.class);
//                            ChirpNoteSession session = new ChirpNoteSession(setName.getText().toString(), new Key(Key.RootNote.A, Key.Type.MAJOR),
//                                    Integer.parseInt(setTempo.getText().toString()), basePath + "midiTrack.mid", basePath + "audioTrack.mp3");
                            session = new ChirpNoteSession(setName.getText().toString(), newKey,
                                    Integer.parseInt(setTempo.getText().toString()), basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3");

                            // Insert the new session into the realm database
                            if(user != null) {
                                realm.executeTransactionAsync(r -> {
                                    Session sessionToInsert = new Session(session, user.getUsername());
                                    r.insert(sessionToInsert);
                                });
                            }
                            intent.putExtra("session", session);
                            intent.putExtra("user", user);
                            startActivity(intent);

                            mInterstitialAd = null;
                            setAds();
                        }
                    });
                } else {
                    Intent intent = new Intent(NewSessionActivity.this, SessionOverviewActivity.class);
//                    ChirpNoteSession session = new ChirpNoteSession(setName.getText().toString(), new Key(Key.RootNote.A, Key.Type.MAJOR),
//                            Integer.parseInt(setTempo.getText().toString()), basePath + "midiTrack.mid", basePath + "audioTrack.mp3");
                    session = new ChirpNoteSession(setName.getText().toString(), newKey,
                            Integer.parseInt(setTempo.getText().toString()), basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3");

                    // Insert the new session into the realm database
                    if(user != null) {
                        realm.executeTransactionAsync(r -> {
                            Session sessionToInsert = new Session(session, user.getUsername());
                            r.insert(sessionToInsert);
                        });
                    }
                    intent.putExtra("session", session);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
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
        Intent intent = new Intent(NewSessionActivity.this, HomeScreenActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(NewSessionActivity.this, HomeScreenActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setAds()
    {
        AdRequest adRequest = new AdRequest.Builder().build();

//        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest,
        InterstitialAd.load(this,getString(R.string.adsUnit), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
//                        Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
//                        Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
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