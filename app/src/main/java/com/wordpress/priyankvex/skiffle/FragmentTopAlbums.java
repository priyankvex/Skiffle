package com.wordpress.priyankvex.skiffle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by priyank on 23/12/14.
 * Class to get the top albums
 */
public class FragmentTopAlbums extends Fragment {

    //Globals for the class
    private final String url = "http://itunes.apple.com/us/rss/topalbums/limit=10/json";
    List<HashMap> songs = new ArrayList<>();
    List<Bitmap> coverArts = new ArrayList<>();

    //ListView widget
    ListView albums_list_view;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_top_albums, container, false);
        albums_list_view = (ListView)rootView.findViewById(R.id.list_view_albums);

        DatabaseHandler db = new DatabaseHandler(getActivity());
        boolean tableEmpty = db.isEmptyTableTop10Albums();
        if( !tableEmpty ){
            Log.d("SKIFFLE", "Table is having past update so populating the list with it");
            fillListWithData();
        }

        //OnClick listener for the songs_list_view
        albums_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> song = DBsongs.get(position);
                Bundle b = new Bundle();
                b.putString("img170", song.get("img170"));
                b.putString("name", song.get("name"));
                b.putString("album", song.get("album"));
                b.putString("artist", song.get("artist"));
                b.putString("genre", song.get("genre"));
                b.putString("releaseDate", song.get("date"));
                b.putString("rights", song.get("rights"));
                b.putString("iTunesLink", song.get("iTunesLink"));
                Intent i = new Intent(getActivity(), DetailsAlbumActivity.class);
                i.putExtras(b);
                startActivity(i);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if( isNetworkAvailable()){
            new GetAlbums().execute();
        }
        DatabaseHandler db = new DatabaseHandler(getActivity());
        boolean tableEmpty = db.isEmptyTableTop10Albums();
        if( !tableEmpty ){
            Log.d("SKIFFLE", "Table is having past update so populating the list with it");
            fillListWithData();
        }

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

            Log.d("SKIFFLE", "In onPostExecute()");
            // So far : Downloaded all the song info for the top 10 songs
            // Now storing this info into the database table
            DatabaseHandler db = new DatabaseHandler(getActivity());
            {
                Log.d("SKIFFLE", "Net is working");
                if(songs.get(9) != null && coverArts.get(9) != null){
                    try {
                        Toast.makeText(getActivity(), "Updated with new data", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    db.addTopAlbums(songs, coverArts);
                    Log.d("SKIFFLE", "Done with writing the data into the database");
                }
                fillListWithData();
                Log.d("SKIFFLE", "Updated the list with latest data downloaded");

                Log.d("SKIFFLE", "Net is not working, not writing anything into the database");
            }
        }
    }

    List<Map>DBsongs;
    List<Bitmap>DBcoverArts;
    private void fillListWithData(){
        DatabaseHandler db = new DatabaseHandler(getActivity());
        Map<String, List> map = db.readTopAlbums();
        DBsongs = map.get("songs");
        DBcoverArts= map.get("images");
        Log.d("SKIFFLE", "Done reading the data from database");
        SongListAdapter adapter = new SongListAdapter(getActivity(), R.layout.song_list_item_row, DBsongs, DBcoverArts);
        albums_list_view.setAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
