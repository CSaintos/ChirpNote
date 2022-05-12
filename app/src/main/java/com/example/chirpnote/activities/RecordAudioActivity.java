package com.example.chirpnote.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.chirpnote.AudioTrack;
import com.example.chirpnote.ChirpNoteSession;
import com.example.chirpnote.ChirpNoteUser;
import com.example.chirpnote.DriveServiceHelper;
import com.example.chirpnote.ExportHelper;
import com.example.chirpnote.Key;
import com.example.chirpnote.Mixer;
import com.example.chirpnote.R;
import com.example.chirpnote.Session;
import com.example.chirpnote.WaveformView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.slider.Slider;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.billthefarmer.mididriver.MidiDriver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.Timer;

import io.realm.Realm;

/**
 * This class represents recording audio from the user's microphone. It will provide certain monitoring elements such as the current chord and waveform representation of intensity of amplitude.
 */
public class RecordAudioActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static Realm realm;
    private static ChirpNoteUser user;

    //layout items
    private ImageButton recordButton;
    private ImageButton playRecordedAudioButton;
    private ImageButton stopRecordedAudioButton;
    private Button shareButton;
    private Button exportButton;
    private Button directoryButton;
    private Slider volSlider;

    // An audio track that is recorded with the device's microphone
    private AudioTrack audio;
    Context context = this;
    //This is the minutes/second timer of the recording
    private Chronometer timer;
    //This is the custom view for the waveform generation
    private WaveformView waveformView;
    //This timer is a timer initializer for the recording functionality of a waveform
    Timer ticker = new Timer();
    //audio file container
    File audioFile = null;
    String filePath;
    DriveServiceHelper driveServiceHelper;

    // navigation drawer
    private DrawerLayout drawer;

    private MidiDriver midiDriver;
    private static ChirpNoteSession session;
    private static Mixer mixer;

    /***
     * onCreate function sets values
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);
        hideSystemBars();

        user = (ChirpNoteUser) getIntent().getSerializableExtra("user");
        realm = Realm.getDefaultInstance();

        //navigation drawer
        Toolbar toolbar = findViewById(R.id.nav_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState(); // nav drawer end

        // actionbar play and stop buttons
        ImageView navPlayButton = findViewById(R.id.nav_play_button);
        navPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MelodyActivity.this, "Play", Toast.LENGTH_SHORT).show();
                if(mixer.areTracksPlaying()){
                    mixer.stopTracks();
                }
                mixer.playTracks();
            }
        });
        ImageView navStopButton = findViewById(R.id.nav_stop_button);
        navStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(InsertChordsActivity.this, "Stop", Toast.LENGTH_SHORT).show();
                if(mixer.areTracksPlaying()){
                    mixer.stopTracks();
                }
            }
        }); // play and stop end

        volSlider = findViewById(R.id.volumeSlider);
        timer = findViewById(R.id.recordTimer);
        directoryButton = findViewById(R.id.directoryShowButton);
        recordButton = findViewById(R.id.recordAudioActivityButton);
        playRecordedAudioButton = findViewById(R.id.playRecordedAudioButton);
        playRecordedAudioButton.setEnabled(false);
        stopRecordedAudioButton = findViewById(R.id.stopRecordedAudioButton);
        stopRecordedAudioButton.setEnabled(false);
        shareButton = findViewById(R.id.testShareButton);
        waveformView = findViewById(R.id.waveformView);
        exportButton = findViewById(R.id.exportButton);

        // Session
        session = (ChirpNoteSession) getIntent().getSerializableExtra("session");
        String basePath = this.getFilesDir().getPath();
        if(session == null) {
            session = new ChirpNoteSession("Name", new Key(Key.RootNote.C, Key.Type.MAJOR), 120,
                    basePath + "/midiTrack.mid", basePath + "/audioTrack.mp3");
        }
        mixer = new Mixer(session);
        midiDriver = MidiDriver.getInstance();

        // Audio track
        audioFile = new File(context.getFilesDir() + "/Session/Audio", "SessionAudio " +Calendar.getInstance().getTime() +".mp3");
        filePath = context.getFilesDir().getPath() + "/audioTrack.mp3";
        audio = mixer.audioTrack;
        recordButton.setColorFilter(Color.parseColor("#777777"));
        //timer logic
        Handler handler = new Handler();
        final Runnable runnableCode = new Runnable() {
            public void run() {
                waveformView.insertAmplitude((float) (audio.getMediaRecorder().getMaxAmplitude()));
                handler.postDelayed(this,20);
            }
        };
        audio.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                timer.stop();
            }
        });
        volSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                audio.getMediaPlayer().setVolume(value,value);
            }
        });
        volSlider.setVisibility(View.INVISIBLE);
        //TODO HERE
        //I'm not sure how to go about this, its sending it to the overview after "adding the file" is the idea
        if (getIntent().getStringExtra("flag").equals("fromPopActivity")){
            audioFile = (File) getIntent().getSerializableExtra("selectedFile");
            System.out.println(audioFile.getTotalSpace());
            audioFile.renameTo(new File("audioTrack.mp3"));
            System.out.println(audioFile.getPath() + "\n" + audioFile.getName() + "\n" + session.getAudioPath());
            redirectActivity(this,SessionOverviewActivity.class);
        }
        // Event listener for record audio button (to record audio from the device's microphone)
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //purge the timer ticker at every onclick instance to avoid all multiplicative timer errors.
                ticker.cancel();
                ticker.purge();
                //refresh the chronometer timer to 0:00
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();

                playRecordedAudioButton.setEnabled(audio.isRecording());

                if(!audio.isRecording()){
                    audio.startRecording();
                    recordButton.setColorFilter(Color.parseColor("#994444"));
                    //run
                    runnableCode.run();
                } else {
                    stopRecordedAudioButton.setEnabled(true);
                    audio.stopRecording();
                    timer.stop();
                    recordButton.setColorFilter(Color.parseColor("#777777"));
                    //shut down the task so you can create onClick (Avoid taskAlreadyScheduled)
                    handler.removeCallbacks(runnableCode);
                    handler.removeCallbacksAndMessages(null);
                }
            }
        });

        /*
        OnClick Listener for the play button
         */
        playRecordedAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!audio.isPlaying()){
                    timer.setBase(SystemClock.elapsedRealtime());
                    timer.stop();
                    timer.start();
                    audio.play();
                } else {
                    audio.stop();
                    timer.stop();
                }
            }
        });

        /*
         The listener for the save button
        */
        stopRecordedAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //write to file
                try {
                    InputStream inputStream = new FileInputStream(session.getAudioPath());
                    byte arr[] = readByte(inputStream);

                    //file output stream
                    audioFile = new File(context.getFilesDir() + "/Session/Audio", "SessionAudio " +Calendar.getInstance().getTime() +".mp3");
                    FileOutputStream fileOutput = new FileOutputStream(audioFile);
                    fileOutput.write(arr);
                    fileOutput.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(RecordAudioActivity.this,"Audio saved to " + audioFile.getPath(),Toast.LENGTH_LONG).show();
                System.out.println(audioFile.getPath());
            }
        });

        /*
        The listener for the share button intent
         */
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportHelper.shareFile(context,audioFile);
            }
        });

        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("find this");
                try {
                    new ZipFile(getFilesDir() + "/audio.zip").addFolder(new File(getFilesDir() + "/Session/"));
                } catch (ZipException e) {
                    e.printStackTrace();
                }
                Toast.makeText(RecordAudioActivity.this,"Directory zipped",Toast.LENGTH_SHORT).show();
            }
        });

        directoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordAudioActivity.this,DirectoryPopActivity.class);
                intent.putExtra("session",session);
                startActivity(intent);
            }
        });
    }

    /**
     * This method represents reading in the bytes of a file and is used for transferring file containers
     * @param is input stream of the original file
     * @return the data in bytes
     * @throws IOException
     */
    public static byte[] readByte(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer)) {
            os.write(buffer, 0, len);
        }

        return os.toByteArray();
    }

    private void requestSignIn(){
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this,signInOptions);
        startActivityForResult(client.getSignInIntent(),400);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode)
        {
            case 400:
                if(resultCode == RESULT_OK){
                    handleSignInIntent(data);
                }
                break;
        }
    }

    private void handleSignInIntent(Intent data){
        GoogleSignIn.getSignedInAccountFromIntent(data)
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
            @Override
            public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                GoogleAccountCredential credential = GoogleAccountCredential.
                        usingOAuth2(RecordAudioActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
                credential.setSelectedAccount(googleSignInAccount.getAccount());
                Drive googleDriveService = new Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential)
                        .setApplicationName("Record Audio")
                        .build();
                driveServiceHelper = new DriveServiceHelper(googleDriveService);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    
    public void uploadToDrive(View view){
        ExportHelper.exportToDrive(context,audioFile);
//        ProgressDialog progressDialog = new ProgressDialog(RecordAudioActivity.this);
//        progressDialog.setTitle("Uploading to Drive");
//        progressDialog.setMessage("Uploading...");
//        progressDialog.show();
//        String fileP = "/storage/emulated/0/mp3.mp3";
//        driveServiceHelper.createFileMp3(fileP).addOnSuccessListener(new OnSuccessListener<String>() {
//            @Override
//            public void onSuccess(String s) {
//                progressDialog.dismiss();
//                Toast.makeText(RecordAudioActivity.this,"Uploaded", Toast.LENGTH_SHORT).show();
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                progressDialog.dismiss();
//                Toast.makeText(RecordAudioActivity.this,"Check API", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    // navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                redirectActivity(this, HomeScreenActivity.class);
                break;
            case R.id.nav_overview:
                redirectActivity(this, SessionOverviewActivity.class);
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
                if (session.getSmartKeyboardFlag() == false) {
                    redirectActivity(this, KeyboardActivity.class);
                }
                else {
                    redirectActivity(this, SmartKeyboardActivity.class);
                }
                break;
            case R.id.nav_mixer:
                redirectActivity(this, MixerActivity.class);
                break;
            case R.id.nav_audio:
                // Just close the drawer since we're already on this activity
                drawer.closeDrawer(GravityCompat.START);
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
        if(mixer.areTracksPlaying()){
            mixer.stopTracks();
        }
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("flag", "fromRecordAudioActivity");
        intent.putExtra("session", session);
        intent.putExtra("user", user);
        if(user != null) saveToDB();
        activity.startActivity(intent);
    }

    private static void saveToDB(){
        realm.executeTransactionAsync(r -> {
            Session realmSession = r.where(Session.class).equalTo("_id", session.getId()).findFirst();
            realmSession.setAudioFile(realmSession.encodeFile(session.getAudioPath()));
        });
    }

    // pop up menu with session options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_session_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.subitem1:
                redirectActivity(this, SessionOptionsActivity.class);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();
        mixer.syncWithSession();
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }
}
