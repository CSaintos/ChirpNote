package com.example.chirpnote.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.MusicNote;
import com.example.chirpnote.R;
import com.example.chirpnote.RealTimeMelody;

import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;

public class KeyboardActivity extends AppCompatActivity {

    private MidiDriver midiDriver;
    private ArrayList<MusicNote> pianoKeys;
    RealTimeMelody melody;

    private Button minimizeBtn;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        minimizeBtn = findViewById(R.id.buttonMinimize);

        // If the app is started again while the
        // floating window service is running
        // then the floating window service will stop
        if (isMyServiceRunning()) {
            // onDestroy() method in FloatingWindowActivity
            // class will be called here
            stopService(new Intent(KeyboardActivity.this, FloatingWindowActivity.class));
        }

        Button recordButton = (Button) findViewById(R.id.recordButton);
        Button playButton = (Button) findViewById(R.id.playButton);
        Context context = this;
        String melodyFilePath = context.getFilesDir().getPath() + "/melody.mid";
        melody = new RealTimeMelody(120, melodyFilePath, playButton);

        midiDriver = MidiDriver.getInstance(); // MIDI driver to send MIDI events to
        //midiDriver.setVolume(25);  attempted to change the global volume of the midiDriver just to see if the volume of the keys played would change. They don't.
        pianoKeys = new ArrayList<>(); // List of notes
        // You can also create a new MusicNote without a Melody if you just want to test the keyboard playback stuff
        // For example: pianoKeys.add(new MusicNote(59, (Button) findViewById(R.id.noteBButton))
//        pianoKeys.add(new MusicNote(60, (Button) findViewById(R.id.noteCButton), melody));
//        pianoKeys.add(new MusicNote(62, (Button) findViewById(R.id.noteDButton), melody));
//        pianoKeys.add(new MusicNote(64, (Button) findViewById(R.id.noteEButton), melody));

        pianoKeys.add(new MusicNote(60, (Button) findViewById(R.id.noteC4Button), melody));
        pianoKeys.add(new MusicNote(61, (Button) findViewById(R.id.noteCSharp4Button), melody));
        pianoKeys.add(new MusicNote(62, (Button) findViewById(R.id.noteD4Button), melody));
        pianoKeys.add(new MusicNote(63, (Button) findViewById(R.id.noteDSharp4Button), melody));
        pianoKeys.add(new MusicNote(64, (Button) findViewById(R.id.noteE4Button), melody));
        pianoKeys.add(new MusicNote(65, (Button) findViewById(R.id.noteF4Button), melody));
        pianoKeys.add(new MusicNote(66, (Button) findViewById(R.id.noteFSharp4Button), melody));
        pianoKeys.add(new MusicNote(67, (Button) findViewById(R.id.noteG4Button), melody));
        pianoKeys.add(new MusicNote(68, (Button) findViewById(R.id.noteGSharp4Button), melody));
        pianoKeys.add(new MusicNote(69, (Button) findViewById(R.id.noteA4Button), melody));
        pianoKeys.add(new MusicNote(70, (Button) findViewById(R.id.noteASharp4Button), melody));
        pianoKeys.add(new MusicNote(71, (Button) findViewById(R.id.noteB4Button), melody));
        pianoKeys.add(new MusicNote(72, (Button) findViewById(R.id.noteC5Button), melody));

        // The Main Button that helps to minimize the app
        minimizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // First it confirms whether the
                // 'Display over other apps' permission in given
                if (checkOverlayDisplayPermission()) {
                    // FloatingWindowActivity service is started
                    startService(new Intent(KeyboardActivity.this, FloatingWindowActivity.class));
                    // The MainActivity closes here
                    finish();
                } else {
                    // If permission is not given,
                    // it shows the AlertDialog box and
                    // redirects to the Settings
                    requestOverlayDisplayPermission();
                }
            }
        });

        // Event listener for record button (to record melody)
        recordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!melody.isRecording()){
                    recordButton.setText("End recording");
                    melody.startRecording();
                } else {
                    recordButton.setText("Record");
                    melody.stopRecording();
                }
            }
        });

        // Event listener for play button (to play recorded melody)
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!melody.isPlaying()){
                    playButton.setText("Stop");
                    melody.play();
                } else {
                    playButton.setText("Play");
                    melody.stop();
                }
            }
        });

        // Setup event listener for each piano key
        for(MusicNote note : pianoKeys){
            note.getButton().setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        note.play(midiDriver);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        note.stop(midiDriver);
                    }
                    return false;
                }
            });
        }
    }

    private boolean isMyServiceRunning() {
        // The ACTIVITY_SERVICE is needed to retrieve a
        // ActivityManager for interacting with the global system
        // It has a constant String value "activity".
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);


        // A loop is needed to get Service information that
        // are currently running in the System.
        // So ActivityManager.RunningServiceInfo is used.
        // It helps to retrieve a
        // particular service information, here its this service.
        // getRunningServices() method returns a list of the
        // services that are currently running
        // and MAX_VALUE is 2147483647. So at most this many services
        // can be returned by this method.
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            // If this service is found as a running,
            // it will return true or else false.
            if (FloatingWindowActivity.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void requestOverlayDisplayPermission() {
        // An AlertDialog is created
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        // This dialog can be closed, just by taping
        // anywhere outside the dialog-box
        builder.setCancelable(true);


        // The title of the Dialog-box is set
        builder.setTitle("Screen Overlay Permission Needed");


        // The message of the Dialog-box is set
        builder.setMessage("Enable 'Display over other apps' from System Settings.");



        // The event of the Positive-Button is set
        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The app will redirect to the 'Display over other apps' in Settings.
                // This is an Implicit Intent. This is needed when any Action is needed
                // to perform, here it is
                // redirecting to an other app(Settings).
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));


                // This method will start the intent. It takes two parameter, one is the Intent and the other is
                // an requestCode Integer. Here it is -1.
                startActivityForResult(intent, RESULT_OK);
            }
        });

        dialog = builder.create();
        // The Dialog will
        // show in the screen
        dialog.show();
    }

    private boolean checkOverlayDisplayPermission() {
        // Android Version is lesser than Marshmallow or
        // the API is lesser than 23
        // doesn't need 'Display over other apps' permission enabling.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // If 'Display over other apps' is not enabled
            // it will return false or else true
            if (!Settings.canDrawOverlays(this)) {
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        midiDriver.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        midiDriver.stop();
    }
}
