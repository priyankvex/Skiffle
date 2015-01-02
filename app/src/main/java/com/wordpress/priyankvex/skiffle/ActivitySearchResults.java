package com.wordpress.priyankvex.skiffle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by priyank on 31/12/14.
 * Shows search results in a list view. Max. items  = 20
 */
public class ActivitySearchResults extends ActionBarActivity {
    ListView listSearchResults;

    String url;

    List<Map> songs = new ArrayList<>();
    List<Bitmap> coverArts = new ArrayList<>();

    //Lists for main thread
    List<Map> mSongs = new ArrayList<>();
    List<Bitmap> mCoverArts = new ArrayList<>();

    //For the sake of funny loading messages.
    //This wil show the world that programmers have the best sense of humor.
    private String[] loadingMessages;

    //Hello world! I am a constructor.
    public ActivitySearchResults(){
        loadingMessages = new String[20];
        loadingMessages[0] = "It is still faster then you searching the internet!";
        loadingMessages[1] = "And enjoy the elevator music";
        loadingMessages[2] = "a few bits tried to escape, but we caught them";
        loadingMessages[3] = "the server is powered by a lemon and two electrodes";
        loadingMessages[4] = "we're testing your patience";
        loadingMessages[5] = "scouts are searching songs as fast as they can";
        loadingMessages[6] = "would you prefer chicken, steak, or tofu?";
        loadingMessages[7] = "and dream of faster computers";
        loadingMessages[8] = "go ahead -- hold your breath";
        loadingMessages[9] = "at least you're not on hold";
        loadingMessages[10] = "as if you had any other choice :P";
        loadingMessages[11] = "and curse your internet service provider";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);

        if( !isNetworkAvailable()){
            setContentView(R.layout.empty_list_layout);
            return;
        }

        listSearchResults = (ListView)findViewById(R.id.listview_search_results);

        //OnClick listener for the songs_list_view
        listSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> song = mSongs.get(position);
                Bundle b = new Bundle();
                b.putString("img170", song.get("img170"));
                b.putString("name", song.get("name"));
                b.putString("album", song.get("album"));
                b.putString("artist", song.get("artist"));
                b.putString("genre", song.get("genre"));
                String date = getFormatedDate(song.get("releaseDate"));
                b.putString("releaseDate", date);
                b.putString("rights", song.get("rights"));
                b.putString("iTunesLink", song.get("iTunesLink"));
                Intent i = new Intent(ActivitySearchResults.this, DetailsActivity.class);
                i.putExtras(b);
                startActivity(i);
            }
        });

        Bundle b = getIntent().getExtras();
        url = b.getString("url");
        //Making the content explicit. Because we are the fucking animals.
        url+="&limit=20&explicit=true";
        //Executing the background thread.
        new GetResults().execute();
    }

    // Helper method to check the network availability on the device.
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)ActivitySearchResults.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //Helper function to generate a randmo integer in range(0,11).
    public static int randInt() {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((11) + 1);

        return randomNum;
    }

    //Helper function to format date
    String getFormatedDate(String date){
        date= date.replaceAll("T", " ");
        date = date.replaceAll("Z","");
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        Date parsed = new Date();
        try
        {
            parsed = inputFormat.parse(date);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        String outputText = outputFormat.format(parsed);

        return outputText;
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetResults extends AsyncTask<Void, Void, Void> {

        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String msg = loadingMessages[randInt()];
            progress = ProgressDialog.show(ActivitySearchResults.this, "Please Wait",
                    msg, true);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            if (jsonStr != null) {
                try {

                    //cleaning the past data from the songs and coverArts list
                    songs.clear();
                    coverArts.clear();
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray results = jsonObj.getJSONArray("results");

                    //entry contains array of song objects
                    for(int i = 0; i < results.length(); i++){
                        //getting single song object at a time
                        JSONObject songObj = results.getJSONObject(i);

                        //Storing each element of the song in a string only to bind them in a map later
                        String name = songObj.getString("trackName");
                        String img55 = songObj.getString("artworkUrl60");
                        String img170 = songObj.getString("artworkUrl100");
                        String album = songObj.getString("collectionName");
                        String artist = songObj.getString("artistName");
                        String genre = songObj.getString("primaryGenreName");
                        String releaseDate = songObj.getString("releaseDate");
                        String rights = "rights";
                        String iTunesLink = songObj.getString("trackViewUrl");

                        //Binding all this information related to a single song in a map
                        HashMap<String, String> song = new HashMap<>();
                        song.put("name", name);
                        song.put("img55", img55);
                        song.put("img170", img170);
                        song.put("album", album);
                        song.put("artist", artist);
                        song.put("genre", genre);
                        song.put("releaseDate", releaseDate);
                        song.put("iTunesLink", iTunesLink);
                        song.put("rights", rights);

                        //Adding this map to the list of song maps
                        songs.add(song);

                        //Getting the Bitmap from the url of the small coverArt image
                        Bitmap image = null;
                        try{
                            URL smallImageLink = new URL(img55);
                            image = BitmapFactory.decodeStream(smallImageLink.openConnection().getInputStream());
                        }catch (IOException e){
                        }

                        coverArts.add(image);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(progress.isShowing()){
                progress.dismiss();
            }
            mSongs.clear();
            mCoverArts.clear();
            for (int i = 0; i < songs.size(); i++){
                mSongs.add(songs.get(i));
            }
            for (int i = 0; i < coverArts.size(); i++){
                mCoverArts.add(coverArts.get(i));
            }

            //Giving user a no result screen. When no results are found.
            //Pretty obvious. No?
            if(mSongs.isEmpty()){
                setContentView(R.layout.no_results_layout);
                return;
            }
            SongListAdapter adapter = new SongListAdapter(getApplicationContext(), R.layout.song_list_item_row, mSongs, mCoverArts);
            listSearchResults.setAdapter(adapter);
        }
    }
}
