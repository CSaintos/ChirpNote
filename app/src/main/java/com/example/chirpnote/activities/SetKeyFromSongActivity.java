package com.example.chirpnote.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.R;
import com.example.chirpnote.SongListAdapter;
import com.example.chirpnote.queryResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/***
 * This activity works with the getsongBPM API to take in a user's query for a known song, and to
 * reflect the key of the song to the user for them to select as the session key.
 */
public class SetKeyFromSongActivity extends AppCompatActivity {
    private static final String API_KEY = "b56665025cc9d931bdf1ab71847da39d"; //GetSongKey API Key
    ArrayList<String> songArrayList = new ArrayList<>();
    ArrayList<queryResult> songArrayListFinished = new ArrayList<>();
    boolean flag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        //flag to check if the activity came from the right place
        Intent intent = getIntent();
        String checkFlag = intent.getStringExtra("flag");
        if (checkFlag.equals("fromSetKeyActivity")){
            flag = true;
        }
        else
            flag = false;
        songArrayList.clear();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_key_from_song);
        SongListAdapter adapter = new SongListAdapter(SetKeyFromSongActivity.this, R.layout.song_custom_list_row, songArrayListFinished);
        Button searchButton = (Button) findViewById(R.id.keySearchButton);
        ListView listView = (ListView) findViewById(R.id.songListView);
        listView.setAdapter(adapter);
        EditText editText = (EditText) findViewById(R.id.editSongQuery);

        /*
        This editText listener calls the button press when the user clicks enter in the search field.
         */
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchButton.performClick();
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            /*
             * This onclick represents the search button. It uses threading in order to save ui resources
             * Which avoids freezing of the ui elements during the data calls.
             */
            public void onClick(View v) {
                songArrayList.clear();
                songArrayListFinished.clear();
                searchButton.setText("Searching");
                EditText query = (EditText) findViewById(R.id.editSongQuery);
                String songQueryString = query.getText().toString();
                ProgressDialog progressDialog = new ProgressDialog(SetKeyFromSongActivity.this);
                progressDialog.setTitle("GetSongBPM Search");
                progressDialog.setMessage("Searching...");
                progressDialog.show();
                //Thread for API Call
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            songArrayList.addAll(songData(songQueryString));
                            ArrayList<String> removelist = new ArrayList<>();
                            for (String S: songArrayList){
                                if (S.contains("Key: m")){
                                    removelist.add(S);
                                }}
                            songArrayList.removeAll(removelist);
                            for (String song:songArrayList){
                                String songTitle = StringUtils.substringBetween(song,"Song: "," by");
                                String songArtist = StringUtils.substringBetween(song,"by ", "\nKey:");
                                String songKey = StringUtils.substringBetween(song,"Key: ","|").replace("m", " Minor");
                                if (!songKey.contains("Minor")){
                                    songKey = songKey + " Major";
                                }
                                String songQuery = StringUtils.substringBetween(song,"Song: ", "Key:");
                                System.out.println(songTitle);
                                System.out.println(songArtist);
                                System.out.println(songKey);
                                System.out.println(songQuery);
                                songArrayListFinished.add(new queryResult(songTitle, songArtist, songKey, songQuery));
                            }
                            progressDialog.dismiss();
                            //update the ListView
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    if (adapter.isEmpty()){
                                        searchButton.setText("No Results. Click For New Search");
                                        Toast.makeText(SetKeyFromSongActivity.this,"Either song is not in database or song query contains typos.",Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        searchButton.setText("Click for New Search");
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    /*
    This listener functions for changing the key of the song on item click. also displays a message to user on click.
     */

    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //shows the key that has been set on click
            Toast.makeText(SetKeyFromSongActivity.this, "Session Key Set to " + songArrayListFinished.get(position).getSongKey(),Toast.LENGTH_SHORT).show();
            //TODO set the key in session, need to find ways to link of current session
            if (flag == true)
                System.out.println("yes");
            else
                System.out.println("no");
        }
    });
    }

    /***
     * Creates a list of elements for the top 5 search results as an ArrayList of string
     * @param songQuery The query being used from input
     * @return the songs and keys
     * @throws IOException if any errors occur with query
     */
    public ArrayList<String> songData(String songQuery) throws IOException {
        ArrayList<String> songData = new ArrayList<>();
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //get the query from the user
            String query = songQuery.replace(" ", "+");

            //connect to the API to search for the song
            HttpURLConnection conn = searchDataConnection(query);
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                //bad code throw error
                Toast.makeText(SetKeyFromSongActivity.this,"HTTP ERROR " + conn.getResponseCode(),Toast.LENGTH_LONG).show();
                throw new RuntimeException("HTTP Response Code:" + responseCode);
            } else {
                //Grab needed data
                StringBuilder searchSB = getAPIData(conn);

                //parse returned search data in gson. retrieves the string IDs of the song result
                ArrayList<String> songIDs = getSearchData(searchSB);

                //Parse IDs with API calls again
                for (String x : songIDs) {
                    HttpURLConnection songConn = SongDataConnection(x);
                    songConn.connect();
                    StringBuilder songSB = getAPIData(songConn);

                    //gson parsing and posting results
                    songData.add(getSongData(songSB));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(songData.toString());
        return songData;
    }

    /***
     * Returns a HttpConnection for a search query to the API
     * @param query The query being used in the search
     * @return The connection to be made to the API
     * @throws IOException if there are any errors in connection
     */
    public static HttpURLConnection searchDataConnection(String query) throws IOException {
        URL url = new URL("https://api.getsongbpm.com/search/?api_key=" + API_KEY + "&type=song&lookup=" + query);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.addRequestProperty("User-Agent", "Chrome");
        conn.setRequestMethod("GET");
        return conn;
    }

    /***
     * Returns a HttpConnection for a songID lookup in the API
     * @param SongID The id of the song being used in lookup
     * @return the connection to be made to the API
     * @throws IOException if there are any errors in connection
     */
    public static HttpURLConnection SongDataConnection(String SongID) throws IOException {
        URL songUrl = new URL("https://api.getsongbpm.com/song/?api_key=" + API_KEY + "&id=" + SongID);
        HttpURLConnection songConn = (HttpURLConnection) songUrl.openConnection();
        songConn.addRequestProperty("User-Agent", "Chrome");
        songConn.setRequestMethod("GET");
        return songConn;
    }

    /***
     * Returns an ArrayList of songIDs for at most the 5 popular results.
     * @param searchSB the Stringbuilder which was made from parsing the json returned by the connection
     * @return an arraylist of song IDs
     */
    public static ArrayList<String> getSearchData(StringBuilder searchSB) {
        ArrayList<String> listOfIDs = new ArrayList<>();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement searchRootElement = JsonParser.parseString(String.valueOf(searchSB));
        JsonObject searchRootObject = searchRootElement.getAsJsonObject();
        int n = searchRootObject.getAsJsonArray("search").size();
        System.out.println("amount of results: " + n);
        if (n <= 10) {
            for (int i = 0; i < n; i++) {
                String songID = searchRootObject.getAsJsonArray("search").get(i).getAsJsonObject().get("id").getAsString();
                listOfIDs.add(songID);
            }

        } else {
            for (int i = 0; i < 10; i++) {
                String songID = searchRootObject.getAsJsonArray("search").get(i).getAsJsonObject().get("id").getAsString();
                listOfIDs.add(songID);
            }
        }
        return listOfIDs;
    }

    /***
     * Gets the song data for the given songID, including artist and the written Key
     * @param songSB stringBuilder made from parsing the json returned by the connection
     */
    public static String getSongData(StringBuilder songSB) {
        JsonElement songRootElement = JsonParser.parseString(String.valueOf(songSB));
        JsonObject songRootObject = songRootElement.getAsJsonObject();
        String songName = songRootObject.getAsJsonObject("song").get("title").getAsString();
        String songArtist = songRootObject.getAsJsonObject("song").getAsJsonObject("artist").get("name").getAsString();
        //includes unicode replacement for sharp symbol
        String songKey = songRootObject.getAsJsonObject("song").get("key_of").getAsString().replaceAll("\\u266f", "#");
        return ("\nSong: " + songName + " by " + songArtist + "\nKey: " + songKey + "|");
    }

    /***
     * Runs through the returned data from the API and sets the data into a stringBuilder then closes connection.
     * @param conn the connection to be used in the parsing
     * @return the parsed data as appended into a stringBuilder
     * @throws IOException if there is a connection issue
     */
    public static StringBuilder getAPIData(HttpURLConnection conn) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb;
    }

}
