package com.wordpress.priyankvex.skiffle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by priyank on 23/12/14.
 * Class to get the top albums
 */
public class FragmentTopAlbums extends Fragment {

    //Globals for the class
    private final String urlEnglish = "http://itunes.apple.com/us/rss/topalbums/limit=10/json";
    private final String urlHindi = "http://itunes.apple.com/in/rss/topalbums/limit=10/json";
    private String url = urlEnglish;
    List<Map> songs = new ArrayList<>();
    List<Bitmap> coverArts = new ArrayList<>();

    //ListView widget
    ListView albums_list_view;

    //Lists for main ui thread
    List<Map> mSongs = new ArrayList<>();
    List<Bitmap> mCoverArts = new ArrayList<>();

    Activity activity;

    //For progrss dialog
    private String[] loadingMessages;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentTopAlbums newInstance(int sectionNumber) {
        FragmentTopAlbums fragment = new FragmentTopAlbums();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentTopAlbums() {
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

    public static int randInt() {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((11) + 1);

        return randomNum;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top_albums, container, false);

        if( !isNetworkAvailable()){
            rootView = inflater.inflate(R.layout.empty_list_layout, container, false);
            return rootView;
        }
        albums_list_view = (ListView)rootView.findViewById(R.id.list_view_albums);


        //OnClick listener for the songs_list_view
        albums_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> song = mSongs.get(position);
                Bundle b = new Bundle();
                b.putString("name", song.get("name"));
                b.putString("album", song.get("album"));
                b.putString("artist", song.get("artist"));
                b.putString("genre", song.get("genre"));
                b.putString("releaseDate", song.get("releaseDate"));
                b.putString("iTunesLink", song.get("iTunesLink"));
                b.putString("img170", song.get("img170"));
                Intent i = new Intent(getActivity(), DetailsAlbumActivity.class);
                i.putExtras(b);
                startActivity(i);
            }
        });

        //Getting the suitable link as per the user preferences.
        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String language = prefs.getString("language", "english");
        Log.d("SKIFFLE", language);

        if(language == null || language.equals("english")){
            url = urlEnglish;
        }
        else{
            url = urlHindi;
        }
        new GetAlbums().execute();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();



    }

    // Helper method to check the network availability on the device.
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetAlbums extends AsyncTask<Void, Void, Void> {

        ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String msg = loadingMessages[randInt()];
            progress = ProgressDialog.show(getActivity(), "Please Wait",
                    msg, true);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Log.d("SKIFFLE", "Downloading new song data");
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
                    JSONObject feed = jsonObj.getJSONObject("feed");
                    JSONArray entry = feed.getJSONArray("entry");

                    //entry contains array of 10 song objects
                    for(int i = 0; i < entry.length(); i++){
                        //getting single song object at a time
                        JSONObject songObj = entry.getJSONObject(i);

                        //Storing each element of the song in a string only to bind them in a map later
                        String name = songObj.getJSONObject("im:name").getString("label");
                        JSONArray images = songObj.getJSONArray("im:image");
                        String img55 = images.getJSONObject(0).getString("label");
                        String img170 = images.getJSONObject(2).getString("label");
                        String album = songObj.getJSONObject("im:itemCount").getString("label");
                        String artist = songObj.getJSONObject("im:artist").getString("label");
                        String genre = songObj.getJSONObject("category").getJSONObject("attributes").getString("term");
                        String releaseDate = songObj.getJSONObject("im:releaseDate").getJSONObject("attributes").getString("label");
                        String rights = songObj.getJSONObject("rights").getString("label");
                        String iTunesLink = songObj.getJSONObject("id").getString("label");

                        //Binding all this information related to a single song in a map
                        HashMap<String, String> song = new HashMap<>();
                        song.put("name", name);
                        song.put("img55", img55);
                        song.put("img170", img170);
                        song.put("album", album);
                        song.put("artist", artist);
                        song.put("genre", genre);
                        song.put("releaseDate", releaseDate);
                        song.put("rights", rights);
                        song.put("iTunesLink", iTunesLink);

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

                        Log.d("SKIFFLE", "size of data albums: " + Integer.toString(songs.size()));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("SKIFFLE", "Couldn't get any data from the url");
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
            SongListAdapter adapter = new SongListAdapter(activity, R.layout.song_list_item_row, mSongs, mCoverArts);
            albums_list_view.setAdapter(adapter);

        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        this.activity = activity;
    }
}
