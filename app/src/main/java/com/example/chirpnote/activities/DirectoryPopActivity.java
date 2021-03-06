package com.example.chirpnote.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.example.chirpnote.BuildConfig;
import com.example.chirpnote.ChirpNoteSession;
import com.example.chirpnote.ExportHelper;
import com.example.chirpnote.R;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.util.Zip4jUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DirectoryPopActivity extends Activity {
    ArrayList<String> dirAudioArrayList = new ArrayList<>();
    Context context = this;
    ChirpNoteSession session;
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
        session = (ChirpNoteSession) getIntent().getSerializableExtra("session");
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File exportFile = new File(dir + "/" + dirAudioArrayList.get(position));
                PopupMenu popupMenu = new PopupMenu(context,view);
                popupMenu.getMenuInflater().inflate(R.menu.audio_directory_popup_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.item_share:
                                ExportHelper.shareFile(DirectoryPopActivity.this,exportFile);
                                return true;
                            case R.id.item_add_to_session:
                                System.out.println(exportFile.getName());
                                session.setAudioPath(exportFile.getPath());
                                Intent intent = new Intent(DirectoryPopActivity.this,RecordAudioActivity.class);
                                intent.putExtra("session",session);
                                intent.putExtra("flag","fromPopActivity");
                                intent.putExtra("selectedFile",exportFile);
                                startActivity(intent);
                                return true;
                            case R.id.item_export_drive:
                                ExportHelper.exportToDrive(DirectoryPopActivity.this,exportFile);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.show();

            }
        });

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
