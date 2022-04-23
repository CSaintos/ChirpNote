package com.example.chirpnote;

import android.app.Application;
import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.chirpnote.activities.SetKeyFromSongActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class SongListAdapter extends ArrayAdapter<queryResult> {

    private Context mContext;
    private int mResource;


    public SongListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<queryResult> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater  layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);
        ImageView imageView = convertView.findViewById(R.id.SongImageItem);
        TextView textView = convertView.findViewById(R.id.SongText);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
        ImageLoader.getInstance().init(config);
        ImageLoader imageLoader = ImageLoader.getInstance();
        String queryJsonPre = getItem(position).getImage();
        File text = new File("text.json");

        new Thread(new Runnable() {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        try {
            System.out.println(text.getPath());
            Gson gson = new Gson();
            URL queryURL = new URL(queryJsonPre);
            FileUtils.copyURLToFile(queryURL,text);
            System.out.println(text.getPath());
            Reader reader = Files.newBufferedReader(Paths.get("text.json"));
            Map<?, ?> map = gson.fromJson(reader, Map.class);
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                System.out.println(entry.getKey() + "=" + entry.getValue());
            }
            reader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}).start();
//        JsonElement searchRootElement = JsonParser.parseString();
//        JsonObject searchRootObject = searchRootElement.getAsJsonObject();
//
//
//        String songImg = searchRootObject.getAsJsonArray("results").get(0).getAsJsonObject().get("artworkUrl60").getAsString();
        String songImg = "https://i.imgur.com/TXoM8gQ.png";
        imageLoader.displayImage(songImg,imageView);
        textView.setText(getItem(position).getInformation());
        return convertView;
    }
}
