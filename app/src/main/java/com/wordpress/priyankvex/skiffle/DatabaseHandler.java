package com.wordpress.priyankvex.skiffle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private static final String TABLE_TOP_SONGS = "top_10_songs";
    private static final String TABLE_TOP_ALBUMS = "top_10_albums";

    //Names of columns in TABLE_TOP_SONGS
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_LARGE_IMAGE_LINK = "large_image";
    private static final String KEY_ALBUM = "album";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_GENRE = "genre";
    private static final String KEY_DATE = "date";
    private static final String KEY_RIGHTS = "rights";
    private static final String KEY_LINK = "link";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryCreateTableTop10Songs = "CREATE TABLE " + TABLE_TOP_SONGS + "( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NAME + " TEXT, "
                + KEY_IMAGE + " BLOB, "
                + KEY_LARGE_IMAGE_LINK + " TEXT, "
                + KEY_ALBUM + " TEXT, "
                + KEY_ARTIST + " TEXT, "
                + KEY_GENRE + " TEXT, "
                + KEY_DATE + " TEXT, "
                + KEY_RIGHTS + " TEXT, "
                + KEY_LINK + " TEXT"
                + ")";

        //here key album is used to store the no of songs in the album and key name is the name of the album
        String queryCreateTableTop10Albums = "CREATE TABLE " + TABLE_TOP_ALBUMS + "( "
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NAME + " TEXT, "
                + KEY_IMAGE + " BLOB, "
                + KEY_LARGE_IMAGE_LINK + " TEXT, "
                + KEY_ALBUM + " TEXT, "
                + KEY_ARTIST + " TEXT, "
                + KEY_GENRE + " TEXT, "
                + KEY_DATE + " TEXT, "
                + KEY_RIGHTS + " TEXT, "
                + KEY_LINK + " TEXT"
                + ")";

        //Executing the query to create the table for top songs
        db.execSQL(queryCreateTableTop10Songs);
        db.execSQL(queryCreateTableTop10Albums);

        Log.d("SKIFFLE", "Tables created in the database");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOP_SONGS);

        // Create tables again
        onCreate(db);
    }


    void addTopSongs(List<HashMap> songs, List<Bitmap> coverArts){
        /**
         * @params songs : list of the songs where each element is a map containing all the info about a single song
         * @params coverArt  : bitmap of the small cover art picture downloaded during the network call
         */
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            //Deleting all the previous records of old songs from the table.
            //Soo mean naa! :(
            db.execSQL("delete from "+ TABLE_TOP_SONGS);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < songs.size(); i++) {
                ContentValues values = new ContentValues();
                Map<String, String> song = songs.get(i);
                values.put(KEY_NAME, song.get("name")); //1
                //Converting the respective coverArt to byte[]
                byte[] image = convertBitmapToByteArray(coverArts.get(i));
                values.put(KEY_IMAGE, image); //2
                values.put(KEY_LARGE_IMAGE_LINK, song.get("img170")); //3
                values.put(KEY_ALBUM, song.get("album")); //4
                values.put(KEY_ARTIST, song.get("artist")); //5
                values.put(KEY_GENRE, song.get("genre")); //6
                values.put(KEY_DATE, song.get("releaseDate")); //7
                values.put(KEY_RIGHTS, song.get("rights")); //8
                values.put(KEY_LINK, song.get("iTunesLink")); //9

                //Inserting this content value of a single song into the table
                db.insert(TABLE_TOP_SONGS, null, values);

        }

        db.close(); // Closing database connection

    }

    void addTopAlbums(List<HashMap> songs, List<Bitmap> coverArts){
        /**
         * @params songs : list of the songs where each element is a map containing all the info about a single song
         * @params coverArt  : bitmap of the small cover art picture downloaded during the network call
         */
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            //Deleting all the previous records of old songs from the table.
            //Soo mean naa! :(
            db.execSQL("delete from "+ TABLE_TOP_ALBUMS);
        } catch (Exception e) {
            e.printStackTrace();
        }




        for(int i = 0; i < songs.size(); i++){
                ContentValues values = new ContentValues();
                Map<String, String> song = songs.get(i);
                values.put(KEY_NAME, song.get("name")); //1
                //Converting the respective coverArt to byte[]
                byte[] image = convertBitmapToByteArray(coverArts.get(i));
                values.put(KEY_IMAGE, image); //2
                values.put(KEY_LARGE_IMAGE_LINK, song.get("img170")); //3
                values.put(KEY_ALBUM, song.get("album")); //4
                values.put(KEY_ARTIST, song.get("artist")); //5
                values.put(KEY_GENRE, song.get("genre")); //6
                values.put(KEY_DATE, song.get("releaseDate")); //7
                values.put(KEY_RIGHTS, song.get("rights")); //8
                values.put(KEY_LINK, song .get("iTunesLink")); //9

                //Inserting this content value of a single song into the table
                db.insert(TABLE_TOP_ALBUMS, null, values);

        }

        db.close(); // Closing database connection

    }

    //Helper function to convert Bitmap to byte[]
    private byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return buffer.toByteArray();
    }

    Map readTopSongs(){
        //List of maps to hold all the info related to the songs
        List<Map> songs = new ArrayList<>();
        //List of bitmaps i.e the coverArts of the songs
        List<Bitmap> images = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TOP_SONGS;

        Cursor cursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> song = new HashMap<>();
                song.put("name", cursor.getString(1));
                song.put("img170", cursor.getString(3));
                song.put("album", cursor.getString(4));
                song.put("artist", cursor.getString(5));
                song.put("genre", cursor.getString(6));
                song.put("date", cursor.getString(7));
                song.put("rights", cursor.getString(8));
                song.put("iTunesLink", cursor.getString(9));
                songs.add(song);

                //Preparing the bitmap image
                byte[] img = cursor.getBlob(2);
                Bitmap image = convertByteArrayToBitmap(img);
                images.add(image);

            } while (cursor.moveToNext());
        }

        Map<String, List> map = new HashMap<>();
        map.put("songs", songs);
        map.put("images", images);

        // return map of the 2 lists
        return map;
    }

    Map readTopAlbums(){
        //List of maps to hold all the info related to the songs
        List<Map> songs = new ArrayList<>();
        //List of bitmaps i.e the coverArts of the songs
        List<Bitmap> images = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TOP_ALBUMS;

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Map<String, String> song = new HashMap<>();
                song.put("name", cursor.getString(1));
                song.put("img170", cursor.getString(3));
                song.put("album", cursor.getString(4));
                song.put("artist", cursor.getString(5));
                song.put("genre", cursor.getString(6));
                song.put("date", cursor.getString(7));
                song.put("rights", cursor.getString(8));
                song.put("iTunesLink", cursor.getString(9));
                songs.add(song);

                //Preparing the bitmap image
                byte[] img = cursor.getBlob(2);
                Bitmap image = convertByteArrayToBitmap(img);
                images.add(image);

            } while (cursor.moveToNext());
        }

        Map<String, List> map = new HashMap<>();
        map.put("songs", songs);
        map.put("images", images);

        // return map of the 2 lists
        return map;
    }

    //Helper function to get the Bitmap image from the byte[]
    private Bitmap convertByteArrayToBitmap(byte[] img){
        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        return bitmap;
    }


    //Function to check a table is empty or not
    public boolean isEmptyTableTop10Songs(){
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String countQuery = "SELECT count(*) FROM " + TABLE_TOP_SONGS;
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

    //Function to check a table is empty or not
    public boolean isEmptyTableTop10Albums(){
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String countQuery = "SELECT count(*) FROM " + TABLE_TOP_ALBUMS;
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
