package com.example.chirpnote.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.R;

import java.util.ArrayList;

public class InsertChordsActivity extends AppCompatActivity implements View.OnClickListener
{
    LinearLayout layoutList;
    Button buttonAdd;
    Button buttonSubmitList;
    Button[] measures;
    ArrayList<Button[]> listOfMeasures;
    Button measure1;
    Button measure2;
    Button measure3;
    Button measure4;
    Button[] sessionChords;

//    List<String> keyTypeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_chords);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        layoutList = findViewById(R.id.layout_list); // the space where the rows will be added
        buttonAdd = findViewById(R.id.button_add_row);
//        buttonSubmitList = findViewById(R.id.button_submit_list);

        initSessionChords();
        listOfMeasures = new ArrayList<>();

        for (int i = 0; i < sessionChords.length; i++)
        {
            sessionChords[i].setOnClickListener(this);
        }
        buttonAdd.setOnClickListener(this);
//        buttonSubmitList.setOnClickListener(this);

//        keyTypeList.add("Type");
//        keyTypeList.add("Major");
//        keyTypeList.add("Minor");
    }

    public void initSessionChords()
    {
        sessionChords = new Button[7];
        sessionChords[0] = findViewById(R.id.roman1);
        sessionChords[1] = findViewById(R.id.roman2);
        sessionChords[2] = findViewById(R.id.roman3);
        sessionChords[3] = findViewById(R.id.roman4);
        sessionChords[4] = findViewById(R.id.roman5);
        sessionChords[5] = findViewById(R.id.roman6);
        sessionChords[6] = findViewById(R.id.roman7);
    }

    @Override
    public void onClick(View v)
    {
//        addView();
        switch (v.getId())
        {
            case R.id.button_add_row:
                addView();
                break;

            case R.id.roman1:
                modifyMeasure(sessionChords[0]);
                break;

            case R.id.roman2:
                modifyMeasure(sessionChords[1]);
                break;

            case R.id.roman3:
                modifyMeasure(sessionChords[2]);
                break;

            case R.id.roman4:
                modifyMeasure(sessionChords[3]);
                break;

            case R.id.roman5:
                modifyMeasure(sessionChords[4]);
                break;

            case R.id.roman6:
                modifyMeasure(sessionChords[5]);
                break;

            case R.id.roman7:
                modifyMeasure(sessionChords[6]);
                break;

//            case R.id.button_submit_list:
        }
    }

    private void modifyMeasure(Button sessionChord)
    {
//        System.out.println("layoutList size = " + layoutList.getChildCount());
        if (layoutList.getChildCount() == 0)
        {
            Toast.makeText(getApplicationContext(), "Please add row of measures first.", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Select a measure.", Toast.LENGTH_LONG).show();
            for (int row = 0; row < layoutList.getChildCount(); row++)
            {
                measures = new Button[4];
                measures[0] = layoutList.getChildAt(row).findViewById(R.id.measure1);
                measures[1] = layoutList.getChildAt(row).findViewById(R.id.measure2);
                measures[2] = layoutList.getChildAt(row).findViewById(R.id.measure3);
                measures[3] = layoutList.getChildAt(row).findViewById(R.id.measure4);

                listOfMeasures.add(measures);
            }
        listOfMeasures.get(0)[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listOfMeasures.get(0)[0].setText(sessionChord.getText());
            }
        });
        listOfMeasures.get(0)[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listOfMeasures.get(0)[1].setText(sessionChord.getText());
            }
        });
        listOfMeasures.get(0)[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listOfMeasures.get(0)[2].setText(sessionChord.getText());
            }
        });
        listOfMeasures.get(0)[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listOfMeasures.get(0)[3].setText(sessionChord.getText());
            }
        });

        }
    }

    private void addView()
    {
        View rowView = getLayoutInflater().inflate(R.layout.add_row, null, false);

        ImageView imageClose = (ImageView)rowView.findViewById(R.id.row_remove);

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(rowView);
                System.out.println("Layoutlist Size = " + layoutList.getChildCount());
            }
        });

        layoutList.addView(rowView);
//        measure1 = layoutList.getChildAt(0).findViewById(R.id.measure1);
//        measure1.setText("r1m1");
//        measure3 = layoutList.getChildAt(0).findViewById(R.id.measure3);
//        measure3.setText("r1m3");

    }

    private void removeView(View view)
    {
        layoutList.removeView(view);
    }

}
