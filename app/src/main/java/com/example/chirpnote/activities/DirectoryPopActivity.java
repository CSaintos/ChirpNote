package com.example.chirpnote.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.chirpnote.R;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class DirectoryPopActivity extends Activity {
    ArrayList<String> dirAudioArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_file_selection);
        //size of the screen
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        //set to 75% of screen
        ListView listView = findViewById(R.id.dirListView);
        ArrayAdapter adapter = new ArrayAdapter<>(DirectoryPopActivity.this,android.R.layout.simple_list_item_1,dirAudioArrayList);
        listView.setAdapter(adapter);
        //TODO: set it to a specific directory dynamically later
        File dir  = new File(this.getFilesDir() + "/Session/Audio");
        traverseAndAdd(dir);
        adapter.notifyDataSetChanged();
        getWindow().setLayout((int)(width * 0.85),(int) (height * 0.85));

    }
    public void traverseAndAdd (File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {

                for (int i = 0; i < files.length; ++i) {
                    File file = files[i];
                    if (file.isDirectory()) {
                        traverseAndAdd(file);
                    } else {
                        dirAudioArrayList.add(file.getName());
                    }
                }
            }
        }
    }


}
