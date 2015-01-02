package com.wordpress.priyankvex.skiffle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by priyank on 22/12/14.
 * Class to handle all the database operations and provides functions to perform them.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "skiffle_database";

    //Names of the tables in the database
    private static final String TABLE_FAVOURTIES = "favourites";


    //Names of columns in TABLE_FAVOURITES
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_ALBUM = "album";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_GENRE = "genre";
    private static final String KEY_DATE = "date";
    private static final String KEY_LINK = "link";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryCreateTableTop10Songs = "CREATE TABLE " + TABLE_FAVOURTIES + "( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NAME + " TEXT, "
                + KEY_IMAGE + " BLOB, "
                + KEY_ALBUM + " TEXT, "
                + KEY_ARTIST + " TEXT, "
                + KEY_GENRE + " TEXT, "
                + KEY_DATE + " TEXT, "
                + KEY_LINK + " TEXT"
                + ")";



        //Executing the query to create the table for top songs
        db.execSQL(queryCreateTableTop10Songs);


        Log.d("SKIFFLE", "Tables created in the database");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURTIES);

        // Create tables again
        onCreate(db);
    }


    void addFavourites(SQLiteDatabase db, Bundle b, Bitmap coverArt){
        /**
         * @params songs : list of the songs where each element is a map containing all the info about a single song
         * @params coverArt  : bitmap of the small cover art picture downloaded during the network call
         */

        {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, b.getString("name")); //1
            //Converting the respective coverArt to byte[]
            byte[] image = convertBitmapToByteArray(coverArt);
            values.put(KEY_IMAGE, image); //2
            values.put(KEY_ALBUM, b.getString("album")); //4
            values.put(KEY_ARTIST, b.getString("artist")); //5
            values.put(KEY_GENRE, b.getString("genre")); //6
            values.put(KEY_DATE, b.getString("releaseDate")); //7
            Log.d("SKIFFLE", b.getString("releaseDate"));
            values.put(KEY_LINK, b.getString("iTunesLink")); //9

            //Inserting this content value of a single song into the table
            db.insert(TABLE_FAVOURTIES, null, values);

        }



    }


    //Helper function to convert Bitmap to byte[]
    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }

    Map readFavourites(SQLiteDatabase db){
        //List of maps to hold all the info related to the songs
        List<Map> songs = new ArrayList<>();
        //List of bitmaps i.e the coverArts of the songs
        List<Bitmap> images = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FAVOURTIES;

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> song = new HashMap<>();
                song.put("name", cursor.getString(1));
                images.add(convertByteArrayToBitmap(cursor.getBlob(2)));
                song.put("album", cursor.getString(3));
                song.put("artist", cursor.getString(4));
                song.put("genre", cursor.getString(5));
                song.put("releaseDate", cursor.getString(6));
                song.put("iTunesLink", cursor.getString(7));
                songs.add(song);

            } while (cursor.moveToNext());
        }

        Map<String, List> map = new HashMap<>();
        map.put("songs", songs);
        map.put("images", images);

        // return map of the 2 lists
        return map;
    }

    Bitmap getCoverArt(SQLiteDatabase db, Bundle b){
        Bitmap image = null;
        byte[] raw_image;
        String sql = "SELECT *"  + " FROM " + TABLE_FAVOURTIES + " WHERE " + KEY_LINK + " = " + "\"" + b.getString("iTunesLink") + "\"";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            raw_image = cursor.getBlob(2);
            image = convertByteArrayToBitmap(raw_image);
        }
        return image;
    }

    void deleteFromFavourites(Bundle b){
        String sql = "DELETE FROM " +TABLE_FAVOURTIES + " WHERE " + KEY_LINK + " = " + "\"" + b.getString("iTunesLink") + "\"";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);

        //db.close();
    }

    boolean isInFavourites(Bundle b) {
        String sql = "SELECT count(*) FROM " + TABLE_FAVOURTIES + " WHERE " + KEY_LINK + " = " + "\"" + b.getString("iTunesLink") + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor mcursor = null;
        int icount = 0;
        try {
            mcursor = db.rawQuery(sql, null);
            mcursor.moveToFirst();
            icount = mcursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(icount >= 1){
            return true;
        }
        else{
           return false;
        }
    }


    //Helper function to get the Bitmap image from the byte[]
    private Bitmap convertByteArrayToBitmap(byte[] img){
        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        return bitmap;
    }


    //Function to check a table is empty or not
    public boolean isEmptyTableFavourites(SQLiteDatabase db){

        String countQuery = "SELECT count(*) FROM " + TABLE_FAVOURTIES;
        Cursor mcursor = null;
        int icount = 0;
        try {
            mcursor = db.rawQuery(countQuery, null);
            mcursor.moveToFirst();
            icount = mcursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(icount > 0 ){
            return false;
        }
        return true;
    }



}
