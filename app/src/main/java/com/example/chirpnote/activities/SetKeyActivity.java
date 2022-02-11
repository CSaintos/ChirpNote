package com.example.chirpnote.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chirpnote.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
public class SetKeyActivity extends AppCompatActivity {
    private static final String API_KEY = "b56665025cc9d931bdf1ab71847da39d"; //GetSongKey API Key
    ArrayList<String> songArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        songArrayList.clear();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_key_from_song);
        ArrayAdapter adapter = new ArrayAdapter<>(SetKeyActivity.this, android.R.layout.simple_list_item_1, songArrayList);
        Button searchButton = (Button) findViewById(R.id.keySearchButton);
        ListView listView = (ListView) findViewById(R.id.songListView);
        listView.setAdapter(adapter);


        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            /*
             * This onclick represents the search button. It uses threading in order to save ui resources
             * Which avoids freezing of the ui elements during the data calls.
             */
            public void onClick(View v) {
                songArrayList.clear();
                searchButton.setText("Searching");
                EditText query = (EditText) findViewById(R.id.editSongQuery);
                String songQueryString = query.getText().toString();
                //Thread for API Call
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            songArrayList.addAll(songData(songQueryString));

                            //update the ListView
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                    if (adapter.isEmpty()){
                                        searchButton.setText("No Results. Click For New Search");
                                    }
                                    else {
                                        searchButton.setText("Click for New Search");
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });


    }


    /***
     * Creates a list of elements for the top 5 search results as an ArrayList of string
     * @param songQuery The query being used from input
     * @return the songs and keys
     * @throws IOException if any errors occur with query
     */
    public static ArrayList<String> songData(String songQuery) throws IOException {
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
//        if (n == 1) {
//            String noSongID = searchRootObject.getAsJsonArray("search").get(0).getAsJsonObject().getAsString();
//        }
        if (n <= 5) {
            for (int i = 0; i < n; i++) {
                String songID = searchRootObject.getAsJsonArray("search").get(i).getAsJsonObject().get("id").getAsString();
                listOfIDs.add(songID);
            }

        } else {
            for (int i = 0; i < 5; i++) {
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
        return ("\nSong: " + songName + " by " + songArtist + "\nKey: " + songKey);
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
