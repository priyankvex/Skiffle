package com.wordpress.priyankvex.skiffle;

/**
 * Created by priyank on 26/12/14.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by priyank on 23/12/14.
 */
public class SongListAdapter extends ArrayAdapter<Map> {

    List<Bitmap> coverArts;

    public SongListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public SongListAdapter(Context context, int resource, List<Map> items, List<Bitmap> images) {
        super(context, resource, items);
        this.coverArts = images;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.song_list_item_row, null);

        }

        Map<String, String> song = getItem(position);
        Bitmap image = coverArts.get(position);

        if (song != null) {
            TextView name = (TextView) v.findViewById(R.id.song_list_item_name);
            TextView artist = (TextView) v.findViewById(R.id.song_list_item_artist);
            ImageView coverArt = (ImageView) v.findViewById(R.id.song_list_item_image_view);

            name.setText(song.get("name"));
            artist.setText(song.get("artist"));
            coverArt.setImageBitmap(image);
        }

        return v;

    }

}