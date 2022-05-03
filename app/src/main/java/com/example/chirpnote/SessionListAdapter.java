package com.example.chirpnote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import io.realm.RealmResults;

public class SessionListAdapter extends ArrayAdapter<Session> {
    private Context mContext;
    private int mResource;

    public SessionListAdapter(@NonNull Context context, int resource, @NonNull RealmResults<Session> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView name = convertView.findViewById(R.id.SessionNameItem);
        TextView tempo = convertView.findViewById(R.id.SessionTempoItem);
        TextView key = convertView.findViewById(R.id.SessionKeyItem);

        name.setText(getItem(position).getName());
        tempo.setText("Tempo: " + getItem(position).getTempo() + " BPM");
        key.setText("Key: " + getItem(position).getKey());
        return convertView;
    }
}
