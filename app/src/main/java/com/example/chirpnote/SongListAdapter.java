package com.example.chirpnote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;

import com.example.midiFileLib.src.event.meta.Text;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class SongListAdapter extends ArrayAdapter<queryResult> {

    private Context mContext;
    private int mResource;
    //handler to launch things on main thread where required
    final Handler handler = new Handler();


    /**
     * A SongListAdapter
     * @param context the context
     * @param resource the resource
     * @param objects the related objects
     */
    public SongListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<queryResult> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    /**
     * Recieve the packaged view in the listview context per item
     * @param position the current item
     * @param convertView the packaged view
     * @param parent the parent context
     * @return the packaged view
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater  layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource,parent,false);
        ImageView imageView = convertView.findViewById(R.id.SongImageItem);
        TextView textView = convertView.findViewById(R.id.SongText);
        TextView songArtistView = convertView.findViewById(R.id.SongArtistText);
        TextView songKeyView = convertView.findViewById(R.id.SongKeyText);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
        ImageLoader.getInstance().init(config);
        ImageLoader imageLoader = ImageLoader.getInstance();
        String queryJsonPre = getItem(position).getImage();
        File text = new File(mContext.getFilesDir() + "/" + getItem(position).getSongArtist() + "_text.json");
        try {
            text.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
         * This thread does a bulk of the work related to network required commands, which is why it
         * loads in data asynchronously for the images. It's best to just let it work when it does.
         */
        new Thread(new Runnable() {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {
        try {
            URL queryURL = new URL(queryJsonPre);
            FileUtils.copyURLToFile(queryURL,text);
            JsonElement searchRootElement = null;
            try {
                searchRootElement = new JsonParser().parse(new FileReader(text));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            JsonObject searchRootObject = searchRootElement.getAsJsonObject();
            /*
            Avoid crashes related to esoteric results.
            */
            if (!searchRootElement.getAsJsonObject().isJsonObject() || searchRootObject.size() == 0 || searchRootObject.getAsJsonArray("results").isEmpty()){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageDrawable(AppCompatResources.getDrawable(mContext,R.drawable.chirpnote_temp_icon));
                        mContext.deleteFile(text.getName());
                    }
                });
            }
            else {
                String songImg = searchRootObject.getAsJsonArray("results").get(0).getAsJsonObject().get("artworkUrl100").getAsString();
                imageLoader.loadImage(songImg, new SimpleImageLoadingListener(){
                    @Override
                    /*
                     * this listener is for the image loading process that works with a url
                     */
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(loadedImage);
                                mContext.deleteFile(text.getName());
                            }
                        });
                    }
                });
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        }).start();
        /*
        Set all the rest of the elements before packaginng the view back to the listview
         */
        textView.setText(getItem(position).getSongTitle());
        songArtistView.setText(getItem(position).getSongArtist());
        songKeyView.setText(getItem(position).getSongKey());

        return convertView;
    }
}
