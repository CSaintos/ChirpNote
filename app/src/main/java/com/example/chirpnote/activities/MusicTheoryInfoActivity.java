package com.example.chirpnote.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.R;

public class MusicTheoryInfoActivity extends AppCompatActivity implements View.OnClickListener
{

    ViewFlipper viewFlipper;
    Button next;
    Button back;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_theory_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        next = (Button) findViewById(R.id.next);
        back = (Button) findViewById(R.id.back);

        next.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        if (v == next)
        {
            viewFlipper.showNext();
        }
        else if (v == back)
        {
            viewFlipper.showPrevious();
        }
    }
}