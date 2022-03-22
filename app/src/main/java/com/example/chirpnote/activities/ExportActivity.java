package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.chirpnote.R;

public class ExportActivity extends AppCompatActivity {

    private Button backButton;
    private Button toMP3;
    private Button toPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        backButton = (Button) findViewById(R.id.exportBackButton);
        toMP3 = (Button) findViewById(R.id.exportToMP3Button);
        toPDF = (Button) findViewById(R.id.exportToPDF);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        toMP3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        toPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}