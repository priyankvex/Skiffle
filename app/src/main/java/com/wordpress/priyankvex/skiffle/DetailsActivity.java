package com.wordpress.priyankvex.skiffle;

import android.content.Intent;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

/**
 * Created by priyank on 26/12/14.
 * Class to display the details of the song selected from the list.
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

    Bundle b;

    Bitmap image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

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
        b = getIntent().getExtras();
        textview_name.setText(b.getString("name"));
        textview_artist.setText("Artist : " + b.getString("artist"));
        textview_album.setText("Album : " + b.getString("album"));
        textview_releaseDate.setText("Release Date : " + b.getString("releaseDate"));
        textview_genre.setText("Genre : " + b.getString("genre"));

        //Getting the coverArt from the img170 link
        coverArt = (ImageView)findViewById(R.id.details_coverart);
        int height = (int)getScreenWidth();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(height, height);
        coverArt.setLayoutParams(layoutParams);
        String img170 = b.getString("img170");
        new GetCoverArt().execute(img170);

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
    }

    //Helper function to get the screen width of the device in dp
    private float getScreenWidth(){
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        //float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels;
        Log.d("SKIFFLE", dpWidth+"");
        return dpWidth;
    }

    private class GetCoverArt extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            //Getting the Bitmap from the url of the small coverArt image
            Log.d("SKIFFLE", "downloading the cover art");
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
            Log.d("SKIFFLE", "done with the cover art");
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
        shareIntent.putExtra(Intent.EXTRA_TEXT , "Checkout the song " + b.get("name") + " by " + b.get("artist") + ". Shared via: #SkiffleApp");
        return shareIntent;
    }
}
