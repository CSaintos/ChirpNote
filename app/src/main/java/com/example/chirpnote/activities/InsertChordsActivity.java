package com.example.chirpnote.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.example.chirpnote.R;

import java.util.ArrayList;
import java.util.List;

public class InsertChordsActivity extends AppCompatActivity implements View.OnClickListener
{
    LinearLayout layoutList;
    Button buttonAdd;

    List<String> keyTypeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_chords);

        layoutList = findViewById(R.id.layout_list);
        buttonAdd = findViewById(R.id.button_add_row);

        buttonAdd.setOnClickListener(this);

        keyTypeList.add("Type");
        keyTypeList.add("Major");
        keyTypeList.add("Minor");
    }

    @Override
    public void onClick(View v)
    {
        addView();
    }

    private void addView()
    {
        View rowView = getLayoutInflater().inflate(R.layout.add_row, null, false);

        EditText editText = (EditText)rowView.findViewById(R.id.edit_key_name);
        AppCompatSpinner keyTypeSpinner = (AppCompatSpinner)rowView.findViewById(R.id.spinner_key_type);
        ImageView imageClose = (ImageView)rowView.findViewById(R.id.row_remove);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, keyTypeList);


        keyTypeSpinner.setAdapter(arrayAdapter);

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(rowView);
            }
        });

        layoutList.addView(rowView);
    }

    private void removeView(View view)
    {
        layoutList.removeView(view);
    }

}
