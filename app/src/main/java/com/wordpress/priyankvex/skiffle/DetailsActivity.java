package com.wordpress.priyankvex.skiffle;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

/**
 * Created by priyank on 26/12/14.
 * Class to display the details of the song selected from the list.
 * This activity receives intents from a activities. FragmentTopSongs and FragmentFavourites
 */
public class DetailsActivity extends ActionBarActivity {
    private TextView textview_name;
    private TextView textview_artist;
    private TextView textview_album;
    private TextView textview_releaseDate;
    private TextView textview_genre;
    private ImageView coverArt;
    private ImageView btn_itunes;
    private ImageView btn_youtube;
    private ImageView btn_grooveshark;
    private ImageView btn_favourites;

    //Look a bundle :P
    Bundle b;

    Bitmap image = null;

    boolean songInFavourites = false;

    DatabaseHandler db;

    SQLiteDatabase database;

    //Flag to determine whether intent is from FragmentTopSongs or FragmentFavourites
    boolean favouriteFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Objects for database access.
        db = new DatabaseHandler(DetailsActivity.this);
        database = db.getWritableDatabase();

        //Initializing view widgets
        textview_name = (TextView)findViewById(R.id.details_name);
        textview_artist = (TextView)findViewById(R.id.details_artist);
        textview_album = (TextView)findViewById(R.id.details_album);
        textview_releaseDate = (TextView)findViewById(R.id.details_releasedate);
        textview_genre = (TextView)findViewById(R.id.details_genre);
        btn_itunes = (ImageView)findViewById(R.id.btn_itunes);
        btn_youtube = (ImageView)findViewById(R.id.btn_youtube);
        btn_grooveshark = (ImageView)findViewById(R.id.btn_grooveshark);
        btn_favourites = (ImageView)findViewById(R.id.btn_favourites);

        //setting the text of the TextViews taking from the bundle
        //It's a fucking fat bundle.
        b = getIntent().getExtras();
        textview_name.setText(b.getString("name"));
        textview_artist.setText("Artist : " + b.getString("artist"));
        textview_album.setText("Album : " + b.getString("album"));
        textview_releaseDate.setText("Release Date : " + b.getString("releaseDate"));
        textview_genre.setText("Genre : " + b.getString("genre"));
        coverArt = (ImageView)findViewById(R.id.details_coverart);

        if ( ! ((b.getString("img170")).equals("NA")) ) {

            //Downloading the coverArt from the img170 link over the inernet.

            int height = (int)getScreenWidth();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(height, height);
            coverArt.setLayoutParams(layoutParams);
            String img170 = b.getString("img170");
            new GetCoverArt().execute(img170);
        }
        else{

            //image is coming from favourites and thus not needed to be downloaded
            image = db.getCoverArt(database, b);
            favouriteFlag = true;
            Drawable drawable = new BitmapDrawable(getResources(), image);
            drawable.setAlpha(100);
            coverArt.setImageDrawable(drawable);
            coverArt.setVisibility(View.VISIBLE);

        }

        //Checking if song is in favourites or not
        songInFavourites = db.isInFavourites(b);

        if( songInFavourites ){
            btn_favourites.setImageResource(R.drawable.btn_favourites_on);
            songInFavourites = true;
        }
        else{
            btn_favourites.setImageResource(R.drawable.btn_favourites_off);
            songInFavourites = false;
        }

        //Setting the onClick listeners on the image views

        btn_itunes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = b.getString("iTunesLink");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        btn_youtube.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String url = "https://www.youtube.com/results?search_query=" + b.get("name") + " " + b.get("artist");
                url=url.replaceAll(" ", "%20");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        btn_grooveshark.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String url = "http://grooveshark.com/#!/search/" + b.get("name") + " " + b.get("artist");
                url=url.replaceAll(" ", "%20");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        btn_favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(songInFavourites){
                    //song already in favourites. We will remove it
                    db.deleteFromFavourites(b);
                    btn_favourites.setImageResource(R.drawable.btn_favourites_off);
                    Toast toast  = Toast.makeText(DetailsActivity.this, "Item unpinned from favourites.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    songInFavourites = false;
                }
                else if(image != null){
                    //song not present in favorites. we will add the song
                    db.addFavourites(database, b, image);
                    btn_favourites.setImageResource(R.drawable.btn_favourites_on);
                    Toast toast  = Toast.makeText(DetailsActivity.this, "Whoa! Nice choice. Item pinned to favourites.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                    songInFavourites = true;
                }

            }
        });
    }

    //Helper function to get the screen width of the device in dp
    private float getScreenWidth(){
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        return dpWidth;
    }

    /**
     * Background thread to download the cover art from the internet.
     */
    private class GetCoverArt extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            //Getting the Bitmap from the url of the small coverArt image
            image = null;
            try{
                URL smallImageLink = new URL(params[0]);
                image = BitmapFactory.decodeStream(smallImageLink.openConnection().getInputStream());
            }catch (IOException e){
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Drawable drawable = new BitmapDrawable(getResources(), image);
            drawable.setAlpha(100);
            coverArt.setImageDrawable(drawable);
            coverArt.setVisibility(View.VISIBLE);
        }
    }

    //Adding the share button. God help me with this one

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);

        android.support.v7.widget.ShareActionProvider mShareActionProvider = (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);


        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareIntent());
        }
        else{

        }
        return super.onCreateOptionsMenu(menu);
    }

    private Intent createShareIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        if( favouriteFlag ){
            shareIntent.putExtra(Intent.EXTRA_TEXT , "Checkout one of my favourite " + b.get("name") + " by " + b.get("artist") + ". Shared via: #SkiffleApp");
        }
        else{
            shareIntent.putExtra(Intent.EXTRA_TEXT , "Checkout the song " + b.get("name") + " by " + b.get("artist") + ". Shared via: #SkiffleApp");
        }
        return shareIntent;
    }
}
