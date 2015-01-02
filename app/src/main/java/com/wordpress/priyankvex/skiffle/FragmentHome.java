package com.wordpress.priyankvex.skiffle;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
 */
public class FragmentHome extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    ImageView img_song_topper_english;
    ImageView img_song_topper_hindi;
    ImageView img_album_topper_english;
    ImageView img_album_topper_hindi;
    ImageView img_fav_1;
    ImageView img_fav_2;
    ImageView img_fav_3;

    Button btn_see_all;

    private final String url1 = "http://itunes.apple.com/us/rss/topsongs/limit=2/json";
    private final String url2 = "http://itunes.apple.com/in/rss/topsongs/limit=2/json";
    private final String url3 = "http://itunes.apple.com/us/rss/topalbums/limit=2/json";
    private final String url4 = "http://itunes.apple.com/in/rss/topalbums/limit=2/json";

    Bundle b1;
    Bundle b2;
    Bundle b3;
    Bundle b4;
    Bundle bf1;
    Bundle bf2;
    Bundle bf3;


    Bitmap image1 = null;
    Bitmap image2 = null;
    Bitmap image3 = null;
    Bitmap image4 = null;




    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentHome newInstance(int sectionNumber) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentHome() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        img_song_topper_english = (ImageView)rootView.findViewById(R.id.img_song_topper_english);
        img_song_topper_hindi = (ImageView)rootView.findViewById(R.id.img_song_topper_hindi);
        img_album_topper_english = (ImageView)rootView.findViewById(R.id.img_album_topper_english);
        img_album_topper_hindi = (ImageView)rootView.findViewById(R.id.img_album_topper_hindi);
        img_fav_1 = (ImageView)rootView.findViewById(R.id.img_fav_1);
        img_fav_2 = (ImageView)rootView.findViewById(R.id.img_fav_2);
        img_fav_3 = (ImageView)rootView.findViewById(R.id.img_fav_3);
        btn_see_all = (Button)rootView.findViewById(R.id.btn_see_all);

        b1 = new Bundle();
        b2 = new Bundle();
        b3 = new Bundle();
        b4 = new Bundle();
        bf1 = new Bundle();
        bf2 = new Bundle();
        bf3 = new Bundle();

        //Setting the layout params of favourites images
        int height = (int)getScreenWidth();
        int h = height/3;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(h, h);
        img_fav_1.setLayoutParams(layoutParams);
        img_fav_2.setLayoutParams(layoutParams);
        img_fav_3.setLayoutParams(layoutParams);
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(height, height);
        img_song_topper_english.setLayoutParams(layoutParams1);
        img_song_topper_hindi.setLayoutParams(layoutParams1);
        img_album_topper_english.setLayoutParams(layoutParams1);
        img_album_topper_hindi.setLayoutParams(layoutParams1);





        btn_see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, FragmentFavourites.newInstance(4))
                        .commit();
            }
        });


        img_song_topper_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DetailsActivity.class);
                i.putExtras(b1);
                if( b1.getString("name") != null)
                    startActivity(i);
            }
        });

        img_song_topper_hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DetailsActivity.class);
                i.putExtras(b2);
                if( b2.getString("name") != null)
                    startActivity(i);
            }
        });

        img_album_topper_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DetailsAlbumActivity.class);
                i.putExtras(b3);
                if( b3.getString("name") != null)
                    startActivity(i);
            }
        });

        img_album_topper_hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DetailsAlbumActivity.class);
                i.putExtras(b4);
                if( b4.getString("name") != null)
                    startActivity(i);
            }
        });

        img_fav_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DetailsActivity.class);
                i.putExtras(bf1);
                startActivity(i);
            }
        });

        img_fav_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DetailsActivity.class);
                i.putExtras(bf2);
                startActivity(i);
            }
        });

        img_fav_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), DetailsActivity.class);
                i.putExtras(bf3);
                startActivity(i);
            }
        });

        new GetHomeItems().execute();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();
        viewFavouriteSong();
    }

    //Helper function to get the screen width of the device in dp
    private float getScreenWidth(){
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        //float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels;
        Log.d("SKIFFLE", dpWidth + "");
        return dpWidth;
    }

    //Function to show favourite songs images in the grid in home screen
    void viewFavouriteSong(){
        DatabaseHandler mDatabaseHandler = new DatabaseHandler(getActivity());
        SQLiteDatabase db = mDatabaseHandler.getWritableDatabase();
        List<Map> mSongs = new ArrayList<>();
        List<Bitmap> mCoverArts = new ArrayList<>();
        Map<String, String> song = new HashMap<>();

        if( !mDatabaseHandler.isEmptyTableFavourites(db) ){
            Map<String, List> map = mDatabaseHandler.readFavourites(db);
            mSongs = map.get("songs");
            mCoverArts = map.get("images");
            for(int i = 0; i < mCoverArts.size(); i++){
                if( i == 0 ){
                    img_fav_1.setImageBitmap(mCoverArts.get(i));
                    img_fav_1.setVisibility(View.VISIBLE);
                    //filling the bundle
                    song = mSongs.get(0);
                    bf1.putString("name", song.get("name"));
                    bf1.putString("artist", song.get("artist"));
                    bf1.putString("album", song.get("album"));
                    bf1.putString("releaseDate", song.get("releaseDate"));
                    bf1.putString("genre", song.get("genre"));
                    bf1.putString("iTunesLink", song.get("iTunesLink"));
                    bf1.putString("img170", "NA");
                    img_fav_2.setVisibility(View.INVISIBLE);
                    img_fav_3.setVisibility(View.INVISIBLE);
                }
                else if( i == 1 ){
                    img_fav_2.setImageBitmap(mCoverArts.get(i));
                    img_fav_2.setVisibility(View.VISIBLE);

                    song = mSongs.get(1);
                    bf2.putString("name", song.get("name"));
                    bf2.putString("artist", song.get("artist"));
                    bf2.putString("album", song.get("album"));
                    bf2.putString("releaseDate", song.get("releaseDate"));
                    bf2.putString("genre", song.get("genre"));
                    bf2.putString("iTunesLink", song.get("iTunesLink"));
                    bf2.putString("img170", "NA");

                    img_fav_3.setVisibility(View.INVISIBLE);
                }
                else if( i == 2 ){
                    img_fav_3.setImageBitmap(mCoverArts.get(i));
                    img_fav_3.setVisibility(View.VISIBLE);

                    song = mSongs.get(2);
                    bf3.putString("name", song.get("name"));
                    bf3.putString("artist", song.get("artist"));
                    bf3.putString("album", song.get("album"));
                    bf3.putString("releaseDate", song.get("releaseDate"));
                    bf3.putString("genre", song.get("genre"));
                    bf3.putString("iTunesLink", song.get("iTunesLink"));
                    bf3.putString("img170", "NA");
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    class GetHomeItems extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {

            //downloading song topper english
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url1, ServiceHandler.GET);

            Log.d("SKIFFLE", "Downloading home items");

            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject feed = jsonObj.getJSONObject("feed");
                    JSONArray entry = feed.getJSONArray("entry");



                    {
                        //getting single song object at a time
                        JSONObject songObj = entry.getJSONObject(0);

                        //Storing each element of the song in a string only to bind them in a map later
                        String name = songObj.getJSONObject("im:name").getString("label");
                        JSONArray images = songObj.getJSONArray("im:image");
                        String img55 = images.getJSONObject(0).getString("label");
                        String img170 = images.getJSONObject(2).getString("label");
                        String album = songObj.getJSONObject("im:collection").getJSONObject("im:name").getString("label");
                        String artist = songObj.getJSONObject("im:artist").getString("label");
                        String genre = songObj.getJSONObject("category").getJSONObject("attributes").getString("term");
                        String releaseDate = songObj.getJSONObject("im:releaseDate").getJSONObject("attributes").getString("label");
                        String rights = songObj.getJSONObject("rights").getString("label");
                        String iTunesLink = songObj.getJSONObject("id").getString("label");

                        //Putting all the downloaded info in the bundle to send to details activity
                        b1.putString("name", name);
                        b1.putString("img170", img170);
                        b1.putString("album", album);
                        b1.putString("artist", artist);
                        b1.putString("genre", genre);
                        b1.putString("releaseDate", releaseDate);
                        b1.putString("iTunesLink", iTunesLink);



                        //Getting the Bitmap from the url of the small coverArt image
                        image1 = null;
                        try{
                            URL smallImageLink = new URL(img170);
                            image1 = BitmapFactory.decodeStream(smallImageLink.openConnection().getInputStream());
                        }catch (IOException e){
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("SKIFFLE", "Couldn't get any data from the url");
            }

            //Downloading song topper hindi

            // Making a request to url and getting response
            jsonStr = sh.makeServiceCall(url2, ServiceHandler.GET);

            Log.d("SKIFFLE", "Downloading home items");

            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject feed = jsonObj.getJSONObject("feed");
                    JSONArray entry = feed.getJSONArray("entry");

                    //entry contains array of 10 song objects
                    {
                        //getting single song object at a time
                        JSONObject songObj = entry.getJSONObject(0);

                        //Storing each element of the song in a string only to bind them in a map later
                        String name = songObj.getJSONObject("im:name").getString("label");
                        JSONArray images = songObj.getJSONArray("im:image");
                        String img55 = images.getJSONObject(0).getString("label");
                        String img170 = images.getJSONObject(2).getString("label");
                        String album = songObj.getJSONObject("im:collection").getJSONObject("im:name").getString("label");
                        String artist = songObj.getJSONObject("im:artist").getString("label");
                        String genre = songObj.getJSONObject("category").getJSONObject("attributes").getString("term");
                        String releaseDate = songObj.getJSONObject("im:releaseDate").getJSONObject("attributes").getString("label");
                        String rights = songObj.getJSONObject("rights").getString("label");
                        String iTunesLink = songObj.getJSONObject("id").getString("label");

                        //Binding all this information related to a single song in a map
                        HashMap<String, String> song = new HashMap<>();
                        b2.putString("name", name);
                        b2.putString("img170", img170);
                        b2.putString("album", album);
                        b2.putString("artist", artist);
                        b2.putString("genre", genre);
                        b2.putString("releaseDate", releaseDate);
                        b2.putString("iTunesLink", iTunesLink);



                        //Getting the Bitmap from the url of the small coverArt image
                        image2 = null;
                        try{
                            URL smallImageLink = new URL(img170);
                            image2 = BitmapFactory.decodeStream(smallImageLink.openConnection().getInputStream());
                        }catch (IOException e){
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("SKIFFLE", "Couldn't get any data from the url");
            }



            //Downloading album topper english

            // Making a request to url and getting response
            jsonStr = sh.makeServiceCall(url3, ServiceHandler.GET);

            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject feed = jsonObj.getJSONObject("feed");
                    JSONArray entry = feed.getJSONArray("entry");

                    //entry contains array of 10 song objects
                    {
                        //getting single song object at a time
                        JSONObject songObj = entry.getJSONObject(0);

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
                        b3.putString("name", name);
                        b3.putString("img170", img170);
                        b3.putString("album", album);
                        b3.putString("artist", artist);
                        b3.putString("genre", genre);
                        b3.putString("releaseDate", releaseDate);
                        b3.putString("iTunesLink", iTunesLink);


                        //Getting the Bitmap from the url of the small coverArt image
                        image3 = null;
                        try {
                            URL smallImageLink = new URL(img170);
                            image3 = BitmapFactory.decodeStream(smallImageLink.openConnection().getInputStream());
                        } catch (IOException e) {
                        }
                    }
                }catch (Exception e){

                }

            }

            // Downloading album topper hindi

            // Making a request to url and getting response
            jsonStr = sh.makeServiceCall(url4, ServiceHandler.GET);

            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONObject feed = jsonObj.getJSONObject("feed");
                    JSONArray entry = feed.getJSONArray("entry");

                    //entry contains array of 10 song objects
                    {
                        //getting single song object at a time
                        JSONObject songObj = entry.getJSONObject(0);

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
                        b4.putString("name", name);
                        b4.putString("img170", img170);
                        b4.putString("album", album);
                        b4.putString("artist", artist);
                        b4.putString("genre", genre);
                        b4.putString("releaseDate", releaseDate);
                        b4.putString("iTunesLink", iTunesLink);


                        //Getting the Bitmap from the url of the small coverArt image
                        image4 = null;
                        try {
                            URL smallImageLink = new URL(img170);
                            image4 = BitmapFactory.decodeStream(smallImageLink.openConnection().getInputStream());
                        } catch (IOException e) {
                        }
                    }
                }catch (Exception e){

                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if( image1 != null)
                img_song_topper_english.setImageBitmap(image1);
            if( image2 != null)
                img_song_topper_hindi.setImageBitmap(image2);
            if( image3 != null)
                img_album_topper_english.setImageBitmap(image3);
            if( image4 != null)
                img_album_topper_hindi.setImageBitmap(image4);
        }
    }
}
