package com.wordpress.priyankvex.skiffle;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by priyank on 23/12/14.
 */
public class FragmentFavourites extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    DatabaseHandler mDatabaseHandler;
    SQLiteDatabase db;

    Activity activity;

    ListView listView_favourites;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentFavourites newInstance(int sectionNumber) {
        FragmentFavourites fragment = new FragmentFavourites();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentFavourites() {

    }

    //Lists for main thread
    List<Map> mSongs = new ArrayList<>();
    List<Bitmap> mCoverArts = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        listView_favourites = (ListView)rootView.findViewById(R.id.list_view_favourites);
        mDatabaseHandler = new DatabaseHandler(getActivity());
        db = mDatabaseHandler.getWritableDatabase();
        if(mDatabaseHandler.isEmptyTableFavourites(db)){
            rootView = inflater.inflate(R.layout.no_favourites, container, false);
        }

        listView_favourites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> song = mSongs.get(position);
                Bitmap image = mCoverArts.get(position);
                Bundle b = new Bundle();
                b.putParcelable("image", image);
                b.putString("img170", "NA");
                b.putString("name", song.get("name"));
                b.putString("album", song.get("album"));
                b.putString("artist", song.get("artist"));
                b.putString("genre", song.get("genre"));
                b.putString("releaseDate", song.get("releaseDate"));
                b.putString("iTunesLink", song.get("iTunesLink"));
                Intent i = new Intent(getActivity(), DetailsActivity.class);
                i.putExtras(b);
                startActivity(i);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Map<String, List> map = mDatabaseHandler.readFavourites(db);
        mSongs = map.get("songs");
        mCoverArts = map.get("images");
        SongListAdapter adapter = new SongListAdapter(activity, R.layout.song_list_item_row, mSongs, mCoverArts);
        listView_favourites.setAdapter(adapter);
        Log.d("SKIFFLE", Integer.toString(mCoverArts.size()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));

        this.activity = activity;
    }
}