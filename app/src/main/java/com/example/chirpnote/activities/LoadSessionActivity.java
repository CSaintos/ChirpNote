package com.example.chirpnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.chirpnote.ChirpNoteSession;
import com.example.chirpnote.Key;
import com.example.chirpnote.R;
import com.example.chirpnote.Session;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

public class LoadSessionActivity extends AppCompatActivity {
    RealmResults<Session> sessions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_session);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String username = getIntent().getStringExtra("username");
        String basePath = this.getFilesDir().getPath();

        // To let user know that results are loading
        ProgressDialog progressDialog = new ProgressDialog(LoadSessionActivity.this);
        progressDialog.setTitle("Finding your sessions");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        // Fill sessions list with all of the user's sessions
        Realm realm = Realm.getDefaultInstance();
        sessions = realm.where(Session.class).findAllAsync();

        ArrayAdapter<Session> adapter = new ArrayAdapter<>(LoadSessionActivity.this, android.R.layout.simple_list_item_1, sessions);
        ListView listView = (ListView) findViewById(R.id.sessionList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Session rSession = sessions.get(position);
                ChirpNoteSession session = new ChirpNoteSession(rSession.getName(), Key.decode(rSession.getKey()), rSession.getTempo(),
                        basePath + "midiTrack.mid", basePath + "audioTrack.mp3", rSession.getUsername());
                if(rSession.getMidiFile() != null) {
                    rSession.writeEncodedFile(rSession.getMidiFile(), session.getMidiPath());
                }
                Intent intent = new Intent(LoadSessionActivity.this, TestTrackPersistenceActivity.class);
                intent.putExtra("session", session);
                startActivity(intent);
            }
        });

        sessions.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Session>>() {
            @Override
            public void onChange(RealmResults<Session> sessions, OrderedCollectionChangeSet changeSet){
                adapter.notifyDataSetChanged();
                if(adapter.isEmpty()){
                    Toast.makeText(LoadSessionActivity.this,"No sessions found.",Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });
    }
}